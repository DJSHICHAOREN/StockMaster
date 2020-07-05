package com.example.stockmaster.http;

import android.content.Context;
import android.util.Log;

import com.example.stockmaster.util.StockManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络请求管理类
 * 设置请求的计时器
 */
public class DataQueryerManager {
    private static DataQueryerManager instance;
    private SinaDataQueryer mSinaDataQueryer;
    final ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();

    public DataQueryerManager(Context context){
        // 创建工具实例
        mSinaDataQueryer = new SinaDataQueryer(context);
    }

    public static DataQueryerManager getInstance(Context context){
        if(instance == null){
            instance = new DataQueryerManager(context);
        }
        return instance;
    }

    public void queryOneStockFiveDayPrice(String stockId, boolean isNewStock){
        mSinaDataQueryer.queryStocksFiveDayPrice(stockId, isNewStock);
    }

    /**
     * 请求股票五天的价格
     */
    public void queryFiveDayPrice(){
        // 每天只请求一次五日的价格
        for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    queryOneStockFiveDayPrice(stockId, false);
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
        Timer timer = new Timer("beginQueryTodayPrice");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                //获取系统时间
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour < 9 || hour > 16){
                    return;
                }
                for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
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
     * 如果不在交易时间，则只请求一次今天的股票数据和分时股票数据
     * 请求今天股票数据为了分析今日买卖点
     * 请求分时股票数据为了得到股票名称
     */
    public void queryAllOnce(){
        Calendar calendar = Calendar.getInstance();
        //获取系统时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour < 9 || hour > 16){
            // 请求最近交易时间
            mSinaDataQueryer.queryLastDealDate();

            // 请求一天股票数据
            for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSinaDataQueryer.queryStocksTodayPrice(stockId);
                    }
                };
                mCachedThreadPool.execute(runnable);
            }
        }
        // 请求分时股票数据
        String stockIdStr = "";
        for(String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            stockIdStr = stockIdStr + "rt_" + stockId + ",";
        }
        final String stockIdString = stockIdStr;
        mSinaDataQueryer.queryStocksNowPrice(stockIdString);

        // 请求均线数据
        for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mSinaDataQueryer.queryStocksMAPrice(stockId);
                }
            };
            mCachedThreadPool.execute(runnable);
        }
    }

    /**
     * 获取全部股票每分钟的数据
     */
    public void beginQueryMinutePrice(){
        // 拼出股票列表字符串
        String stockIdStr = "";
        for(String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            stockIdStr = stockIdStr + "rt_" + stockId + ",";
        }
        final String stockIdString = stockIdStr;
        Calendar calendar = Calendar.getInstance();
        //获取系统时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour < 9 || hour > 16){
            return;
        }
        // 设置计时器进行请求
        Timer timer = new Timer("beginQueryMinutePrice");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mSinaDataQueryer.queryStocksNowPrice(stockIdString);
            }
        }, 0, 1000 * 60); // 1 seconds
    }

    /**
     * 每半小时请求一次日均线数据
     */
    public void beginQueryMaPrice(){
        // 设置计时器进行请求
        Timer timer = new Timer("beginQueryMaPrice");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                //获取系统时间
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour < 9 || hour > 16){
                    return;
                }
                for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            mSinaDataQueryer.queryStocksMAPrice(stockId);
                        }
                    };
                    mCachedThreadPool.execute(runnable);
                }
            }
        }, 0, 1000*60*30); // 1 seconds
    }

    /**
     * 每隔20分钟请求一次最近开盘日期
     */
    public void beginQueryLastDealDate(){
        Timer timer = new Timer("queryLastDealDate");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                //获取系统时间
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if(hour < 9 || hour > 16){
                    return;
                }
                mSinaDataQueryer.queryLastDealDate();

            }
        }, 0, 1000*60*20);
    }



}
