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
//        List<StockPrice> filteredStockPriceList = filterKeyStockPrice(stockPriceList);
        Log.d("lwd", String.format("%d分钟K线分析_stockId:%s", mKLevel, mStockId));
        // 添加价格列表之后计算均值
        for(int i=MaCalculater.getMinCountedDay(); i<stockPriceList.size(); i++){
            maStateList.add(MaCalculater.calMaState( filterPreviousKeyStockPrice(stockPriceList.subList(0, i)) ));
            maStateAnalyser.analyse(mStockId, maStateList, mKLevel);
        }
    }

    /**
     * 最后一个stockPrice不变，他的时间代表
     * 其他的找前面的最近的keyStockPrice
     * @param stockPriceList
     * @return
     */
    private List<StockPrice> filterPreviousKeyStockPrice(List<StockPrice> stockPriceList){
        List<StockPrice> filteredStockPriceList = new ArrayList<>();
        StockPrice lastStockPrice = stockPriceList.get(filteredStockPriceList.size()-1);
        for(StockPrice stockPrice : stockPriceList){

        }
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
            Date time = stockPrice.getTime();
            String minuteTime = "";
            if(time != null){
                String hour = getDoubleNumString(time.getHours());
                String minute = getDoubleNumString(time.getMinutes());
                String second = getDoubleNumString(time.getSeconds());
                minuteTime = hour + ":" + minute + ":" + second;
            }
            if(TIME_POINT_STRING.indexOf(minuteTime) != -1){
                filteredStockPriceList.add(stockPrice);
            }
        }
        return filteredStockPriceList;
    }

    public String convertDateToMinutesString(Date time){

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
