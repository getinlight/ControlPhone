package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.getinlight.controlphone.R;

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
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

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
