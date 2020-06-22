package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.util.List;

public class K30Minutes extends KBase {

    public K30Minutes(Stock stock) {
        super(stock, "10:00:00, 10:30:00, " +
                "11:00:00, 11:30:00, " +
                "12:00:00," +
                "13:30:00," +
                "14:00:00, 14:30:00, " +
                "15:00:00, 15:30:00, " +
                "16:00:00, 16:10:00", 30);
    }
}
