package com.example.stockmaster.entity.ma;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.ui.activity.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MaBase {
    private final String TIME_POINT_STRING = "" +
            "09:45:00, " +
            "10:00:00, 10:15:00, 10:30:00, 10:45:00, " +
            "11:00:00, 11:15:00, 11:30:00, 11:45:00," +
            "12:00:00," +
            "13:15:00, 13:30:00, 13:45:00, " +
            "14:00:00, 14:15:00, 14:30:00, 14:45:00, " +
            "15:00:00, 15:15:00, 15:30:00, 15:45:00, " +
            "16:00:00, 16:10:00";
    private List<StockPrice> maPriceList = new ArrayList<>();
    //
    private List<StockPrice> mKeyStockPriceList;

    public MaBase(List<StockPrice> keyStockPriceList){

    }

    public void setMaStockPriceList(List<StockPrice> mKeyStockPriceList) {
        for(StockPrice stockPrice : mKeyStockPriceList){
            String minuteTime = stockPrice.getTime().toString();
            if(TIME_POINT_STRING.indexOf(minuteTime) == -1){
                this.mKeyStockPriceList.add(stockPrice);
            }
        }
    }
}
