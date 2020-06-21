package com.example.stockmaster.entity.evaluate;

import com.example.stockmaster.entity.strategy.StrategyResult;

import java.util.ArrayList;
import java.util.List;

public class SuccessResult {
    private String stockId;
    private float allIncreaseRate = 0;
    private float averageIncreaseRate = 0;
    List<StrategyResult> buyStrategyResultList = new ArrayList<>();
    List<StrategyResult> saleStrategyResultList = new ArrayList<>();
    List<Float> increaseRateList = new ArrayList<>();
    int size = 0;
    public SuccessResult(String stockId){
        this.stockId = stockId;
    }

    public void addBuyAndSaleStrategyResult(StrategyResult buyStrategyResult, StrategyResult saleStrategyResult){
        buyStrategyResultList.add(buyStrategyResult);
        saleStrategyResultList.add(saleStrategyResult);
        // 计算增长率
        float increaseRate = (saleStrategyResult.getPrice() - buyStrategyResult.getPrice()) / buyStrategyResult.getPrice();
        increaseRateList.add(increaseRate);
        // 存储数量和全部增长率
        size++;
        allIncreaseRate += increaseRate;
    }

    public String toString(){
        return String.format("stockId:%s, size:%d, allIncreaseRate:%f, averageIncreaseRate:%f",
                getStockId(), getSize(), getAllIncreaseRate(), getAverageIncreaseRate());
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public float getAllIncreaseRate() {
        return allIncreaseRate;
    }

    public void setAllIncreaseRate(float allIncreaseRate) {
        this.allIncreaseRate = allIncreaseRate;
    }

    public float getAverageIncreaseRate() {
        if(size == 0){
            return -1;
        }
        averageIncreaseRate = allIncreaseRate / size;
        return averageIncreaseRate;
    }

    public void setAverageIncreaseRate(float averageIncreaseRate) {
        this.averageIncreaseRate = averageIncreaseRate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
