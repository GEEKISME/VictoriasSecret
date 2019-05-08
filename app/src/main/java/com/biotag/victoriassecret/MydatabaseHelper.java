package com.biotag.victoriassecret;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lxh on 2017/10/31.
 */

public class MydatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "InandOut.db";
    private Context mcontext;
    public static volatile MydatabaseHelper mInstance ;

    public static final String CREAT_BOOK = "create table Inoutinfo("
            +"id integer primary key autoincrement,"
            +"StaffID text,"
            +"ChipCode text,"
            +"AreaNo text,"
            +"Action_Type text,"
            +"ActionTime text)";

    private MydatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, 1);
        mcontext = context;
    }

    public static  MydatabaseHelper getInstance(Context context){
        if(mInstance==null){
            synchronized (MydatabaseHelper.class){
                if(mInstance==null){
                    mInstance = new MydatabaseHelper(context.getApplicationContext(),"InandOut.db",null,1);
                }
            }
        }
        return mInstance;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_BOOK);
//        Toast.makeText(mcontext, "建表成功", Toast.LENGTH_SHORT).show();
        Log.i(Constants.TAG,"suc");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
