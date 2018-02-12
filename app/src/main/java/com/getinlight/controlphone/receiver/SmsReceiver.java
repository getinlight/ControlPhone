package com.getinlight.controlphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.getinlight.controlphone.R;
import com.getinlight.controlphone.service.LocationService;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;

/**
 * Created by getinlight on 2018/2/11.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //1.判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            //2.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")) {
                    //播放音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (messageBody.contains("#*location*#")) {
                    //开启服务
                    Intent i = new Intent(context, LocationService.class);
                    context.startService(i);
                }
            }
        }
    }
}
