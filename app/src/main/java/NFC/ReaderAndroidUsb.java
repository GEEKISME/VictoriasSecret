package NFC;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import com.mwcard.Reader;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2017-10-20.
 */

public class ReaderAndroidUsb extends Reader {
    private UsbRequest urReceive = new UsbRequest();
    private UsbDevice m_hidReader;
    private UsbDeviceConnection m_hidCom;
    private UsbManager m_manager;
    private int m_timeouts = 5600;
    private ReaderAndroidUsb.WaiterRun mCurRun = new ReaderAndroidUsb.WaiterRun();
    private Thread mThread;
    private ReaderAndroidUsb.WaiterThread mWaiterThread;
    private int protocolType;
    private static final String INTERFACE_USB = "AndroidUSB";
    public static int HID_REPORT_SIZE_63 = 63;
    public static int HID_REPORT_SIZE_254 = 254;
    private final int HID_GETREPORT_TYPE;
    private final int HID_SETREPORT_TYPE;
    private final int HID_GETREPORT_REQUEST;
    private final int HID_SETREPORT_REQUEST;
    private final int HID_GETREPORT_VALUE;
    private final int HID_SETREPORT_VALUE;

    public ReaderAndroidUsb(UsbManager manager) {
        super("AndroidUSB", "115200");
        this.mThread = new Thread(this.mCurRun);
        this.protocolType = 0;
        this.HID_GETREPORT_TYPE = 161;
        this.HID_SETREPORT_TYPE = 33;
        this.HID_GETREPORT_REQUEST = 1;
        this.HID_SETREPORT_REQUEST = 9;
        this.HID_GETREPORT_VALUE = 774;
        this.HID_SETREPORT_VALUE = 774;
        this.m_manager = manager;
    }

    public static boolean isSupported(UsbDevice device) {
        boolean bSt = false;
        int iVID = device.getVendorId();
        int iPID = device.getProductId();
        if(iVID == 1155 && iPID == 19799 || iVID == 4292 && iPID == '苍') {
            bSt = true;
        }

        return bSt;
    }

    public int openReader(UsbDevice device) throws Exception {
        int st = 1;
        if(this.m_manager == null) {
            return -1;
        } else {
            if(this.m_hidCom != null) {
                this.closeReader();
            }

            this.m_hidReader = device;
            st = this.m_hidReader.getInterfaceCount();
            Log.i("mwcard", "接口个数" + this.m_hidReader.getInterfaceCount());
            int iVID = device.getVendorId();
            int iPID = device.getProductId();
            if(iVID == 1155 && iPID == 19799) {
                this.protocolType = 1;
            } else if(iVID == 4292 && iPID == '苍') {
                this.protocolType = 0;
            }

            this.m_hidCom = this.m_manager.openDevice(device);
            if(this.m_hidCom != null) {
                this.devHandle = (long)this.m_hidCom.getFileDescriptor();
                this.strParas = Long.toString(this.devHandle);
                st = this.openReader();
            }

            return st;
        }
    }

    public int closeReader() {
        if(this.m_hidCom != null) {
            this.m_hidCom.close();
        }

        return super.closeReader();
    }

    public int getProtocolType() {
        return this.protocolType;
    }

    public int claim() {
        Log.i("mwcard", "接口个数" + this.m_hidReader.getInterfaceCount());
        int st = 0;
        boolean status = this.m_hidCom.claimInterface(this.m_hidReader.getInterface(0), true);
        if(status) {
            st = 1;
            return 1;
        } else {
            return st;
        }
    }

    public int release() {
        int st = 0;
        boolean status = this.m_hidCom.releaseInterface(this.m_hidReader.getInterface(0));
        if(status) {
            st = 1;
            return 1;
        } else {
            return st;
        }
    }

