package com.getinlight.controlphone.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.db.dao.AppLockDao;
import com.getinlight.controlphone.domain.AppInfo;
import com.getinlight.controlphone.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {

    private Button btn_unlock;
    private Button btn_lock;
    private LinearLayout ll_unlock;
    private LinearLayout ll_lock;
    private TextView tv_unlock;
    private TextView tv_lock;
    private ListView lv_unlock;
    private ListView lv_lock;
    private List<AppInfo> mAppInfoList;
    private ArrayList<AppInfo> mLockList;
    private ArrayList<AppInfo> mUnlockList;
    private AppLockDao mLockDao;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnlockAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mLockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            mUnlockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(mUnlockAdapter);
        }
    };
    private TranslateAnimation mTranslateAnimation;


    class MyAdapter extends BaseAdapter {

        private boolean isLock;

        //用于区分已加锁和未加锁
        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            tv_unlock.setText("未加锁应用: "+mUnlockList.size());
            tv_lock.setText("已加锁应用: "+mLockList.size());
            if (isLock) {
                return mLockList.size();
            } else {
                return  mUnlockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockList.get(position);
            } else {
                return mUnlockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.iv_lock = convertView.findViewById(R.id.iv_lock);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            AppInfo appInfo = getItem(position);
            viewHolder.iv_icon.setBackgroundDrawable(appInfo.getIcon());
            viewHolder.tv_name.setText(appInfo.getName());
            if (isLock) {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.lock);
            } else {
                viewHolder.iv_lock.setBackgroundResource(R.drawable.unlock);
            }

            final View animationView = convertView;

            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加动画 阻塞动画
                    animationView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock) {  //已加锁 ---------> 未加锁
                                mLockList.remove(appInfo);
                                mUnlockList.add(appInfo);
                                //维护数据库
                                mLockDao.delete(appInfo.getPackageName());
                                //刷新数据适配器
                                mLockAdapter.notifyDataSetChanged();
                            } else {
                                mLockList.add(appInfo);
                                mUnlockList.remove(appInfo);
                                //维护数据库
                                mLockDao.insert(appInfo.getPackageName());
                                //刷新数据适配器
                                mUnlockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initUI();
        initData();
        initAnimation();
    }

    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);

    }

    private void initData() {

        new Thread(){
            @Override
            public void run() {
                //1.获取手机中的所有应用
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁和未加锁应用
                mLockList = new ArrayList<>();
                mUnlockList = new ArrayList<>();

                mLockDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mLockDao.findAll();
                for (AppInfo appInfo : mAppInfoList) {
                    if (lockPackageList.contains(appInfo.getPackageName())) {
                        mLockList.add(appInfo);
                    } else {
                        mUnlockList.add(appInfo);
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();


    }

    private void initUI() {
        btn_unlock = findViewById(R.id.btn_unlock);
        btn_lock = findViewById(R.id.btn_lock);

        ll_unlock = findViewById(R.id.ll_unlock);
        ll_lock = findViewById(R.id.ll_lock);

        tv_unlock = findViewById(R.id.tv_unlock);
        tv_lock = findViewById(R.id.tv_lock);

        lv_unlock = findViewById(R.id.lv_unlock);
        lv_lock = findViewById(R.id.lv_lock);

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                btn_lock.setBackgroundResource(R.drawable.tab_right_default);
            }
        });

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
                btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
            }
        });
    }
}
