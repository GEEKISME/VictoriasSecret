package com.biotag.victoriassecret;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import NFC.CardInfo;
import NFC.NFCToolManager;
import NFC.Utils;
import bean.IsCancelBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import service.HeadimgDownloadService;
import service.UpLoadingInandOutInfoService;
import service.UploadHeadImgService;
import utils.DataBaseUtils;
import utils.NetCheckUtil;
import utils.SharedPreferencesUtils;
import utils.ThreadManager;

public class GuestInfoActivity extends AppCompatActivity {
    //====+++++++++++++++++++++++++++++++++++++++下面是Opencv需要的东西
    private JavaCameraView openCvCameraView;
    private File headImg;
    private String staffphotourl;
    private Bitmap bitmap;

    //Cascade classifier class for object detection.用来进行人脸识别的关键类
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(GuestInfoActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    openCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    private void initializeOpenCVDependencies() {
        // Copy the resource into a temp file so OpenCV can load it,加载训练好的模型文件
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
//            File mCascadeFile = new File(cascadeDir, "lbpcascade_dog.xml");
            FileOutputStream fos = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("tms", "error loading cascade");
        }
        //// And we are ready to go
//        openCvCameraView.enableView();
    }


    //====++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private ProgressBar pb ;
    private String staffId;
    private String imageUrl;
    private String currentday,currenttime;
    public static final String HEAD_IMG = "face.jpg";
    public static final String TAG = "Tms";
    private ImageView iv_headpic;
    private TextView tv_authority, tv_vstitle1, tv_vstitle2, tv_vstitle3,
            tv_vstitle4, tv_approved, tv_denied;
    private TextView tv_name, tv_brand, tv_id, tv_simplify,tv_simplify2;
    private LinearLayout rl_backpic;
    private RelativeLayout  rl_title, rl_camera;
    private RelativeLayout rl_info;
    private RelativeLayout rl_wrapcamera;
    private static final  int GETSTAFFPHOTO = 1;
    private static final  int GETSTAFFPHOTOUSELESS = 2;
    // 相机对象
//    private Camera ca = null;
    // Bitmap对象
    private Bitmap mb = null;
    //    private Camera.AutoFocusCallback myAutoFocusCallback = null;
    private int cameraCount = 0;
    private int cindex = -1, qz;
    //    private SurfaceView surfaceView;
    private Context context = this;
    //    private FaceView faceView;
//    private GoogleFaceDetect googleFaceDetect = null;
//    private MainHandler mMainHandler = null;
    public static final int REQUEST_PERMISSION_CAMERA = 20;
    private long mExittime;
    private long starttime,finishtime;// 无论如何超过3s
    private CardInfo cardInfos;


