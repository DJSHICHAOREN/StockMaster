package com.example.stockmaster.entity.k;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.MaCalculater;
import com.example.stockmaster.util.MaStateAnalyser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KBase {
    public String TIME_POINT_STRING = "";
    private List<MaState> maStateList = new ArrayList<>();
    private MaStateAnalyser maStateAnalyser = MaStateAnalyser.getInstance();
    private List<StockPrice> qualifiedPricePointList = new ArrayList<>();
    private int mKLevel = 0; // K线的级别
    private String mStockId;
    public KBase(String stockId, String TIME_POINT_STRING, int kLevel){
        this.TIME_POINT_STRING = TIME_POINT_STRING;
        mKLevel = kLevel;
        mStockId = stockId;
    }

    /**
     * 添加关键价格列表
     * @param stockPriceList
     */
    public void setKeyStockPriceList(List<StockPrice> stockPriceList) {
        if(stockPriceList == null){
            Log.e("lwd", "stockPriceList 为null");
        }
        List<StockPrice> filteredStockPriceList = filterKeyStockPrice(stockPriceList);
        Log.d("lwd", String.format("%d分钟K线分析_stockId:%s", mKLevel, mStockId));
        // 添加价格列表之后计算均值
        for(int i=MaCalculater.getMinCountedDay(); i<stockPriceList.size(); i++){
            MaState maState = MaCalculater.calMaState( filterPreviousKeyStockPrice(stockPriceList.subList(0, i), filteredStockPriceList));
            if(maState != null && maState.getMa5() != 0){
                maStateList.add(maState);
            }
            calLastMaStateCandleArgs(maStateList);
            maStateAnalyser.analyse(mStockId, maStateList, mKLevel, TIME_POINT_STRING);
        }
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

            // 如果是关键点价格
            if(isDateTheKeyTime(lastMaState.getTime())){
                lastMaState.setCandleArgs(lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice());
                lastMaState.setSupportPrice(lastMaState.getLowestPrice());
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
                    Log.d("lwd", String.format("new lowest price：%f, time:%s", lastMaState.getPrice(),
                            lastMaState.getTime().toString()));
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
