package com.example.stockmaster.ui.activity.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.activity.recommand.RecommandActivity;
import com.example.stockmaster.ui.adapter.MonitorPanelAdapter;
import com.example.stockmaster.ui.adapter.StockListAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.vp_monitor_panel)
    ViewPager vp_monitor_panel;
    @BindView(R.id.tab_layout)
    TabLayout tab_layout;

    MonitorPanelAdapter monitorPanelAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 设置viewPager的tabLayout
        tab_layout.setupWithViewPager(vp_monitor_panel);
        // 设置viewPager的adapter
        monitorPanelAdapter = new MonitorPanelAdapter(getSupportFragmentManager());
        vp_monitor_panel.setAdapter(monitorPanelAdapter);

        // 开启service
        Intent intent = new Intent(this, BrainService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



}

