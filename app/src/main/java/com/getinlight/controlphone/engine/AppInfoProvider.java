package com.getinlight.controlphone.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.getinlight.controlphone.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by getinlight on 2018/3/4.
 */

public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context ctx) {
        //1.包的管理者对象
        PackageManager pm = ctx.getPackageManager();
        //2.获取安装在手机上应用相关信息的集合
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        ArrayList<AppInfo> appInfoList = new ArrayList<>();
        //3.循环遍历应用信息的集合
        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageInfo.packageName);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.setName(applicationInfo.loadLabel(pm).toString());
            appInfo.setIcon(applicationInfo.loadIcon(pm));
            //判断是否为系统应用  每一个手机上的应用对应的flag都不一致
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                appInfo.setSystem(true);
            } else {
                appInfo.setSystem(false);
            }

            //判断是否是sd卡中安装的应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                appInfo.setSdCard(true);
            } else {
                appInfo.setSdCard(false);
            }

            appInfoList.add(appInfo);
        }

        return appInfoList;
    }
}
