package com.example.stockmaster.entity.ma;

import android.util.Log;

import java.util.Date;

public class MaState {
    public float price;
    public float ma5;
    public float ma10;
    public float ma20;
    public float ma30;
    public float ma60;
    private float maPriceDispersion;
    public Date time;
    public Date previousTime;
    public float supportPrice;
    public float lowestPrice;
    public float highestPrice;
    public float beginPrice;
    public float endPrice;

    public float minPriceInOneHour = -1;

    public MaState(Date time, float price, Date previousTime){
        this.time = time;
        this.price = price;
        this.previousTime = previousTime;
    }

    public MaState(float price, float ma5, float ma10, float ma20, float ma30, float ma60, Date time) {
        this.ma5 = ma5;
        this.ma10 = ma10;
        this.ma20 = ma20;
        this.ma30 = ma30;
        this.ma60 = ma60;
        this.time = time;
    }

    public float getMa5() {
        return ma5;
    }

    public void setMa5(float ma5) {
        this.ma5 = ma5;
    }

    public float getMa10() {
        return ma10;
    }

    public void setMa10(float ma10) {
        this.ma10 = ma10;
    }

    public float getMa20() {
        return ma20;
    }

    public void setMa20(float ma20) {
        this.ma20 = ma20;
    }

    public float getMa30() {
        return ma30;
    }

    public void setMa30(float ma30) {
        this.ma30 = ma30;
    }

    public float getMa60() {
        return ma60;
    }

    public void setMa60(float ma60) {
        this.ma60 = ma60;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(float lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public float getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(float highestPrice) {
        this.highestPrice = highestPrice;
    }

    public float getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(float beginPrice) {
        this.beginPrice = beginPrice;
    }

    public float getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(float endPrice) {
        this.endPrice = endPrice;
    }

    public float getSupportPrice() {
        return supportPrice;
    }

    public void setSupportPrice(float supportPrice) {
//        Log.d("lwd", String.format("setSupportPrice time:%s, price:%s", this.getTime(), this.getPrice()));
        this.supportPrice = supportPrice;
    }

    public void setMaPrice(float price, int countedDay){
        if(countedDay == 5){
            setMa5(price);
        }
        else if(countedDay == 10){
            setMa10(price);
        }
        else if(countedDay == 20){
            setMa20(price);
        }
        else if(countedDay == 30){
            setMa30(price);
        }
        else if(countedDay == 60){
            setMa60(price);
        }
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getMaPriceDispersion() {
        if(maPriceDispersion != 0){
            return maPriceDispersion;
        }
        if(ma5 != 0 && ma10 != 0 && ma20!= 0){
            maPriceDispersion = (Math.abs(ma5-ma10) + Math.abs(ma10-ma20) + Math.abs(ma5-ma20)) / (3 * ma5);
        }
        return maPriceDispersion;
    }

    @Override
    public String toString(){
        return String.format("time:%s ma5:%f, ma10:%f, ma20:%f, ma30:%f, ma60:%f",
                getTime().toString(), getMa5(), getMa10(), getMa20(), getMa30(), getMa60());
    }

    public String toCandleString(){
        return String.format("time %s, highestPrice:%f, lowestPrice:%f, beginPrice:%f, endPrice:%f, supportPrice:%f",
                getTime(), getHighestPrice(), getLowestPrice(), getBeginPrice(), getEndPrice(), getSupportPrice());
    }

    public void setCandleArgs(float beginPrice, float endPrice, float highestPrice, float lowestPrice){
        setBeginPrice(beginPrice);
        setEndPrice(endPrice);
        setHighestPrice(highestPrice);
        setLowestPrice(lowestPrice);
    }
}
