package com.getinlight.controlphone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;

/**
 * Created by getinlight on 2018/1/30.
 */

public class SettingItemView extends RelativeLayout {

    private static final String TAG = "SettingItemView";
    private CheckBox cb_box;
    private TextView tv_des;
    private TextView tv_title;
    private String off;
    private String on;

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

//        Log.i(TAG, "initAttrs: "+attrs.getAttributeCount());
//        for (int i = 0; i < attrs.getAttributeCount(); i++) {
//            Log.i(TAG, "initAttrs: key = "+attrs.getAttributeName(i));
//            Log.i(TAG, "initAttrs: value = "+attrs.getAttributeValue(i));
//        }

        String title = attrs.getAttributeValue(ConstantValue.NAMESPACE, "desTitle");
        off = attrs.getAttributeValue(ConstantValue.NAMESPACE, "desOff");
        on = attrs.getAttributeValue(ConstantValue.NAMESPACE, "desOn");

        tv_title.setText(title);

    }

    public boolean isChecked() {
        return cb_box.isChecked();
    }

    /**
     * 设置checked
     * @param checked
     */
    public void setChecked(boolean checked) {
        cb_box.setChecked(checked);
        if (checked) {
            tv_des.setText(on);
        } else {
            tv_des.setText(off);
        }
    }

}
