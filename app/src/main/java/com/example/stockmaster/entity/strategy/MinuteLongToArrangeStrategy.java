package com.example.stockmaster.entity.strategy;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.util.StockManager;

import java.util.Date;

import static java.lang.Math.abs;

public class MinuteLongToArrangeStrategy extends BaseStrategy {

    public MinuteLongToArrangeStrategy() {
        super(R.integer.strategyMinuteLongToArrange);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        return analyse(stockForm, stock, R.integer.formMinuteLongToArrange, (float) 2.5);
    }

}
