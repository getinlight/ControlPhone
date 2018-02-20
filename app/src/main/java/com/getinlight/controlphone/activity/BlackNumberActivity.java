package com.getinlight.controlphone.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.db.dao.BlackNumberDao;
import com.getinlight.controlphone.db.dao.BlackNumberInfo;

import java.util.List;

//1.复用convertView
//2.对findviewbyid次数的优化, 使用viewholder
//3.将viewholder定义成静态, 不会创建多个对象
//4.listview如果有多个条目的时候, 可以做分页处理, 每一次加载20条 , 逆序返回
public class BlackNumberActivity extends AppCompatActivity {

    private Button btn_add;
    private ListView lv_blacknumber;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> infos;
    private MyAdapter myAdapter;
    //防止加载的过程中又加载
    private boolean isLoad = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (myAdapter == null) {
                myAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(myAdapter);
            } else {
                myAdapter.notifyDataSetChanged();
            }
        }
    };
    private int mCount;

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//            } else {
//                view = convertView;
//            }
            //服用convertView
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                //对findviewbyid次数优化, 使用viewholder
                //2.减少findviewbyid调用次数
                holder = new ViewHolder();
                holder.tv_phone = convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BlackNumberInfo info = infos.get(position);

            switch (Integer.parseInt(info.getMode())) {
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
                default:
                    break;
            }

            holder.tv_phone.setText(info.getPhone());
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.数据库删除
                    dao.delete(info.getPhone());
                    //2.集合中删除
                    infos.remove(position);
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        lv_blacknumber = findViewById(R.id.lv_blacknumber);
        //监听listview的滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (infos == null) {
                    return;
                }

                if (infos.size() > mCount) {
                    return;
                }

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && lv_blacknumber.getLastVisiblePosition() >= infos.size() - 1
                        && !isLoad) {
                    List<BlackNumberInfo> more = dao.find(infos.size());
                    infos.addAll(more);
                    mHandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        dao = BlackNumberDao.getInstance(getApplicationContext());
        infos = dao.find(0);
        mCount = dao.getCount();
        mHandler.sendEmptyMessage(0);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        alertDialog.setView(view);
        alertDialog.show();
    }
}
