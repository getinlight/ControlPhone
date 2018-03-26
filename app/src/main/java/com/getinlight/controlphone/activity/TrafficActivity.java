package com.getinlight.controlphone.activity;

import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.getinlight.controlphone.R;

public class TrafficActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        long mobileTxBytes = TrafficStats.getMobileTxBytes();

        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        int x = 10/0;

    }
}
