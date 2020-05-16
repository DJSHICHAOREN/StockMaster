package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.StockPrice;

import java.util.List;

public class K30Minutes extends KBase {
    public String TIME_POINT_STRING =
            "10:00:00, 10:30:00, " +
            "11:00:00, 11:30:00, " +
            "12:00:00," +
            "13:30:00," +
            "14:00:00, 14:30:00, " +
            "15:00:00, 15:30:00, " +
            "16:00:00, 16:10:00";

    @Override
    public void setKeyStockPriceList(List<StockPrice> keyStockPriceList) {
        super.setTIME_POINT_STRING(TIME_POINT_STRING);
        super.setKeyStockPriceList(keyStockPriceList);
    }
}
