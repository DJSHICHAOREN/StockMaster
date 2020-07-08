package com.example.stockmaster.entity.k;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.MaCalculater;
import com.example.stockmaster.util.MaStateAnalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KBase {
    public String TIME_POINT_STRING = "";
    public List<String> TIME_POINT_STRING_LIST;
    private List<MaState> maStateList = new ArrayList<>();
    private MaStateAnalyser maStateAnalyser = MaStateAnalyser.getInstance();
    private List<StockPrice> qualifiedPricePointList = new ArrayList<>();
    private List<StockPrice> mKeyStockPriceList;
    private int mKLevel = 0; // K线的级别
    private String mStockId;
    private Stock mStock;
    public KBase(Stock stock, String TIME_POINT_STRING, int kLevel){
        this.TIME_POINT_STRING = TIME_POINT_STRING;
        TIME_POINT_STRING_LIST = Arrays.asList(TIME_POINT_STRING.split(","));
        mKLevel = kLevel;
        mStockId = stock.getId();
        mStock = stock;
    }

    /**
     * 更新最后一个价格后重新计算
     * 由于五日线虽然滞后，但也不是滞后非常多，所以直接忽略中间缺少的maState
     * @param wholeStockPriceList
     */
    public List<StockForm> updateLastStockPrice(List<StockPrice> wholeStockPriceList){
        if(wholeStockPriceList == null){
            Log.e("lwd", "upDateLastStockPrice wholeStockPriceList 为null");
        }
        // 得到关键数据
        ArrayList<StockPrice> updatedStockPriceList = new ArrayList<>();
        updatedStockPriceList.addAll(mKeyStockPriceList);
        updatedStockPriceList.add(wholeStockPriceList.get(wholeStockPriceList.size()-1));
        // 得到最新的maState
        MaState newMaState = MaCalculater.calMaState(updatedStockPriceList);
        // 得到存储的最后一个maState
        MaState lastMaState = maStateList.get(maStateList.size() - 1);
        // 当最新的maState在最后一个maState之后时，将其加入
        if(newMaState.getTime().after(lastMaState.getTime())){
            maStateList.add(newMaState);
        }
        List<StockForm> stockFormList = maStateAnalyser.analyse(mStockId, maStateList, mKLevel, TIME_POINT_STRING, mStock, wholeStockPriceList);
        return stockFormList;
    }

    /**
     * 将分时数据与五日数据之间的空白价格补上。
     * @param wholeStockPriceList
     * @return
     */
    public List<StockPrice> completeStockPriceList(List<StockPrice> wholeStockPriceList){
        // 得到mKeyStockPriceList的最后一个，看它的下一个是否在
        List<StockPrice> completedKeyStockPriceList = new ArrayList<>();
        completedKeyStockPriceList.addAll(mKeyStockPriceList);

        StockPrice lastKeyStockPrice = completedKeyStockPriceList.get(completedKeyStockPriceList.size()-1);
        Date nextKeyDate =  getNextKeyDate(lastKeyStockPrice.getTime());

        StockPrice lastMinuteStockPrice = wholeStockPriceList.get(wholeStockPriceList.size()-1);
        while(nextKeyDate.before(lastMinuteStockPrice.getTime())){
            completedKeyStockPriceList.add(new StockPrice(lastKeyStockPrice.getStockId(),
                    nextKeyDate, lastMinuteStockPrice.getPrice(), lastKeyStockPrice.getQueryType()));

            nextKeyDate = getNextKeyDate(lastKeyStockPrice.getTime());
        }
        completedKeyStockPriceList.add(lastMinuteStockPrice);
        return completedKeyStockPriceList;
    }

    public Date getNextKeyDate(Date stockPriceTime){
        String shortPriceTime = convertDateToShortString(stockPriceTime);
        int stockPriceIndex = TIME_POINT_STRING_LIST.indexOf(shortPriceTime);
        int nextStockPriceIndex=0;
        if(stockPriceIndex == TIME_POINT_STRING_LIST.size()-1){
            nextStockPriceIndex = 0;
        }
        else {
            nextStockPriceIndex = nextStockPriceIndex + 1;
        }
        Date date = new Date(stockPriceTime.getYear() + "-" + stockPriceTime.getMonth()
                + "-" + stockPriceTime.getDate() + " " + TIME_POINT_STRING_LIST.get(nextStockPriceIndex));
        return date;
    }

    /**
     * 添加关键价格列表
     * @param stockPriceList
     */
    public List<StockForm> setKeyStockPriceList(List<StockPrice> stockPriceList) {
        if(stockPriceList == null){
            Log.e("lwd", "setKeyStockPriceList stockPriceList 为null");
        }
        List<StockPrice> filteredStockPriceList = filterKeyStockPrice(stockPriceList);
        Log.d("lwd", String.format("%d分钟K线分析_stockId:%s", mKLevel, mStockId));
        List<StockForm> stockFormList = new ArrayList<>();
        // 添加价格列表之后计算均值
        for(int i=MaCalculater.getMinCountedDay(); i<stockPriceList.size(); i++){
            MaState maState = MaCalculater.calMaState( filterPreviousKeyStockPrice(stockPriceList.subList(0, i), filteredStockPriceList));
            if(maState != null && maState.getMa5() != 0){
                maStateList.add(maState);
            }
            else{
                continue;
            }
            calLastMaStateCandleArgs(maStateList);

            // 蜡烛图日志
//            if(isDateTheKeyTime(maState.getTime())){
//                Log.d("lwd", String.format("level:%d %s", mKLevel, maState.toCandleString()));
//            }
            stockFormList.addAll(maStateAnalyser.analyse(mStockId, maStateList, mKLevel, TIME_POINT_STRING, mStock, stockPriceList.subList(0, i)));
        }
        return stockFormList;
    }

    private void calLastMaStateCandleArgs(List<MaState> maStateList){
        if(maStateList.size() < 1){
            return;
        }
        MaState lastMaState = maStateList.get(maStateList.size()-1);
        if(maStateList.size() == 1){
            lastMaState.setCandleArgs(lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice());
            lastMaState.setSupportPrice(-1);
        }
        else{
            MaState previousMaState = maStateList.get(maStateList.size()-2);

            // 如果前一个是关键点价格点
            // 支撑价是上一个前一段的最低价
            if(isDateTheKeyTime(previousMaState.getTime())){
                lastMaState.setCandleArgs(lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice());
                lastMaState.setSupportPrice(previousMaState.getLowestPrice());
            }
            else{
                // 得到最高价
                if(lastMaState.getPrice() > previousMaState.getHighestPrice()){
                    lastMaState.setHighestPrice(lastMaState.getPrice());
                }
                else{
                    lastMaState.setHighestPrice(previousMaState.getHighestPrice());
                }

                // 得到最低价
                if(lastMaState.getPrice() < previousMaState.getLowestPrice()){
                    lastMaState.setLowestPrice(lastMaState.getPrice());
//                    Log.d("lwd", String.format("new lowest price：%f, time:%s", lastMaState.getPrice(),
//                            lastMaState.getTime().toString()));
                }
                else{
                    lastMaState.setLowestPrice(previousMaState.getLowestPrice());
                }

                // 得到开盘价
                lastMaState.setBeginPrice(previousMaState.getBeginPrice());
                // 得到收盘价
                lastMaState.setEndPrice(lastMaState.getPrice());
                // 得到支撑价
                lastMaState.setSupportPrice(previousMaState.getSupportPrice());
            }
        }
    }


    /**
     * 最后一个stockPrice不变，他的时间代表
     * 其他的找前面的最近的keyStockPrice
     * @param stockPriceList
     * @return
     */
    private List<StockPrice> filterPreviousKeyStockPrice(List<StockPrice> stockPriceList, List<StockPrice> filteredStockPriceList){
        List<StockPrice> resultStockPriceList = new ArrayList<>();
        StockPrice lastStockPrice = stockPriceList.get(stockPriceList.size()-1);
        // 将最后一个价格之前的关键价格加入列表
        for(StockPrice stockPrice : filteredStockPriceList){
            if(stockPrice.getTime().before(lastStockPrice.getTime())){
                resultStockPriceList.add(stockPrice);
            }
        }
        // 将最后一个价格加入列表
        resultStockPriceList.add(lastStockPrice);
        return resultStockPriceList;
    }

    /**
     * 过滤股票价格
     * @param keyStockPriceList
     * @return
     */
    private List<StockPrice> filterKeyStockPrice(List<StockPrice> keyStockPriceList){
        // 过滤股票价格
        List<StockPrice> filteredStockPriceList = new ArrayList<>();
        for(StockPrice stockPrice : keyStockPriceList){
            if(isDateTheKeyTime(stockPrice.getTime())){
                filteredStockPriceList.add(stockPrice);
            }
        }
        // 存储关键价格对象
        mKeyStockPriceList = filteredStockPriceList;
        return filteredStockPriceList;
    }

    private boolean isDateTheKeyTime(Date time){
        String minuteTime = convertDateToShortString(time);
        if(TIME_POINT_STRING.indexOf(minuteTime) != -1){
            return true;
        }
        return false;
    }

    /**
     * 将Date对象转为可以对比的字符串
     * @param time
     * @return
     */
    private String convertDateToShortString(Date time){
        String minuteTime = "";
        if(time != null){
            String hour = getDoubleNumString(time.getHours());
            String minute = getDoubleNumString(time.getMinutes());
            String second = getDoubleNumString(time.getSeconds());
            minuteTime = hour + ":" + minute + ":" + second;
        }
        return minuteTime;
    }

    private String getDoubleNumString(int num){
        return num >= 10 ? num + "" : "0" + num;
    }

    public void setTIME_POINT_STRING(String TIME_POINT_STRING) {
        this.TIME_POINT_STRING = TIME_POINT_STRING;
    }

    public List<StockPrice> getQualifiedPricePointList() {
        return qualifiedPricePointList;
    }
}
