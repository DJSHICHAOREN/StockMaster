package com.example.stockmaster.entity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.k.K30Minutes;
import com.example.stockmaster.entity.k.KBase;
import com.example.stockmaster.entity.ma.DayMaPrice;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.MinuteRiseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.entity.strategy.VBBStrategy;
import com.example.stockmaster.util.DateUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public boolean isReceiveTodayData = false; //在为true时，才可以接收分钟的数据
    public List<StockPrice> todayStockPriceList = new ArrayList<>();
    public List<StockPrice> lowerStockPriceList = new ArrayList<>();
    public List<StockPrice> higherStockPriceList = new ArrayList<>();
    public List<StockPrice> buyStockPriceList = new ArrayList<>();
    public List<StockPrice> saleStockPriceList = new ArrayList<>();
    public List<StockPrice> dealPriceList = new ArrayList<>(); // 用来在detail页面显示全部交易列表
    public enum DealType{SALE, BUY, NULL}
    private DealType previousDealType = DealType.NULL;
    private List<Float> previousFourDayPriceList;
    private List<StockPrice> wholeStockPriceList = new ArrayList<>();
    private float mFiveDayHighestPrice = -1;
    private float mFiveDayLowestPrice = 100000;
    private float mFiveDayHighestEndPrice;
    private List<BaseStrategy> mStrategyList = Arrays.asList(new VBBStrategy(), new MinuteRiseStrategy());

    private List<StockPrice> mStockPriceList = new ArrayList<>();
    private int mLastExactStockPriceIndex = -1;

    public Stock(){
        this.mKBaseList = Arrays.asList(new K30Minutes(this));
    }

    public Stock(String id, String name, int monitorType){
        this.id = id;
        this.name = name;
        this.monitorType = monitorType;
        this.mKBaseList = Arrays.asList(new K30Minutes(this));
    }

    public Stock(String id, String name, int monitorType, DayMaPrice dayMaPrice, List<Float> previousFourDayPriceList){
        this.id = id;
        this.name = name;
        this.monitorType = monitorType;
        this.mKBaseList = Arrays.asList(new K30Minutes(this));
        this.mDayMaPrice = dayMaPrice;
        this.previousFourDayPriceList = previousFourDayPriceList;
    }

    /**
     * 清空价格列表
     */
    public void clearAdvanceState(Date time){
        int lowerStockPriceListIndex = lowerStockPriceList.size() -1;
        while (lowerStockPriceListIndex >= 0){
            if(DateUtil.isDateAfter(lowerStockPriceList.get(lowerStockPriceListIndex).getTime(), time)
                    || DateUtil.isDateEqual(lowerStockPriceList.get(lowerStockPriceListIndex).getTime(), time) ){
                lowerStockPriceList.remove(lowerStockPriceListIndex);
                lowerStockPriceListIndex--;
            }
            else{
                break;
            }
        }

        int higherStockPriceListIndex = higherStockPriceList.size() -1;
        while (higherStockPriceListIndex >= 0){
            if(DateUtil.isDateAfter(higherStockPriceList.get(higherStockPriceListIndex).getTime(), time)
                    || DateUtil.isDateEqual(higherStockPriceList.get(higherStockPriceListIndex).getTime(), time) ){
                higherStockPriceList.remove(higherStockPriceListIndex);
                higherStockPriceListIndex--;
            }
            else{
                break;
            }
        }

        for (KBase kBase : mKBaseList) {
            kBase.clearAdvanceState(time);
        }
    }


    /**
     * 添加关键价格
     * 只对自己级别的准确度负责
     * @param stockPrice
     */
    public List<StrategyResult> addStockPrice(StockPrice stockPrice){
        boolean isUpdateStockPrice = false;
        if(mStockPriceList.size() > 1){
            // 判断是否比价格列表中的最后一个价格新
            StockPrice lastStockPrice = mStockPriceList.get(mStockPriceList.size()-1);
            if(DateUtil.isDateAfter(stockPrice.getTime(), lastStockPrice.getTime()) ){
                mStockPriceList.add(stockPrice);
                isUpdateStockPrice = true;
            }
            else if((DateUtil.isDateEqual(stockPrice.getTime(), lastStockPrice.getTime()) &&
                    stockPrice.getPrice() != lastStockPrice.getPrice())){
                mStockPriceList = mStockPriceList.subList(0, mStockPriceList.size()-1);
                mStockPriceList.add(stockPrice);
                isUpdateStockPrice = true;
            }
        }
        else{
            mStockPriceList.add(stockPrice);
            isUpdateStockPrice = true;
        }

        // 若股票价格有更新，则进行形态、策略分析
        List<StrategyResult> strategyResultList = new ArrayList<>();
        if(isUpdateStockPrice) {
            // 得到形态
            List<StockForm> stockFormList = new ArrayList<>();
            for (KBase kBase : mKBaseList) {
                stockFormList.addAll(kBase.addStockPrice(stockPrice));
            }
            // 分析形态
            if (stockFormList != null) {
                for (StockForm stockForm : stockFormList) {
                    strategyResultList.addAll(analyse(stockForm));
                }
            }
        }
        return strategyResultList;
    }

    /**
     * 添加关键价格列表
     * @param stockPriceList
     */
    public List<StrategyResult> addStockPriceList(List<StockPrice> stockPriceList){

        List<StockPrice> newPartStockPriceList = stockPriceList;
        // 删除老的价格段
        // 寻找新的价格段
        if(mLastExactStockPriceIndex != -1){
            StockPrice lastExactStockPrice = mStockPriceList.get(mLastExactStockPriceIndex);
            for(int i=0; i < stockPriceList.size(); i++){
                // 当出现新的价格时
                if(DateUtil.isDateAfter(stockPriceList.get(i).getTime(), lastExactStockPrice.getTime())
                || (DateUtil.isDateEqual(stockPriceList.get(i).getTime(), lastExactStockPrice.getTime()) &&
                        stockPriceList.get(i).getPrice() != lastExactStockPrice.getPrice())){
                    // 删除老的价格段
                    mStockPriceList = mStockPriceList.subList(0, mLastExactStockPriceIndex);
                    // 截取新的价格段
                    newPartStockPriceList = stockPriceList.subList(i, stockPriceList.size());
                    // 清除分时请求的超前的状态
                    clearAdvanceState(newPartStockPriceList.get(0).getTime());

                    // 清理以后退出循环
                    break;
                }
            }
        }
        // 添加新的价格段
        List<StrategyResult> strategyResultList = new ArrayList<>();
        for(StockPrice stockPrice : newPartStockPriceList){
            strategyResultList.addAll(addStockPrice(stockPrice));
        }
        return strategyResultList;
    }

    /**
     * 添加关键价格列表的列表
     * @param stockPriceListList
     */
    public List<StrategyResult> addStockPriceListList(List<List<StockPrice>> stockPriceListList) {
        List<StrategyResult> strategyResultList = new ArrayList<>();
        for(List<StockPrice> stockPriceList : stockPriceListList){
            strategyResultList.addAll(addStockPriceList(stockPriceList));
        }
        return strategyResultList;
    }

    /**
     * 计算前四日的最高价
     * @param stockPriceEveryDayList
     */
    public void calFiveDayHighestAndLowestPrice(List<List<StockPrice>> stockPriceEveryDayList){
        for(int i=0; i<stockPriceEveryDayList.size(); i++){
            if(i == stockPriceEveryDayList.size()-1){
                break;
            }
            List<StockPrice> stockPriceList = stockPriceEveryDayList.get(i);
            for(StockPrice stockPrice : stockPriceList){
                if(stockPrice.getPrice() > mFiveDayHighestPrice){
                    mFiveDayHighestPrice = stockPrice.getPrice();
                }
                if(stockPrice.getPrice() < mFiveDayLowestPrice){
                    mFiveDayLowestPrice = stockPrice.getPrice();
                }
            }
        }
    }

    public void calFiveDayHighestEndPrice(List<List<StockPrice>> stockPriceEveryDayList){
        for(int i=0; i<stockPriceEveryDayList.size(); i++){
            if(i == stockPriceEveryDayList.size()-1){
                break;
            }
            List<StockPrice> stockPriceList = stockPriceEveryDayList.get(i);
            if(stockPriceList != null && stockPriceList.size() > 0){
                Float lastPrice = stockPriceList.get(stockPriceList.size()-1).getPrice();
                if(mFiveDayHighestEndPrice < lastPrice){
                    mFiveDayHighestEndPrice = lastPrice;
                }
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

    /**
     * 设置是否接收一日价格
     */
    public void receiveTodayData(){
        isReceiveTodayData = true;
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
                StrategyResult strategyResult = baseStrategy.analyse(stockForm, this);
                if (strategyResult != null) {
                    strategyResultList.add(strategyResult);
                    mStrategyResultList.add(strategyResult);
                }
            }
        }

        // 打印买卖点信息
        for(StrategyResult strategyResult : strategyResultList){
            Log.d("lwd", strategyResult.toLongString());
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

    public List<Float> getPreviousFourDayPriceList() {
        return previousFourDayPriceList;
    }

    public float getFiveDayHighestEndPrice() {
        return mFiveDayHighestEndPrice;
    }



    public void setPreviousFourDayPriceList(List<Float> previousFourDayPriceList) {
        this.previousFourDayPriceList = previousFourDayPriceList;
    }

    public float getMa5(float nowPrice) {
        float sum = 0;
        if(previousFourDayPriceList != null && previousFourDayPriceList.size() == 4){
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

    public boolean isReceiveTodayData() {
        return isReceiveTodayData;
    }

    public void setReceiveTodayData(boolean receiveTodayData) {
        isReceiveTodayData = receiveTodayData;
    }

    public List<StrategyResult> getStrategyAnalyseResultList() {
        return mStrategyResultList;
    }

    public void setStrategyAnalyseResultList(List<StrategyResult> mStockFormList) {
        this.mStrategyResultList = mStockFormList;
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

    public int getLastExactStockPriceIndex() {
        return mLastExactStockPriceIndex;
    }

    public void setLastExactStockPriceIndex(int mLastExactStockPriceIndex) {
        this.mLastExactStockPriceIndex = mLastExactStockPriceIndex;
    }

    public List<StockPrice> getStockPriceList() {
        return mStockPriceList;
    }

    public void setStockPriceList(List<StockPrice> mStockPriceList) {
        this.mStockPriceList = mStockPriceList;
    }

    public float getFiveDayLowestPrice() {
        return mFiveDayLowestPrice;
    }

    public void setFiveDayLowestPrice(float mFiveDayLowestPrice) {
        this.mFiveDayLowestPrice = mFiveDayLowestPrice;
    }

    @NonNull
    @Override
    public String toString() {
        if(id != null && name != null && currentPrice != null){
            return String.format("id:%s, name:%s, current_price:%s", id, name, currentPrice.toString());
        }
        return super.toString();
    }

    public String getStrategyAnalyseDescribeString(){
        if(this.mStrategyResultList == null){
            return "没有信息";
        }
        String resultString = "";
        for(StrategyResult strategyResult : this.mStrategyResultList){
            resultString += strategyResult.toLongString() + "\n";
        }
        return resultString;

    }
}
