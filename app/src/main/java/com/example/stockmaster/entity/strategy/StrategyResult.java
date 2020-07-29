package com.example.stockmaster.entity.strategy;

import com.example.stockmaster.R;
import com.example.stockmaster.util.DateUtil;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name="StrategyAnalyseResult")
public class StrategyResult {
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

    public StrategyResult(String stockId, float price, int strategyId, Date time, int type) {
        this.stockId = stockId;
        this.price = price;
        this.strategyId = strategyId;
        this.time = time;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public int getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getNotificationId(){
        return Integer.parseInt(this.stockId.substring(2));
    }

    public String toNotificationString(){
        String saleType = this.type == 0 ? "B" : "S";
        String strategyType = "";
        switch (this.strategyId){
            case R.integer.strategyVBB:{
                strategyType = "vbb";
                break;
            }
            case R.integer.strategyMinuteRise:{
                strategyType = "mr";
                break;
            }
        }
        return String.format("%s %s，%s，%f，%s", strategyType, saleType, DateUtil.convertDateToShortMinuteString(time), getPrice(), getStockId());
    }

    @Override
    public String toString(){
        String saleType = this.type == 0 ? "B" : "S";
        String strategyType = "";
        switch (this.strategyId){
            case R.integer.strategyVBB:{
                strategyType = "vbb";
                break;
            }
            case R.integer.strategyMinuteRise:{
                strategyType = "mr";
                break;
            }
        }
        return String.format("%s %s，时间：%s，价格：%f, stockId:%s", strategyType, saleType, DateUtil.convertDateToShortMinuteString(time), getPrice(), getStockId());
    }

    public String toLongString(){
        String saleType = this.type == 0 ? "B" : "S";
        String strategyType = "";
        switch (this.strategyId){
            case R.integer.strategyVBB:{
                strategyType = "vbb";
                break;
            }
            case R.integer.strategyMinuteRise:{
                strategyType = "mr";
                break;
            }
        }
        return String.format("%s %s，时间：%s，价格：%f, stockId:%s", strategyType, saleType, getTime(), getPrice(), getStockId());
    }
}
