package NFC;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.biotag.victoriassecret.Constants;
import com.mwcard.Reader;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2017-10-19.
 */

public class NFCToolManager {
    private final String TAG = this.getClass().getSimpleName();
    private Activity activity;
    private String Device_USB = "com.android.example.USB";
    private UsbManager manager;
    private Reader reader = null;
    private CheckCardThread mCheckCard;

    private String curId = "";
    public NFCToolManager(Activity activity){
        this.activity = activity;

        IntentFilter filter = new IntentFilter(Device_USB);
        this.activity.registerReceiver(usbReceiver, filter);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Device_USB.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openReader();
                    } else {

                    }
                }
            }
        }
    };

    private int stimes = 0;
    public void openReader(){
        manager = null;
        reader = null;
        curId = "";
        // 获取USB管理器
        manager = (UsbManager)activity.getSystemService(Context.USB_SERVICE);
        // 获取一个已连接的USB设备，并且包含方法，以访问其标识信息、 接口和端点
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if (deviceList.size() == 0) {
            //etResultAddStr("未找到设备");
            return;
        }
        // 获取deviceList迭代器
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        // 判断迭代器中是否有元素
        while (deviceIterator.hasNext()) {
            // 如果有，获取元素
            UsbDevice usbDevice = deviceIterator.next();

            if (!ReaderAndroidUsb.isSupported(usbDevice)) {
                continue;
            }
            // 判断是否拥有该设备的连接权限
            if (!manager.hasPermission(usbDevice)) {
                // 如果没有则请求权限
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this.activity, 0,
                        new Intent(Device_USB), PendingIntent.FLAG_UPDATE_CURRENT);
						/*
						 * 展示征求用户同意连接这个设备的权限的对话框。 当用户回应这个对话框时,
						 * 广播接收器就会收到一个包含用一个boolean值来表示结果的EXTRA_PERMISSION_GRANTED字段的意图。
						 * 在连接设备之前检查这个字段的值是否为true和设备之间的“交流”
						 */
                manager.requestPermission(usbDevice, mPermissionIntent);
            } else {
                // 如果已经拥有该设备的连接权限，直接对该设备操作
                ReaderAndroidUsb readerAndroidUsb = new ReaderAndroidUsb(manager);
                try {
                    int st = readerAndroidUsb.openReader(usbDevice);


//                    if(tt >= 1){
//                        throw new Exception();
//                    }
                    if (st >= 0) {
                        reader = readerAndroidUsb;
                        mCheckCard = new CheckCardThread();
                        mCheckCard.start();

                        mHandler.removeCallbacks(CheckAliveRunnable);
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.postDelayed(CheckAliveRunnable, 10000);

                        stimes ++;
                    }
                    Log.w(TAG, "openReader: stimes = " + stimes);

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    Log.w(TAG, "openReader: restart reader");

                    mHandler.sendEmptyMessageDelayed(MSG_REOPENREADER, 1000);

//                    Intent intent = this.activity.getIntent();
//                    this.activity.finish();
//                    this.activity.startActivity(intent);
                }
            }
        }
    }

    private void readerBeep(boolean isSuccess){
        try {
            if (reader == null) {
                return;
            }
            if(isSuccess){
                reader.beep(1, 1, 2);
            }else{
                reader.beep(3, 1, 2);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void destory(){
        this.activity.unregisterReceiver(usbReceiver);

        if(mCheckCard != null){
            mCheckCard.stopThread();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(CheckAliveRunnable);
    }

    private void stopCheck(){
        if(mCheckCard != null){
            mCheckCard.stopThread();
        }
    }

    private void clearCurId(){
        curId = "";
    }

    private Runnable ClearCurIDRunnable = new Runnable() {
        @Override
        public void run() {
            clearCurId();
        }
    };

    private long time = -1;
    private Runnable CheckAliveRunnable = new Runnable() {
        @Override
        public void run() {
            try{
                Log.w(TAG, "run: CheckAliveRunnable time = " + time);
                if(time > 0 && System.currentTimeMillis() - time > 10000){
                    if(mCheckCard == null){
                        time = -1;
                        doReOpen();
                        return;
                    }else if (mCheckCard.isCanStop()) {
                        time = -1;
                        doReOpen();
                        return;
                    }else if(System.currentTimeMillis() - time > 13000){
                        time = -1;
                        doReOpen();
                        return;
                    }
                }
                mHandler.postDelayed(this, 5000);
            }catch(Exception e){
                time = -1;
                doReOpen();
            }

        }
    };

    private void doReOpen(){
        if (reader != null) {
            reader.closeReader();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(CheckAliveRunnable);
        if (mCheckCard != null) {
            mCheckCard.stopThread();
            mCheckCard = null;
        }

        openReader();
    }



    class CheckCardThread extends Thread {

        private boolean isRun = true;
        private int times = 0;
        private boolean canStop = true;

        public void stopThread(){
            isRun = false;
        }

        public boolean isCanStop(){
            return canStop;
        }

        public void doWait(){
            try{
                synchronized (mCheckCard){
                    wait();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isRun) {

                try {
                    if (reader != null) {
//                        mHandler.removeCallbacks(ReOpenNFCRunnable);
//                        mHandler.postDelayed(ReOpenNFCRunnable,3000);



                        String cardid = reader.openCard(0);

                        Log.i("tag", "1run: curId = " + curId);
//                        if(!cardid.equals(curId)){
                            curId = cardid;
                            mHandler.removeCallbacks(ClearCurIDRunnable);
                            mHandler.postDelayed(ClearCurIDRunnable,3000);
                            Message msg = mHandler.obtainMessage(MSG_GETCARDID,cardid);
                            mHandler.sendMessage(msg);

                            times ++;
                            Log.i("tag", "2run: curId = " + curId);
                            synchronized (mCheckCard){
                                canStop = false;
                                Log.w(TAG, "run: canStop = " + canStop);
                                wait();
                                canStop = true;
                                Log.w(TAG, "run: canStop = " + canStop);
                            }
//                        }
                    }
                } catch (Exception e) {
                    Log.i("tag", "3run: curId = " + curId);
//                    curId = "";
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
                Log.w(TAG, "run: times = " + times);
//                if(times >= 3){
//                    break;
//                }else{
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                }
            }

//            if(isRun){
//                Log.w(TAG, "run: reOpenReader" );
//
//                Message msg = mHandler.obtainMessage(MSG_REOPENREADER);
//                mHandler.sendMessage(msg);
//            }
        }
    }

    public void writeAreaNow(int code,String AreaNow){
        Log.i("tag", "xxx writeAreaNo: ");
        mReadNFCThread.writeAreaNow(code,AreaNow);
//        new WriteNFCThread(AreaNow).start();
    }

    private ReadNFCInterface mInterface = null;
    public void setReadNFCInterface(ReadNFCInterface i){
        mInterface = i;
    }

    private final int MSG_GETCARDID = 1;
    private final int MSG_READNFCOK = 2;
    private final int MSG_READNFCFAIL = 3;
    private final int MSG_WRITE_AREANOW_OK = 4;
    private final int MSG_WRITE_AREANOW_FAIL = 5;
    private final int MSG_DENIED = 6;
    private final int MSG_CHECK_EMPLOYEECARD = 7;
    private final int MSG_REOPENREADER = 8;
    private final int MSG_CHECKCARDALIIVE = 9;
    private final int MSG_SCANEMPLOYCARD = 10;

    private MyM1Handler mHandler = new MyM1Handler();

    private ReadNFCThread mReadNFCThread = null;
    class MyM1Handler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GETCARDID) {
                time = System.currentTimeMillis();
                if(mInterface != null){
                    mInterface.onGetID((String)msg.obj);
                }
                mReadNFCThread = new ReadNFCThread((String)msg.obj);
                mReadNFCThread.start();
            }else if(msg.what == MSG_READNFCOK){
                if(mInterface != null){
                    mInterface.onGetCardInfo((CardInfo)msg.obj);
                }
            }else if(msg.what == MSG_READNFCFAIL){
                if(mInterface != null){
                    mInterface.onReadCardFail();
                    readerBeep(false);
                }
            }else if(msg.what == MSG_WRITE_AREANOW_OK){
                if (mInterface != null) {
                    mInterface.onWriteAreaOk(true,(CardInfo) msg.obj);
                    readerBeep(true);
                }
            } else if (msg.what == MSG_WRITE_AREANOW_FAIL) {
                if (mInterface != null) {
                    mInterface.onWriteAreaFail();
                    readerBeep(false);
                }
            } else if (msg.what == MSG_DENIED) {
                if (mInterface != null) {
                    int code = (Integer) msg.obj;
                    mInterface.onDenied(code);
                    readerBeep(false);
                }
            } else if (msg.what == MSG_CHECK_EMPLOYEECARD) {
                int code = (Integer) msg.obj;
                if(code == Constants.CHECK_EMPLOYEECARD_OK)readerBeep(true);
                else if(code == Constants.CHECK_EMPLOYEECARD_FAIL)readerBeep(false);
            } else if (msg.what ==  MSG_REOPENREADER) {
                doReOpen();
            } else if (msg.what == MSG_CHECKCARDALIIVE) {
                Log.w(TAG, "handleMessage: MSG_CHECKCARDALIIVE");
                time = System.currentTimeMillis();
            }
        }
    }


    class ReadNFCThread extends Thread {
        private String id;
        private String AreaNow;

        private int code = -1;
        public ReadNFCThread(String id){
            this.id = id;
        }

        public void writeAreaNow(int code,String AreaNow){
            this.AreaNow = AreaNow;
            this.code = code;

            synchronized (mReadNFCThread){
                mReadNFCThread.notify();
            }
        }
        @Override
        public void run() {
            try {
                if(id == null){
                    // TODO: 2017-10-19 读取错误
                    synchronized (mCheckCard) {
                        mCheckCard.notify();
                    }
                    mHandler.sendEmptyMessage(MSG_READNFCFAIL);
                    return;
                }else{
                    NFCToolRead nfcToolRead = new NFCToolRead(reader);
                    CardInfo cardInfo = nfcToolRead.readCard(id);
                    if(cardInfo != null){
                        Message msg = mHandler.obtainMessage(MSG_READNFCOK,cardInfo);
                        mHandler.sendMessage(msg);
                    }else{
                        synchronized (mCheckCard) {
                            mCheckCard.notify();
                        }
                        mHandler.sendEmptyMessage(MSG_READNFCFAIL);
                        return;
                    }
                    synchronized (mReadNFCThread) {
                        wait();
                    }

                    boolean result = true;
                    Log.i(TAG, "run:kxkxkx AreaNow = " + AreaNow);
                    Log.i(TAG, "run:kxkxkx (AreaNow != null) = " + (AreaNow != null));
                    Log.i(TAG, "run:kxkxkx (!AreaNow.equals(\"\")) = " + (!AreaNow.equals("")));
                    Log.i(TAG, "run:kxkxkx (AreaNow != null && !AreaNow.equals(\"\")) = " + (AreaNow != null && !AreaNow.equals("")));

                    if(AreaNow != null && !AreaNow.equals("")){
                        result = nfcToolRead.writeAreaNow(AreaNow);

                        if(result){
                            Message msg = mHandler.obtainMessage(MSG_WRITE_AREANOW_OK,cardInfo);
                            mHandler.sendMessage(msg);
                        }else{
                            mHandler.sendEmptyMessage(MSG_WRITE_AREANOW_FAIL);
                        }
                    }else{
                        Log.w(TAG, "run: code = " + code);
                        if(code == Constants.CHECK_EMPLOYEECARD_OK || code == Constants.CHECK_EMPLOYEECARD_FAIL){
                            Message msg = mHandler.obtainMessage(MSG_CHECK_EMPLOYEECARD, code);
                            mHandler.sendMessage(msg);
                        } else if(code > 0){
                            Message msg = mHandler.obtainMessage(MSG_DENIED, code);
                            mHandler.sendMessage(msg);
                        }
                    }
                    Log.w(TAG, "run: mCheckCard.isAlive = " + mCheckCard.isAlive());
                    sleep(10);
                    synchronized (mCheckCard) {
                        mCheckCard.notify();
                    }
                    Log.w(TAG, "run: mCheckCard.notify()");

                }

            } catch (Exception e) {
                if (mCheckCard != null) {
                    synchronized (mCheckCard) {
                        mCheckCard.notify();
                    }
                }
                mHandler.sendEmptyMessage(MSG_READNFCFAIL);
                Log.w(TAG, "run: ReadNFCThread exception");
                clearCurId();
                e.printStackTrace();

            }
        }
    }


    public interface ReadNFCInterface{
        public void onGetID(String CardID);
        public void onGetCardInfo(CardInfo cardInfo);
        public void onReadCardFail();
        public void onWriteAreaFail();
        public void onWriteAreaOk(boolean isAllow,CardInfo cardInfo);
        public void onDenied(int code);
    }


}
