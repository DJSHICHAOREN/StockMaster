package com.example.stockmaster.entity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.k.K15Minutes;
import com.example.stockmaster.entity.k.K30Minutes;
import com.example.stockmaster.entity.k.K5Minutes;
import com.example.stockmaster.entity.k.K60Minutes;
import com.example.stockmaster.entity.k.KBase;
import com.example.stockmaster.entity.ma.DayMaPrice;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.entity.strategy.VBBStrategy;
import com.example.stockmaster.util.DateUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Table(name = "stock")
public class Stock {
    @Column(name = "id", isId = true)
    public String id;
    @Column(name = "name")
    public String name;
    @Column(name = "currentPrice")
    public StockPrice currentPrice;
    @Column(name = "monitorType")
    public int monitorType; //{0：不监控，1：监控买点，2：监控卖点}

    private DayMaPrice mDayMaPrice;

    public List<KBase> mKBaseList;
    public List<StrategyResult> mStrategyResultList = new ArrayList<>();

    public boolean isReceivedTodayData = false; //在为true时，才可以接收分钟的数据
    public List<StockPrice> todayStockPriceList = new ArrayList<>();
    public List<StockPrice> lowerStockPriceList = new ArrayList<>();
    public List<StockPrice> higherStockPriceList = new ArrayList<>();
    public List<StockPrice> buyStockPriceList = new ArrayList<>();
    public List<StockPrice> saleStockPriceList = new ArrayList<>();
    public List<StockPrice> dealPriceList = new ArrayList<>(); // 用来在detail页面显示全部交易列表
    private List<StockPrice> mKeyStockPriceList = new ArrayList<>();
    public enum DealType{SALE, BUY, NULL}
    private DealType previousDealType = DealType.NULL;
    private List<Float> previousFourDayPriceList;
    private List<StockPrice> wholeStockPriceList = new ArrayList<>();
    private float mFiveDayHighestPrice;
    private List<BaseStrategy> mStrategyList = Arrays.asList(new VBBStrategy());
    public Stock(){

    }

    public Stock(String id, String name, int monitorType){
        this.id = id;
        this.name = name;
        this.monitorType = monitorType;
        this.mKBaseList = Arrays.asList(new K5Minutes(this), new K15Minutes(this), new K30Minutes(this), new K60Minutes(this));
    }

    public Stock(String id, String name, int monitorType, DayMaPrice dayMaPrice){
        this.id = id;
        this.name = name;
        this.monitorType = monitorType;
        this.mKBaseList = Arrays.asList(new K5Minutes(this), new K15Minutes(this), new K30Minutes(this), new K60Minutes(this));
        this.mDayMaPrice = dayMaPrice;
    }

    /**
     * 清空价格列表
     */
    public void clearPriceList(){
        todayStockPriceList.clear();
        lowerStockPriceList.clear();
        higherStockPriceList.clear();
        buyStockPriceList.clear();
        saleStockPriceList.clear();
        dealPriceList.clear();
    }

    /**
     * 加入全部价格列表
     * @param stockPrice
     */
    public List<StrategyResult> addToWholeStockPriceList(StockPrice stockPrice){
        List<StrategyResult> strategyResultList = new ArrayList<>();
        if(!wholeStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = wholeStockPriceList.get(wholeStockPriceList.size()-1);
            if(DateUtil.isMinuteEqual(stockPrice.getTime(), lastStockPrice.getTime()) && lastStockPrice.price != stockPrice.price){
                wholeStockPriceList.remove(todayStockPriceList.size()-1);
                wholeStockPriceList.add(stockPrice);
            }
            // 比列表靠后的时间，添加进列表
            else if(stockPrice.time.after(lastStockPrice.time)){
                wholeStockPriceList.add(stockPrice);
                // 得到形态
                List<StockForm> stockFormList = new ArrayList<>();
                for(KBase kBase : mKBaseList){
                    stockFormList.addAll(kBase.updateLastStockPrice(wholeStockPriceList));
                }
                // 得到策略
                for(StockForm stockForm : stockFormList){
                    strategyResultList.addAll(analyse(stockForm));
                }

            }
        }
        return strategyResultList;
    }

