package com.getinlight.controlphone.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.getinlight.controlphone.db.BlackNumberOpenHelper;
import com.getinlight.controlphone.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式
 * Created by getinlight on 2018/2/19.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper blackNumberOpenHelper;

    //1.私有化构造方法
    private BlackNumberDao(Context ctx) {
        //创建数据库
        blackNumberOpenHelper = new BlackNumberOpenHelper(ctx);
    }
    //2.生命一个当前类对象
    private static BlackNumberDao blackNumberDao = null;
    //3.提供一个静态方法, 如果当前类的对象为空, 创建一个新的
    public static BlackNumberDao getInstance(Context ctx) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(ctx);
        }
        return blackNumberDao;
    }

    //增加一个条目
    public void insert(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    public void delete(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber", "phone = ?", new String[]{phone});
        db.close();
    }

    public void update(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknumber", values, "phone = ?", new String[]{phone});
        db.close();
    }

    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"},
                null, null, null,null, "_id desc");
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String phone = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfo.setPhone(phone);
            blackNumberInfo.setMode(mode);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfos;
    }

    /**
     * 每次查询20条数据
     * @param index
     * @return
     */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone,mode FROM blacknumber ORDER BY _id DESC LIMIT ?,20", new String[]{index+""});
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String phone = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfo.setPhone(phone);
            blackNumberInfo.setMode(mode);
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberInfos;
    }

    public int getCount() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM blacknumber", null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    /**
     * 根据电话号码查询拦截模式
     * @param phone
     * @return 1.短信 2.电话 3.所有 0.没有此条数据
     */
    public int getMode(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT mode FROM blacknumber WHERE phone = ?", new String[]{phone});
        int mode = 0;
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return mode;
    }

}
