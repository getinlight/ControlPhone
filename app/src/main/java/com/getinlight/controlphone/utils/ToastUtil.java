package com.getinlight.controlphone.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by getinlight on 2018/1/26.
 */

public class ToastUtil {

    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
