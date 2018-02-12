package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

public class SetupOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置成功  停在完成界面
        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setup_over);

            initUI();

        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //开启新界面后  关闭功能列表界面
            finish();
        }
    }

    private void initUI() {
        TextView tv_safe_phone = findViewById(R.id.tv_safe_phone);
        tv_safe_phone.setText(SpUtil.getString(this, ConstantValue.CONTACT_PHONE, ""));

        ImageView iv_protect = findViewById(R.id.iv_protect);
        if (SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false)) {
            iv_protect.setImageResource(R.drawable.lock);
        } else {
            iv_protect.setImageResource(R.drawable.unlock);
        }

        View tv_reset = findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                //开启新界面后  关闭功能列表界面
                finish();
            }
        });


    }
}
