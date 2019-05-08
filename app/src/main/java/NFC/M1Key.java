package NFC;

import com.biotag.victoriassecret.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-18.
 */

public class M1Key {

    public static Map<String, String> CalculateKey(String id){
        String hexCardSnr = id;
        if (hexCardSnr.length() < 8)
        {
            hexCardSnr = hexCardSnr + String.format("%0" + 8 + "d",0);
        }
//        Log.i(TAG, "CalculateKey: hexCardSnr = " + hexCardSnr);
        System.out.println("hexCardSnr = " + hexCardSnr);
        String tmpHex = hexCardSnr.substring(6, 8) + hexCardSnr.substring(4, 6) + hexCardSnr.substring(2, 4) + hexCardSnr.substring(0, 2);
        hexCardSnr = tmpHex;

        System.out.println("xxxx = " + Integer.toHexString(Integer.valueOf("FF",16) - Integer.valueOf(hexCardSnr.substring(0, 2),16)));
        tmpHex = AddLenHex(Integer.toHexString(Integer.valueOf("FF",16) - Integer.valueOf(hexCardSnr.substring(0, 2),16))) +
                AddLenHex(Integer.toHexString(Integer.valueOf("FF",16) - Integer.valueOf(hexCardSnr.substring(2, 4),16))) +
                AddLenHex(Integer.toHexString(Integer.valueOf("FF",16) - Integer.valueOf(hexCardSnr.substring(4, 6),16))) +
                AddLenHex(Integer.toHexString(Integer.valueOf("FF",16) - Integer.valueOf(hexCardSnr.substring(6, 8),16)));
        System.out.println("tmpHex = " + tmpHex);
        hexCardSnr = (hexCardSnr + tmpHex).toUpperCase();
        System.out.println("hexCardSnr = " + hexCardSnr);

        byte[] hexCardSnr1 = Utils.hexStringToBytes(hexCardSnr);

        Map<String ,String> map = new HashMap<>();
        boolean cardtype = false;
        for (int i = 0;i < 2;i ++){
            if(i == 0) cardtype = false;
            else if(i == 1) cardtype = true;

            byte[] bytes = TripleDes.CreateEncryptByte(hexCardSnr1, cardtype);
            if(bytes == null){
                return null;
            }
            String KeySys = Utils.bytesToHexString(bytes).toUpperCase().substring(0,12);

            System.out.println("bytes = " + KeySys);

            byte[] KeySysByte = Utils.hexStringToBytes(KeySys);
            if (!cardtype) {

                map.put(Constants.KEYA, KeySys);
            } else {
                map.put(Constants.KEYB, KeySys);
            }
        }

        return map;



    }

    private static String AddLenHex(String s) {
        String returnval = "";
        if (s.length() == 1)
        {
            returnval = '0' + s;
        }
        else
        {
            returnval = s.substring(0, 2);
        }
        return returnval;
    }
}
