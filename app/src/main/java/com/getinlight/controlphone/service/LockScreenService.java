package com.getinlight.controlphone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.getinlight.controlphone.engine.ProcessInfoProvider;

public class LockScreenService extends Service {

    private IntentFilter intentFilter;
    private InnerReceiver innerReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理进程
            ProcessInfoProvider.killAll(getApplicationContext());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
