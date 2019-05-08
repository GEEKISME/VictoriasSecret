package utils;

import android.content.Context;
import android.widget.Toast;

import com.biotag.victoriassecret.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lxh on 2017/11/1.
 */

public class DataBaseUtils {
    //com.biotag.vsstaffedition
    // ---/data/data/com.biotag.victoriassecret/databases/
    public static void importdatabasefromassets(Context context){
        String DB_PATH = "/data/data/com.biotag.victoriassecret/databases/";
        String DB_NAME = "InandOut.db";
        File dbfile = new File(DB_PATH,DB_NAME);
        if(!dbfile.exists()){
            File directory = new File(DB_PATH);
            if(!directory.exists()){
                directory.mkdir();
            }
            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = context.getAssets().open(DB_NAME);
                // 输出流,在指定路径下生成db文件
                OutputStream os = new FileOutputStream(dbfile);
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer))!=-1){
                    os.write(buffer,0,length);
                    os.flush();
                }
                os.close();
                is.close();
                Log.i(Constants.TAG,"数据库文件已经写入APP程序区");
                Toast.makeText(context, "数据库文件已经写入APP程序区", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
