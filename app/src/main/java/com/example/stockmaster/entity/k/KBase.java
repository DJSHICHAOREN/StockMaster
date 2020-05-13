package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.StockPrice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KBase {
    private final String TIME_POINT_STRING = "" +
            "09:45:00, " +
            "10:00:00, 10:15:00, 10:30:00, 10:45:00, " +
            "11:00:00, 11:15:00, 11:30:00, 11:45:00," +
            "12:00:00," +
            "13:15:00, 13:30:00, 13:45:00, " +
            "14:00:00, 14:15:00, 14:30:00, 14:45:00, " +
            "15:00:00, 15:15:00, 15:30:00, 15:45:00, " +
            "16:00:00, 16:10:00";
    private List<StockPrice> ma5PriceList = new ArrayList<>();
    private List<StockPrice> ma10PriceList = new ArrayList<>();
    private List<StockPrice> ma20PriceList = new ArrayList<>();
    private List<StockPrice> ma30PriceList = new ArrayList<>();
    private List<StockPrice> ma60PriceList = new ArrayList<>();
    //
    private List<StockPrice> mKeyStockPriceList = new ArrayList<>();

    public KBase(){

    }

    /**
     * 添加关键价格列表
     * @param keyStockPriceList
     */
    public void setKeyStockPriceList(List<StockPrice> keyStockPriceList) {
        if(keyStockPriceList == null){
            return;
        }
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
                this.mKeyStockPriceList.add(stockPrice);
            }
        }
        // 添加价格列表之后计算均值
        for(int i=5; i <= mKeyStockPriceList.size(); i++){

        }
        calMaPrice();
    }

    public void calMaPrice(){
        for
    }

    private String getDoubleNumString(int num){
        return num >= 10 ? num + "" : "0" + num;
    }
}
