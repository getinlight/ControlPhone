package com.getinlight.controlphone.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by getinlight on 2018/1/30.
 * 能够获取焦点的自定义textview
 */

@SuppressLint("AppCompatCustomView")
public class FocusTextView extends TextView {
    public FocusTextView(Context context) {
        super(context);
    }
    //由我们系统调用
    public FocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //布局文件中带样式文件构造方法
    public FocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
