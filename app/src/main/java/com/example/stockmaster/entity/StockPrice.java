package com.example.stockmaster.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockPrice {
    public String id, name, today_begin_price, yesterday_end_price,
            today_highest_price, increase,percent,yest_value;

    public Date time;
    public float price;

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
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        try {
            this.time = simpleDateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPrice() {
        return String.format("%.3f", price);
    }

    public String getIncrease() {
        double d = Double.parseDouble(increase);
        return String.format("%.2f", d);
    }

    public String getPercent() {
        double d = Double.parseDouble(percent);
        return String.format("%.2f", d) + "% ";
    }

    @Override
    public String toString() {
        return String.format("price:%s", getPrice());
    }
}
