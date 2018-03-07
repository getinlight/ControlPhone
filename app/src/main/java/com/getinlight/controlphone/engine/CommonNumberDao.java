package com.getinlight.controlphone.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by getinlight on 2018/2/7.
 */

public class CommonNumberDao {
    private static final String TAG = "CommonNumberDao";
    public static String path = "data/data/com.getinlight.controlphone/files/commonnum.db";
    private static String address = "未知号码";

    /**
     * 组数据 和 每组总child的数据
     */
    public static List<Group> getGroup() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor1 = db.query("classlist", new String[]{"name", "idx"},
                null, null, null, null, null);
        ArrayList<Group> groups = new ArrayList<>();
        while (cursor1.moveToNext()) {
            Group group = new Group();
            String name = cursor1.getString(0);
            String idx = cursor1.getString(1);
            group.name = name;
            group.idx = idx;
            groups.add(group);
        }

        for (Group group : groups) {
            List<Child> children = getChild(group.idx);
            group.children = children;
        }

        db.close();

        return groups;
    }

    /**
     * 获取每个孩子节点中的数据
     */
    public static List<Child> getChild(String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        ArrayList<Child> children = new ArrayList<>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            String id = cursor.getString(0);
            String number = cursor.getString(1);
            String name = cursor.getString(2);
            child.id = id;
            child.name = name;
            child.number = number;
            children.add(child);
        }

        return children;
    }

    public static class Group {
        public String name;
        public String idx;
        public List<Child> children;
    }

    public static class Child {
        public String id;
        public String number;
        public String name;
    }

}
