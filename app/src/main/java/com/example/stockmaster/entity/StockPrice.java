package com.example.stockmaster.entity;

public class StockPrice {
    public String id, name, today_begin_price, yesterday_end_price, current_price,
            today_highest_price, time, increase,percent,yest_value;


    public String getCurrent_price() {
        double d = Double.parseDouble(current_price);
        return String.format("%.3f", d);
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
        return "Stock{" +
                "id_='" + id + '\'' +
                ", name_='" + name + '\'' +
                ", now_='" + current_price + '\'';
    }
}
