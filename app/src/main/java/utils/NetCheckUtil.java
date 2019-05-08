package utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by Lxh on 2017/8/7.
 */

public class NetCheckUtil {
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    /*
   * 获取网络类型,已结网络是否可用
   */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public static int getNetworkType(Activity activity){
        int netType = 0 ;
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType == connectivityManager.TYPE_MOBILE){
            String extraInfo = networkInfo.getExtraInfo();
            if(!TextUtils.isEmpty(extraInfo)){
                if(extraInfo.toLowerCase().equals("cmnet")){
                    netType = NETTYPE_CMNET;
                }else {
                    netType = NETTYPE_CMWAP;
                }
            }
        }else if (nType == ConnectivityManager.TYPE_WIFI){
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}
