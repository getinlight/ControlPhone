package com.getinlight.controlphone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getinlight.controlphone.R;

/**
 * Created by getinlight on 2018/1/30.
 */

public class SettingItemView extends RelativeLayout {

    private CheckBox cb_box;
    private TextView tv_des;
    private TextView tv_title;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_item_view, this);

//        View view = View.inflate(context, R.layout.setting_item_view, this);
//        this.addView(view);

        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_update);


    }

    public boolean isChecked() {
        return cb_box.isChecked();
    }

    public void setChecked(boolean checked) {
        cb_box.setChecked(checked);
        if (checked) {
            tv_des.setText("自动更新已开启");
        } else {
            tv_des.setText("自动更新已关闭");
        }
    }

}
