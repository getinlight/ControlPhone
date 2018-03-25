package com.getinlight.controlphone.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by getinlight on 2018/2/7.
 */

public class VirusDao {

    private static final String TAG = "VirusDao";
    public static String path = "data/data/com.getinlight.controlphone/files/antivirus.db";

    public static List<String> getVirtusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        ArrayList<String> virtusList = new ArrayList<>();
        while (cursor.moveToNext()) {
            virtusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        return virtusList;
    }

}
