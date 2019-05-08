package utils;

//

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by duanfangfang on 2017/3/2.
 */
public class OkHttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private volatile static OkHttpClient mOkHttpClient = null;

    public static OkHttpClient getOkHttpClient(){
        if(null == mOkHttpClient){
            synchronized (OkHttpUtil.class){
                if(null == mOkHttpClient){

                    int cacheSize = 10*1024*1024;

                    //Log.d("OkHttpUtil","create...OkHttpClient..success");

                    mOkHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(3000, TimeUnit.MILLISECONDS)
                            .readTimeout(10000, TimeUnit.MILLISECONDS)
//                            .cache(new Cache(new File(FileUtils.getCacheDir()),cacheSize))//10M
                            .build();

                }
            }
        }
        return mOkHttpClient;
    }

    public static Response getPostResponse(String url, RequestBody formBody) throws IOException {

        Request request = new Request.Builder().url(url).post(formBody).build();

        return  getOkHttpClient().newCall(request).execute();

    }






}
