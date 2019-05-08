package useless;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
//
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{      
    SurfaceHolder holder;      
    Camera myCamera;   
    public MySurfaceView(Context context)      
    {      
        super(context);  
        holder = getHolder();//获得surfaceHolder引用      
        holder.addCallback(this);      
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型        
    }  
    @Override      
    public void surfaceCreated(SurfaceHolder holder) {      
        // TODO Auto-generated method stub      
        if(myCamera == null)      
        {      
            myCamera = Camera.open();//开启相机,不能放在构造函数中，不然不会显示画面.      
            try {      
                myCamera.setPreviewDisplay(holder);      
            } catch (IOException e) {      
                e.printStackTrace();      
            }      
        }             
    }      
    @Override      
    public void surfaceChanged(SurfaceHolder holder, int format, int width,      
            int height) {      
        myCamera.startPreview();              
    }      
        @Override      
    public void surfaceDestroyed(SurfaceHolder holder) {      
        // TODO Auto-generated method stub      
        myCamera.stopPreview();//停止预览      
         myCamera.release();//释放相机资源      
         myCamera = null;      
        Log.d("ddd", "4");        
    }      
}      