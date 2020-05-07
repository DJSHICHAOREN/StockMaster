package com.example.stockmaster.ui.activity.recommand;


import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockManager;

import java.util.List;

public class RecommandPresent extends BasePresent {

    public RecommandPresent(AppCompatActivity view) {
        super(view);
    }

    public List<Stock> getLineUpStocks(){
        return StockManager.getLineUpStocks();
    }

}
