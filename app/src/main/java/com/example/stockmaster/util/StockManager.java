package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.strategy.StrategyAnalyseResult;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.base.UIManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 管理股票类：新建股票类，建立新的价格
 */
public class StockManager {
    private static List<Stock> mStockList = new ArrayList<Stock>();
    private static List<String> mStockIdList = new ArrayList<String>();
    private  static List<Stock> mQualifiedStockList = new ArrayList<>();
    private static ShortSwingAnalyser mShortSwingAnalyser = new ShortSwingAnalyser();
    private static UIManager mPriceMonitorUIManager;
    private static UIManager mStockMonitorUIManager;
    private static BrainService mBrainService;
    private static ArrayList<String> DEFAULT_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("hk01349", "hk03709", "hk01622", "hk01565", "hk02606", "hk09926", "hk02400", "hk06060", "hk09969","hk00981","hk00302", "hk01055", "hk06186", "hk01610", "hk00772", "hk06855", "hk03319", "hk09916", "hk01941", "hk01873", "hk02013", "hk03331", "hk00853", "hk00777", "hk00826", "hk09928", "hk02018", "hk06919", "hk01745", "hk06185", "hk09966", "hk03759", "hk01501", "hk01300", "hk01691", "hk09922", "hk00175", "hk00589", "hk01525", "hk01347"));
//    private static ArrayList<String> STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("hk09926", "hk02400", "hk06060", "hk00589"));
//    private static ArrayList<String> DEFAULT_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("hk06855"));

    public static void loadStocks(){
//        getStocksFromDB();
        createStocks(DEFAULT_STOCK_ID_LIST, false);
    }

    public static void loadStockPrice(){
        for(Stock stock : getStockList()){
            getKeyStockPriceFromDB(stock);
        }
    }

    public static void setBrainService(BrainService brainService){
        mBrainService = brainService;
    }

    public static void setPriceMonitorFragmentUIManager(UIManager mainActivityUIManager) {
        mPriceMonitorUIManager = mainActivityUIManager;
    }

    public static void setStockMonitorFragmentUIManager(UIManager stockMonitorUIManager){
        mStockMonitorUIManager = stockMonitorUIManager;
    }

    /**
     * 从数据库中读取全部的股票
     */
    public static void getStocksFromDB(){
        mStockList = DBUtil.getAllStocks();
        for(Stock stock : mStockList){
            mStockIdList.add(stock.getId());
        }
        // 如果数据库为空，则添加默认股票
        if(mStockIdList.size() == 0){
            createStocks(DEFAULT_STOCK_ID_LIST, false);
        }
    }

    public static void getKeyStockPriceFromDB(Stock stock){
        stock.setKeyStockPriceList(DBUtil.getStockPriceList(stock.getId()));
    }

    /**
     * 根据股票id列表创建股票实例
     * @param stockIdList
     */
    public static void createStocks(ArrayList<String> stockIdList, boolean isMonitorBuyPoint){
        for(String stockId : stockIdList){
            if(stockId.length() > 2 && !stockId.substring(0,2).equals("hk")){
                stockId = "hk" + stockId;
            }
            Stock stock = new Stock(stockId, "", isMonitorBuyPoint ? 1 : 0);
            // 将股票实例存入数据库
            stock = DBUtil.saveStock(stock);
            mStockList.add(stock);
            Log.d("lwd", "添加stockId:" + stockId);
            mStockIdList.add(stockId);

        }
        if(mPriceMonitorUIManager != null){
            mPriceMonitorUIManager.notifyStockListDateSetChanged();
        }
    }

    public static List<String> getDefaultStockIdList(){
        return mStockIdList;
    }

    /**
     * 添加单只股票的价格
     * @param stockPrice
     */
    public static void add(Stock stock, StockPrice stockPrice){
        if(stock.addStockPrice(stockPrice)){
            mShortSwingAnalyser.analyse(stock);
        }
    }

    /**
     * 添加股票价格到股票对象
     * @param stockPriceList
     * @return stockId 获取成功的股票Id
     */
    public static void addStockPriceList(List<StockPrice> stockPriceList, String stockId, boolean isClearBeforeData){
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
    public static void saveStockPriceList(List<StockPrice> stockPriceList, String stockId){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            // 初始计算满足条件stock
            stock.setKeyStockPriceList(stockPriceList);
            List<StrategyAnalyseResult> strategyAnalyseResultList = DBUtil.getStrategyAnalyseResultByStockId(stock.getId());
            Log.d("lwd", "strategyAnalyseResultList的长度：" + strategyAnalyseResultList.size());
            if(mStockMonitorUIManager != null){
                if(strategyAnalyseResultList.size() > 0){
                    stock.setStrategyAnalyseResultList(strategyAnalyseResultList);
                    mQualifiedStockList.add(stock);
                    mStockMonitorUIManager.notifyStockListDateSetChanged();
                }
            }

//            for(StockPrice stockPrice : stockPriceList){
//                // 保存价格到数据库
//                DBUtil.saveStockPrice(stockPrice);
//            }
            Log.d("lwd", String.format("%s 五日关键数据加载完毕", stockId));
        }
    }

    /**
     * 添加实时股票价格
     * 在添加了从开盘到现在的数据之后，再添加实时的每分钟的数据
     * @param stockPriceList
     */
    public static void addMinuteStockPrice(List<StockPrice> stockPriceList){
        for(StockPrice stockPrice : stockPriceList){
            int stockIndex = mStockIdList.indexOf(stockPrice.stockId);
            if(stockIndex == -1){
                Log.e("lwd", String.format("没有找到价格对应的股票id：%s", stockPrice.stockId));
            }
            Stock stock = mStockList.get(stockIndex);
            // 设置股票名称
            if(stockPrice.getName() != null && stock.getName().equals("")){
                stock.setName(stockPrice.getName());
                DBUtil.updateStock(stock);
            }
            if(stock != null && stock.isReceivedTodayData){
                add(stock, stockPrice);
//                Log.d("lwd", String.format("加载分钟数据:%s", stock.id));
            }
        }

    }
    //todo:将stock从map存储改为用两个list存储
    public static List<Stock> getStockList(){
        return mStockList;
    }

    public static List<StockPrice> getThisStockDealPriceList(int stockIndex){
        return mStockList.get(stockIndex).getDealStockPriceList();
    }

    public static void setPreviousFourDayPriceList(List<Float> previousFourDayPriceList, String stockId) {
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setPreviousFourDayPriceList(previousFourDayPriceList);
            Log.d("lwd", String.format("%s 加载前四日收盘数据", stockId));
        }
