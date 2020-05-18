package com.example.stockmaster.entity.k;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaBase;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.MaCalculater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KBase {
    private String TIME_POINT_STRING = "" +
            "09:45:00, " +
            "10:00:00, 10:15:00, 10:30:00, 10:45:00, " +
            "11:00:00, 11:15:00, 11:30:00, 11:45:00," +
            "12:00:00," +
            "13:15:00, 13:30:00, 13:45:00, " +
            "14:00:00, 14:15:00, 14:30:00, 14:45:00, " +
            "15:00:00, 15:15:00, 15:30:00, 15:45:00, " +
            "16:00:00, 16:10:00";
    private MaBase ma5PriceList = new MaBase(5);
    private MaBase ma10PriceList = new MaBase(10);
    private MaBase ma30PriceList = new MaBase(30);
    private MaBase ma60PriceList = new MaBase(60);
    private List<MaBase> maBaseList = new ArrayList<>();
    private List<StockPrice> mKeyStockPriceList = new ArrayList<>();
    private List<MaState> maStateList = new ArrayList<>();

    public KBase(){
        maBaseList.add(ma5PriceList);
        maBaseList.add(ma10PriceList);
        maBaseList.add(ma30PriceList);
        maBaseList.add(ma60PriceList);
    }

    /**
     * 添加关键价格列表
     * @param keyStockPriceList
     */
    public void setKeyStockPriceList(List<StockPrice> keyStockPriceList) {
        if(keyStockPriceList == null){
            Log.e("lwd", "keyStockPriceList 为null");
            return;
        }
        // 过滤股票价格
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
        for(MaBase maBase : maBaseList){
            maBase.setKeyStockPriceList(this.mKeyStockPriceList);
        }

        for(int i=0; i<keyStockPriceList.size(); i++){
            maStateList.add(MaCalculater.calMaState(keyStockPriceList.subList(0, i)));
        }
    }

    private String getDoubleNumString(int num){
        return num >= 10 ? num + "" : "0" + num;
    }

    public void setTIME_POINT_STRING(String TIME_POINT_STRING) {
        this.TIME_POINT_STRING = TIME_POINT_STRING;
    }
}