    static class GuestinfoHandler extends Handler{
        private WeakReference<GuestInfoActivity>guestInfoActivityWeakReference;
        public GuestinfoHandler(GuestInfoActivity guestInfoActivity){
            guestInfoActivityWeakReference = new WeakReference<GuestInfoActivity>(guestInfoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GuestInfoActivity guestInfoActivity = guestInfoActivityWeakReference.get();
            if(guestInfoActivity!=null){
                switch (msg.what){
                    case GETSTAFFPHOTO:
                        Toast.makeText(guestInfoActivity, "照片拍摄完成", Toast.LENGTH_SHORT).show();
                        if(guestInfoActivity.tv_approved.getText().equals("APPROVED")){
                            guestInfoActivity.pb.setVisibility(View.INVISIBLE);
                            guestInfoActivity.tv_approved.setVisibility(View.VISIBLE);

                        }
                        Bundle bundle = msg.getData();
                        String staffid = bundle.getString("staffid","");
                        if(NetCheckUtil.isNetworkConnected(guestInfoActivity)){
                            Log.i(Constants.TAG,"网络连接状态");
                            Intent intent = new Intent(guestInfoActivity,UploadHeadImgService.class);
                            intent.putExtra("staffid",staffid);
                            intent.putExtra("currentday",guestInfoActivity.currentday);
                            Log.i(Constants.TAG,"将要启动服务了");
                            guestInfoActivity.startService(intent);
                        }
                        break;
                    case GETSTAFFPHOTOUSELESS:  //从扫卡之后的时间算起，只要是approved 状态,5s内无论是否拍到照片都要把approved显示出来
                        if(guestInfoActivity.tv_approved.getText().equals("APPROVED")){
                            guestInfoActivity.pb.setVisibility(View.INVISIBLE);
                            guestInfoActivity.tv_approved.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 90:
                        String imgUrl = guestInfoActivity.cardInfos.getImageUrl();
                        String tempurl = imgUrl.replaceAll("\\\\","/");
                        Log.i("nsdc",tempurl);
//                        String tempdir = imgUrl.split("")
                        File file = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecretBackUp/"+tempurl);
//                        File headImgLocal = new File(guestInfoActivity.headImgBackUp,tempurl);
                        Log.i("nsdc","file path = " + file.getAbsolutePath());
                        //        File headImgLocal = new File(headImgBackUp, cardInfo.getID().trim() + ".jpg");
                        if (!file.isDirectory() && file.exists()) {
                            Log.i("nsdc","图片存在");

                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            guestInfoActivity.iv_headpic.setImageBitmap(bitmap);
                        }else{
                            Log.i("nsdc","图片不存在");
                            guestInfoActivity.iv_headpic.setImageResource(R.mipmap.userss);
                        }
                        break;
                    case 900:
                        guestInfoActivity.iv_headpic.setImageBitmap(guestInfoActivity.bitmap);
                        Log.i("nsdc","来自网络");
                        break;
                    case 901:
                        guestInfoActivity.tv_approved.setText("APPROVED");
                        guestInfoActivity.tv_approved.setTextColor(guestInfoActivity.getResources().getColor(R.color.deepgreen));
                        guestInfoActivity.tv_approved.setVisibility(View.VISIBLE);
                        break;
                    case 91:
                        int tictype = guestInfoActivity.cardInfos.getCardType();
                        if(tictype==Constants.CHIP_TICKET){
                            guestInfoActivity.tv_approved.setText("该门票已被注销");
                        }else if(tictype==Constants.CHIP_EMPLOYEECARD){
                            guestInfoActivity.tv_approved.setText("该工作证已被注销");
                        }
                        guestInfoActivity.tv_approved.setTextColor(Color.RED);
                        guestInfoActivity.tv_approved.setVisibility(View.VISIBLE);
                        guestInfoActivity.pb.setVisibility(View.INVISIBLE);

                        break;
                }
            }
        }
    }
    private GuestinfoHandler handler = new GuestinfoHandler(GuestInfoActivity.this);
    private NFCToolManager mNFCTool;
    private File headImgBackUp = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecretBackUp/") ;
    private MydatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guestinfo);
        creatFile();
        initAuthority();
        initView();
        initdbfile();
        if (ContextCompat.checkSelfPermission(GuestInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GuestInfoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            return;
        }
        openCvCameraView = new JavaCameraView(this, -1);
        openCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
                absoluteFaceSize = (int) (height * 0.2);
            }

            @Override
            public void onCameraViewStopped() {

            }

            int faceSerialCount = 0;

