package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;

public class MinuteRiseStrategy extends BaseStrategy {
    private StockForm lastStockForm;
    public MinuteRiseStrategy() {
        super(R.integer.strategyMinuteRise);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        if(stockForm == null){
//            Log.d("lwd", "MinuteRiseStrategy analyse stockFormList == null");
            return null;
        }

        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formMinuteRise){
//            if(stockForm.getType() == 0 && stock.getMonitorType() == 0){
//                return null;
//            }
//            else if(stockForm.getType() == 1 && stock.getMonitorType() != 2){
//                return null;
//            }

            // 第一个要添加买点，然后买卖点交替添加
            boolean isGenStrategy = false;
            if(lastStockForm == null){
                if(stockForm.getType() == 0){
                    isGenStrategy = true;
                }
            }
            else{
                if((stockForm.getType() == 0 && lastStockForm.getType() == 1) || (stockForm.getType() == 1 && lastStockForm.getType() == 0) ){
                    isGenStrategy = true;
                }
            }
            if(isGenStrategy){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), stockForm.getType());
                lastStockForm = stockForm;
                mStrategyResultList.add(strategyResult);
//                Log.d("lwd", strategyResult.toLongString());
            }
        }
        return strategyResult;
    }
}
