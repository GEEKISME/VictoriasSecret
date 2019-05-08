package useless;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biotag.victoriassecret.Log;
import com.biotag.victoriassecret.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.biotag.victoriassecret.GuestInfoActivity.HEAD_IMG;

public class TakephotoActivity extends Activity {
//	private CameraView cav;
	// 相机对象
	private Camera ca = null;
	// Bitmap对象
	private Bitmap mb = null;
	private SurfaceView sv;
	private AutoFocusCallback myAutoFocusCallback = null;
	private int cameraCount = 0;
	private int cindex = -1,qz;
	private FrameLayout fff;
	private SurfaceView surfaceView;
	private Context context = this;


	// 准备一个图片保存对象
	@TargetApi(9)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//自动聚焦
		myAutoFocusCallback= new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
       				if(success){
       					System.out.println("成功");
       				}else 	
       					System.out.println("失败");
			}
		};
		Window window = getWindow();
		// 去除标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_takephoto);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 提供一个帧布局
//		FrameLayout f1 = new FrameLayout(this);
		// 创建一个照相预览用的SurfaceView子类,并放在帧布局的底部
		//判断是否有前置摄像头
	    qz = FindFrontCamera();
		System.out.println("front="+qz);
				//是否有前置摄像头
		if(qz==-1){
				 qz = FindBackCamera();
				if(qz==-1){
					 Toast.makeText(this, "当前无摄像头,即将退出", Toast.LENGTH_SHORT).show();
					 this.finish();
				 }
		}
		fff = (FrameLayout)findViewById(R.id.FrameLayout1);
		 RelativeLayout relate = new RelativeLayout(this);
		 RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		 //因为在布局中写的控件无法准确布局，所以在代码里写
		 RelativeLayout.LayoutParams rlb = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		 rlb.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 rlb.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 ImageButton imageB = new ImageButton(this);
		 imageB.setImageResource(R.mipmap.camera);
		 imageB.setHorizontalFadingEdgeEnabled(false);
		 imageB.setHorizontalScrollBarEnabled(false);
		 imageB.setLayoutParams(rlb);
		 relate.addView(imageB);
		 relate.setLayoutParams(rl);
		fff.addView(relate);
		//ig=(ImageButton)findViewById(R.id.cameraB);
		imageB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ca.autoFocus(myAutoFocusCallback);
				ca.takePicture(null,null, pictureCallback);
			}
		});
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
	    SurfaceHolder holder = surfaceView.getHolder();
	    holder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 停止预览
				ca.stopPreview();
				System.out.println("停止阅览了  释放资源，并滞空！");
				// 释放相机资源并置空
				ca.release();
				ca = null;
			}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				ca = Camera.open(qz);
				try {
					// 设置预览
					ca.setPreviewDisplay(holder);
				} catch (IOException e) {
					// 释放相机资源并置空
					ca.release();
					ca = null;
				}
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
				setCameraDisplayOrientation(TakephotoActivity.this, cindex, ca);
				// 获得相机参数对象
				Camera.Parameters parameters = ca.getParameters();
				// 设置格式
				// parameters.setPictureFormat(PixelFormat.JPEG);
				// 设置预览大小，这里我的测试机是Milsstone所以设置的是854x480
				// parameters.setPreviewSize(854, 480);
				// 设置自动对焦
				// parameters.setFocusMode("auto");
				// 设置图片保存时的分辨率大小
				//parameters.setPictureSize(1024, 768);
				//照片正向旋转270度
				//parameters.set("rotation", 90);
				// 给相机对象设置刚才设定的参数
				ca.setParameters(parameters);
				// 开始预览
				ca.startPreview();

			}
		});
//		f1.addView(cav);
//		TextView tv = new TextView(this);
//        tv.setText("请点击屏幕拍摄");
//        fff.addView(findViewById(R.id.surfaceView));
//		fff.addView(tv);
//        f1.addView(tv);
        //触摸拍照事件
//        fff.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				ca.autoFocus(myAutoFocusCallback);
//				ca.takePicture(null,null, pictureCallback);
//				return false;
//			}
//		});	
     // 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设置Activity的根内容视图
		//setContentView(f1);
	}
	// 照相视图
