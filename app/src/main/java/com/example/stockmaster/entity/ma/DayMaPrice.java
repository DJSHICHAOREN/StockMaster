package com.example.stockmaster.entity.ma;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Table(name = "DayMaPrice")
public class DayMaPrice {
    @Column(name = "id", isId = true)
    public String id;

    @Column(name = "stockId")
    public String stockId;

    @Column(name = "date")
    public String date;

    @Column(name = "ma10")
    public float ma10;
    @Column(name = "ma30")
    public float ma30;
    @Column(name = "ma50")
    public float ma50;
    @Column(name = "ma100")
    public float ma100;
    @Column(name = "ma250")
    public float ma250;

    public float mHighestMaPrice;
    public float mLowestMaPrice;

    public DayMaPrice(String stockId, String date, String ma10, String ma30, String ma50, String ma100, String ma250) {
        this.id = stockId + "_" + date;
        this.stockId = stockId;
        this.date = date;
        setMAPrice(ma10, ma30, ma50, ma100, ma250);
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

        List<Float> priceList = new ArrayList<Float>(Arrays.asList(this.ma10, this.ma30, this.ma50));
        setHighestMaPrice(Collections.max(priceList));
        setLowestMaPrice(Collections.min(priceList));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public float getMa10() {
        return ma10;
    }

    public void setMa10(float ma10) {
        this.ma10 = ma10;
    }

    public float getMa30() {
        return ma30;
    }

    public void setMa30(float ma30) {
        this.ma30 = ma30;
    }

    public float getMa50() {
        return ma50;
    }

    public void setMa50(float ma50) {
        this.ma50 = ma50;
    }

    public float getMa100() {
        return ma100;
    }

    public void setMa100(float ma100) {
        this.ma100 = ma100;
    }

    public float getMa250() {
        return ma250;
    }

    public void setMa250(float ma250) {
        this.ma250 = ma250;
    }

    public float getHighestMaPrice() {
        return mHighestMaPrice;
    }

    public void setHighestMaPrice(float mHighestMaPrice) {
        this.mHighestMaPrice = mHighestMaPrice;
    }

    public float getLowestMaPrice() {
        return mLowestMaPrice;
    }

    public void setLowestMaPrice(float mLowestMaPrice) {
        this.mLowestMaPrice = mLowestMaPrice;
    }
}
