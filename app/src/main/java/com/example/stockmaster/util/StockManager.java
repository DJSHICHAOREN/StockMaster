package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.ui.activity.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理股票类：新建股票类，建立新的价格
 */
public class StockManager {
    private static List<Stock> mStockList = new ArrayList<Stock>();
    private static List<String> mStockIdList = new ArrayList<String>();
    private static StockAnalyser mStockAnalyser = new StockAnalyser();
    private MainActivity.MainActivityUIManager mMainActivityUIManager;
    private float ma5;
    public StockManager(){
    }

    public void setMainActivityUIManager(MainActivity.MainActivityUIManager mainActivityUIManager) {
        this.mMainActivityUIManager = mainActivityUIManager;
        mStockAnalyser.setMainActivityUIManager(mainActivityUIManager);
    }

    /**
     * 根据股票id列表创建股票实例
     * @param stockIdList
     */
    public void createStocks(ArrayList<String> stockIdList){
        mStockList.clear();
        mStockIdList.clear();
        for(String stockId : stockIdList){
            Stock stock = new Stock(mStockAnalyser, stockId, "");
            mStockList.add(stock);
            mStockIdList.add(stockId);
        }
        mStockIdList = stockIdList;
    }

    public List<String> getStockIdList(){
        return mStockIdList;
    }

    /**
     * 添加单只股票的价格
     * @param stockPrice
     */
    public void add(Stock stock, StockPrice stockPrice){
        if(stock.addStockPrice(stockPrice)){
            mStockAnalyser.analyse(stock);
        }
    }

    /**
     * 添加从开盘到现在的股票价格
     * @param stockPriceList
     * @return stockId 获取成功的股票Id
     */
    public void addTodayStockPrice(List<StockPrice> stockPriceList, String stockId){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            for(StockPrice stockPrice : stockPriceList){
                add(stock, stockPrice);
            }
            // 设置获取开盘到当前数据完毕
            stock.receiveTodayData();
//            Log.d("lwd", String.format("%s 开盘到当前数据加载完毕", stockId));
        }

    }

    public float getMa5() {

        return ma5;
    }

    /**
     * 添加当前股票价格
     * 在添加了从开盘到现在的数据之后，再添加实时的每分钟的数据
     * @param stockPriceList
     */
    public void addMinuteStockPrice(List<StockPrice> stockPriceList){
        for(StockPrice stockPrice : stockPriceList){
            int stockIndex = mStockIdList.indexOf(stockPrice.id);
            Stock stock = mStockList.get(stockIndex);
            if(stock != null && stock.isReceivedTodayData){
                add(stock, stockPrice);
                Log.d("lwd", String.format("加载分钟数据:%s", stock.id));
            }
        }

    }
    //todo:将stock从map存储改为用两个list存储
    public List<Stock> getStockList(){
        return mStockList;
    }

    public List<StockPrice> getThisStockDealPriceList(int stockIndex){
        return mStockList.get(stockIndex).getDealStockPriceList();
    }

    public void setPreviousFourDayPriceList(List<Float> previousFourDayPriceList, String stockId) {
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setPreviousFourDayPriceList(previousFourDayPriceList);
            Log.d("lwd", String.format("%s 加载前四日收盘数据", stockId));
        }
        Log.d("lwd", String.format("stockId:%s, ma5:%f", stock.id, stock    .getMa5()));
    }

    public void addMAPrice(String stockId, String ma10, String ma30, String ma50, String ma100, String ma250) {
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setMAPrice(ma10, ma30, ma50, ma100, ma250);
            Log.d("lwd", String.format("%s 加载均线数据数据", stockId));
        }
    }
}
