package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name = "StockForm")
public class StockForm {

    @Column(name = "id", isId = true)
    public String id;

    @Column(name = "stockId")
    public String stockId;

    @Column(name = "formId")
    public int formId;

    @Column(name = "time")
    public Date time;

    @Column(name = "type")
    public int type; // 类型：购买：0，卖出：1

    @Column(name = "kLevel")
    public int kLevel;

    @Column(name = "price")
    public float price;


    public StockForm(){

    }

    public StockForm(String stockId, int formId, int kLevel, Date time, int type, float price){
        this.id = stockId + "_" + formId + "_" + time.toString() + "_" + type + "_" + kLevel;
        this.stockId = stockId;
        this.formId = formId;
        this.time = time;
        this.type = type;
        this.kLevel = kLevel;
        this.price = price;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getkLevel() {
        return kLevel;
    }

    public void setkLevel(int kLevel) {
        this.kLevel = kLevel;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString(){
        String optionString = this.type == 0 ? "买点" : "卖点";
        String formTypeString = "";
        switch (this.getFormId()){
            case R.integer.formLongToArrange:{
                formTypeString = "long_to_arrange";
                break;
            }
            case R.integer.formFallThroughSupport:{
                formTypeString = "fall_through_support";
                break;
            }
            case R.integer.formMinuteRise:{
                formTypeString = "minute_rise";
                break;
            }

        }
        return String.format("%dK线, %s, %s, 时间：%s, 价格：%s",
                this.kLevel, optionString, formTypeString, this.time.toString().substring(0, this.time.toString().indexOf("GMT")),
                getPrice());
    }
}
