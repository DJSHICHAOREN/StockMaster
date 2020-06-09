package com.example.stockmaster.ui.activity.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.example.stockmaster.R;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.adapter.MonitorPanelAdapter;
import com.example.stockmaster.util.StockManager;
import com.google.android.material.tabs.TabLayout;

import java.io.File;

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

        getSDCardPath();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static String getSDCardPath() {
        String SDPATH = Environment.getExternalStorageDirectory() + "/" + "sme_lwd";
        File out = new File(SDPATH);
        boolean res = false;
        if (!out.exists()) {
            res = out.mkdirs();
        }
        return SDPATH;
    }

}