            @Override
            public Mat onCameraFrame(Mat inputFrame) {
                Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
                MatOfRect faces = new MatOfRect();
                if (cascadeClassifier != null) {
                    cascadeClassifier.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                }
                Rect[] facesArray = faces.toArray();
                int facecount = facesArray.length;
                if (facecount > 0) {
                    faceSerialCount++;
                } else {
                    faceSerialCount = 0;
                }

                finishtime = System.currentTimeMillis();
//                File isExistFile = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecret/"+currentday+"/"+staffId+".jpg");
                File isExistFile = new File(Environment.getExternalStorageDirectory()+"/VictoriaSecret/"+currentday+"/"+staffId+".jpg");
                Log.i("tmd","isExistFile 是否存在："+ isExistFile.exists());
                if (faceSerialCount > 6 && !tv_name.getText().toString().trim().equals("") &&!isExistFile.exists()
                        &&tv_approved.getText().toString().trim().equals("APPROVED")) {  //只有是 APPROVED状态 的才会进行拍照
                    Log.i("tmd", "大于6 次，name不为空，文件不存在，Approved，需要存储这帧数据了");
                    openCvCameraView.takephoto(staffId);
//                    openCvCameraView.takephoto("staffId");
                    faceSerialCount = -1;
                    Message message = Message.obtain();
                    message.what = GETSTAFFPHOTO;
                    Bundle bundle = new Bundle();
                    bundle.putString("staffid",staffId);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else if (finishtime>starttime&&(finishtime-starttime)/1000==1){
                    Message message = Message.obtain();
                    message.what = GETSTAFFPHOTOUSELESS;
                    handler.sendMessage(message);
                }
                for (int i = 0; i < facesArray.length; i++) {
                    Core.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
                }
                //在下面执行拍照动作
//                openCvCameraView.doTakePicture();
                return inputFrame;
            }
        });

        // And we are ready to go

        openCvCameraView.enableView();
        openCvCameraView.setCameraIndex(99);

//        initializeOpenCVDependencies();

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rl_wrapcamera.addView(openCvCameraView, lp);
//        initlistener();
//        detectCamera();
//        initholder();
//        googleFaceDetect = new GoogleFaceDetect(getApplicat
//        mMainHandler = new MainHandler();ionContext(), mMainHandler);
//        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);

        mNFCTool = new NFCToolManager(this);
        mNFCTool.setReadNFCInterface(mInterface);
        mNFCTool.openReader();

    }

    private void initdbfile() {
        DataBaseUtils.importdatabasefromassets(this);
//        dbHelper = new MydatabaseHelper(this,"InandOut.db",null,1);
        dbHelper = MydatabaseHelper.getInstance(this);
        db = dbHelper.getReadableDatabase();
        Intent intent = new Intent(this, UpLoadingInandOutInfoService.class);
        startService(intent);
    }

    private void creatFile() {
        long current = System.currentTimeMillis();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentday = sd.format(current);
        currenttime = sds.format(current);
        Log.i(Constants.TAG, "当前的日期是" + currentday);
        Log.i(Constants.TAG,"当前的精确时间是"+currenttime);

        headImg = new File(Environment.getExternalStorageDirectory() + "/VictoriaSecret/" + currentday + "/");
        if (!headImg.exists()) {
            headImg.mkdirs();
        }
    }


    private void initlayout() {
//        String tempurl = imageUrl.replaceAll("\\\\","/");
//        File headImgLocal = new File(headImgBackUp,tempurl);
//
//        Log.i(Constants.TAG,"imageurl is " + imageUrl);
//        if(headImgLocal.exists()){
//            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/VictoriaSecretBackUp/"+tempurl);
//            iv_headpic.setImageBitmap(bitmap);
//            Log.i(Constants.TAG,"bendi");
//        }else  {
////            String staffphotourl = Constants.URL_GETSTAFFPHOTO;
////            staffphotourl = GetJson.replace(staffphotourl, "{staffid}", staffId);
//            staffphotourl = Constants.URL_GETSTAFFPHOTO2;
//            staffphotourl = GetJson.replace(staffphotourl, "{path}", imageUrl);
//            PicassoUtil.getInstance().loadImage(GuestInfoActivity.this, staffphotourl, iv_headpic);
//        }
        staffphotourl = Constants.URL_GETSTAFFPHOTO2;
        staffphotourl = replacememgtUrl(staffphotourl, "{path}", cardInfos.getImageUrl());
        Log.i(Constants.TAG, "url is" + staffphotourl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = Picasso.with(GuestInfoActivity.this).load(staffphotourl).placeholder(R.mipmap.userss).get();
                    if(!(bitmap==null)){
                        handler.sendEmptyMessage(900);
                        Log.i("nsdc","获取图片成功");
                    }else {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(90);
                    Log.i("nsdc","获取图片失败");
                }
            }
        }).start();

    }

    private String replacememgtUrl(String staffphotourl, String regex, String replacement) {
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        while ((index = staffphotourl.indexOf(regex)) >= 0) {
            buffer.append(staffphotourl.substring(0, index));
            buffer.append(replacement);
            staffphotourl = staffphotourl.substring(index + regex.length());
        }
        buffer.append(staffphotourl);
        return buffer.toString();
    }
    private void initAuthority() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(GuestInfoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
//            return;
//        }

        if (
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请了两种权限：WRITE_EXTERNAL_STORAGE与 CAMERA 权限
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CAMERA);
            return;
        }
    }

