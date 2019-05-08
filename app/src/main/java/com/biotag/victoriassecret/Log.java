package com.biotag.victoriassecret;

/**
 * Created by Lxh on 2017/7/11.
 */

public class Log {
    public static boolean isLog = true;
    public static void i(String TAG , String msg){
        if(isLog){
            android.util.Log.i(TAG, "==========================================================");

            android.util.Log.i(TAG, msg);
        }
    }
    public static void i(String TAG , String msg , Throwable throwable){
        if(isLog){
            android.util.Log.i(TAG, "==========================================================");
            android.util.Log.i(TAG, msg, throwable);
        }
    }

    public static void v(String TAG , String msg){
        if(isLog){
            android.util.Log.v(TAG, "==========================================================");
            android.util.Log.v(TAG, msg);
        }
    }
    public static void v(String TAG , String msg , Throwable throwable){
        if(isLog){
            android.util.Log.v(TAG, "==========================================================");
            android.util.Log.v(TAG, msg, throwable);
        }
    }

    public static void d(String TAG , String msg){
        if(isLog){
            android.util.Log.d(TAG, "==========================================================");
            android.util.Log.d(TAG, msg);
        }
    }
    public static void d(String TAG , String msg , Throwable throwable){
        if(isLog){
            android.util.Log.d(TAG, "==========================================================");
            android.util.Log.d(TAG, msg, throwable);
        }
    }

    public static void e(String TAG , String msg){
        if(isLog){
            android.util.Log.e(TAG, "==========================================================");
            android.util.Log.e(TAG, msg);
        }
    }
    public static void e(String TAG , String msg , Throwable throwable){
        if(isLog){
            android.util.Log.e(TAG, "==========================================================");
            android.util.Log.e(TAG, msg, throwable);
        }
    }
}