//		class CameraView extends SurfaceView {
//			private SurfaceHolder holder = null;
//			// 构造函数
//			@TargetApi(9)
//			public CameraView(Context context,final int index) {
//				super(context);
//				System.out.println("index="+index);
//				// 操作surface的holder
//				holder = this.getHolder();
//				
//				//获取摄像头的个数：前置和后置摄像头
//				
//				
//				// 创建SurfaceHolder.Callback对象
//				holder.addCallback(new SurfaceHolder.Callback() {
//					@Override
//					public void surfaceDestroyed(SurfaceHolder holder) {
//						
//					}
//
//					@Override
//					public void surfaceCreated(SurfaceHolder holder) {
//						// 当预览视图创建的时候开启相机
//						
//					}
//
//					// 当surface视图数据发生变化时，处理预览信息
//					@Override
//					public void surfaceChanged(SurfaceHolder holder, int format,
//							int width, int height) {
//						
//					}
//
//				});
				
//			}
//		}
	//拍片子的类
	public Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Toast.makeText(TakephotoActivity.this, "正在保存...",
					Toast.LENGTH_SHORT).show();
			// 用BitmapFactory.decodeByteArray()方法可以把相机传回的裸数据转换成Bitmap对象
			System.out.println(data.length+","+cindex);
			mb = BitmapFactory.decodeByteArray(data, 0, data.length);
			System.out.println("dasdsada");
			System.out.println("正在保存小片中");
			// 接下来的工作就是把Bitmap保存成一个存储卡中的文件
			//文件名中不能有“：”
//			File file = new File("/sdcard/Android/data/"
//					+ new DateFormat().format("yyyy-MM-dd_hh-mm-ss",
//							Calendar.getInstance(Locale.CHINA)) + ".jpg");
//			File file = new File(Environment.getExternalStorageDirectory()+"/victoriasecret/",HEAD_IMG);
			File file = new File(context.getExternalFilesDir("head")+HEAD_IMG);
			Uri uri = Uri.fromFile(file);
			Log.i("tms","uri 的路径是"+uri.toString());
			if(file.exists()){
				file.delete();
			}
			try {
				file.createNewFile();
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				mb.compress(Bitmap.CompressFormat.JPEG,100, bos);
				Log.i("tms","数据写入完成");
				bos.flush();
				bos.close();
				Toast.makeText(getApplicationContext(), "图片保存完毕，storage/emulated/0/Android/data/" +
								"com.biotag.victoriassecret/files/headface.jpg",
						Toast.LENGTH_LONG).show();
				//重新启动照相机
				ca.startPreview();
				//ig.setVisibility(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// 智能手机没有拍照键，将爱写屏幕触碰事件
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// System.out.println("这方法有用？");
	// Log.i("yao", "MainActivity.onKeyDown");
	// System.out.println("woqu "+keyCode);
	// System.out.println(KeyEvent.KEYCODE_CAMERA);
	// if (keyCode == KeyEvent.KEYCODE_CAMERA) {
	//
	// if (ca != null) {
	// Log.i("yao", "ca.takePicture");
	// // 当按下相机按钮时，执行相机对象的takePicture()方法,该方法有三个回调对象做入参，不需要的时候可以设null
	// ca.takePicture(null, null, pictureCallback);
	// }
	// }
	// return cav.onKeyDown(keyCode, event);
	// }

	@Override
	public void onBackPressed() {
		finish();
	}

	// 这个方法用来防止画面颠倒，原理我还不懂
	@SuppressLint("NewApi")
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, Camera camera) {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		System.out.println("info=="+info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		System.out.println("rotation="+rotation);//=0
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

		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			System.out.println("info.orientation="+info.orientation+"degree="+degrees);
			result = (360 - result) % 360; // compensate the mirror
			System.out.println("=================result="+result);
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
			System.out.println("=================No=result="+result);
		}

		camera.setDisplayOrientation(result);
	}
	//寻找前置摄像头
	
	@TargetApi(9)
	private int FindFrontCamera(){
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for(cindex=0;cindex < cameraCount;cindex++){
			Camera.getCameraInfo(cindex, cameraInfo );
			if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
			{ System.out.println("info.facing="+cameraInfo.facing);
			return cindex;}
		}
		return -1;
	}
	//寻找后置摄像头
		@TargetApi(9)
		private int FindBackCamera(){
			CameraInfo cameraInfo = new CameraInfo();
			cameraCount = Camera.getNumberOfCameras();
			for(cindex=0;cindex < cameraCount;cindex++){
				Camera.getCameraInfo(cindex,cameraInfo);
				if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
				{ 
				return cindex;}
			}
			return -1;
		}
}
