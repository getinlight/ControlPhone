package com.getinlight.controlphone.global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by z on 2018/3/26.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //在获取到的未捕获的异常后处理
                e.printStackTrace();
                Log.i(TAG, "uncaughtException: "+e);

                String path = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator +"errorlog.log";
                File file = new File(path);

                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    e.printStackTrace(printWriter);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //上传公司服务器
                System.exit(0);
            }
        });
    }
}
