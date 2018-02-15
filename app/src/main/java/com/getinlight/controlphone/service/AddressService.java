package com.getinlight.controlphone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.engine.AddressDao;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

/**
 * 来电提醒服务
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWN;
    private View mViewToast;

    @Override
    public void onCreate() {
        super.onCreate();

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(receiver);
    }

    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = AddressDao.getAddress(number);
            showToast(address);
        }
    }

    private void showToast(String text) {
        mWN = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //制定吐司位置
        params.gravity = Gravity.LEFT+Gravity.TOP;
        params.setTitle("Toast");

        //吐司显示效果
        mViewToast = View.inflate(this, R.layout.toast_address, null);
        int[] bgs = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        int style = SpUtil.getInt(this, ConstantValue.ADDRESS_STYLE, 0);
        mViewToast.setBackgroundResource(bgs[style]);
        TextView tv = mViewToast.findViewById(R.id.tv_number);
        tv.setText(text);
        mWN.addView(mViewToast, params);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("电话铃声响了...");
                    String address = AddressDao.getAddress(incomingNumber);
//                    ToastUtil.show(AddressService.this, address);
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWN != null && mViewToast != null) {
                        mWN.removeView(mViewToast);
                        mViewToast = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
