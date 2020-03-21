package com.example.stockmaster.http.converter;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.sina.SinaResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ResponseStringToObject {

    public List<StockPrice> sinaNowPriceResponseToObjectList(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<StockPrice> stockPriceBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            StockPrice stockPriceNow = new StockPrice();
            String[] lefts = left.split("_");
            stockPriceNow.setId(lefts[2]);

            String[] values = right.split(",");
            try{
                stockPriceNow.setName(values[0]);
                stockPriceNow.setPrice(values[3]);
                stockPriceNow.setTime(values[31]);

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockPriceBeanList.add(stockPriceNow);
        }

        return stockPriceBeanList;
    }

    public List<StockPrice> sinaTodayPriceResponseToObjectList(String response){
        response = response.replaceAll("\n", "");
        String[] stockStr = response.split(":::");

        String stockId = stockStr[1];
        String stockPriceJsonStr = stockStr[2].replaceAll("\\(", "").replaceAll("\\)", "");
        JsonObject jsonObject = new JsonParser().parse(stockPriceJsonStr).getAsJsonObject();
        Gson gson = new Gson();
        SinaResponse sinaResponse = gson.fromJson(jsonObject, new TypeToken<SinaResponse>(){}.getType());
        return null;
    }
}
