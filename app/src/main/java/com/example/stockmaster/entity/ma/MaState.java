package com.example.stockmaster.entity.ma;

import java.util.Date;

public class MaState {
    public float ma5;
    public float ma10;
    public float ma20;
    public float ma30;
    public float ma60;

    public Date time;

    public MaState(Date time){
        this.time = time;
    }

    public MaState(float ma5, float ma10, float ma20, float ma30, float ma60, Date time) {
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

    @Override
    public String toString(){
        return String.format("time:%s ma5:%f, ma10:%f, ma20:%f, ma30:%f, ma60:%f",
                getTime().toString(), getMa5(), getMa10(), getMa20(), getMa30(), getMa60());
    }
}
