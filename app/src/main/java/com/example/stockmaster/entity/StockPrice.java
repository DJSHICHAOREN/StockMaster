package com.example.stockmaster.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockPrice {
    public String id, name="";
    public Date time;
    public float price;
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

    public void setTime(String timeStr){
        try {
            this.time = mSimpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPrice() {
        return String.format("%.3f", price);
    }


    @Override
    public String toString() {
        return String.format("price:%s", getPrice());
    }
}
