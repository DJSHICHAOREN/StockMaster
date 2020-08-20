package com.example.stockmaster.entity.stock;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name = "dealDate")
public class DealDate {
    @Column(name = "id", isId = true)
    public String id;

    @Column(name = "date")
    public Date date;

    public DealDate(){

    }

    public DealDate(Date date){
        id = date.toString();
        setDate(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        this.date = date;
    }
}
