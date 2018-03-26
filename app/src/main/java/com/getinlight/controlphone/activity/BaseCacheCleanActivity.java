package com.getinlight.controlphone.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.getinlight.controlphone.R;

public class BaseCacheCleanActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clean);

        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("clear_sdcard").setIndicator("SD卡清理");

        tab1.setContent(new Intent(this, CacheCleanActivity.class));
        tab2.setContent(new Intent(this, SDCacheCleanActivity.class));

        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
