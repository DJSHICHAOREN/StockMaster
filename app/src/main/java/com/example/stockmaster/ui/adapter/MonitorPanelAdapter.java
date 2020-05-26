package com.example.stockmaster.ui.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.stockmaster.ui.fragment.price_monitor.PriceMonitorFragment;
import com.example.stockmaster.ui.fragment.stock_monitor.StockMonitorFragment;

public class MonitorPanelAdapter extends FragmentPagerAdapter {
    public MonitorPanelAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new PriceMonitorFragment();
            }
            case 1:{
                return new StockMonitorFragment();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:{
                return "价格监控";
            }
            case 1:{
                return "股票监控";
            }
        }
        return "";
    }
}
