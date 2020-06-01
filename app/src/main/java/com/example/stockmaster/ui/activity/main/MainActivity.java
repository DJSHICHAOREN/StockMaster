package com.example.stockmaster.ui.activity.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.stockmaster.R;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.adapter.MonitorPanelAdapter;
import com.example.stockmaster.util.StockManager;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


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

        // 载入股票列表
        StockManager.initStockManager();

        // 开启service
        Intent intent = new Intent(this, BrainService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



}