    public int writeData(byte[] src, int timeOuts) {
        int flag = 0;
        int len = 0;
        boolean status = false;
        int iTotal;
        if(this.protocolType == 0) {
            iTotal = src.length;
            iTotal = 0;

            do {
                byte[] bArraySend = new byte[255];
                bArraySend[0] = 6;
                if(iTotal <= HID_REPORT_SIZE_254) {
                    System.arraycopy(src, iTotal, bArraySend, 1, iTotal);
                    iTotal = 0;
                } else {
                    System.arraycopy(src, iTotal, bArraySend, 1, HID_REPORT_SIZE_254);
                    iTotal -= HID_REPORT_SIZE_254;
                    iTotal += HID_REPORT_SIZE_254;
                }

                len = this.writeFeature(bArraySend, timeOuts);
                if(len <= 0) {
                    return -5;
                }
            } while(iTotal != 0);
        } else {
            UsbRequest urSend = new UsbRequest();
            status = urSend.initialize(this.m_hidCom, this.m_hidReader.getInterface(0).getEndpoint(1));
            if(!status) {
                Log.i("mwcard", "初始化中断传输失败");
                return -5;
            }

            iTotal = src.length;
            int index = 0;

            do {
                byte[] bArraySend = new byte[HID_REPORT_SIZE_63 + 1];
                bArraySend[0] = 2;
                if(iTotal <= HID_REPORT_SIZE_63) {
                    System.arraycopy(src, index, bArraySend, 1, iTotal);
                    iTotal = 0;
                } else {
                    System.arraycopy(src, index, bArraySend, 1, HID_REPORT_SIZE_63);
                    iTotal -= HID_REPORT_SIZE_63;
                    index += HID_REPORT_SIZE_63;
                }

                ByteBuffer bbSend = ByteBuffer.wrap(bArraySend, 0, bArraySend.length);
                status = urSend.queue(bbSend, HID_REPORT_SIZE_63 + 1);
                if(!status) {
                    Log.i("mwcard", "写一包失败");
                    return -5;
                }

                UsbRequest urResult = this.m_hidCom.requestWait();
                if(urResult == urSend) {
                    ;
                }
            } while(iTotal != 0);
        }

        return flag;
    }

    public byte[] readData(int uiRead, int timeOuts) {
        int flag = 0;
        int len = 0;
        byte[] bArrayResult = null;
        byte[] bArrayReceive;

        if(this.protocolType == 0) {
            bArrayReceive = new byte[HID_REPORT_SIZE_254 + 1];
            bArrayReceive[0] = 6;
            len = this.m_hidCom.controlTransfer(161, 1, 774, 0, bArrayReceive, bArrayReceive.length, timeOuts);
            if(len < 0) {
                return null;
            }

            bArrayResult = new byte[HID_REPORT_SIZE_254];
            System.arraycopy(bArrayReceive, 1, bArrayResult, 0, HID_REPORT_SIZE_254);
        } else {
            UsbEndpoint inEndpoint = this.m_hidReader.getInterface(0).getEndpoint(0);
            int inMax = inEndpoint.getMaxPacketSize();
            bArrayReceive = new byte[HID_REPORT_SIZE_63 + 1];
            bArrayReceive[0] = 3;
            boolean status = this.urReceive.initialize(this.m_hidCom, this.m_hidReader.getInterface(0).getEndpoint(0));
            if(!status) {
                return null;
            }

            ByteBuffer bbReceive = ByteBuffer.wrap(bArrayReceive, 0, bArrayReceive.length);
            this.mWaiterThread = new ReaderAndroidUsb.WaiterThread();
            this.mWaiterThread.start();
            status = this.urReceive.queue(bbReceive, inMax);
            if(!status) {
                return null;
            }

            long timeBegin = System.currentTimeMillis();

            try {
                this.mWaiterThread.join((long)timeOuts);
                long timeEnd = System.currentTimeMillis();
                if(timeEnd - timeBegin >= (long)timeOuts) {
                    this.mWaiterThread.interrupt();
                    return null;
                }
            } catch (Exception var15) {
                return null;
            }

            bArrayResult = new byte[HID_REPORT_SIZE_63];
            System.arraycopy(bArrayReceive, 1, bArrayResult, 0, HID_REPORT_SIZE_63);
        }

        return bArrayResult;
    }

    private int writeFeature(byte[] packet, int timeout) {
        int len = this.m_hidCom.controlTransfer(33, 9, 774, 0, packet, packet.length, timeout);
        return len;
    }

    public static void main(String[] args) {
    }

    private class WaiterRun implements Runnable {
        private WaiterRun() {
        }

        public void run() {
            try{
                if(ReaderAndroidUsb.this.m_hidCom.requestWait() == ReaderAndroidUsb.this.urReceive) {
                    int i = 0;
                    int j = 1;
                    int var10000 = i + j;
                }
            }catch(Exception e){

            }
        }
    }

    private class WaiterThread extends Thread {
        public boolean mStop;

        private WaiterThread() {
        }

        public void run() {
            try{
                if(ReaderAndroidUsb.this.m_hidCom.requestWait() == ReaderAndroidUsb.this.urReceive) {
                    int i = 0;
                    int j = 1;
                    int var10000 = i + j;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}