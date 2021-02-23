package com.example.stockmaster.http;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.example.stockmaster.util.DateUtil;
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

    public void queryOneStockFiveDayPrice(String stockId){
        mSinaDataQueryer.queryStocksFiveDayPrice(stockId);
    }

    /**
     * 请求股票五天的价格
     */
    public void queryFiveDayPrice(){
        // 每天只请求一次五日的价格
        for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            queryOneStockFiveDayPrice(stockId);
        }
    }


    /**
     * 请求股票今天的价格
     */
    public void beginQueryTodayPrice(){
        Log.d("lwd","获取今天股票数据");
        // 设置计时器进行请求
        Timer timer = new Timer("beginQueryTodayPrice");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!DateUtil.isDealTime()){
                    return;
                }
                for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
                    mSinaDataQueryer.queryStocksTodayPrice(stockId);
                }
            }
        }, 5000, 1000*60*30);
    }

    /**
     * 如果不在交易时间，则只请求一次今天的股票数据和分时股票数据
     * 请求分时股票数据为了得到股票名称
     */
    public void queryBeginOnce(){
        // 请求最近交易时间
        mSinaDataQueryer.queryLastDealDate();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 请求分时股票数据
        String stockIdStr = "";
        for(String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            stockIdStr = stockIdStr + "rt_" + stockId + ",";
        }
        final String stockIdString = stockIdStr;
        mSinaDataQueryer.queryStocksNowPrice(stockIdString);

        // 请求均线数据
        queryAllMaOnce();
    }

    public void queryAllMaOnce(){

        for(final String stockId : StockManager.getDefaultStockMonitorStockIdList()) {
            queryOneStockMaOnce(stockId);
        }
    }

    public void queryOneStockMaOnce(String stockId){
        mSinaDataQueryer.queryStocksMAPrice(stockId);
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

        // 设置计时器进行请求
        Timer timer = new Timer("beginQueryMinutePrice");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(DateUtil.isDealTime()){
                    mSinaDataQueryer.queryStocksNowPrice(stockIdString);
                }
            }
        }, 0, 1000 * 20); // 1 seconds
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
                if(DateUtil.isDealTime()){
                    queryAllMaOnce();
                }

            }
        }, 1000*60*60, 1000*60*60); // 1 seconds
    }

    /**
     * 每隔20分钟请求一次最近开盘日期
     */
    public void beginQueryLastDealDate(){
        Timer timer = new Timer("queryLastDealDate");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(DateUtil.isDealTime()){
                    mSinaDataQueryer.queryLastDealDate();
                }

            }
        }, 0, 1000*60*10);
    }




}
