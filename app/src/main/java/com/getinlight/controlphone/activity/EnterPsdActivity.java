package com.getinlight.controlphone.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ToastUtil;

public class EnterPsdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_psd);
        //获取包名
        String packagename = getIntent().getStringExtra("packagename");

        TextView tv_app_name = findViewById(R.id.tv_app_name);
        ImageView iv_app_icon = findViewById(R.id.iv_app_icon);
        EditText et_psd = findViewById(R.id.et_psd);
        Button bt_submit = findViewById(R.id.bt_submit);

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            Drawable drawable = applicationInfo.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(drawable);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if (!TextUtils.isEmpty(psd)) {
                    if (psd.equals("123")) {
                        finish();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

    }
}
