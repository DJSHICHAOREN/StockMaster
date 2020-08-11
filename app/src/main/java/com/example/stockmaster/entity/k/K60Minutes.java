package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.stock.Stock;

public class K60Minutes extends KBase {

    public K60Minutes(Stock stock) {
        super(stock, "10:30:00, " +
                "11:30:00, " +
                "13:30:00, " +
                "14:30:00, " +
                "15:30:00, " +
                "16:00:00", 60);
    }
}
