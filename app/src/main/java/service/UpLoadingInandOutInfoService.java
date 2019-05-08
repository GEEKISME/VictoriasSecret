package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.biotag.victoriassecret.Constants;
import com.biotag.victoriassecret.Log;
import com.biotag.victoriassecret.MydatabaseHelper;

import org.json.JSONObject;

import bean.AddInfoSucBean;
import receriver.InandOutReceiver;
import utils.OkhttpPlusUtil;
import utils.ThreadManager;

/**
 * Created by Lxh on 2017/11/14.
 */

public class UpLoadingInandOutInfoService extends Service {
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("panpan","uploadinandoutservice 启动了");
        db = MydatabaseHelper.getInstance(this).getReadableDatabase();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Cursor cursor = db.query("Inoutinfo",new String[]{"StaffID","ChipCode","AreaNo","Action_Type","ActionTime"},null,null,null,null,null);
        if(cursor.moveToFirst()){
            Log.i("panpan","开始读取数据库条目");
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    do {
                        JSONObject obj = new JSONObject();
                        try {
                            String staffid = cursor.getString(cursor.getColumnIndex("StaffID"));
                            obj.put("StaffID",staffid);
                            Log.i(Constants.TAG,"staffid is "+ staffid);
                            obj.put("ChipCode",cursor.getString(cursor.getColumnIndex("ChipCode")));
                            obj.put("AreaNo",cursor.getString(cursor.getColumnIndex("AreaNo")));
                            obj.put("Action_Type",cursor.getString(cursor.getColumnIndex("Action_Type")));
                            obj.put("ActionTime",cursor.getString(cursor.getColumnIndex("ActionTime")));
                            String json = String.valueOf(obj);
                            Log.i("panpan","json is  "+ json);
                            AddInfoSucBean asb =OkhttpPlusUtil.getInstance().post(Constants.URL_POSTINOUT_EMPLOYCARD,json, AddInfoSucBean.class);
                            if(asb!=null&&asb.isIsSuccess()){
                                Log.i("panpan","信息上传成功");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }while (cursor.moveToNext());
                    cursor.close();
                    Log.i(Constants.TAG,"cursor 已经关闭 ");
                }
            });
        }else {
            Log.i("tms","数据库条目为空");
        }


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int fivemin = 5*60*1000;//5 min
        Log.i(Constants.TAG,"Alarm 设置5 min");
        long triggerAtTime = SystemClock.elapsedRealtime()+fivemin;
        Intent intent1 = new Intent(this, InandOutReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent1,0);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        Log.i(Constants.TAG,"set 已过");
        return super.onStartCommand(intent, flags, startId);

    }
}
