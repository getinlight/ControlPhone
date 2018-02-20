package com.getinlight.controlphone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.engine.SmsBackup;

import java.io.File;

public class ToolActivity extends AppCompatActivity {

    private TextView tv_address;
    private TextView tv_sms_backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        initPhoneAddress();
        initSmsBackup();
    }

    private void initPhoneAddress() {
        tv_address = findViewById(R.id.tv_query_address);
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
                SmsBackup.backup(getApplicationContext(), path, dialog);
            }
        }.start();
    }

}
