package com.getinlight.controlphone.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by getinlight on 2018/2/20.
 */

public class SmsBackup {

    public static void backup(Context ctx, String path, CallBack callback) {
        File file = new File(path);
        FileOutputStream fos = null;
        Cursor cursor = null;
        try {

            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            fos = new FileOutputStream(file);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "smss");
            int max = cursor.getCount();
            if (callback != null) {
                callback.setMax(max);
            }


            int index = 0;

            while (cursor.moveToNext()) {

                serializer.startTag(null, "sms");
                serializer.startTag(null, "address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null, "address");
                serializer.startTag(null, "date");
                serializer.text(cursor.getString(1));
                serializer.endTag(null, "date");
                serializer.startTag(null, "type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null, "type");
                serializer.startTag(null, "body");
                serializer.text(cursor.getString(3));
                serializer.endTag(null, "body");
                serializer.endTag(null, "sms");

                Thread.sleep(500);

                index++;
                //progressdialog 可以在子线程中更新进度条的改变
                if (callback != null) {
                    callback.setProgress(index);
                }

            }

            serializer.endTag(null, "smss");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //回调
    //1.定义一个借口
    //2.定义借口中未实现的业务逻辑方法
    //3.传递一个实现了此借口的类对象, 接口的实现类一定实现了接口方法
    //4.获取传递进来的对象, 在合适的地方调用

    public interface CallBack {
        //设置短信总数
        public void setMax(int max);
        //备份过程中百分比
        public void setProgress(int index);
    }
}
