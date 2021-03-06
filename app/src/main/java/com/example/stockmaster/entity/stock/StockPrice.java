package com.example.stockmaster.entity.stock;

import com.example.stockmaster.entity.stock.Stock;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

import static com.example.stockmaster.util.DateUtil.convertStringToDate;

@Table(name = "stockPrice")
public class StockPrice {
    @Column(name = "id", isId = true)
    public String id;
    @Column(name = "stockId")
    public String stockId;
    @Column(name="time")
    public Date time;
    @Column(name="dealDate")
    public Date dealDate; // 交易的日期
    @Column(name="price")
    public float price;
    public float avgPrice = -1;
    public String name;



    public enum QueryType{FIVEDAY, TODAY, MINUTE, NULL}
    private QueryType queryType = QueryType.NULL;

    public Stock.DealType dealType = Stock.DealType.NULL;


    public StockPrice(){
    }

    public StockPrice(String stockId, String time, String price, QueryType queryType, String avgPrice){
        id = stockId + time.toString();
        setStockId(stockId);
        setTime(time);
        setPrice(price);
        setQueryType(queryType);
        setAvgPrice(avgPrice);
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public void setPrice(String price){
        this.price = Float.parseFloat(price);
    }

    public void setTime(String timeStr) throws NumberFormatException{
        this.time = convertStringToDate(timeStr);

        Date date = (Date)this.time.clone();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        setDealDate(date);
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

    public float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = Float.parseFloat(avgPrice);
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    @Override
    public String toString() {
        if(time == null){
            return "";
        }
        String dealTime = time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
        return String.format("stockId：%s, 时间：%s，价格：%s", getStockId(), getTime() , getPriceString());
    }

    public String toStringWithId(){
        return String.format("%s, %s", stockId, toString());
    }
}
