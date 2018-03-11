package com.getinlight.controlphone.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.engine.ProcessInfoProvider;
import com.getinlight.controlphone.receiver.MyWidget;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private static final String TAG = "UpdateWidgetService";
    private Timer timer;
    private InnerReceiver innerReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册开锁和解锁的广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);

        //管理内存总数和可用进程数(定时器)
        startTimer();
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startTimer();
            } else {
                stopTimer();
            }
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //ui定时刷新
                updateAppWidget();
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        Log.d(TAG, "updateAppWidget: ");
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数: " + ProcessInfoProvider.getProcessCount(this));
        String avaStr = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存: " + avaStr);

        //点击窗体小部件 进入应用
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        //通过延期意图发送广播, 在广播接受者中杀死进程
        Intent intent1 = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcastIntent);

        ComponentName componentName = new ComponentName(this, MyWidget.class);
        aWM.updateAppWidget(componentName, remoteViews);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
        stopTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
