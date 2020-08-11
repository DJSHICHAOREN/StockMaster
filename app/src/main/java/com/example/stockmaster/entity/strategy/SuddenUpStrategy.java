package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;

public class SuddenUpStrategy extends BaseStrategy {
    private StrategyResult lastSuddenUpStrategyResult;
    public SuddenUpStrategy() {
        super(R.integer.strategySuddenUp);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        if(stockForm == null){
//            Log.d("lwd", "SuddenUp analyse stockFormList == null");
            return null;
        }

        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.strategySuddenUp){
            if(lastSuddenUpStrategyResult != null){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                lastSuddenUpStrategyResult = strategyResult;
            }
        }
        return strategyResult;
    }
}
