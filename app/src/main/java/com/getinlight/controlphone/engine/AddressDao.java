package com.getinlight.controlphone.engine;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by getinlight on 2018/2/7.
 */

public class AddressDao {
    private static final String TAG = "AddressDao";
    public static String path = "data/data/com.getinlight.controlphone/files/address.db";
    private static String address = "未知号码";

    public static String getAddress(String phone) {
        String regularExpression = "^1[3-8]\\d{9}$";
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regularExpression)) {
            phone = phone.substring(0, 7);
            Cursor cursor =
                    db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                Cursor indexCursor = db.query(
                        "data2", new String[]{"location"}, "id=?", new String[]{outkey}, null,null,null);
                if (indexCursor.moveToNext()) {
                    String location = indexCursor.getString(0);
                    Log.i(TAG, "getAddress: location = "+location);
                    address = location;
                } else {
                    address = "未知号码";
                }
                indexCursor.close();
            }
            cursor.close();
        } else {
            switch (phone.length()) {
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "银行号码";
                    break;
                case 7:
                case 8:
                    address = "本地号码";
                    break;
                case 11:
                    String areaCode = phone.substring(1, 3);
                    Cursor cursor = db.query(
                            "data2",new String[]{"location"}, "area=?",new String[]{areaCode}, null,null,null);
                    if (cursor.moveToNext()) {
                        address = cursor.getString(0);
                    } else {
                        address = "未知号码";
                    }
                    break;
                case 12:
                    String areaCode1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query(
                            "data2",new String[]{"location"}, "area=?",new String[]{areaCode1}, null,null,null);
                    if (cursor1.moveToNext()) {
                        address = cursor1.getString(0);
                    } else {
                        address = "未知号码";
                    }
                    break;
                default:
                    address = "未知号码";
                    break;

            }
        }

        return address;
    }

    private static byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }
}
