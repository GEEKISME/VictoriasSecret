package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.NewCommmonMethod;
import utils.SharedPreferencesUtils;
import utils.ThreadManager;

/**
 * Created by Lxh on 2017/10/19.
 */

public class UploadHeadImgService extends Service {

    private int Imgnums = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Log.i(Constants.TAG,"进入到服务的子线程了");
                String staffid = intent.getStringExtra("staffid");
                String currentday = intent.getStringExtra("currentday");
                File uploadFile = new File("sdcard/VictoriaSecret/"+currentday+"/"+staffid+".jpg");
                Map<String,String> map = new HashMap<String, String>();
                map.put("chipcode",staffid+".jpg");
                try {
                    NewCommmonMethod.getInstance().uploadForm(map,staffid,uploadFile,null,Constants.URL_POSTPHOTO);
                    Imgnums++;
                    SharedPreferencesUtils.saveInt(UploadHeadImgService.this,"imgnums",Imgnums);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
