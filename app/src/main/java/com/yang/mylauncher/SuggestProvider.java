package com.yang.mylauncher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.yang.mylauncher.utils.Utils;


/**
 * 提示 数据来源
 * 包括命令 建议等
 * 用数据库 存储
 */
public class SuggestProvider extends ContentProvider {
    private SQLiteDatabase sqlDB;
    private DatabaseHelper dbHelper;
    public static final Uri URI = Uri.parse("content://com.yang.mylauncher.SuggestProvider");

    private static final String  DATABASE_NAME = "suggest.db";
    private static final int  DATABASE_VERSION= 1;
    private static final String TABLE_NAME = "command_suggests";

    //显示的名字
    public static final String DISPLAY_NAME = "display_name";
    //根据输入搜索的名字
    public static final String SEARCH_NAME = "search_txt";
    //使用计数
    public static final String USE_COUNT = "use_count";
    //最后使用时间
    public static final String USE_TIME = "last_use_time";
    //保存类名
    public static final String CMD_CLASS_NAME = "class_name";
    //保存类名
    public static final String CMD_CLICK_RUN = "runable";
    //保存类型
    public static final String TYPE = "type";

    public SuggestProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int i =  db.delete(TABLE_NAME,selection,selectionArgs);
        Log.e("DB","delete "+i+selection);
        return i;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        sqlDB = dbHelper.getWritableDatabase();
        long rowId =sqlDB.insert(TABLE_NAME,"0",values);
        Log.e("DATABASE", "插入"+rowId);
        return null;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //String sql = "SELECT * FROM " + TABLE_READ_HISTORY + " order by read_time desc limit " + num;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        qb.setTables(TABLE_NAME);
        //db, String[] projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder,"10");
    }


    /**
     * 改造过后表示
     * 命令计数+1
     * 时间更新
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET "+ USE_COUNT +"="+ USE_COUNT +"+1,"+ USE_TIME +"=? WHERE "+ DISPLAY_NAME +"=?";
        Log.e("sql",sql);
        String time = Utils.getCurrentTime();
        String name = selectionArgs[0];
        db.execSQL(sql,new Object[]{time,name});
        return 0;
    }



    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);//继承父类
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String sql = "CREATE TABLE " + TABLE_NAME + "("
                    + DISPLAY_NAME + " TEXT PRIMARY KEY,"
                    + SEARCH_NAME + " TEXT NOT NULL,"
                    + TYPE + " INTEGER NOT NULL DEFAULT -1,"
                    + USE_COUNT + " INTEGER NOT NULL DEFAULT 0,"
                    + USE_TIME + " DATETIME NOT NULL DEFAULT 0,"
                    + CMD_CLASS_NAME +" TEXT NOT NULL DEFAULT unknown,"
                    + CMD_CLICK_RUN + " INTEGER NOT NULL DEFAULT 0"
                    + ")";
            db.execSQL(sql);
            Log.e("DATABASE", TABLE_NAME +"数据表创建成功");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
