package com.yang.mylauncher.suggest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.yang.mylauncher.utils.Utils;

public class SuggestProvider extends ContentProvider {
    private SQLiteDatabase sqlDB;
    private DatabaseHelper    dbHelper;

    public static final Uri CONTENT_URI = Uri.parse("content://com.yang.mylauncher.suggest.SuggestProvider");
    private static final String  DATABASE_NAME = "suggest.db";
    private static final int  DATABASE_VERSION= 1;
    private static final String TABLE_NAME= "suggests";
    public static final String COLUM_ID = "_id";
    public static final String COLUM_NAME = "name";
    public static final String COLUM_TYPE = "type";
    public static final String COLUM_USE_COUNT = "count";
    public static final String COLUM_USE_TIME = "last_use_time";

    public SuggestProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_NAME,selection,selectionArgs);
        //how to use
        //getContentResolver().delete(SuggestProvider.CONTENT_URI,SuggestProvider.COLUM_NAME+"=? ",new String[]{"name4"});
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        sqlDB = dbHelper.getWritableDatabase();
        long rowId =  sqlDB.insert(TABLE_NAME,"0",values);
        return null;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
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
        String sql = "UPDATE " + TABLE_NAME + " SET "+COLUM_USE_COUNT+"="+COLUM_USE_COUNT+"+1,"+COLUM_USE_TIME+"=? WHERE "+COLUM_NAME+"=?";
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
                    +COLUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +COLUM_NAME+ " TEXT NOT NULL,"
                    +COLUM_TYPE+ " INTEGER NOT NULL,"
                    +COLUM_USE_COUNT+ " INTEGER NOT NULL,"
                    +COLUM_USE_TIME+ " DATETIME NOT NULL"
                    + ")";
            db.execSQL(sql);
            Log.e("DATABASE", TABLE_NAME+"数据表创建成功");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
