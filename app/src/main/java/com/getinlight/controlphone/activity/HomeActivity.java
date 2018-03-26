package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.MD5Util;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.utils.ToastUtil;

public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;
    private String[] mTitles;
    private int[] mImgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gv_home = findViewById(R.id.gv_home);
        initData();
    }

    private void initData() {
        mTitles = new String[]{
                "手机防盗","通信卫士","软件管理",
                "进程管理", "流量统计","手机杀毒",
                "缓存清理", "高级工具", "设置中心"
        };

        mImgs = new int[]{
                R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
                R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
        };

        gv_home.setAdapter(new MyAdapter());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        Intent intent1 = new Intent(getApplicationContext(), BlackNumberActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(getApplicationContext(), AppManagerActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(getApplicationContext(), ProcessManagerActivity.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(getApplicationContext(), TrafficActivity.class);
                        startActivity(intent4);
                        break;
                    case 5:
                        Intent intent5 = new Intent(getApplicationContext(), AntiVirusActivity.class);
                        startActivity(intent5);
                    case 6:
                        Intent intent6 = new Intent(getApplicationContext(), BaseCacheCleanActivity.class);
                        startActivity(intent6);
                        break;
                    case 7:
                        Intent intent7 = new Intent(getApplicationContext(), ToolActivity.class);
                        startActivity(intent7);
                        break;
                    case 8:
                        Intent intent8 = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent8);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void showDialog() {

        String pwd = SpUtil.getString(this, ConstantValue.PWD, "");
        if (TextUtils.isEmpty(pwd)) {  //1.初始设置密码框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.create();

            View view = View.inflate(this, R.layout.dialog_set_pwd, null);

            Button confirmBtn = view.findViewById(R.id.btn_confirm);
            Button cancelBtn = view.findViewById(R.id.btn_cancel);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et_pwd = view.findViewById(R.id.et_set_pwd);
                    EditText et_confirmPwd = view.findViewById(R.id.et_confirm_pwd);
                    if (TextUtils.isEmpty(et_pwd.getText().toString()) || TextUtils.isEmpty(et_confirmPwd.getText().toString())) {
                        ToastUtil.show(getApplicationContext(), "请输入密码");
                    } else {
                        if (et_pwd.getText().toString().equals(et_confirmPwd.getText().toString())) {
                            alertDialog.dismiss();
                            SpUtil.putString(getApplicationContext(), ConstantValue.PWD, MD5Util.encode(et_pwd.getText().toString()));
//                            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                            Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.show(getApplicationContext(), "输入密码有误");
                        }
                    }
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

//            alertDialog.setView(view);
            alertDialog.setView(view, 0,0,0,0);
            alertDialog.show();

        } else {  //2.确认密码框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.create();

            View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);

            Button confirmBtn = view.findViewById(R.id.btn_confirm);
            Button cancelBtn = view.findViewById(R.id.btn_cancel);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et_pwd = view.findViewById(R.id.et_set_pwd);
                    if (TextUtils.isEmpty(et_pwd.getText().toString())) {
                        ToastUtil.show(getApplicationContext(), "请输入密码");
                    } else {
                        if (MD5Util.encode(et_pwd.getText().toString()).equals(SpUtil.getString(getApplicationContext(), ConstantValue.PWD, ""))) {
                            alertDialog.dismiss();
//                            Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                            Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
                            startActivity(intent);
                        } else {//错误
                            ToastUtil.show(getApplicationContext(), "输入密码有误");
                        }
                    }

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            //为了兼容低版本 内边距为0
            alertDialog.setView(view, 0,0,0,0);
//            alertDialog.setView(view);
            alertDialog.show();
        }

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mImgs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            TextView tv_title = view.findViewById(R.id.tv_title);
            iv_icon.setBackgroundResource(mImgs[position]);
            tv_title.setText(mTitles[position]);
            return view;
        }
    }
}
