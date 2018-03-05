package com.getinlight.controlphone.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by getinlight on 2018/3/4.
 */

public class ProcessInfoProvider {
    //获取进程总数
    public static int getProcessCount(Context ctx) {
        //1.获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.返回集合总数
        return runningAppProcesses.size();
    }

    public static long getAvailSpace(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    public static long getTotalSpace(Context ctx) {
//        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);
//        return memoryInfo.totalMem;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineone = bufferedReader.readLine();
            char[] chars = lineone.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString())*1028;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

    public static List<ProcessInfo> getProcessInfoList(Context ctx) {
        ArrayList<ProcessInfo> processInfoList = new ArrayList<>();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        //2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.循环遍历上述集合, 回去进程相关信息
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //4.获取京城的名称 => 应用的包名
            processInfo.packageName = info.processName;
            //5.获取进程占用的内存大小
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //6.返回数组中索引位置为0的对象, 为当前进程的内存信息对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //7.获取已使用的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //8.获取应用的名称
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                //9.获取应用的图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //10.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.name = processInfo.packageName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.lock);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }

        return processInfoList;
    }

    public static void killProcess(Context ctx, ProcessInfo info) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(info.packageName);
    }

    public static void killAll(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            if (info.processName.equals(ctx.getPackageName())) {
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }
}
