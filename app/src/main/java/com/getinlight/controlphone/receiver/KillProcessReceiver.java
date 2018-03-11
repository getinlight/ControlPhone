package com.getinlight.controlphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getinlight.controlphone.engine.ProcessInfoProvider;

/**
 * Created by getinlight on 2018/3/11.
 */

public class KillProcessReceiver extends BroadcastReceiver {

    private static final String TAG = "KillProcessReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 清理进程");
        ProcessInfoProvider.killAll(context);
    }
}
