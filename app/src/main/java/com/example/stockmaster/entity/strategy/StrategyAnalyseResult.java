package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.entity.Stock;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name="StrategyAnalyseResult")
public class StrategyAnalyseResult {
    @Column(name = "id", isId = true)
    public String id;

    @Column(name = "stockId")
    public String stockId;

    @Column(name = "strategyId")
    public int strategyId;

    @Column(name = "time")
    public Date time;

    @Column(name = "type")
    public int type; // 类型：购买：0，卖出：1

    @Column(name = "price")
    public float price;

    public StrategyAnalyseResult(String stockId, float price, int strategyId, Date time, int type) {
        this.stockId = stockId;
        this.price = price;
        this.strategyId = strategyId;
        this.time = time;
        this.type = type;
    }

    @Override
    public String toString(){
        return String.format("买点：%s，价格：%f", time, price);
    }
}
