package com.getinlight.controlphone.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by getinlight on 2018/2/11.
 */

public class ServiceStatusUtil {
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            String className = info.service.getClassName();
            if (className.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