//        Log.d("lwd", String.format("stockId:%s, ma5:%f", stock.id, stock.getMa5()));
    }

    public static void addMAPrice(String stockId, String ma10, String ma30, String ma50, String ma100, String ma250) {
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setMAPrice(ma10, ma30, ma50, ma100, ma250);
            Log.d("lwd", String.format("%s 加载均线数据数据", stockId));
        }
    }

    /**
     * 添加买卖点
     * @param stock
     * @param stockPrice
     * @param dealType
     */
    public static void addBuyAndSaleStockPrice(Stock stock, StockPrice stockPrice, Stock.DealType dealType){
        if(stock.addBuyAndSaleStockPrice(stockPrice, dealType)){
            if(mPriceMonitorUIManager != null){
                mPriceMonitorUIManager.refreshUIWhenGetNewDealPoint(stock.getName() + " " +stockPrice.toStringWithId(),
                        stockPrice.getNotificationId(), stockPrice.getNotificationContent());
                mPriceMonitorUIManager.notifyStockListItemChanged(mStockIdList.indexOf(stock.id));
            }
            // 若为请求的分时价格，则为实时的，则发送通知
            if(mBrainService != null
                    && stockPrice.getQueryType() == StockPrice.QueryType.MINUTE){
                // 若为监控卖点则一定通知
                // 若为监控买点且遇到买点则通知
                if(stock.getMonitorType() == 2
                        || (stock.getMonitorType() == 1 && dealType == Stock.DealType.BUY)){
                    mBrainService.sendNotification(stockPrice.getNotificationId(),
                            stock.getName() + " " + stockPrice.getNotificationContent());
                }
            }
        }
    }

    public static List<Stock> getQualifiedStockList() {
        return mQualifiedStockList;
    }

    public static List<Stock> getLineUpStocks(){
        List<Stock> lineUpStockList = new ArrayList<>();
        for(Stock stock : mStockList){
            if(ShortSwingAnalyser.isFiveDayLineUp(stock)){
                lineUpStockList.add(stock);
            }
        }
        return lineUpStockList;
    }
}
