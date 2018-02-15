package com.getinlight.controlphone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

public class ToastLocationActivity extends Activity {

    private ImageView iv_drag;
    private Button btn_top;
    private Button btn_bottom;

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

                }

                return true;
            }
        });

    }

}
