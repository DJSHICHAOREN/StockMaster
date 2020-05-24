package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.StockPrice;

import java.util.List;

public class K15Minutes extends KBase {
    public String TIME_POINT_STRING = "" +
            "09:45:00, " +
            "10:00:00, 10:15:00, 10:30:00, 10:45:00, " +
            "11:00:00, 11:15:00, 11:30:00, 11:45:00," +
            "12:00:00," +
            "13:15:00, 13:30:00, 13:45:00, " +
            "14:00:00, 14:15:00, 14:30:00, 14:45:00, " +
            "15:00:00, 15:15:00, 15:30:00, 15:45:00, " +
            "16:00:00, 16:10:00";

    @Override
    public List<StockPrice> setKeyStockPriceList(List<StockPrice> keyStockPriceList) {
        super.setTIME_POINT_STRING(TIME_POINT_STRING);
       return super.setKeyStockPriceList(keyStockPriceList);
    }

}
