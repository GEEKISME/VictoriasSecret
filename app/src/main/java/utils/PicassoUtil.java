package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.biotag.victoriassecret.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Lxh on 2017/8/9.
 */

public class PicassoUtil {
    private volatile  static  PicassoUtil mInstance;
    private PicassoUtil(){}

    public static PicassoUtil getInstance() {
        if (mInstance == null) {
            synchronized (PicassoUtil.class) {
                if (mInstance == null) {
                    mInstance = new PicassoUtil();
                }
            }
        }
        return mInstance;
    }

    public static final int FILE=1;
    public static final int IMAGE =0;

    private static float offset = 0;

    /**
     * 刷新图片内容，type:0 网络图片。1本地图片
     * @param context
     * @param url
     * @param type
     */
    public void invalidate(Context context, String url, int type) {
        if (!url.equals("")) {
            switch (type) {
                case IMAGE:
                    Picasso.with(context).invalidate(url);
                    break;
                case FILE:
                    Picasso.with(context).invalidate(new File(url));
                    break;
            }
        }
    }
    /**
     * 加载用cock的头像
     *
     * @param context
     * @param url
     * @param imageView
     */
    public void loadImage(Context context, String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).placeholder(R.mipmap.userss).error(R.mipmap.userss).into(imageView);

        }
    }
    /**
     * 加载普通的图片
     */
    public void loadnormalImage(Context context, String url, ImageView imageView){
        if(!TextUtils.isEmpty(url)){
            Picasso.with(context).load(url).placeholder(R.mipmap.user).error(R.mipmap.user).config(Bitmap.Config.RGB_565).into(imageView);
        }
    }
}
