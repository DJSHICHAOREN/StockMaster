package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.service.BrainService;
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
    private static MainActivity.MainActivityUIManager mMainActivityUIManager;
    private static BrainService mBrainService;
    public StockManager(){
        mStockAnalyser.setStockManager(this);
    }

    public void setBrainService(BrainService brainService){
        mBrainService = brainService;
    }

    public void setMainActivityUIManager(MainActivity.MainActivityUIManager mainActivityUIManager) {
        this.mMainActivityUIManager = mainActivityUIManager;
    }

    /**
     * 根据股票id列表创建股票实例
     * @param stockIdList
     */
    public void createStocks(ArrayList<String> stockIdList){
        mStockList.clear();
        mStockIdList.clear();
        for(String stockId : stockIdList){
            if(stockId.length() > 2 && !stockId.substring(0,2).equals("hk")){
                stockId = "hk" + stockId;
            }
            Stock stock = new Stock(mStockAnalyser, stockId, "");
            mStockList.add(stock);
            mStockIdList.add(stockId);

            // 将股票实例存入数据库
            DBUtil.saveStock(stock);
        }
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
     * 添加股票价格到股票对象
     * @param stockPriceList
     * @return stockId 获取成功的股票Id
     */
    public void addStockPriceList(List<StockPrice> stockPriceList, String stockId, boolean isClearBeforeData){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null && stockPriceList.size() > 0){
            // 在添加今天的数据之前要清空之前的价格数据
            if(isClearBeforeData){
                stock.clearPriceList();
            }
            for(StockPrice stockPrice : stockPriceList){
                add(stock, stockPrice);
            }
            // 设置获取开盘到当前数据完毕
            stock.receiveTodayData();
            Log.d("lwd", String.format("%s 开盘到当前数据加载完毕", stockId));
        }
    }

    /**
     * 保存股票价格到数据库
     * @param stockPriceList
     * @param stockId
     */
    public void saveStockPriceList(List<StockPrice> stockPriceList, String stockId){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            for(StockPrice stockPrice : stockPriceList){
                // 保存价格到数据库
                DBUtil.saveStockPrice(stockPrice);
            }
            Log.d("lwd", String.format("%s 五日关键数据加载完毕", stockId));
        }
    }

    /**
     * 添加实时股票价格
     * 在添加了从开盘到现在的数据之后，再添加实时的每分钟的数据
     * @param stockPriceList
     */
    public void addMinuteStockPrice(List<StockPrice> stockPriceList){
        for(StockPrice stockPrice : stockPriceList){
            int stockIndex = mStockIdList.indexOf(stockPrice.stockId);
            if(stockIndex == -1){
                Log.e("lwd", String.format("没有找到价格对应的股票id：%s", stockPrice.stockId));
            }
            Stock stock = mStockList.get(stockIndex);
            // 设置股票名称
            if(stockPrice.getName() != null && stock.getName().equals("")){
                stock.setName(stockPrice.getName());
            }
            if(stock != null && stock.isReceivedTodayData){
                add(stock, stockPrice);
//                Log.d("lwd", String.format("加载分钟数据:%s", stock.id));
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

    public void addBuyAndSaleStockPrice(Stock stock, StockPrice stockPrice, Stock.DealType dealType){
        if(stock.addBuyAndSaleStockPrice(stockPrice, dealType)){
            if(mMainActivityUIManager != null){
                mMainActivityUIManager.refreshUIWhenGetNewDealPoint(stock.getName() + " " +stockPrice.toStringWithId(),
                        stockPrice.getNotificationId(), stockPrice.getNotificationContent());
                mMainActivityUIManager.notifyStockListItemChanged(mStockIdList.indexOf(stock.id));
            }
            if(mBrainService != null){
//                mBrainService.sendNotification(stockPrice.getNotificationId(), stockPrice.getNotificationContent());
            }
        }
    }

    public static List<Stock> getLineUpStocks(){
        List<Stock> lineUpStockList = new ArrayList<>();
        for(Stock stock : mStockList){
            if(StockAnalyser.isFiveDayLineUp(stock)){
                lineUpStockList.add(stock);
            }
        }
        return lineUpStockList;
    }
}
