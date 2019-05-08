package receriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;

import service.UpLoadingInandOutInfoService;

/**
 * Created by Lxh on 2017/11/14.
 */

public class InandOutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG,"InandoutReceiver 收到消息了");
        Intent intent1 = new Intent(context, UpLoadingInandOutInfoService.class);
        context.startService(intent1);
        Log.i(Constants.TAG,"InandoutReceiver 再次启动service");
    }
}
