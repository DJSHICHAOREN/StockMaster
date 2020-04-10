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
    float ma10, ma30, ma50, ma100, ma250;



    public enum DealType implements Serializable{SALE, BUY, NULL}
    private DealType previousDealType = DealType.SALE;
    StockAnalyser mStockAnalyser;
    private List<Float> previousFourDayPriceList;

    public Stock(StockAnalyser stockAnalyser, String id, String name){
        this.id = id;
        this.name = name;
        mStockAnalyser = stockAnalyser;
    }

    /**
     * 加入股票价格
     * @param stockPrice
     * @return 如果成功加入，则返回true，否则返回false
     */
    public boolean addStockPrice(StockPrice stockPrice){
        currentPrice = stockPrice;
        if(!todayStockPriceList.isEmpty()){
            StockPrice lastStockPrice  = todayStockPriceList.get(todayStockPriceList.size()-1);
            // 对于同一分钟的价格，去掉旧的，加入新的
            if(stockPrice.time.compareTo(lastStockPrice.time) == 0 && lastStockPrice.price != stockPrice.price){
                todayStockPriceList.remove(todayStockPriceList.size()-1);
                todayStockPriceList.add(stockPrice);
                return true;
            }
            // 比列表靠后的时间，添加进列表
            else if(stockPrice.time.after(lastStockPrice.time)){
                todayStockPriceList.add(stockPrice);
                return true;
            }
        }
        // 如果价格列表为空，则直接加入
        else{
            todayStockPriceList.add(stockPrice);
            return true;
        }

        return false;
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
//            Log.d("lwd", "上一个是买点");
            stockPrice.setDealType(DealType.BUY);
            dealPriceList.add(stockPrice);
//            if(id.equals("00038")){
//                Log.d("lwd", "00038买点");
//            }
            return true;
        }
        else if(previousDealType == DealType.BUY && dealType == DealType.SALE){
            saleStockPriceList.add(stockPrice);
            previousDealType = DealType.SALE;
//            Log.d("lwd", "上一个是卖点");
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

    public void setPreviousFourDayPriceList(List<Float> previousFourDayPriceList) {
        this.previousFourDayPriceList = previousFourDayPriceList;
    }

    public float getMa5() {
        float sum = 0;
        if(previousFourDayPriceList != null && previousFourDayPriceList.size() == 4 && currentPrice != null){
            for(Float price : previousFourDayPriceList){
                sum += price;
            }
            sum += currentPrice.price;
            sum /= 5;
        }
        else{
            Log.d("lwd", "无法获取五日均价");
        }
        return sum;
    }

    public void setMAPrice(String ma10, String ma30, String ma50, String ma100, String ma250) {
        if(!ma10.equals("NA")){
            this.ma10 = Float.parseFloat(ma10);
        }
        if(!ma30.equals("NA")){
            this.ma30 = Float.parseFloat(ma30);
        }
        if(!ma50.equals("NA")){
            this.ma50 = Float.parseFloat(ma50);
        }
        if(!ma100.equals("NA")){
            this.ma100 = Float.parseFloat(ma100);
        }
        if(!ma250.equals("NA")){
            this.ma250 = Float.parseFloat(ma250);
        }
    }
}