    /**
     * 加入今天的股票价格
     * @param stockPrice
     * @return 如果成功加入，则返回true，否则返回false
     */
    public boolean addToTodayStockPriceList(StockPrice stockPrice){
        currentPrice = stockPrice;
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            // 对于同一分钟的价格，去掉旧的，加入新的
            if(stockPrice.time.compareTo(lastStockPrice.time) == 0 && lastStockPrice.price != stockPrice.price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                todayStockPriceList.add(stockPrice);
                return true;
            }
            // 比列表靠后的时间，添加进列表
            else if(stockPrice.time.after(lastStockPrice.time)){
                todayStockPriceList.add(stockPrice);
                return true;
            }
        }
        // 如果价格列表为空，则直接加入
        else{
            todayStockPriceList.add(stockPrice);
            return true;
        }

        return false;
    }

    /**
     * 计算前四日的最高价
     * @param stockPriceEveryDayList
     */
    private void calFiveDayHighestPrice(List<List<StockPrice>> stockPriceEveryDayList){
        for(int i=0; i<stockPriceEveryDayList.size(); i++){
            if(i == stockPriceEveryDayList.size()-1){
                break;
            }
            List<StockPrice> stockPriceList = stockPriceEveryDayList.get(i);
            for(StockPrice stockPrice : stockPriceList){
                if(stockPrice.getPrice() > mFiveDayHighestPrice){
                    mFiveDayHighestPrice = stockPrice.getPrice();
                }
            }
        }
    }

    /**
     * 添加关键价格列表
     * @param stockPriceEveryDayList
     */
    public void setWholeStockPriceList(List<List<StockPrice>> stockPriceEveryDayList) {
        // 由于在数据库中读取的stock不会经过构造函数，所以mKBaseList可能为空
        if(mKBaseList == null){
//            this.mKBaseList = Arrays.asList(new K5Minutes(id), new K15Minutes(id), new K30Minutes(id), new K60Minutes(id));
            this.mKBaseList = Arrays.asList(new K30Minutes(this), new K60Minutes(this));
        }

        calFiveDayHighestPrice(stockPriceEveryDayList);

        List<StockPrice> keyStockPriceList = new ArrayList<>();
        for(List<StockPrice> stockPriceList : stockPriceEveryDayList){
            for(StockPrice stockPrice : stockPriceList){
                keyStockPriceList.add(stockPrice);
            }
        }

        List<StockForm> stockFormList = new ArrayList<>();
        for(KBase kBase : mKBaseList){
            stockFormList.addAll(kBase.setKeyStockPriceList(keyStockPriceList));
        }

        wholeStockPriceList = keyStockPriceList;

        // 提取形态列表
//        List<StockForm> stockFormList = DBUtil.getStockFormByStockId(getId());
        if(stockFormList != null){
            for(StockForm stockForm : stockFormList){
                analyse(stockForm);
            }
        }

    }

    /**
     * 添加买点或者卖点，如果上一个是卖点，当前才是买点，上一个是买点，当前才是卖点
     * @param stockPrice 股票价格、时间点
     * @param dealType 交易类型：是买还是卖
     */
    public boolean addBuyAndSaleStockPrice(StockPrice stockPrice, DealType dealType){
        if(previousDealType != DealType.BUY && dealType == DealType.BUY){
            buyStockPriceList.add(stockPrice);
            previousDealType = DealType.BUY;
            stockPrice.setDealType(DealType.BUY);
            dealPriceList.add(stockPrice);
            return true;
        }
        else if(previousDealType != DealType.SALE && dealType == DealType.SALE){
            saleStockPriceList.add(stockPrice);
            previousDealType = DealType.SALE;
            stockPrice.setDealType(DealType.SALE);
            dealPriceList.add(stockPrice);
            return true;
        }
        return false;
    }

    /**
     * 得到最近的买卖时间点
     */
    public String getRecentDealTips(){
        String dealTip = "";
        if(previousDealType == DealType.BUY && buyStockPriceList.size() > 0){
            StockPrice stockPrice = buyStockPriceList.get(buyStockPriceList.size()-1);
            dealTip = stockPrice.toString();
        }
        else if(previousDealType == DealType.SALE && saleStockPriceList.size() > 0){
            StockPrice stockPrice = saleStockPriceList.get(saleStockPriceList.size()-1);
            dealTip = stockPrice.toString();
        }
        return dealTip;
    }


    public void receiveTodayData(){
        isReceivedTodayData = true;
    }

    public float getCurrentPrice(){
        if(currentPrice != null){
            return currentPrice.price;
        }
        return -1;
    }

    public List<StockPrice> getDealStockPriceList() {
        return dealPriceList;
    }

    @NonNull
    @Override
    public String toString() {
        if(id != null && name != null && currentPrice != null){
            return String.format("id:%s, name:%s, current_price:%s", id, name, currentPrice.toString());
        }
        return super.toString();
    }

    public void setPreviousFourDayPriceList(List<Float> previousFourDayPriceList) {
        this.previousFourDayPriceList = previousFourDayPriceList;
    }

    public float getMa5(float nowPrice) {
        float sum = 0;
        if(previousFourDayPriceList != null && previousFourDayPriceList.size() == 4 && currentPrice != null){
            for(Float price : previousFourDayPriceList){
                sum += price;
            }
            sum += nowPrice;
            sum /= 5;
        }
        else{
            Log.d("lwd", "无法获取五日均价");
        }
        return sum;
    }

    public DayMaPrice getDayMaPrice() {
        return mDayMaPrice;
    }

    public void setDayMaPrice(DayMaPrice mDayMaPrice) {
        this.mDayMaPrice = mDayMaPrice;
    }

    /**
     * 将监控类型轮转：无->买->卖
     */
    public void ringMonitorType(){
        monitorType =  (monitorType + 1) % 3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(int monitorType) {
        this.monitorType = monitorType;
    }



    /**
     * 打印挑选出的时间点
     * @param stockPriceList
     */
    public void printQualifiedTimePoint(List<StockPrice> stockPriceList, int countedDay){
        if(countedDay != 30)
            return;
        for(StockPrice stockPrice : stockPriceList){
            String msg = String.format("合理买入点，时间点:%s，价格:%f",
                    stockPrice.getTime().toString(),
                    stockPrice.getPrice());
            Log.d("lwd", msg);
        }
    }

    public void setCurrentPrice(StockPrice currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isReceivedTodayData() {
        return isReceivedTodayData;
    }

    public void setReceivedTodayData(boolean receivedTodayData) {
        isReceivedTodayData = receivedTodayData;
    }

    public List<StrategyResult> getStrategyAnalyseResultList() {
        return mStrategyResultList;
    }

    public void setStrategyAnalyseResultList(List<StrategyResult> mStockFormList) {
        this.mStrategyResultList = mStockFormList;
    }

    public String getStrategyAnalyseDescribeString(){
        if(this.mStrategyResultList == null){
            return "没有信息";
        }
        String resultString = "";
        for(StrategyResult strategyResult : this.mStrategyResultList){
            resultString += strategyResult.toString() + "\n";
        }
        return resultString;

    }

    /**
     * 分析每一个价格
     * @param
     * @param
     * @return
     */
    public List<StrategyResult> analyse(StockForm stockForm){
        List<StrategyResult> strategyResultList = new ArrayList<>();
        if(stockForm != null) {
            for (BaseStrategy baseStrategy : mStrategyList) {
                StrategyResult strategyResult = baseStrategy.analyse(stockForm, getId());
                if (strategyResult != null) {
                    strategyResultList.add(strategyResult);
                    mStrategyResultList.add(strategyResult);
                }
            }
        }

        // 打印买卖点信息
        for(StrategyResult strategyResult : strategyResultList){
            Log.d("lwd", strategyResult.toString());
        }
        return strategyResultList;
    }

    public int getStrategyResultListSize(){
        return mStrategyResultList.size();
    }

    public float getFiveDayHighestPrice() {
        return mFiveDayHighestPrice;
    }

    public void setFiveDayHighestPrice(float mFiveDayHighestPrice) {
        this.mFiveDayHighestPrice = mFiveDayHighestPrice;
    }
}
