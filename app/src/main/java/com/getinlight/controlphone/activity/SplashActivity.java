package com.getinlight.controlphone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.getinlight.controlphone.R;
import com.getinlight.controlphone.utils.ConstantValue;
import com.getinlight.controlphone.utils.SpUtil;
import com.getinlight.controlphone.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SplashActivity";

    private static final int UPDATE_VERSION = 1;
    private static final int ENTER_HOME = 2;

    private TextView tv_version_name;
    private String mVersionDes;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:  //进入应用程序主界面
                    enterHome();
                    break;
                default:
                    break;
            }

        }
    };
    private String mDownloadUrl;
    private RelativeLayout rl_root;

    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //下载apk
                downloadApk();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {  //取消
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void downloadApk() {
        //apk下载地址
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mobilesafe74.apk";
            HttpUtils http = new HttpUtils();
            http.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.d(TAG, "onSuccess: 下载成功");
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.d(TAG, "onSuccess: 下载失败");
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Log.d(TAG, "onStart: 开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.d(TAG, "onLoading: "+current/total);
                }
            });

        }
    }

    private void installApk(File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除activity头第一种方式
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        initUI();
        initData();
        //初始化动画
        initAnimation();
        initDB("address.db");
    }

    private void initDB(String name) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            File file = getFilesDir();
            File dbFile = new File(file, name);
            if (!dbFile.exists()) {
                inputStream = getAssets().open(name);
                fileOutputStream = new FileOutputStream(dbFile);
                byte[] bs = new byte[1024];
                int temp = -1;
                while ((temp = inputStream.read(bs)) != -1) {
                    fileOutputStream.write(bs, 0, temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if(inputStream!=null && fileOutputStream!=null){
                    inputStream.close();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }


    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            tv_version_name.setText("版本名称: "+packageInfo.versionName);

            if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
                //监测更新
                int mVersionCode = packageInfo.versionCode;
                checkVersion(mVersionCode);
            } else {
                mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void checkVersion(int mVersionCode) {
        new Thread(){

            @Override
            public void run() {
                Message msg = new Message();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    connection.setRequestMethod("GET");

                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.streamToString(is);
                        Log.d(TAG, "run: "+json);
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDescription");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        if (mVersionCode < Integer.parseInt(versionName)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    if (duration < 4000) {
                        try {
                            Thread.sleep(4000 - duration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }

            }
        }.start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

    }

    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
        rl_root = findViewById(R.id.rl_root);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
