package com.example.stockmaster.http;

import android.content.Context;
import android.util.Log;

import com.example.stockmaster.util.StockManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络请求管理类
 * 控制请求的时机、查询数据库中是否已存在数据
 */
public class DataQueryerManager {

    private SinaDataQueryer mSinaDataQueryer;
    private ArrayList<String> mStockIdList = new ArrayList<String>(Arrays.asList("hk02400", "hk06060", "hk09969","hk00981","hk00302", "hk01055", "hk06186", "hk01610", "hk00772", "hk06855", "hk03319", "hk09916", "hk01941", "hk01873", "hk02013", "hk03331", "hk00853", "hk00777", "hk00826", "hk09928", "hk02018", "hk06919", "hk01745", "hk06185", "hk09966", "hk03759", "hk01501", "hk01300", "hk01691", "hk09922", "hk00175", "hk00589", "hk01525", "hk01347"));
    final ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();
    private StockManager mStockManager;

    public DataQueryerManager(Context context, StockManager stockManager){
        // 创建工具实例
        mStockManager = stockManager;
        mSinaDataQueryer = new SinaDataQueryer(context, mStockManager);
        // 实例化股票对象
        mStockManager.createStocks(mStockIdList);
    }

    /**
     * 请求股票五天的价格
     */
    public void queryFiveDayPrice(){
        // 每天只请求一次五日的价格
        for(final String stockId : mStockManager.getStockIdList()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mSinaDataQueryer.queryStocksFiveDayPrice(stockId);
                }
            };
            mCachedThreadPool.execute(runnable);
        }

    }

    /**
     * 请求股票今天的价格
     * 每半个小时请求一次今日价格和均价
     */
    public void beginQueryTodayPrice(){
        Log.d("lwd","获取今天股票数据");
        // 设置计时器进行请求
        Timer timer = new Timer("TodayStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                //获取系统时间
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour < 9 || hour > 16){
                    return;
                }
                for(final String stockId : mStockManager.getStockIdList()) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mSinaDataQueryer.queryStocksTodayPrice(stockId);
                        }
                    };
                    mCachedThreadPool.execute(runnable);
                }
            }
        }, 0, 1000*60*30); // 1 seconds
    }

    /**
     * 获取全部股票每分钟的数据
     */
    public void beginQueryMinutePrice(){
        // 拼出股票列表字符串
        String stockIdStr = "";
        for(String stockId : mStockManager.getStockIdList()) {
            stockIdStr = stockIdStr + "rt_" + stockId + ",";
        }
        final String stockIdString = stockIdStr;
        // 设置计时器进行请求
        Timer timer = new Timer("MinuteStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mSinaDataQueryer.queryStocksNowPrice(stockIdString);
            }
        }, 0, 2000); // 1 seconds
    }
}
