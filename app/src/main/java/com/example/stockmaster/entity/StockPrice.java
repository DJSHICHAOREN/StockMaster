package com.example.stockmaster.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Table(name = "stockPrice")
public class StockPrice {
    @Column(name = "id", isId = true)
    public String id;
    @Column(name = "stockId")
    public String stockId;
    @Column(name="time")
    public Date time;
    @Column(name="price")
    public float price;

    public String name;

    public enum QueryType{FIVEDAY, TODAY, MINUTE, NULL}
    private QueryType queryType = QueryType.NULL;

    public Stock.DealType dealType = Stock.DealType.NULL;
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public StockPrice(){
    }

    public StockPrice(String stockId, String time, String price, QueryType queryType){
        setStockId(stockId);
        setTime(time);
        setPrice(price);
        setQueryType(queryType);
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
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
//        Log.d("lwd", String.format("set id:%s, price:%s, dealType:%s, time:%s", id, price, dealType, time));
    }

    public Stock.DealType getDealType() {
        return dealType;
    }

    public String getPriceString() {
        return String.format("%.3f", price);
    }

    public float getPrice() {
        return price;
    }

    public String getStockId() {
        return stockId;
    }

    public String getNotificationContent(){
        return stockId + " " + toString();
    }

    public Date getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public int getNotificationId(){
        return Integer.parseInt(this.stockId.substring(2));
    }

    @Override
    public String toString() {
        String dealTime = time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
        if(dealType == Stock.DealType.BUY){
            return String.format("买点，时间：%s，价格：%s", dealTime, getPriceString());
        }
        else if(dealType == Stock.DealType.SALE){
            return String.format("卖点，时间：%s，价格：%s", dealTime , getPriceString());
        }
        return "非买卖点";
    }

    public String toStringWithId(){
        return String.format("%s, %s", stockId, toString());
    }
}
