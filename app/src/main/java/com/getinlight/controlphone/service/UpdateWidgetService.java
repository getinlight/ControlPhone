package com.getinlight.controlphone.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
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

    @Override
    public void onCreate() {
        super.onCreate();
        //管理内存总数和可用进程数(定时器)
        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer();
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
        ComponentName componentName = new ComponentName(this, MyWidget.class);
        aWM.updateAppWidget(componentName, remoteViews);
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
