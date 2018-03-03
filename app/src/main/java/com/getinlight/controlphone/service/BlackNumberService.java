package com.getinlight.controlphone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.getinlight.controlphone.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

import static android.app.admin.DevicePolicyManager.WIPE_RESET_PROTECTION_DATA;

public class BlackNumberService extends Service {

    private InnerSmsReceiver receiver;
    private BlackNumberDao dao;

    private TelephonyManager tm;
    private BlackNumberService.MyListener listener;
    private MyContentObserver myContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        receiver = new InnerSmsReceiver();
        registerReceiver(receiver, filter);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new BlackNumberService.MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (myContentObserver != null) {
            getContentResolver().unregisterContentObserver(myContentObserver);
        }
        if (listener != null) {
            tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容, 获取发送短信的号码
            //2.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                dao = BlackNumberDao.getInstance(context);
                int mode = dao.getMode(originatingAddress);
                if (mode == 1 || mode == 3) {
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //挂断电话 aidl文件
                    endCall(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    break;
                default:
                    break;
            }
        }
    }

    private void endCall(String incomingNumber) {
        if (dao == null) {
            dao = BlackNumberDao.getInstance(this);
        }
        int mode = dao.getMode(incomingNumber);
        if (mode == 2 || mode == 3) {

            try {
                //1.获取ServiceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3.反射调用此方法
                IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4.调用获取aidl文件方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //删除通信记录
            //通过内容观察者, 观察数据库的变化 通过内容观察者, 观察数据库的变化
            myContentObserver = new MyContentObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),
                    true, myContentObserver);

            //6.删除此被拦截电话号码的通信记录
//            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{incomingNumber});

        }

    }

    class MyContentObserver extends ContentObserver {

        private String phone;

        public MyContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //内容解析器
            getContentResolver().delete(Uri.parse("content://call_log/calls"),
                    "number = ?", new String[]{phone});
        }
    }
}
