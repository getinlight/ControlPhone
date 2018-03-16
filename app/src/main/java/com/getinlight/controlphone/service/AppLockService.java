package com.getinlight.controlphone.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.getinlight.controlphone.db.dao.AppLockDao;

import java.util.List;

public class AppLockService extends Service {

    private boolean isWatch;
    private AppLockDao dao;
    private List<String> allPackageName;

    @Override
    public void onCreate() {
        super.onCreate();

        dao = AppLockDao.getInstance(this);
        allPackageName = dao.findAll();
        //维护一个看门狗的死循环
        isWatch = true;
        watch();
    }

    private void watch() {
        //1.在子线程中开启一个可控的死循环
        new Thread() {
            @Override
            public void run() {
                while (isWatch) {
                    //2.监测现在正在开启的应用, 任务栈
                    //3.获取activity的管理者对象
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //4.获取正在开启应用的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //5.获取栈顶的activity, 获取包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //6.检查包名是否存在
                    if (allPackageName.contains(packageName)) {
                        //弹出拦截界面
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packagename", packageName);
                        startActivity(intent);
                    }

                    //睡眠一下
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
