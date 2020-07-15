package com.example.stockmaster.entity.sina;

public class NDaysSinaStockPrice {
    private String m;
    private String price;
    private String volume;
    private String avg_p;

    private String date;
    private String prevclose;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrevclose() {
        return prevclose;
    }

    public void setPrevclose(String prevclose) {
        this.prevclose = prevclose;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getAvg_p() {
        return avg_p;
    }

    public void setAvg_p(String avg_p) {
        this.avg_p = avg_p;
    }
}
