package com.getinlight.controlphone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.engine.SmsBackup;

import java.io.File;

public class ToolActivity extends AppCompatActivity {

    private TextView tv_address;
    private TextView tv_sms_backup;
//    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        initPhoneAddress();
        initSmsBackup();
        initCommonNumberQuery();
        initAppLock();
    }

    private void initAppLock() {
        TextView tv_app_lock = findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        TextView tv_common_number_query = findViewById(R.id.tv_common_number_query);
        tv_common_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
            }
        });
    }

    private void initPhoneAddress() {
        tv_address = findViewById(R.id.tv_query_address);
//        pb_bar = findViewById(R.id.pb_bar);
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QureyAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSmsBackup() {
        tv_sms_backup = findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackupDialog();
            }
        });
    }

    private void showSmsBackupDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.drawable.lock);
        dialog.setTitle("短信备份");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        //短信的获取
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sms74.xml";
                SmsBackup.backup(getApplicationContext(), path, new SmsBackup.CallBack() {
                    @Override
                    public void setMax(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        dialog.setProgress(index);
                    }

                    @Override
                    public void dismiss() {
                        dialog.dismiss();
                    }
                });
            }
        }.start();
    }

}
