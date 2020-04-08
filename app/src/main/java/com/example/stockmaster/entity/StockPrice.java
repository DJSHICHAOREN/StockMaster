package com.example.stockmaster.entity;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockPrice implements Serializable {
    public String id, name="";
    public Date time;
    public float price;
    public Stock.DealType dealType = Stock.DealType.NULL;

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public StockPrice(){
    }

    public StockPrice(String id, String time, String price){
        setId(id);
        setTime(time);
        setPrice(price);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price){
        this.price = Float.parseFloat(price);
    }

    public void setTime(String timeStr) throws NumberFormatException{
        try {
            if(timeStr.contains("/")){
                timeStr = timeStr.replaceAll("/", "-");
            }
            this.time = mSimpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDealType(Stock.DealType dealType) {
        this.dealType = dealType;
        Log.d("lwd", String.format("set id:%s, price:%s, dealType:%s, time:%s", id, price, dealType, time));
    }

    public Stock.DealType getDealType() {
        return dealType;
    }

    public String getPrice() {
        return String.format("%.3f", price);
    }

    public String getNotificationContent(){
        return id + " " + toString();
    }

    public int getNotificationId(){
        return Integer.parseInt(this.id.substring(2));
    }

    @Override
    public String toString() {
        String dealTime = time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
        if(dealType == Stock.DealType.BUY){
            return String.format("买点，时间：%s，价格：%s", dealTime, getPrice());
        }
        else if(dealType == Stock.DealType.SALE){
            return String.format("卖点，时间：%s，价格：%s", dealTime , getPrice());
        }
        return "非买卖点";
    }

    public String toStringWithId(){
        return String.format("%s, %s", id, toString());
    }
}
