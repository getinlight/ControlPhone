package com.getinlight.controlphone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

public class ToastLocationActivity extends Activity {

    private ImageView iv_drag;
    private Button btn_top;
    private Button btn_bottom;
    private long startTime;
    public static final String TAG = "ToastLocationActivity";

    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }

    private void initUI() {

        iv_drag = findViewById(R.id.iv_drag);
        btn_top = findViewById(R.id.btn_top);
        btn_bottom = findViewById(R.id.btn_bottom);

        WindowManager mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        int screenHeight = mWM.getDefaultDisplay().getHeight();
        int screenWidth = mWM.getDefaultDisplay().getWidth();

        int locationX = SpUtil.getInt(getApplication(), ConstantValue.LOCATION_X, 0);
        int locationY = SpUtil.getInt(getApplication(), ConstantValue.LOCATION_Y, 0);

        Log.i(TAG, "onClick: 要显示的X"+locationX);
        Log.i(TAG, "onClick: 要显示的Y"+locationY);

        //左上角坐标
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = locationX;
        params.topMargin = locationY;

        if (locationY > (screenHeight * 0.5 - 22 - 30)) {
            btn_top.setVisibility(View.VISIBLE);
            btn_bottom.setVisibility(View.INVISIBLE);
        } else {
            btn_top.setVisibility(View.INVISIBLE);
            btn_bottom.setVisibility(View.VISIBLE);
        }

        iv_drag.setLayoutParams(params);


        //监听某一个控件的拖拽过程
        iv_drag.setOnTouchListener(new View.OnTouchListener() {

            private float startY;
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();

                        float disX = moveX - startX;
                        float disY = moveY - startY;

                        float left = iv_drag.getLeft()+disX;
                        float top = iv_drag.getTop()+disY;
                        float right = iv_drag.getRight()+disX;
                        float bottom = iv_drag.getBottom()+disY;

                        //容错处理
                        if (left < 0) {
                            return true;
                        }

                        if (right > screenWidth) {
                            return true;
                        }

                        if (top < 0) {
                            return true;
                        }

                        if (bottom > screenHeight - 22 - 30) {
                            return true;
                        }

                        if (top > (screenHeight * 0.5 - 22 - 30)) {
                            btn_top.setVisibility(View.VISIBLE);
                            btn_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            btn_top.setVisibility(View.INVISIBLE);
                            btn_bottom.setVisibility(View.VISIBLE);
                        }

                        iv_drag.layout((int)left, (int)top, (int)right, (int)bottom);
                        //重置一次起始坐标
                        startX = event.getRawX();
                        startY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplication(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                        SpUtil.putInt(getApplication(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                        break;
                    default:
                        break;

                }
                //既要响应点击事件, 又要响应拖拽事件, 则次返回值结果需要返回false
                return false;
            }
        });

        iv_drag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length-1]-mHits[0] < 500) {
//                    int left = (int) ((screenWidth - iv_drag.getWidth()) * 0.5);
//                    int right = (int) ((screenWidth + iv_drag.getWidth()) * 0.5);
//                    int top = (int) ((screenHeight  - iv_drag.getHeight()) * 0.5);
//                    int bottom = (int) ((screenHeight  + iv_drag.getHeight()) * 0.5);
//                    iv_drag.layout(left, top, right, bottom);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_drag.getLayoutParams();
                    layoutParams.leftMargin = (int) ((screenWidth - iv_drag.getWidth()) * 0.5);
                    layoutParams.topMargin = (int) ((screenHeight - iv_drag.getHeight()) * 0.5);
                    iv_drag.setLayoutParams(layoutParams);

                    SpUtil.putInt(getApplication(), ConstantValue.LOCATION_X, layoutParams.leftMargin);
                    SpUtil.putInt(getApplication(), ConstantValue.LOCATION_Y, layoutParams.topMargin);

                    Log.i(TAG, "onClick: 要存储的X"+iv_drag.getLeft());
                    Log.i(TAG, "onClick: 要存储的Y"+iv_drag.getTop());
                }
            }
        });

    }

}
