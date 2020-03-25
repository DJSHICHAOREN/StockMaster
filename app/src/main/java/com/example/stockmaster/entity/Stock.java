package com.example.stockmaster.entity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stockmaster.util.StockAnalyser;

import java.util.ArrayList;
import java.util.List;


public class Stock {
    public String id, name;
    public StockPrice currentPrice;
    public List<StockPrice> todayStockPriceList = new ArrayList<>();
    public List<StockPrice> lowerStockPriceList = new ArrayList<>();
    public List<StockPrice> higherStockPriceList = new ArrayList<>();
    public List<StockPrice> buyStockPriceList = new ArrayList<>();
    public List<StockPrice> saleStockPriceList = new ArrayList<>();
    public enum DealType{SALE, BUY}
    private DealType previousDealType = DealType.SALE;

    StockAnalyser mStockAnalyser;
    public Stock(StockAnalyser stockAnalyser, String id, String name){
        this.id = id;
        this.name = name;
        mStockAnalyser = stockAnalyser;
    }

    public void addStockPrice(StockPrice stockPrice){
        currentPrice = stockPrice;
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            // 对于同一分钟的价格，进行价格的更新
            if(stockPrice.time.compareTo(lastStockPrice.time) == 0 && lastStockPrice.price != stockPrice.price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                todayStockPriceList.add(stockPrice);
            }
            // 比列表靠后的时间，添加进列表
            else if(stockPrice.time.after(lastStockPrice.time)){
                todayStockPriceList.add(stockPrice);
            }
        }
        else{
            todayStockPriceList.add(stockPrice);
        }

        mStockAnalyser.analyse(this);
    }

    /**
     * 添加买点或者卖点
     * @param stockPrice 股票价格、时间点
     * @param dealType 交易类型：是买还是卖
     */
    public void addBuyAndSaleStockPrice(StockPrice stockPrice, DealType dealType){
        if(previousDealType == DealType.SALE && dealType == DealType.BUY){
            buyStockPriceList.add(stockPrice);
            previousDealType = DealType.BUY;
            Log.d("lwd", "上一个是买点");
        }
        else if(previousDealType == DealType.BUY && dealType == DealType.SALE){
            saleStockPriceList.add(stockPrice);
            previousDealType = DealType.SALE;
            Log.d("lwd", "上一个是卖点");
        }
    }

    @NonNull
    @Override
    public String toString() {
        if(id != null && name != null && currentPrice != null){
            return String.format("id:%s, name:%s, current_price:%s", id, name, currentPrice.toString());
        }
        return super.toString();
    }
}
