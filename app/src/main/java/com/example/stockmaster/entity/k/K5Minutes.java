package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.StockPrice;

import java.util.List;

public class K5Minutes extends KBase {
    public String TIME_POINT_STRING =
            "09:35:00, 09:40:00, 09:45:00, 09:50:00, 09:55:00," +
            "10:00:00, 10:05:00, 10:10:00, 10:15:00, 10:20:00, 10:25:00, 10:30:00, 10:35:00, 10:40:00, 10:45:00, 10:50:00, 10:55:00," +
            "11:00:00, 11:05:00, 11:10:00, 11:15:00, 11:20:00, 11:25:00, 11:30:00, 11:35:00, 11:40:00, 11:45:00, 11:50:00, 11:55:00," +
            "12:00:00," +
            "13:05:00, 13:10:00, 13:15:00, 13:20:00, 13:25:00, 13:30:00, 13:35:00, 13:40:00, 13:45:00, 13:50:00, 13:55:00, " +
            "14:00:00, 14:05:00, 14:10:00, 14:15:00, 14:20:00, 14:25:00, 14:30:00, 14:35:00, 14:40:00, 14:45:00, 14:50:00, 14:55:00." +
            "15:00:00, 15:05:00, 15:10:00, 15:15:00, 15:20:00, 15:25:00, 15:30:00, 15:35:00, 15:40:00, 15:45:00, 15:50:00, 15:55:00." +
            "16:00:00, 16:05:00, 16:10:00";

    @Override
    public List<StockPrice> setKeyStockPriceList(List<StockPrice> keyStockPriceList) {
//        super.setTIME_POINT_STRING(TIME_POINT_STRING);
        return super.setKeyStockPriceList(keyStockPriceList);
    }
}
