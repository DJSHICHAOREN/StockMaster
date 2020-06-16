package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VBBStrategy extends BaseStrategy {
    private Stock.DealType previousDealType = Stock.DealType.NULL;
    public VBBStrategy() {
        super(R.integer.strategyFallThroughSupport);
    }

    @Override
    public List<StrategyAnalyseResult> analyse(List<StockForm> stockFormList, List<Date> dateList, String stockId) {
        List<StrategyAnalyseResult> strategyAnalyseResultList = new ArrayList<>();
        // 若之前有买点，则写入卖出策略
        for(StockForm stockForm : stockFormList){
            if(stockForm.getkLevel() != 30){
                continue;
            }
            // 添加买卖点
            if(stockForm.getFormId() == R.integer.formLongToArrange){
                strategyAnalyseResultList.add(new StrategyAnalyseResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), R.integer.typeStrategyBuy));
                previousDealType = Stock.DealType.BUY;
            }
            else if(previousDealType == Stock.DealType.BUY && stockForm.getFormId() == R.integer.formFallThroughSupport){
                strategyAnalyseResultList.add(new StrategyAnalyseResult(stockId, stockForm.getPrice(), getStrategyId(), stockForm.getTime(), R.integer.typeStrategySale));
                previousDealType = Stock.DealType.SALE;
            }
        }
        return strategyAnalyseResultList;
    }
}
