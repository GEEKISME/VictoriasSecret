package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import bean.AllUrlBean;
import bean.GetAllNumBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import receriver.AlarmReceriver;
import utils.GetJson;
import utils.OkhttpPlusUtil;
import utils.ThreadManager;

/**
 * Created by Lxh on 2017/10/22.
 */

public class HeadimgDownloadService extends Service{

    private String  lastId = "F0D0F3E9-515A-4F7A-B57F-7B1E84D547E0" ;
    private boolean isFirst;
//    private String  lastId ;
    private GetAllNumBean getAllNumBean;
    private String requesturl;
    private File backupFile;
    private OkHttpClient client = new OkHttpClient();


    @Override
    public void onCreate() {
        super.onCreate();
        isFirst = true;
        backupFile = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecretBackUp/");
        if(!backupFile.exists()){
            backupFile.mkdirs();
        }

        Log.i(Constants.TAG,"service 的 oncreat 方法");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Constants.TAG,"service 的 onstartcommand 方法");
        requesturl = Constants.URL_GETALLHEADNUM;
        if(isFirst){
            Log.i(Constants.TAG,"service 第一次启动了");
            requesturl = GetJson.replace(requesturl,"{id}",lastId);
        }else {
            Log.i(Constants.TAG,"不是第一次启动");
            requesturl = GetJson.replace(requesturl,"{id}",lastId);
        }
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getAllNumBean =  OkhttpPlusUtil.getInstance().get(requesturl,GetAllNumBean.class);
                if(getAllNumBean!=null&&getAllNumBean.isIsSuccess()&&getAllNumBean.getValues()!=null){
                    String values = getAllNumBean.getValues();
                    Log.i(Constants.TAG,"原来的values is"+values);
                    values =  new StringBuilder().append("{").append("\"Values\":").append(values).append("}").toString();
                    Log.i(Constants.TAG,"拼接之后的values is"+values);

                    AllUrlBean aub = new Gson().fromJson(values,AllUrlBean.class);

                    final ArrayList<AllUrlBean.ValuesBean> allurllist =  aub.getValues();
                    int urlnum = allurllist.size();
                    AllUrlBean.ValuesBean vb = allurllist.get(urlnum-1);
                    lastId = vb.getID().trim();
                    Log.i(Constants.TAG,"lastid is "+lastId+"   urlnum 的数目是"+urlnum);
                    for (int i = 0; i <urlnum ; i++) {
                        final AllUrlBean.ValuesBean avb = allurllist.get(i);
                        if(avb.getPhotoUrl()==null||!avb.getPhotoUrl().endsWith("jpg")){
                            continue;
                        }

//                        String tempurl = avb.getPhotoUrl().replaceAll(" ","");
                        Log.i(Constants.TAG,"getPhotoUrl = " + avb.getPhotoUrl());
                        String photourl = avb.getPhotoUrl().replaceAll("\\\\","/").replaceAll(" ","");
                        Log.i(Constants.TAG,"photourl = " + photourl);
                        String requrl = Constants.MAINHOST+"uploadimage/"+photourl;
                        Log.i(Constants.TAG,"requrl is "+requrl);
                        Request rest = new Request.Builder().url(requrl).build();
                        client.newCall(rest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                try {
                                    Log.i(Constants.TAG,"开始下载了");
                                    InputStream is = response.body().byteStream();
                                    String photourl = avb.getPhotoUrl().replaceAll("\\\\","/").replaceAll(" ","");
                                    //                                String photourls = photourl[1].replaceAll(" ","");
                                    Log.i("tnd","urls is "+photourl);

                                    String filePath = backupFile + File.separator +  photourl;

                                    String dic = photourl.split("/")[0];
                                    String dicPath = backupFile + File.separator + dic;
                                    File dicFile = new File(dicPath);
                                    if(!dicFile.exists()){
                                        dicFile.mkdir();
                                    }

                                    Log.i(Constants.TAG, "filePath = " + filePath);
                                    File headimgfile = new File(filePath);
                                    if(!headimgfile.exists()){
                                        headimgfile.createNewFile();

                                        FileOutputStream fos = new FileOutputStream(headimgfile);
                                        byte[] b = new byte[1024];
                                        int num = -1;
                                        while ((num = is.read(b))!=-1){
                                            fos.write(b,0,num);
                                            fos.flush();
                                        }
                                        fos.close();
                                        is.close();
                                        Log.i(Constants.TAG,"本张图片下载结束了");
                                    }else{
                                        Log.i(Constants.TAG,"图片已存在");
                                    }

                                } catch (Exception e) {
                                    Log.i(Constants.TAG,"出错了");
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }else {
                    Log.i(Constants.TAG,"values 为空了");
                }
            }
        });
        isFirst = false;
        Log.i(Constants.TAG,"图片下载完毕后 将isfirst 置为 false");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int twomin = 2*60*1000;//2 min
        Log.i(Constants.TAG,"Alarm 设置了2 min");
        long triggerAtTime = SystemClock.elapsedRealtime()+twomin;
        Intent intent1 = new Intent(this, AlarmReceriver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent1,0);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        Log.i(Constants.TAG,"set 已过");
        return super.onStartCommand(intent, flags, startId);
    }
}
