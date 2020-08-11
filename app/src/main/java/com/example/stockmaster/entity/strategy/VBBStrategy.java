package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.StockForm;

import java.util.List;

public class VBBStrategy extends BaseStrategy {
    private StrategyResult lastLongToArrangeStrategyResult;
    private StrategyResult lastFallThroughSupportStrategyResult;
    public VBBStrategy() {
        super(R.integer.strategyVBB);
    }

    @Override
    public StrategyResult analyse(StockForm stockForm, Stock stock) {
        if(stockForm == null){
//            Log.d("lwd", "VBBStrategy analyse stockFormList == null");
            return null;
        }

        // 添加买卖点
        // 出现多头向上形态
        StrategyResult strategyResult = null;
        if(stockForm.getFormId() == R.integer.formLongToArrange) {
            // 五天内第一次出现时
            if (lastLongToArrangeStrategyResult == null) {
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 0);
                lastLongToArrangeStrategyResult = strategyResult;
            }
        }
        else if(stockForm.getFormId() == R.integer.formFallThroughSupport){
            if(lastFallThroughSupportStrategyResult == null && lastLongToArrangeStrategyResult != null){
                strategyResult = new StrategyResult(stock.getId(), stockForm.getPrice(), getStrategyId(), stockForm.getTime(), 1);
                lastFallThroughSupportStrategyResult = strategyResult;
            }
        }

        return strategyResult;
    }




    public List<StrategyResult> getmStrategyResultList() {
        return mStrategyResultList;
    }

    public void setmStrategyResultList(List<StrategyResult> mStrategyResultList) {
        this.mStrategyResultList = mStrategyResultList;
    }
}
