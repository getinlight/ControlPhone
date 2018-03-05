package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.domain.ProcessInfo;
import com.getinlight.controlphone.engine.ProcessInfoProvider;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv_process_list;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private ProcessInfo mCurrentProcessInfo;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mSystemList = new ArrayList<ProcessInfo>();
            mCustomerList = new ArrayList<ProcessInfo>();
            for (ProcessInfo info : mProcessInfoList) {
                if (info.isSystem) {
                    mSystemList.add(info);
                } else {
                    mCustomerList.add(info);
                }
            }
            myAdapter = new MyAdapter();
            lv_process_list.setAdapter(myAdapter);
        }
    };
    private TextView tv_des;
    private MyAdapter myAdapter;

    class MyAdapter extends BaseAdapter {

        //获取数据适配器中条目类型的总数, 修改成两种(纯文字, 图片+文字)
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        //指定索引指向的条目类型, 条目类型状态码指定(0复用类型 1)
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //返回0 代表纯文本状态码
                return 0;
            } else {
                //返回1 代表图片和文字
                return 1;
            }
        }

        @Override
        public int getCount() {
            if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)) {
                return mCustomerList.size() + 1;
            } else {
                return mCustomerList.size() + mSystemList.size() + 2;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return null;
            }
            if (position < mCustomerList.size() + 1) {
                return mCustomerList.get(position - 1);
            } else {
                return mSystemList.get(position - mCustomerList.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                //展示灰色条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户进程("+mCustomerList.size()+")");
                } else {
                    holder.tv_title.setText("系统进程("+mSystemList.size()+")");
                }

                return convertView;
            } else {
                //展示appinfo
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (getItem(position).getIcon() != null) {
                    holder.iv_icon.setBackground(getItem(position).getIcon());
                }

                holder.tv_name.setText(getItem(position).getName());
                holder.tv_memory_info.setText("内存占用: "+Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize));

                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }

                holder.cb_box.setChecked(getItem(position).isCheck);

                return convertView;
            }

        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        TextView tv_process_count = findViewById(R.id.tv_process_count);
        TextView tv_memory_info = findViewById(R.id.tv_memory_info);
        tv_des = findViewById(R.id.tv_des);
        lv_process_list = findViewById(R.id.lv_process_list);

        tv_process_count.setText("进程总数: "+ ProcessInfoProvider.getProcessCount(this));

        long availSpace = ProcessInfoProvider.getAvailSpace(this);
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);

        String availSpaceStr = Formatter.formatFileSize(this, availSpace);
        String totalSpaceStr = Formatter.formatFileSize(this, totalSpace);

        tv_memory_info.setText("剩余/总和: "+availSpaceStr+"/"+totalSpaceStr);

        Button bt_all = findViewById(R.id.bt_all);
        Button bt_reverse = findViewById(R.id.bt_reverse);
        Button bt_clear = findViewById(R.id.bt_clear);
        Button bt_setting = findViewById(R.id.bt_setting);

        bt_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        initListData();
    }

    private void initListData() {
        new Thread(){
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfoList(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }.start();

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中的view就是listview对象
                //firstVisibleItem 第一个可见条目
                //visibleItemCount 当前屏幕上可见条目
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size()+1) {
                        //滚动到了系统条目
                        tv_des.setText("系统应用("+mSystemList.size()+")");
                    } else {
                        //滚动到了用户条目
                        tv_des.setText("用户应用("+mCustomerList.size()+")");
                    }
                }

            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size()+1) {
                        mCurrentProcessInfo = mCustomerList.get(position - 1);
                    } else {
                        mCurrentProcessInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    if (mCurrentProcessInfo != null && !mCurrentProcessInfo.packageName.equals(getPackageName())) {
                        mCurrentProcessInfo.isCheck = !mCurrentProcessInfo.isCheck;
                        CheckBox cb_box = view.findViewById(R.id.cb_box);
                        cb_box.setChecked(mCurrentProcessInfo.isCheck);
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_all:
                selectAll();
                break;
            case R.id.bt_reverse:
                selectReverse();
                break;
            case R.id.bt_setting:
                setting();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            default:
                break;
        }
    }

    private void setting() {
//        startActivity(new Intent(this, ProcessSettingActivity.class));
        startActivityForResult(new Intent(this, ProcessSettingActivity.class), 0);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        myAdapter.notifyDataSetChanged();
    }

    private void clearAll() {
        ArrayList<ProcessInfo> deleteList = new ArrayList<>();

        for (ProcessInfo info : mProcessInfoList) {
            if (info.packageName.equals(getPackageName())){
                continue;
            }
            if (info.isCheck) {
                deleteList.add(info);
            }
        }

        for (ProcessInfo info : deleteList) {
            mProcessInfoList.remove(info);
            ProcessInfoProvider.killProcess(this, info);
        }

        new Thread(){
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfoList(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }.start();

        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void selectReverse() {
        for (ProcessInfo info : mProcessInfoList) {
            if (info.packageName.equals(getPackageName())){
                continue;
            }
            info.isCheck = !info.isCheck;
        }

        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }


    }

    private void selectAll() {
        for (ProcessInfo info : mProcessInfoList) {
            if (info.packageName.equals(getPackageName())){
                continue;
            }
            info.isCheck = true;
        }

        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }
}
