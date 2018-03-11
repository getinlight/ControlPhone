package com.getinlight.controlphone.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.service.UpdateWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    private static final String TAG = "MyWidget";

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: 创建第一个app小窗体");
        //开启服务(onCreate)
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: 创建多一个app小窗体");
        //开启服务
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d(TAG, "onAppWidgetOptionsChanged: 当窗体小部件宽高发生改变的时候调用此方法, 创建小部件的时候也调用此方法");
        //开启服务
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //关闭服务
        Log.d(TAG, "onDeleted: 当删除一个小部件调用此方法");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled: 移除最后一个app小窗体");
        context.stopService(new Intent(context, UpdateWidgetService.class));
    }
}

