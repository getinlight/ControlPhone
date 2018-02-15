package com.getinlight.controlphone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getinlight.controlphone.R;

/**
 * Created by getinlight on 2018/2/11.
 */

public class SettingClickView extends RelativeLayout {

    private TextView tv_title;
    private TextView tv_desc;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_setting_click, this);

        tv_title = findViewById(R.id.tv_title);
        tv_desc = findViewById(R.id.tv_des);

    }

    public void setTitle(String text) {
        tv_title.setText(text);
    }

    public void setDesc(String text) {
        tv_desc.setText(text);
    }
}
