package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.StockPrice;

import java.util.List;

public class K60Minutes extends KBase {

    public K60Minutes(String stockId) {
        super(stockId, "10:30:00, " +
                "11:30:00, " +
                "13:30:00," +
                "14:30:00, " +
                "15:30:00, " +
                "16:00:00, 16:10:00", 60);
    }
}
