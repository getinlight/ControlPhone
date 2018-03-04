package com.getinlight.controlphone.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.domain.AppInfo;
import com.getinlight.controlphone.engine.AppInfoProvider;
import com.getinlight.controlphone.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private List<AppInfo> mAppInfoList;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            myAdapter = new MyAdapter();
            lv_app_list.setAdapter(myAdapter);
            if (tv_des != null || mCustomerList != null) {
                tv_des.setText("用户应用("+mCustomerList.size()+")");
            }

        }
    };
    private ListView lv_app_list;
    private MyAdapter myAdapter;
    private ArrayList<AppInfo> mCustomerList;
    private ArrayList<AppInfo> mSystemList;
    private TextView tv_des;
    private AppInfo mCurrentAppInfo;
    private PopupWindow mPopWindow;

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
            return mCustomerList.size() + mSystemList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
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
                    holder.tv_title.setText("用户应用("+mCustomerList.size()+")");
                } else {
                    holder.tv_title.setText("系统应用("+mSystemList.size()+")");
                }

                return convertView;
            } else {
                //展示appinfo
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = convertView.findViewById(R.id.tv_name);
                    holder.tv_path = convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (getItem(position).getIcon() != null) {
                    holder.iv_icon.setBackground(getItem(position).getIcon());
                }

                holder.tv_name.setText(getItem(position).getName());
                if (getItem(position).isSdCard()) {
                    holder.tv_path.setText("SD卡应用");
                } else {
                    holder.tv_path.setText("手机应用");
                }
                return convertView;
            }

        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        lv_app_list = findViewById(R.id.lv_app_list);
        tv_des = findViewById(R.id.tv_des);
        initTitle();

        initList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新获取数据
        new Thread() {

            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem()) {
                        mSystemList.add(appInfo);
                    } else {
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initList() {

        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size()+1) {
                        mCurrentAppInfo = mCustomerList.get(position - 1);
                    } else {
                        mCurrentAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View tapView) {
        View view = View.inflate(this, R.layout.popupwindiw_layout, null);
        TextView tv_uninstall = view.findViewById(R.id.tv_uninstall);
        TextView tv_start = view.findViewById(R.id.tv_start);
        TextView tv_share = view.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //透明变成不透明
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);

        mPopWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.showAsDropDown(tapView, 200, -view.getHeight()-150);
        view.startAnimation(set);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mCurrentAppInfo.isSystem()) {
                    ToastUtil.show(this, "此应用不能卸载");
                } else {
                    Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"+mCurrentAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(mCurrentAppInfo.getPackageName());
                if (intent != null) {
                    startActivity(intent);
                } else {
                    ToastUtil.show(this, "此引用不能被开启");
                }
                break;
            case R.id.tv_share:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, "分享一个应用: "+mCurrentAppInfo.getName());
                intent1.setType("text/plain");
                startActivity(intent1);
                break;
            default:
                break;

        }

        if (mPopWindow != null) {
            mPopWindow.dismiss();
        }
    }

    private void initTitle() {
        String path = Environment.getDataDirectory().getAbsolutePath();

        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();


        String memorySize = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdSize = Formatter.formatFileSize(this, getAvailSpace(sdPath));

        TextView tv_memory = findViewById(R.id.tv_memory);
        TextView tv_sd_memory = findViewById(R.id.tv_sd_memory);

        tv_memory.setText("磁盘可用: "+memorySize);
        tv_sd_memory.setText("SD卡可用: "+sdSize);

    }

    //int代表多少G
    //ibyte = 8bit
    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
//        int count = statFs.getAvailableBlocks();//获取可用区块的个数
//        int size = statFs.getBlockSize();//获取区块的大小

        return statFs.getAvailableBytes();
    }
}
