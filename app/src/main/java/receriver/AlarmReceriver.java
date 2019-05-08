package receriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;

import service.HeadimgDownloadService;

/**
 * Created by Lxh on 2017/10/22.
 */

public class AlarmReceriver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG,"AlarmReceiver 收到消息了");
        Intent intent1 = new Intent(context, HeadimgDownloadService.class);
        context.startService(intent1);
        Log.i(Constants.TAG,"AlarmReceiver 再次启动service");
    }
}
