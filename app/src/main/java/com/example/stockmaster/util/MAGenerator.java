package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;

import java.util.ArrayList;
import java.util.List;

public class MAGenerator {
    private TextUtil mTextUtil = new TextUtil();

    public List<Float> generateDayMA5(String response){
        List<String> closedPriceList = mTextUtil.getAllSatisfyStrings(response,
                "\"prevclose\":\"\\d*\\.\\d*\"");

        List<Float> fiveDayPriceList = new ArrayList<>();
        for(String closedPrice : closedPriceList){
            String stringPrice = closedPrice.split(":")[1].replaceAll("\"", "");
            fiveDayPriceList.add(Float.parseFloat(stringPrice));
        }
        return fiveDayPriceList;
    }
}
