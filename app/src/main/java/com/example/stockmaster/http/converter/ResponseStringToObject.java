package com.example.stockmaster.http.converter;

import android.app.DownloadManager;
import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.sina.SinaResponse;
import com.example.stockmaster.entity.sina.SinaStockPrice;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
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
                stockPriceNow.setName(values[1]);
                stockPriceNow.setPrice(values[6]);
                stockPriceNow.setTime(values[17] + " " + values[18]);
                stockPriceNow.setQueryType(StockPrice.QueryType.MINUTE);

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
    public List<StockPrice> sinaTodayPriceResponseToObjectList(String response, boolean isUseTimePoint, StockPrice.QueryType queryType){
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
                // 添加这一天特定时间的股票价格
                for(SinaStockPrice sinaStockPrice : oneDaySinaStockPrice){
                    if(date == null || date.equals("")){
                        Log.e("lwd", "sinaTodayPriceResponseToObjectList not get date");
                        return null;
                    }
                    if(sinaStockPrice.getM() == null || sinaStockPrice.getM().equals("")){
                        return null;
                    }
                    // 是否过滤特殊时间点
                    if(isUseTimePoint){
                        String timePointString =
                                "09:35:00, 09:40:00, 09:45:00, 09:50:00, 09:55:00," +
                                        "10:00:00, 10:05:00, 10:10:00, 10:15:00, 10:20:00, 10:25:00, 10:30:00, 10:35:00, 10:40:00, 10:45:00, 10:50:00, 10:55:00," +
                                        "11:00:00, 11:05:00, 11:10:00, 11:15:00, 11:20:00, 11:25:00, 11:30:00, 11:35:00, 11:40:00, 11:45:00, 11:50:00, 11:55:00," +
                                        "12:00:00," +
                                        "13:05:00, 13:10:00, 13:15:00, 13:20:00, 13:25:00, 13:30:00, 13:35:00, 13:40:00, 13:45:00, 13:50:00, 13:55:00, " +
                                        "14:00:00, 14:05:00, 14:10:00, 14:15:00, 14:20:00, 14:25:00, 14:30:00, 14:35:00, 14:40:00, 14:45:00, 14:50:00, 14:55:00." +
                                        "15:00:00, 15:05:00, 15:10:00, 15:15:00, 15:20:00, 15:25:00, 15:30:00, 15:35:00, 15:40:00, 15:45:00, 15:50:00, 15:55:00." +
                                        "16:00:00, 16:05:00, 16:10:00";
                        if(timePointString.indexOf(sinaStockPrice.getM()) == -1){
                            continue;
                        }
                    }
                    StockPrice stockPrice = new StockPrice(stockId, date+ " " +sinaStockPrice.getM(), sinaStockPrice.getPrice(), queryType);
                    stockPriceList.add(stockPrice);
                }
            }
        }
        return stockPriceList;
    }
}
