package com.example.stockmaster.http.converter;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.sina.SinaResponse;
import com.example.stockmaster.entity.sina.SinaStockPrice;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 将接收的数据字符串转化为对象
 */
public class ResponseStringToObject {

    /**
     * 将分时价格转换为价格对象列表
     * @param response
     * @return
     */
    public List<StockPrice> sinaMinutePriceResponseToObjectList(String response)throws NumberFormatException{
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<StockPrice> stockPriceList = new ArrayList<>();
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
            stockPriceNow.setStockId(lefts[3]);

            String[] values = right.split(",");
            try{
                stockPriceNow.setPrice(values[6]);
                stockPriceNow.setTime(values[17] + " " + values[18]);

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockPriceList.add(stockPriceNow);
        }

        return stockPriceList;
    }

    /**
     * 将今天的价格转换为对象列表
     * @param response
     * @return
     */
    public List<StockPrice> sinaTodayPriceResponseToObjectList(String response){
        response = response.replaceAll("\n", "");
        String[] stockStr = response.split(":::");

        String stockId = stockStr[1];
        String stockPriceJsonStr = stockStr[2].replaceAll("\\(", "").replaceAll("\\)", "");
        // 将字符串转为jsonObject
        JsonObject jsonObject = new JsonParser().parse(stockPriceJsonStr).getAsJsonObject();
        // 将jsonObject转为SinaResponse对象
        Gson gson = new Gson();
        SinaResponse sinaResponse = gson.fromJson(jsonObject, new TypeToken<SinaResponse>(){}.getType());
        // 将SinaResponse转为List<StockPrice>
        List<StockPrice> stockPriceList = new ArrayList<>();
        if(sinaResponse != null && sinaResponse.getResult() != null && sinaResponse.getResult().getData() != null){
            List<List<SinaStockPrice>> data = sinaResponse.getResult().getData();
            for(List<SinaStockPrice> oneDaySinaStockPrice : data){
                // 得到这一天的日期
                String date = "";
                if(oneDaySinaStockPrice.get(0) != null && oneDaySinaStockPrice.get(0).getDate() != null){
                    date = oneDaySinaStockPrice.get(0).getDate();
                }
                for(SinaStockPrice sinaStockPrice : oneDaySinaStockPrice){
                    if(date == ""){
                        Log.d("lwd", "sinaTodayPriceResponseToObjectList not get date");
                    }
                    // 生成StockPrice
                    StockPrice stockPrice = new StockPrice(stockId, date+ " " +sinaStockPrice.getM(), sinaStockPrice.getPrice());
                    stockPriceList.add(stockPrice);
                }
            }
        }
        return stockPriceList;
    }
}
