package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;

public class SuddenUpStrategy extends BaseStrategy {
    public SuddenUpStrategy() {
        super(R.integer.strategySuddenUp);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        return analyse(stockForm, stock, R.integer.formSuddenUp, (float) 1);
    }
}
