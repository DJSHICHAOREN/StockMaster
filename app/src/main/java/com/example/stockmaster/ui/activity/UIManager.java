package com.example.stockmaster.ui.activity;

import com.example.stockmaster.entity.Stock;

public interface UIManager{
    void refreshUIWhenReceiveNewPrice(Stock stock);

    void refreshUIWhenGetNewBuyPoint();
}