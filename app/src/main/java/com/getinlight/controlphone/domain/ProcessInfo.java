package com.getinlight.controlphone.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by getinlight on 2018/3/4.
 */

public class ProcessInfo {
    public String name;
    public Drawable icon;
    public long memSize;
    public boolean isCheck;
    public boolean isSystem;
    public String packageName;//如果应用没有名称, 用包名作为名称
}
