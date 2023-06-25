package com.phage.ex_sepim.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DbContect extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DBNAME="pc.db";   //  创建数据库名叫 phage_chat
    private Context mContext;

    public DbContect(Context context){
        super(context,DBNAME,null,VERSION);
        mContext = context;
    }
    //创建数据库
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table chat_tb(id integer primary key autoincrement," +
                "l_account integer,u_id integer,username varchar(255),avatar varchar(255),content varchar(255),date varchar(255),no_read integer)");
    }
    //数据库版本更新
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
        db.execSQL("drop table if exists chat_tb");
        onCreate(db);
    }


}

