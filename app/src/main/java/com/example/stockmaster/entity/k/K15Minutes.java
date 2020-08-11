package com.example.stockmaster.entity.k;

import com.example.stockmaster.entity.stock.Stock;

public class K15Minutes extends KBase {

    public K15Minutes(Stock stock) {
        super(stock, "09:45:00, " +
                "10:00:00, 10:15:00, 10:30:00, 10:45:00, " +
                "11:00:00, 11:15:00, 11:30:00, 11:45:00, " +
                "12:00:00, " +
                "13:15:00, 13:30:00, 13:45:00, " +
                "14:00:00, 14:15:00, 14:30:00, 14:45:00, " +
                "15:00:00, 15:15:00, 15:30:00, 15:45:00, " +
                "16:00:00", 15);
    }

}