//    private  class MainHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            switch (msg.what){
//                case EventUtil.UPDATE_FACE_RECT:
//                    Log.i("YanZi", "收到检测到人脸的消息了，准备绘制红色方框 ");
//                    Camera.Face[] faces = (Camera.Face[]) msg.obj;
//                    faceView.setFaces(faces);
//                    //一旦检测到面部，并且APPROVE 是处于可见状态，那么就进行拍照，否则的话不进行操作
////                    ca.autoFocus(myAutoFocusCallback);
////                    ca.takePicture(null, null, pictureCallback);
////                    if(tv_approved.getVisibility()==View.VISIBLE){
////                        ca.autoFocus(myAutoFocusCallback);
////                        ca.takePicture(null,null,pictureCallback);
////                    }
//                    break;
//                case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
//                    Log.i(Constants.TAG,"准备进入GFD内部了");
//                    startGoogleFaceDetect();
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//
//    }

    //=========================================================================================
//    private void startGoogleFaceDetect() {
//        Log.i(Constants.TAG,"进入到GFD内部了");
//        Camera.Parameters params = ca.getParameters();
//        if(params!=null){
//            int i = params.getMaxNumDetectedFaces();
//            if(i > 0){
//                Log.i(Constants.TAG,"params.getMaxNumDetectedFaces()数目是 "+i);
//                if(faceView != null){
//                    faceView.clearFaces();
//                    faceView.setVisibility(View.VISIBLE);
//                }
//                ca.setFaceDetectionListener(googleFaceDetect);
//                Log.i(Constants.TAG,"设置好了detection Listener");
//                ca.startFaceDetection();
//                Log.i(Constants.TAG,"开始faceDetection");
//            }else {
//                Log.i(Constants.TAG,"params.getMaxNumDetectedFaces()数目是 "+i);
//            }
//        }else {
//            Log.i(Constants.TAG,"params 为 null");
//        }
//    }


//    private void stopGoogleFaceDetect(){
//        Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
//        if(params.getMaxNumDetectedFaces() > 0){
//            ca.setFaceDetectionListener(null);
//            ca.stopFaceDetection();
//            faceView.clearFaces();
//        }
//    }

    //========================================================================================

