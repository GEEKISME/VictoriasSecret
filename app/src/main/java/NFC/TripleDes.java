package NFC;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * Created by Administrator on 2017-10-16.
 */

public class TripleDes {

    private static byte[] pcdIV = { 0x1D, 0x61, 0x1D, 0x75, (byte)0xF5, 0x38, 0x6C, 0x6F };
    private static byte[] icdIV = { 0x34, 0x29, 0x62, (byte)0x9B, (byte)0xA1, 0x12, 0x3D, (byte)0xA2 };
    private static byte[] pcdKeys = { 0x1D, 0x61, 0x1D, 0x75, (byte)0xF5, 0x38, 0x6C, 0x6F, 0x0D, (byte)0x85, 0x78, 0x7B, (byte)0x87, 0x7B, (byte)0xA7, (byte)0x9D
            ,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    private static byte[] icdKeys = { 0x34, 0x29, 0x62, (byte)0x9B, (byte)0xA1, 0x12, 0x3D, (byte)0xA2, 0x56, 0x29, 0x42, 0x13, 0x4B, (byte)0x82, (byte)0x97, 0x46
            ,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};


    public static byte[] CreateEncryptByte(byte[] input,boolean type){
        System.out.println("input = " + Utils.bytesToHexString(input));
        try{
            DESedeKeySpec keySpec = null;
            AlgorithmParameterSpec iv = null;
            SecretKeyFactory keyFactory = null;
            Key key = null;
            if(type){
                keySpec = new DESedeKeySpec(pcdKeys);// 设置密钥参数

//                iv = new IvParameterSpec(pcdIV);// 设置向量
                keyFactory = SecretKeyFactory.getInstance("DESede");// 获得密钥工厂
                key = keyFactory.generateSecret(keySpec);// 得到密钥对象

            }else{
                keySpec = new DESedeKeySpec(icdKeys);// 设置密钥参数
//                iv = new IvParameterSpec(icdIV);// 设置向量
                keyFactory = SecretKeyFactory.getInstance("DESede");// 获得密钥工厂
                key = keyFactory.generateSecret(keySpec);// 得到密钥对象
            }

            Cipher enCipher = Cipher.getInstance("DESede/ECB/NoPadding");
            enCipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] pasByte = enCipher.doFinal(input);

            return pasByte;
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    public static final String KEY_ALGORITHM = "DESede";


}
