package utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.biotag.victoriassecret.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.biotag.victoriassecret.Constants.BOUNDARY;

/**
 * Created by Lxh on 2017/8/11.
 */

public class NewCommmonMethod {
    private static volatile NewCommmonMethod utils  = null;
    private NewCommmonMethod(){}

    public static NewCommmonMethod getInstance() {
        if ( utils== null) {
            synchronized (NewCommmonMethod.class) {
                if (utils == null) {
                    utils = new NewCommmonMethod();
                }
            }
        }
        return utils;
    }

    //头像缩放比例
    private static final int HEAD_IMAGE_SCALE = 90;
    /**
     * 向图片资源管理器发起的请求码
     */
    public static final int HEAD_IMAGE_REQUEST_CODE = 100;
    public static final int HEAD_CAMERA_REQUEST_CODE = 101;








    /**
     * 检测当前模式是否为debug模式
     * @param context
     */
    public void checkDebug(Context context) {
        if (isApkDebugable(context)) {
            Log.isLog = true;
            Toast.makeText(context, "当前模式debug", Toast.LENGTH_SHORT).show();
        } else {
            Log.isLog = false;
        }
    }

    /**
     * 参考yama该函数说明
     */
    public boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int cutBySize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }





    //===========================================================================================
    /**
     *android上传文件到服务器
     * @param params:请求参数
     * @param fileFormName:文件名称
     * @param uploadFile:上传的文件
     * @param newFileName:可不写
     * @param urlStr:后台地址
     * @throws IOException
     */

    public void uploadForm( final Map<String, String> params, String fileFormName,
                           File uploadFile, String newFileName, String urlStr)
            throws IOException {

        Log.i(Constants.TAG,"进来了");
        Log.i("tsk","进来了");
        if (newFileName == null || newFileName.trim().equals("")) {
            newFileName = uploadFile.getName();
        }

        StringBuilder sb = new StringBuilder();
        /**
         * 普通的表单数据
         */
        if (params != null)
            for (String key : params.keySet()) {
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + key
                        + "\"" + "\r\n");
                sb.append("\r\n");
                sb.append(params.get(key) + "\r\n");
            }
        /**
         * 上传文件的头
         */
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"" + fileFormName
                + "\"; filename=\"" + newFileName + "\"" + "\r\n");
        sb.append("Content-Type: image/jpeg" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
        sb.append("\r\n");

        byte[] headerInfo = sb.toString().getBytes("UTF-8");
        byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
        System.out.println(sb.toString());
        Log.i(Constants.TAG,sb.toString());
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);
        conn.setRequestProperty("Content-Length", String
                .valueOf(headerInfo.length + uploadFile.length()
                        + endInfo.length));
        conn.setDoOutput(true);

        OutputStream out = conn.getOutputStream();
        InputStream in = new FileInputStream(uploadFile);
        out.write(headerInfo);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1)
            out.write(buf, 0, len);

        out.write(endInfo);
        in.close();
        out.close();
        Log.i(Constants.TAG,conn.getResponseCode()+"");
        Log.i("tsk",conn.getResponseCode()+"");
        Log.i(Constants.TAG,conn.getResponseMessage());
        Log.i("tsk",conn.getResponseMessage());
        if (conn.getResponseCode() == 200) {
            Log.i(Constants.TAG,"文件上传成功");
            Log.i("tsk","文件上传成功");
        }else {
            Log.i(Constants.TAG,"文件上传fail");
            Log.i("tsk","文件上传fail");
        }
    }
}
