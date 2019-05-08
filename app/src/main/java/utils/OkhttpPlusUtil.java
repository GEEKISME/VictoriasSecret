package utils;


import com.biotag.victoriassecret.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/22.
 */

public class OkhttpPlusUtil {

    private volatile static  OkhttpPlusUtil utils;

    private OkHttpClient mOkHttpClient ;
    public static  final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkhttpPlusUtil() {

        mOkHttpClient = OkHttpUtil.getOkHttpClient();
    }

    public static OkhttpPlusUtil getInstance() {
        if(utils==null){
            synchronized (OkhttpPlusUtil.class){
                if(utils==null){
                    utils = new OkhttpPlusUtil();
                }
            }
        }
        return utils;
    }


    //
    // 针对所有的post请求均可使用，用execute 方法

    public <T extends Object>T post(String url, Map<String,String> map, Class<T> clazz){

        FormBody.Builder fb = new FormBody.Builder();

        //遍历map集合，将map集合中所有的键值对添加到fb对象
        Set<String> keys = map.keySet();
        for (String s: keys) {
            fb.add(s,map.get(s));
        }

        Request post = new Request.Builder()
                .post(fb.build())
                .url(url)
                .build();

        try {
            Response res =  mOkHttpClient.newCall(post).execute();
            String ss = res.body().toString();
            Log.i(Constants.TAG,url);
            Log.i(Constants.TAG,ss);
            T t = new Gson().fromJson(ss,clazz);

            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    //post 请求提交json数据
    public <T extends Object>T post(String url, String json, Class<T> clazz){

        RequestBody body= RequestBody.create(JSON,json);

        Request post = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        try {
            Response res =  mOkHttpClient.newCall(post).execute();
            String s = res.body().string();
            Log.i(Constants.TAG,url);
            Log.i(Constants.TAG,s);
            T t = new Gson().fromJson(s,clazz);

            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //针对所有get请求，用execute 方法

    /**
     * <T extends Object >T   用于在返回值的位置声明自定义泛型，指代任意类型的类
     * @param url  请求网址
     * @param clazz   解析时使用的class类对象
     *
     * @return  连网解析后的结果
     */
    public <T extends Object>T get (String url, Class<T> clazz){
        Log.i(Constants.TAG,url);
        Request request = new Request.Builder()
                .url(url).build();

        try {
            //获取请求结果
            Response res = mOkHttpClient.newCall(request).execute();
            //针对请求结果进行解析操作
            String resss = res.body().string();
            Log.i(Constants.TAG,resss);

            T zb =  new Gson().fromJson(resss, clazz);
            //将解析后的结果作为返回值返回
            return zb;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("tdn","  ssssssssssssss");
        }

        return null;
    }



}
