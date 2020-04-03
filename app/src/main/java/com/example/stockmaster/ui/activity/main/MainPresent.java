package com.example.stockmaster.ui.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPresent extends BasePresent {
    private MainActivity mMainActivity;
    private MainActivity.MainActivityUIManager mMainActivityUIManager;
    private StockManager mStockManager;
    private SinaDataQueryer mSinaDataQueryer;
    private StockAnalyser mStockAnalyser;
    private Timer timer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    Log.d("lwd","获取今天股票数据");
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    for(final String stockId : stockIdList) {
                        new Thread() {
                            @Override
                            public void run() {
                                mSinaDataQueryer.queryStocksTodayPrice(stockId);
                                mSinaDataQueryer.queryStocksFiveDayAvgPrice(stockId);
                            }
                        }.start();
                    }
                    break;
                }
                case 2:{
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    String stockIdStr = "";
                    for(String stockId : stockIdList) {
                        stockIdStr = stockIdStr + "rt_" + stockId + ",";
                    }
                    mSinaDataQueryer.queryStocksNowPrice(stockIdStr);
                    mMainActivity.notifyStockListDataSetChanged();
                    break;
                }

            }
        }
    };

    public MainPresent(AppCompatActivity view, MainActivity.MainActivityUIManager mainActivityUIManager) {
        super(view);
        mMainActivity = (MainActivity)view;
        mMainActivityUIManager = mainActivityUIManager;
        // 创建工具实例
        mStockManager = new StockManager();
        mStockManager.setMainActivityUIManager(mMainActivityUIManager);
        mSinaDataQueryer = new SinaDataQueryer(mMainActivity, mStockManager);
        // 实例化股票对象
        // 请求股票数据
        ArrayList<String> stockIdList = new ArrayList<String>(Arrays.asList("hk02400", "hk06060", "hk09969"));
        mStockManager.createStocks(stockIdList);
    }

    public List<Stock> getStockList(){
        return mStockManager.getStockList();
    }

    public void beginQueryAndAnalyse(){
        // 获取从开盘到现在的股票数据
        Message todayPriceMessage = Message.obtain();
        todayPriceMessage.what = 1;
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("stockIdList", (ArrayList<String>) mStockManager.getStockIdList());
        todayPriceMessage.setData(bundle);
        handler.sendMessage(todayPriceMessage);
        // 获取每分钟的数据
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message minutePriceMessage = Message.obtain();
                minutePriceMessage.what = 2;
                minutePriceMessage.setData(bundle);
                handler.sendMessage(minutePriceMessage);
            }
        }, 0, 2000); // 1 seconds
    }




}
