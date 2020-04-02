package com.example.stockmaster.entity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stockmaster.util.StockAnalyser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Stock implements Serializable {
    public String id, name;
    public boolean isReceivedTodayData = false; //在为true时，才可以接收分钟的数据
    public StockPrice currentPrice;
    public List<StockPrice> todayStockPriceList = new ArrayList<>();
    public List<StockPrice> lowerStockPriceList = new ArrayList<>();
    public List<StockPrice> higherStockPriceList = new ArrayList<>();
    public List<StockPrice> buyStockPriceList = new ArrayList<>();
    public List<StockPrice> saleStockPriceList = new ArrayList<>();
    public List<StockPrice> dealPriceList = new ArrayList<>(); // 用来在detail页面显示全部交易列表
    public enum DealType implements Serializable{SALE, BUY, NULL}
    private DealType previousDealType = DealType.SALE;
    StockAnalyser mStockAnalyser;

    public Stock(StockAnalyser stockAnalyser, String id, String name){
        this.id = id;
        this.name = name;
        mStockAnalyser = stockAnalyser;
    }

    private void addPriceAndAnalyse(StockPrice stockPrice){
//        if(stockPrice.id.equals("hk02400")){
//            Log.d("lwd", String.format("心动公司：price:%s time:%s", stockPrice.price, stockPrice.time));
//        }
//        Log.d("lwd", String.format("公司：%s, 新的价格：%s, 时间：%s", stockPrice.id, stockPrice.price, stockPrice.time));
        todayStockPriceList.add(stockPrice);
        mStockAnalyser.analyse(this);
    }

    public void addStockPrice(StockPrice stockPrice){

        currentPrice = stockPrice;
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            // 对于同一分钟的价格，进行价格的更新
            if(stockPrice.time.compareTo(lastStockPrice.time) == 0 && lastStockPrice.price != stockPrice.price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                addPriceAndAnalyse(stockPrice);
            }
            // 比列表靠后的时间，添加进列表
            else if(stockPrice.time.after(lastStockPrice.time)){
                addPriceAndAnalyse(stockPrice);
            }
        }
        else{
            addPriceAndAnalyse(stockPrice);
        }
    }

    /**
     * 添加买点或者卖点，如果上一个是卖点，当前才是买点，上一个是买点，当前才是卖点
     * @param stockPrice 股票价格、时间点
     * @param dealType 交易类型：是买还是卖
     */
    public boolean addBuyAndSaleStockPrice(StockPrice stockPrice, DealType dealType){
        if(previousDealType == DealType.SALE && dealType == DealType.BUY){
            buyStockPriceList.add(stockPrice);
            previousDealType = DealType.BUY;
            Log.d("lwd", "上一个是买点");
            stockPrice.setDealType(DealType.BUY);
            dealPriceList.add(stockPrice);
            return true;
        }
        else if(previousDealType == DealType.BUY && dealType == DealType.SALE){
            saleStockPriceList.add(stockPrice);
            previousDealType = DealType.SALE;
            Log.d("lwd", "上一个是卖点");
            stockPrice.setDealType(DealType.SALE);
            dealPriceList.add(stockPrice);
            return true;
        }
        return false;
    }

    /**
     * 得到最近的买卖时间点
     */
    public String getRecentDealTips(){
        String dealTip = "";
        if(previousDealType == DealType.BUY && buyStockPriceList.size() > 0){
            StockPrice stockPrice = buyStockPriceList.get(buyStockPriceList.size()-1);
            dealTip = stockPrice.toString();
        }
        else if(previousDealType == DealType.SALE && saleStockPriceList.size() > 0){
            StockPrice stockPrice = saleStockPriceList.get(saleStockPriceList.size()-1);
            dealTip = stockPrice.toString();
        }
        return dealTip;
    }


    public void receiveTodayData(){
        isReceivedTodayData = true;
    }

    public float getCurrentPrice(){
        if(currentPrice != null){
            return currentPrice.price;
        }
        return -1;
    }

    public List<StockPrice> getDealStockPriceList() {
        return dealPriceList;
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
