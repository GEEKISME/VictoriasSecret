package service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.SharedPreferencesUtils;

/**
 * Created by Lxh on 2017/11/7.
 */

public class AppDownloadService extends Service {

    public static final String VITORIASECRET_APK = "VictoriaSecret.apk";
    private OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String qppurl = Constants.APP_FILE;
        final String newestVersion = intent.getStringExtra("newestVersion");
        Request apkdown = new Request.Builder().url(qppurl).build();
        client.newCall(apkdown).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    InputStream is = response.body().byteStream();
                    File dir = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecret/");
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    File apkfile = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecret/", VITORIASECRET_APK);
                    if(!apkfile.exists()){
                        apkfile.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(apkfile);
                    byte[] buffer = new byte[1024];
                    int num = -1;
                    while ((num = is.read(buffer))!=-1){
                        fos.write(buffer,0,num);
                        fos.flush();
                    }
                    fos.close();
                    is.close();
                    Log.i(Constants.TAG,"apk 成功下载");
                    SharedPreferencesUtils.saveString(getApplicationContext(),"newestVersion",newestVersion);
                    SharedPreferencesUtils.saveString(getApplicationContext(),"apkstatus","1");
                    Log.i(Constants.TAG,"apkstatus已经被置为1");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