//    private void initholder() {
//        SurfaceHolder holder = surfaceView.getHolder();
//        holder.setFormat(PixelFormat.TRANSPARENT);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        holder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                // 停止预览
//                ca.stopPreview();
//                Log.i(Constants.TAG, "停止阅览了  释放资源，并滞空！");
//                // 释放相机资源并置空
//                ca.release();
//                ca = null;
//            }
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                // TODO Auto-generated method stub
//                Log.i(Constants.TAG,"surfaceCreated....");
//                Log.i(Constants.TAG,"qz 是"+qz);
//                ca = Camera.open(qz);//由qz的代码来决定打开的是前置摄像头还是后置摄像头
//                try {
//                    // 设置预览
//                    ca.setPreviewDisplay(holder);
//                } catch (IOException e) {
//                    // 释放相机资源并置空
//                    ca.release();
//                    ca = null;
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                Log.i(Constants.TAG, "surfaceChanged...");
//                setCameraDisplayOrientation(GuestInfoActivity.this, cindex, ca);
//                // 获得相机参数对象
//                Camera.Parameters parameters = ca.getParameters();
//                // 设置格式
//                // parameters.setPictureFormat(PixelFormat.JPEG);
//                // 设置预览大小，这里我的测试机是Milsstone所以设置的是854x480
//                // parameters.setPreviewSize(854, 480);
//                // 设置自动对焦
//                // parameters.setFocusMode("auto");
//                // 设置图片保存时的分辨率大小
//                //parameters.setPictureSize(1024, 768);
//                //照片正向旋转270度
//                //parameters.set("rotation", 90);
//                // 给相机对象设置刚才设定的参数
//                ca.setParameters(parameters);
//                // 开始预览
//                ca.startPreview();
//
//            }
//        });
//        // 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
//        // 摄像头获取到数据后将数据push到surfaceview 上
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }

    // 这个方法用来防止画面颠倒，原理我还不懂
    @SuppressLint("NewApi")
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        System.out.println("info==" + info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        System.out.println("rotation=" + rotation);//=0
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        //System.out.println("info.faceing="+info.facing);

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            System.out.println("info.orientation=" + info.orientation + "degree=" + degrees);
            result = (360 - result) % 360; // compensate the mirror
            System.out.println("=================result=" + result);
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
            System.out.println("=================No=result=" + result);
        }

        camera.setDisplayOrientation(result);
    }

    private void detectCamera() {
        qz = FindFrontCamera();
        System.out.println("front=" + qz);
        Log.d(Constants.TAG, "得到的前置摄像头 返回码是" + qz);
        //是否有前置摄像头
        if (qz == -1) { //等于-1 说明没有前置摄像头，下面继续检查有没有后置摄像头
            qz = FindBackCamera();
            Log.d(Constants.TAG, "得到的后置摄像头返回码是" + qz);
            if (qz == -1) {
                Toast.makeText(this, "No Camera and exited", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private int FindBackCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (cindex = 0; cindex < cameraCount; cindex++) {
            Camera.getCameraInfo(cindex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return cindex;
            }
        }
        return -1;
    }

    private int FindFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (cindex = 0; cindex < cameraCount; cindex++) {
            Camera.getCameraInfo(cindex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                System.out.println("info.facing=" + cameraInfo.facing);
                return cindex;
            }
        }
        return -1;
    }

//    private void initlistener() {
//        rl_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
////                intent.putExtra("fullScreen", false); // 全屏
////                startActivityForResult(intent,1);
//                Intent intent = new Intent(GuestInfoActivity.this, TakephotoActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    private void initView() {
        tv_approved = (TextView) findViewById(R.id.tv_approved);
//        tv_approved.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ca.autoFocus(myAutoFocusCallback);
//                ca.takePicture(null, null, pictureCallback);
//                mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
//            }
//        });
        tv_denied = (TextView) findViewById(R.id.tv_denied);
        tv_authority = (TextView) findViewById(R.id.tv_authority);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        iv_headpic = (ImageView) findViewById(R.id.iv_headpic);
        tv_vstitle1 = (TextView) findViewById(R.id.tv_vstitle1);
        tv_vstitle2 = (TextView) findViewById(R.id.tv_vstitle2);
        tv_vstitle3 = (TextView) findViewById(R.id.tv_vstitle3);
        tv_vstitle4 = (TextView) findViewById(R.id.tv_vstitle4);
        rl_backpic = (LinearLayout) findViewById(R.id.rl_backpic);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_simplify = (TextView) findViewById(R.id.tv_simplify);
        tv_simplify2 = (TextView)findViewById(R.id.tv_simplify2);
        rl_info = (RelativeLayout) findViewById(R.id.rl_info);
//        surfaceView = (SurfaceView) findViewById(surfaceView);
//        surfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
        rl_wrapcamera = (RelativeLayout) findViewById(R.id.rl_wrapcamera);

        String settingAreaNo = SharedPreferencesUtils.getString(GuestInfoActivity.this,"dischosed","");
        tv_authority.setText(settingAreaNo);

        pb = (ProgressBar)findViewById(R.id.pb);

//        faceView = (FaceView) findViewById(R.id.face_view);
    }

    //拍片子的类
//    public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            Toast.makeText(GuestInfoActivity.this, "正在保存...",
//                    Toast.LENGTH_SHORT).show();
//
//            Log.i(Constants.TAG, data.length + "," + cindex);
//            // 用BitmapFactory.decodeByteArray()方法可以把相机传回的裸数据转换成Bitmap对象
//            mb = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//            Log.i(Constants.TAG, "正在保存照片");
//            // 接下来的工作就是把Bitmap保存成一个存储卡中的文件
//            //文件名中不能有“：”
////			File file = new File("/sdcard/Android/data/"
////					+ new DateFormat().format("yyyy-MM-dd_hh-mm-ss",
////							Calendar.getInstance(Locale.CHINA)) + ".jpg");
////			File file = new File(Environment.getExternalStorageDirectory()+"/victoriasecret/",HEAD_IMG);
//            File file = new File(context.getExternalFilesDir("head") + HEAD_IMG);
//            Uri uri = Uri.fromFile(file);
//            Log.i("tms", "uri 的路径是" + uri.toString());
//            if (file.exists()) {
//                file.delete();
//            }
//            try {
//                file.createNewFile();
//                BufferedOutputStream bos = new BufferedOutputStream(
//                        new FileOutputStream(file));
//                mb.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                Log.i("tms", "数据写入完成");
//                bos.flush();
//                bos.close();
//                Toast.makeText(getApplicationContext(), "图片保存完毕，storage/emulated/0/Android/data/" +
//                                "com.biotag.victoriassecret/files/headface.jpg",
//                        Toast.LENGTH_LONG).show();
//                //重新启动照相机
////                ca.startPreview();
//                //ig.setVisibility(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,this,mLoaderCallback);
        }else {
//            initializeOpenCVDependencies();
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        Log.i(Constants.TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.TAG,"onPause");
        if(openCvCameraView!=null){
            openCvCameraView.disableView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(Constants.TAG,"onStop");
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//
        if(keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            if(System.currentTimeMillis()-mExittime>2000){
                Toast.makeText(context, "再按一次退出App", Toast.LENGTH_SHORT).show();
                mExittime = System.currentTimeMillis();
            }else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(GuestInfoActivity.this,UploadHeadImgService.class);
        stopService(intent);
        Log.i(Constants.TAG,"service 停止了");

        Intent intent1 = new Intent(GuestInfoActivity.this, HeadimgDownloadService.class);
        stopService(intent1);
        Intent intent2 = new Intent(GuestInfoActivity.this, UpLoadingInandOutInfoService.class);
        stopService(intent2);

        mNFCTool.destory();
        Log.i(Constants.TAG,"onDestroy");
    }


    private NFCToolManager.ReadNFCInterface mInterface = new NFCToolManager.ReadNFCInterface() {
        @Override
        public void onGetID(String CardID) {

        }

        @Override
        public void onWriteAreaOk(boolean isAllow,CardInfo cardInfo) {
            android.util.Log.i(TAG, "onWriteAreaOk: kxkxkx isAllow = " + isAllow);
            android.util.Log.i(TAG, "onWriteAreaOk: cardInfo = " + cardInfo);
            showAllow(isAllow);
            insertinandout(cardInfo);
        }

        @Override
        public void onWriteAreaFail() {
            android.util.Log.i(TAG, "onWriteAreaFail: kxkxkx");

            showErrow();

        }

        @Override
        public void onReadCardFail() {
            android.util.Log.i(TAG, "onReadCardFail: kxkxkx");

            showErrow();
        }

        @Override
        public void onGetCardInfo(CardInfo cardInfo) {
            cardInfos = cardInfo;
            String newAreaNow = "";
            String AreaNo = cardInfo.getAreaNo();
            String settingAreaNo = SharedPreferencesUtils.getString(GuestInfoActivity.this,"dischosed","").split(" ")[0];
            String AreaNow = cardInfo.getAreaNow();
            Log.i(TAG, "onGetCardInfo:kxkxkx settingAreaNo = " + settingAreaNo);
            if (AreaNow != null && AreaNo != null && Utils.checkArea(AreaNo,settingAreaNo)) {
                Log.i(TAG, "onGetCardInfo:kxkxkx AreaNow.equals(\"\") = " + AreaNow.equals(""));
                if(cardInfo.getCardType() == Constants.CHIP_TICKET){
                    if(AreaNow.equals("")){
                        newAreaNow = settingAreaNo;
                        mNFCTool.writeAreaNow(Constants.ERROR_CODE_SUCCESS,newAreaNow);
                    }else {
                        //                    // TODO: 2017-10-24 已经刷过卡了
                        mNFCTool.writeAreaNow(Constants.ERROR_CODE_HAS_ENTERED,newAreaNow);
                    }
                }else if(cardInfo.getCardType() == Constants.CHIP_EMPLOYEECARD){
                    newAreaNow = settingAreaNo;
                    mNFCTool.writeAreaNow(Constants.ERROR_CODE_SUCCESS,newAreaNow);
                }else{
                    newAreaNow = "";
                    mNFCTool.writeAreaNow(Constants.ERROR_CODE_NOT_TICKET_EMPLEECARD,newAreaNow);
                }

            }else if (cardInfo.getGroupID() == Constants.GROUPID_ALLPASS){
                newAreaNow = settingAreaNo;
                mNFCTool.writeAreaNow(Constants.ERROR_CODE_SUCCESS,newAreaNow);
            }else if (cardInfo.getCardType() != Constants.CHIP_EMPLOYEECARD &&
                    cardInfo.getCardType() != Constants.CHIP_TICKET){
                newAreaNow = "";
                mNFCTool.writeAreaNow(Constants.ERROR_CODE_NOT_TICKET_EMPLEECARD,newAreaNow);
            }else {
                // TODO: 2017-10-24 区域错误
                mNFCTool.writeAreaNow(Constants.ERROR_CODE_FAULT_AREA,newAreaNow);
            }

            showInfo(cardInfo);
        }

        @Override
        public void onDenied(int code) {
            android.util.Log.i(TAG, "onDenied: code = " + code);
            showDeniedMessage(code);
        }
    };

    private void showInfo(CardInfo cardInfo){
        Log.i("panpan","cardInfo is" + cardInfo.toString());
        starttime = System.currentTimeMillis();
        tv_name.setText(cardInfo.getStaffName());
//        tv_brand.setText(cardInfo.getCompanyName());
//        tv_id.setText("EC ID # "+cardInfo.getStaffNo());

        String companyName = cardInfo.getCompanyName();
        if(companyName == null || companyName.equals("")){
            tv_brand.setVisibility(View.INVISIBLE);
        }else{
            tv_brand.setVisibility(View.VISIBLE);
            tv_brand.setText(companyName);
        }
        String staffNo = cardInfo.getStaffNo();
        if(staffNo == null || staffNo.equals("")){
            tv_id.setVisibility(View.INVISIBLE);
        }else{
            tv_id.setVisibility(View.VISIBLE);
            tv_id.setText(staffNo);
        }


        String AreaNo = cardInfo.getAreaNo();
        AreaNo = Utils.convertAreaToDisplay(AreaNo);
//        if(AreaNo.equals("A")){
//            AreaNo = "H M B F S O";
//        }
        tv_simplify.setText(AreaNo);
        AreaNo = cardInfo.getAreaNo();
        AreaNo = Utils.dealAreaNo(AreaNo);
        tv_simplify2.setText(AreaNo);


        staffId = cardInfo.getID();
        imageUrl = cardInfo.getImageUrl();
        initlayout();

    }

    private void insertinandout(CardInfo cardInfo) {
        final String areano = SharedPreferencesUtils.getString(context, "dischosed", "A");
        final String action_type = "1";  //1代表进入，0 代表出去
        Cursor cursor = db.query("Inoutinfo", new String[]{"StaffID", "ChipCode", "AreaNo", "Action_Type",
                "ActionTime"}, "StaffID = ?", new String[]{cardInfo.getID()}, null, null, null);
        if (!cursor.moveToFirst()) { //如果没有查询到该id条目，则在数据库中能插入
            //提交入场人的信息
            Log.i("panpan", "开始往数据库插入条目");
            ContentValues values = new ContentValues();
            values.put("StaffID", cardInfo.getID());
            values.put("ChipCode", cardInfo.getIdCard());
            values.put("AreaNo", areano);
            values.put("Action_Type", action_type);
            values.put("ActionTime", currenttime);
            db.insert("Inoutinfo", null, values);
            Log.i("panpan", "数据库插入条目完成");
        } else {//  说明数据库中有此条目，说明这个人已经进入过了
            Log.i(Constants.TAG, "数据库中已有此条目");
        }
        cursor.close();
    }


    private void showAllow(final boolean isAllow){
        if(isAllow){
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    String isCancelurl = Constants.ISCANCEL_URL;
                    isCancelurl = replacememgtUrl(isCancelurl,"{id}",cardInfos.getID());
                    isCancelurl = replacememgtUrl(isCancelurl,"{chip}",cardInfos.getCardID());
                    Log.i("ttttt","iscancelurl  is  "+isCancelurl);
                    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(2000, TimeUnit.MILLISECONDS).readTimeout(1000,TimeUnit.MILLISECONDS).build();
                    Request request = new Request.Builder().url(isCancelurl).build();
                    try{
                        Response response = client.newCall(request).execute();
                        String s = response.body().string();
                        Log.i("ttttt",s);
                        IsCancelBean icb = new Gson().fromJson(s,IsCancelBean.class);
                        if(icb!=null&&icb.isIsSuccess()){
                            handler.sendEmptyMessage(901);
                        }else {
                            handler.sendEmptyMessage(91);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        handler.sendEmptyMessage(901);

                    }
                }
            });
        }else{
            tv_approved.setText("DENIED");
            tv_approved.setTextColor(Color.RED);
            tv_approved.setVisibility(View.VISIBLE);
            iv_headpic.setImageResource(R.mipmap.user);
        }
    }

    private void showErrow(){
        tv_name.setText("");
        tv_brand.setText("");
        tv_id.setText("");
        tv_simplify.setText("");
        tv_simplify2.setText("");

        tv_approved.setText("READ FAILED");
        tv_approved.setTextColor(Color.RED);
        tv_approved.setVisibility(View.VISIBLE);
        iv_headpic.setImageResource(R.mipmap.userss);
        pb.setVisibility(View.INVISIBLE);
    }

    private void showDeniedMessage(int code) {

        if (code == Constants.ERROR_CODE_HAS_ENTERED) {
            tv_approved.setText("DENIED(失效)");
            tv_approved.setTextColor(Color.RED);
            tv_approved.setVisibility(View.VISIBLE);
//            iv_headpic.setImageResource(R.mipmap.userss);
            pb.setVisibility(View.INVISIBLE);
        }else if(code == Constants.ERROR_CODE_FAULT_AREA){
            tv_approved.setText("DENIED(区域错误)");
            tv_approved.setTextColor(Color.RED);
//            iv_headpic.setImageResource(R.mipmap.userss);
            tv_approved.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
        }else if(code == Constants.ERROR_CODE_NOT_TICKET_EMPLEECARD){
            tv_approved.setText("DENIED(不是工作证或门票)");
            tv_approved.setTextColor(Color.RED);

            tv_approved.setVisibility(View.VISIBLE);
        }
    }
}
