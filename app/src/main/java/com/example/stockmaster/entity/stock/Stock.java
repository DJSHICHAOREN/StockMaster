package com.example.stockmaster.entity.stock;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.k.K30Minutes;
import com.example.stockmaster.entity.k.KBase;
import com.example.stockmaster.entity.ma.DayMaPrice;
import com.example.stockmaster.entity.strategy.BaseStrategy;
import com.example.stockmaster.entity.strategy.MinuteLongToArrangeStrategy;
import com.example.stockmaster.entity.strategy.MinuteRiseStrategy;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.entity.strategy.SuddenUpStrategy;
import com.example.stockmaster.entity.strategy.VBBStrategy;
import com.example.stockmaster.util.DateUtil;
import com.example.stockmaster.util.StockManager;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public boolean isReceiveTodayData = false; //在为true时，才可以分析分钟的数据
    public List<StockPrice> todayStockPriceList = new ArrayList<>();
    public List<StockPrice> lowerStockPriceList = new ArrayList<>();
    public List<StockPrice> higherStockPriceList = new ArrayList<>();
    public List<StockPrice> dealPriceList = new ArrayList<>(); // 用来在detail页面显示全部交易列表
    public enum DealType{SALE, BUY, NULL}
    private List<Float> previousFourDayPriceList;
    private float mFiveDayHighestPrice = -1;
    private float mFiveDayLowestPrice = 100000;
    private float mFiveDayHighestEndPrice;
    private List<BaseStrategy> mStrategyList = Arrays.asList(new VBBStrategy(), new MinuteRiseStrategy(),
            new SuddenUpStrategy(), new MinuteLongToArrangeStrategy());
    private float mLatestAvgPrice = -1; // 所添加价格的最后一个均价

    private List<StockPrice> mStockPriceList = new ArrayList<>();
    private int mLastExactStockPriceIndex = -1;

    HashMap<Integer, List<StrategyResult>> mStrategyResultMap = new LinkedHashMap<>();

    public Stock(){
        this.mKBaseList = Arrays.asList(new K30Minutes(this));

        for(BaseStrategy baseStrategy : mStrategyList){
            mStrategyResultMap.put(baseStrategy.getStrategyId(), new ArrayList<StrategyResult>());
        }
    }

    public Stock(String id, String name, int monitorType){
        this.id = id;
        this.name = name;
        this.monitorType = monitorType;
        this.mKBaseList = Arrays.asList(new K30Minutes(this));

        for(BaseStrategy baseStrategy : mStrategyList){
            mStrategyResultMap.put(baseStrategy.getStrategyId(), new ArrayList<StrategyResult>());
        }
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
//        Log.d("lwd", "addStockPrice time:" + stockPrice.getTime());
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

            // 补充均价
            saveOrReadLatestAveragePrice(stockPrice);
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
            // 将strategyResult分类型存储

        }
        return strategyResultList;
    }

    /**
     * 保证每个价格对象都有最新的均价
     * 若有均价，则存储
     * 若没有均价，则读取
     * @param stockPrice
     */
    private void saveOrReadLatestAveragePrice(StockPrice stockPrice){
        if(stockPrice.getAvgPrice() != -1){
            setLatestAvgPrice(stockPrice.getAvgPrice());
        }
        else{
            stockPrice.setAvgPrice(getLatestAvgPrice());
        }
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
     * 得到最近的买卖时间点
     */
    public String getRecentDealTips(){
        List<StrategyResult> strategyResultList = mStrategyResultMap.get(new MinuteRiseStrategy().getStrategyId());
        if(strategyResultList.size() > 0){
            return strategyResultList.get(strategyResultList.size()-1).toString();
        }
        return "";
    }

    public List<String> getAllMinuteRiseStrategyResult(){
        List<StrategyResult> strategyResultList = mStrategyResultMap.get(new MinuteRiseStrategy().getStrategyId());
        List<String> strategyResultStringList = new ArrayList<>();
        for(StrategyResult strategyResult : strategyResultList){
            strategyResultStringList.add(0, strategyResult.toString());
        }
        return strategyResultStringList;
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
                    // 将策略结果分类加入字典
                    mStrategyResultMap.get(strategyResult.getStrategyId()).add(strategyResult);

                    // 对价格界面进行更新
                    if(strategyResult.getStrategyId() == new MinuteRiseStrategy().getStrategyId()
                        && getMonitorType() != 0){
                        StockManager.notifyPriceMonitorStockListChange(strategyResult.getStockId());
                    }
                }
            }
        }

        // 打印买卖点信息
//        for(StrategyResult strategyResult : strategyResultList){
//            Log.d("lwd", strategyResult.toLongString());
//        }
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
            if(mDayMaPrice != null){
                mDayMaPrice.setMa5(sum);
            }

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

        StockManager.flushPriceMonitorStockList(this);

        StockManager.updateDataAndFlushStockMonitorStockList(this);
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

    public float getLatestAvgPrice() {
        return mLatestAvgPrice;
    }

    public void setLatestAvgPrice(float mLatestAvgPrice) {
        this.mLatestAvgPrice = mLatestAvgPrice;
    }

    public HashMap<Integer, List<StrategyResult>> getStrategyResultMap() {
        return mStrategyResultMap;
    }

    public StrategyResult getLastStrategyResult(int strategyId){
        List<StrategyResult> strategyResultList = mStrategyResultMap.get(strategyId);
        if(strategyResultList.size() < 1){
            return null;
        }
        return strategyResultList.get(strategyResultList.size()-1);
    }

    public void setStrategyResultMap(HashMap<Integer, List<StrategyResult>> mStrategyResultMap) {
        this.mStrategyResultMap = mStrategyResultMap;
    }

    @NonNull
    @Override
    public String toString() {
        if(id != null && name != null && currentPrice != null){
            return String.format("id:%s, name:%s, current_price:%s", id, name, currentPrice.toString());
        }
        return super.toString();
    }

    public String getStockMonitorStrategyResultString(){
        return getMLTAStrategyResultString();
    }

    public String getVBBOrSuddenUpStrategyResultString(){
        if(this.mStrategyResultList == null){
            return "没有信息";
        }
        String resultString = "";
        for(StrategyResult strategyResult : this.mStrategyResultList){
            if(strategyResult != null
                    && (strategyResult.getStrategyId() == R.integer.strategyVBB
                    || strategyResult.getStrategyId() == R.integer.strategySuddenUp)){
                resultString += strategyResult.toString() + "\n";
            }
        }
        return resultString;
    }

    public String getMLTAStrategyResultString(){
        if(this.mStrategyResultList == null){
            return "没有信息";
        }
        String resultString = "";
        for(StrategyResult strategyResult : this.mStrategyResultList){
            if(strategyResult != null
                    && strategyResult.getStrategyId() == R.integer.strategyMinuteLongToArrange){
                resultString += strategyResult.toStockMonitorString() + "\n";
            }
        }
        return resultString;
    }


}
