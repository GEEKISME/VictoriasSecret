package com.biotag.victoriassecret;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.io.File;

import service.AppDownloadService;
import service.HeadimgDownloadService;
import utils.FileUtils;
import utils.NetCheckUtil;
import utils.SharedPreferencesUtils;

import static service.AppDownloadService.VITORIASECRET_APK;

public class SettingDistrictActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_ok;
    private long mExittime;
    private Context context ;
    private FlexRadioGroup flexrg;
    private boolean mProtectFromCheckedChange = false;
    private String[] districts = {"H 区","M 区","B 区","F 区","S 区","C 区","T 区","A 区",};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting_district);
        context = this;
        //开机就启动后台服务去拉取服务器上的照片
        if(NetCheckUtil.isNetworkConnected(SettingDistrictActivity.this)){
            Intent intent = new Intent(this, HeadimgDownloadService.class);
            startService(intent);
        }else {
            Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
        }
        initView();
//        String areano = getIntent().getStringExtra("areano");
//        String[] areaarray = areano.split(" ");
        createRadioButton(districts,flexrg);
        Log.i(Constants.TAG,"oncreate");
    }

    private void createRadioButton(String[] districts, final FlexRadioGroup flexrg) {
        float margin = DensityUtils.dp2px(this,10);
        float width = DensityUtils.getWidth(this);
        for (String district:districts) {
            RadioButton rb = (RadioButton) LayoutInflater.from(this).inflate(R.layout.item_label,null);
            rb.setText(district);
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams((int) (width - margin) / 3, ViewGroup.LayoutParams.WRAP_CONTENT);
            rb.setLayoutParams(lp);
            flexrg.addView(rb);
            flexrg.setOnCheckedChangeListener(new FlexRadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(int checkedId) {
                    mProtectFromCheckedChange = true;
                }
            });
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mProtectFromCheckedChange&&((RadioButton)v).isChecked()){
                        flexrg.clearCheck();
                    }else mProtectFromCheckedChange = false;
                }
            });
        }
    }

    private void initView() {

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        flexrg = (FlexRadioGroup)findViewById(R.id.flexrg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                RadioButton rb = (RadioButton)findViewById(flexrg.getCheckedRadioButtonId());
                if(rb!=null){
                    String dischosed = rb.getText().toString().trim();
                    SharedPreferencesUtils.saveString(context,"dischosed",dischosed);
                    Toast.makeText(this, "该设备被设置的区域是"+ rb.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingDistrictActivity.this,GuestInfoActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(context, "您还没有选择设置区域", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(Constants.TAG,"onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG,"onresume");

        String apkstatus = SharedPreferencesUtils.getString(context, "apkstatus", "0");
        if (apkstatus.equals("1")) {
            Intent intent = new Intent(context, AppDownloadService.class);
            stopService(intent);
            AlertDialog ab = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update tip");
            builder.setMessage("The new version has been downloaded in wifi state,restart the app now ?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferencesUtils.saveString(context, "apkstatus", "0");
                    Intent intent1 = new Intent();
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.setAction(Intent.ACTION_VIEW);
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent1.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    File apkfile = new File(Environment.getExternalStorageDirectory() + "/VictoriaSecret/", VITORIASECRET_APK);
                    intent1.setDataAndType(FileUtils.getUriForFile(context, apkfile), "application/vnd.android.package-archive");
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.TAG,"onpause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(Constants.TAG,"onstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.TAG,"ondestroy");
//        Intent intent = new Intent(this, HeadimgDownloadService.class);
//        stopService(intent);
//        Log.i(Constants.TAG, "service  停止了");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - mExittime > 2000) {
                Toast.makeText(context, "再按一次退出App", Toast.LENGTH_SHORT).show();
                mExittime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
