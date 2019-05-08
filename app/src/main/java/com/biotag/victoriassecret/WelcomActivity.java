package com.biotag.victoriassecret;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

import NFC.CardInfo;
import NFC.NFCToolManager;
import bean.AppVersionBean;
import service.AppDownloadService;
import utils.FileUtil;
import utils.NetCheckUtil;
import utils.OkhttpPlusUtil;
import utils.SharedPreferencesUtils;
import utils.ThreadManager;
import view.RadiationView;

import static service.AppDownloadService.VITORIASECRET_APK;

public class WelcomActivity extends AppCompatActivity {

    private ImageView VS;
    private RelativeLayout rl_staffinfo;
    private TextView tv_name, tv_staffno, tv_tips;
    private FrameLayout fl_anim;
    private Button btn_login;
    private ImageView iv_rotate;
    private RadiationView rv;
    private NFCToolManager mNFCTool;
    private NFCToolManager.ReadNFCInterface mInterface = new NFCToolManager.ReadNFCInterface() {
        @Override
        public void onGetID(String CardID) {

        }

        @Override
        public void onWriteAreaOk(boolean isAllow,CardInfo cardInfo) {
            Log.i(Constants.TAG, "onWriteAreaOk: kxkxkx isAllow = " + isAllow);

        }

        @Override
        public void onWriteAreaFail() {
            Log.i(Constants.TAG, "onWriteAreaFail: kxkxkx");

        }

        @Override
        public void onReadCardFail() {
            Log.i(Constants.TAG, "onReadCardFail: kxkxkx");

            Toast.makeText(WelcomActivity.this, "读取证件信息失败，再试一次吧 ！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetCardInfo(CardInfo cardInfo) {
            String newAreaNow = "";
            String AreaNo = cardInfo.getAreaNo();
            String settingAreaNo = SharedPreferencesUtils.getString(WelcomActivity.this, "dischosed", "");
            String AreaNow = cardInfo.getAreaNow();
            Log.i(Constants.TAG, "onGetCardInfo:kxkxkx settingAreaNo = " + settingAreaNo);

            if(cardInfo != null && cardInfo.getCardType() == Constants.CHIP_EMPLOYEECARD){
                mNFCTool.writeAreaNow(Constants.CHECK_EMPLOYEECARD_OK,newAreaNow);
                showInfo(cardInfo);
            }else{
                mNFCTool.writeAreaNow(Constants.CHECK_EMPLOYEECARD_FAIL,newAreaNow);
                Toast.makeText(WelcomActivity.this, "非工作证", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onDenied(int code) {
            Log.i(Constants.TAG, "onDenied: code = " + code);
            //            showDeniedMessage(code);
        }
    };
    private String appNo, appDownloadUrl, newestVersion,areano;
    public static final int NO_NEED = 10;
    public static final int NEED = 11;
    private Context context = this;

    private void showInfo(CardInfo cardInfo) {
        fl_anim.setVisibility(View.INVISIBLE);
        tv_tips.setVisibility(View.INVISIBLE);
        rl_staffinfo.setVisibility(View.VISIBLE);
        tv_name.setText(cardInfo.getStaffName());
        tv_staffno.setText(cardInfo.getIdCard());
        areano = cardInfo.getAreaNo();
    }

    static class ApkDownloadHandler extends Handler {
        private WeakReference<WelcomActivity> welcomActivityWeakReference;

        public ApkDownloadHandler(WelcomActivity welcomActivity) {
            welcomActivityWeakReference = new WeakReference<WelcomActivity>(welcomActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomActivity welcomActivity = welcomActivityWeakReference.get();
            if (welcomActivity != null) {
                switch (msg.what) {
                    case NO_NEED:
                        break;
                    case NEED:
                        if (NetCheckUtil.isNetworkConnected(welcomActivity)) {
                            Intent intent = new Intent(welcomActivity, AppDownloadService.class);
                            intent.putExtra("newestVersion", welcomActivity.newestVersion);
                            welcomActivity.startService(intent);
                            Log.i(Constants.TAG, "service 启动了");
                        }
                        break;
                }
            }
        }
    }

    private ApkDownloadHandler handler = new ApkDownloadHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcom);
        Log.i(Constants.TAG, "来到welcome");
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            appNo = pi.versionName;
            Log.i(Constants.TAG, "加载welcome 拿到的appno is" + appNo);
        } catch (Exception e) {
            e.printStackTrace();

        }
//        appNo = SharedPreferencesUtils.getString(this, "newestVersion", "6.0");
        Log.i(Constants.TAG, "加载welcome 拿到的appno is" + appNo);
        appDownloadUrl = SharedPreferencesUtils.getString(this, "appdownloadurl", "");
        //        Log.i(Constants.TAG,"appDownloadUrl is "+appDownloadUrl);
        Log.i(Constants.TAG, "开始检测新版本");
        initView();
//        mNFCTool = new NFCToolManager(this);
//        mNFCTool.setReadNFCInterface(mInterface);
//        mNFCTool.openReader();
        checkNewAppVersion();

    }



    private void checkNewAppVersion() {
        ThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String requl = Constants.APP_VERSION;
                AppVersionBean avb = OkhttpPlusUtil.getInstance().get(requl, AppVersionBean.class);
                if (avb != null && avb.isIsSuccess()) {
                    if (!avb.getValues().equals(appNo)) {
                        newestVersion = avb.getValues();
                        handler.sendEmptyMessage(NEED);
                    } else {
                        handler.sendEmptyMessage(NO_NEED);
                    }
                }
            }
        });
    }

    private void initView() {
        VS = (ImageView) findViewById(R.id.VS);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomActivity.this,SettingDistrictActivity.class);
                intent.putExtra("areano",areano);
                Log.i("tms","areano is "+areano);
                startActivity(intent);
                finish();
            }
        });
        rl_staffinfo = (RelativeLayout) findViewById(R.id.rl_staffinfo);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_staffno = (TextView) findViewById(R.id.tv_staffno);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        fl_anim = (FrameLayout) findViewById(R.id.fl_anim);
        iv_rotate = (ImageView) findViewById(R.id.iv_rotate);
        rv = (RadiationView) findViewById(R.id.rv);
        rv.setMinRadius(70);
        rv.startRadiate();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate_circle_anim);
        iv_rotate.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,AppDownloadService.class);
        stopService(intent);
//        mNFCTool.destory();
//        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String apkstatus = SharedPreferencesUtils.getString(context,"apkstatus","0");
        if(apkstatus.equals("1")){
            Intent intent = new Intent(context,AppDownloadService.class);
            stopService(intent);
            AlertDialog ab = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update tip");
            builder.setMessage("The new version has been downloaded in wifi state,restart the app now ? ");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferencesUtils.saveString(context,"apkstatus","0");
                    Intent intent1 = new Intent();
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setAction(Intent.ACTION_VIEW);
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent1.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    File apkfile = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecret/",VITORIASECRET_APK);
                    intent1.setDataAndType(FileUtil.getUriForFile(context,apkfile),"application/vnd.android.package-archive");
                    startActivity(intent1);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            ab = builder.create();
            ab.show();
        }
    }
}
