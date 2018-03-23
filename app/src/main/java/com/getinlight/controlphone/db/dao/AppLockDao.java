package com.getinlight.controlphone.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.getinlight.controlphone.db.AppLockOpenHelper;
import com.getinlight.controlphone.db.BlackNumberOpenHelper;
import com.getinlight.controlphone.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式
 * Created by getinlight on 2018/2/19.
 */

public class AppLockDao {

    private final AppLockOpenHelper appLockOpenHelper;
    private final Context context;

    //1.私有化构造方法
    private AppLockDao(Context ctx) {
        this.context = ctx;
        //创建数据库
        appLockOpenHelper = new AppLockOpenHelper(ctx);
    }
    //2.生命一个当前类对象
    private static AppLockDao appLockDao = null;
    //3.提供一个静态方法, 如果当前类的对象为空, 创建一个新的
    public static AppLockDao getInstance(Context ctx) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(ctx);
        }
        return appLockDao;
    }

    public void insert(String packageName) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("applock", null, values);

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        db.delete("applock", "packagename = ?", new String[]{packageName});

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public List<String> findAll() {
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        ArrayList<String> packages = new ArrayList<>();
        while (cursor.moveToNext()) {
            packages.add(cursor.getString(0));
        }

        db.close();

        return packages;
    }

}
