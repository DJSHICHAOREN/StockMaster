package com.example.stockmaster.http.converter;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.sina.NDaysSinaResponse;
import com.example.stockmaster.entity.sina.NDaysSinaStockPrice;
import com.example.stockmaster.entity.sina.TodaySinaResponse;
import com.example.stockmaster.entity.sina.TodaySinaStockPrice;
import com.example.stockmaster.util.DateUtil;
import com.example.stockmaster.util.StockManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
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
    public List<List<StockPrice>> sinaNDaysPriceResponseToObjectList(String response, boolean isUseTimePoint, StockPrice.QueryType queryType){
        response = response.replaceAll("\n", "");
        String[] stockStr = response.split(":::");

        String stockId = stockStr[1];
        String stockPriceJsonStr = stockStr[2].replaceAll("\\(", "").replaceAll("\\)", "");
        // 将字符串转为jsonObject
        JsonObject jsonObject = new JsonParser().parse(stockPriceJsonStr).getAsJsonObject();
        // 将jsonObject转为SinaResponse对象
        Gson gson = new Gson();
        NDaysSinaResponse NDaysSinaResponse = gson.fromJson(jsonObject, new TypeToken<NDaysSinaResponse>(){}.getType());
        // 将SinaResponse转为List<StockPrice>
        // 存储每天的股票价格列表
        List<List<StockPrice>> stockPriceEveryDayList = new ArrayList<>();
        if(NDaysSinaResponse != null && NDaysSinaResponse.getResult() != null && NDaysSinaResponse.getResult().getData() != null){
            List<List<NDaysSinaStockPrice>> data = NDaysSinaResponse.getResult().getData();
            for(List<NDaysSinaStockPrice> oneDaySinaStockPrice : data){
                List<StockPrice> stockPriceList = new ArrayList<>();
                stockPriceEveryDayList.add(stockPriceList);
                // 得到这一天的日期
                String date = "";
                if(oneDaySinaStockPrice.get(0) != null && oneDaySinaStockPrice.get(0).getDate() != null){
                    date = oneDaySinaStockPrice.get(0).getDate();
                }
                // 添加这一天特定时间的股票价格
                for(NDaysSinaStockPrice sinaStockPrice : oneDaySinaStockPrice){
                    if(date == null || date.equals("")){
                        Log.e("lwd", "sinaTodayPriceResponseToObjectList not get date");
                        return null;
                    }
                    if(sinaStockPrice.getM() == null || sinaStockPrice.getM().equals("")){
                        return null;
                    }
                    // 过滤时间在09:30:00
                    String[] timeArray = sinaStockPrice.getM().split(":");
                    int hour = Integer.parseInt(timeArray[0]);
                    int minutes = Integer.parseInt(timeArray[1]);
                    if(hour < 9 || (hour == 9 && minutes <30)){
                        continue;
                    }

                    // 是否过滤特殊时间点
                    if(isUseTimePoint){
                        String timePointString =
                                "10:00:00, 10:30:00, " +
                                        "11:00:00, 11:30:00, " +
                                        "12:00:00," +
                                        "13:30:00," +
                                        "14:00:00, 14:30:00, " +
                                        "15:00:00, 15:30:00, " +
                                        "16:00:00, 16:10:00";
                        if(timePointString.indexOf(sinaStockPrice.getM()) == -1){
                            continue;
                        }
                    }

                    // 如果sinaStockPrice有时间但是没有价格，说明这是一直新股，那时还没有发行
                    if(!sinaStockPrice.getM().equals("") && sinaStockPrice.getPrice().equals("")){
                        continue;
                    }
                    StockPrice stockPrice = new StockPrice(stockId, date+ " " +sinaStockPrice.getM(),
                            sinaStockPrice.getPrice(), queryType, sinaStockPrice.getAvg_p());
                    stockPriceList.add(stockPrice);
                }
            }
        }
        return stockPriceEveryDayList;
    }

    /**
     * 将今天的价格转换为对象列表
     * @param response
     * @return
     */
    public List<StockPrice> sinaTodayPriceResponseToObjectList(String response, StockPrice.QueryType queryType){
        response = response.replaceAll("\n", "");
        String[] stockStr = response.split(":::");

        String stockId = stockStr[1];
        String stockPriceJsonStr = stockStr[2].replaceAll("\\(", "").replaceAll("\\)", "");
        // 将字符串转为jsonObject
        JsonObject jsonObject = new JsonParser().parse(stockPriceJsonStr).getAsJsonObject();
        // 将jsonObject转为SinaResponse对象
        Gson gson = new Gson();
        TodaySinaResponse todaySinaResponse = gson.fromJson(jsonObject, new TypeToken<TodaySinaResponse>(){}.getType());

        // 判断今天日期
        String dateString = DateUtil.convertDateToShortDayString(StockManager.getLastDealDate());
        if(todaySinaResponse.getResult().getData().size()<100){
            Calendar calendar = Calendar.getInstance();
            //获取系统时间
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dateString = year + "-" + month + "-" + day;
        }

        // 将SinaResponse转为List<StockPrice>
        List<StockPrice> stockPriceList = new ArrayList<>();
        if(todaySinaResponse != null && todaySinaResponse.getResult() != null && todaySinaResponse.getResult().getData() != null){
            for(TodaySinaStockPrice sinaStockPrice : todaySinaResponse.getResult().getData()){

                if(sinaStockPrice.getM() == null || sinaStockPrice.getM().equals("")){
                    return null;
                }
                // 过滤时间在09:20:00
                String[] timeArray = sinaStockPrice.getM().split(":");
                int hour = Integer.parseInt(timeArray[0]);
                int minutes = Integer.parseInt(timeArray[1]);
                if(hour < 9 || (hour == 9 && minutes <20)){
                    continue;
                }

                String completeDate = dateString + " " +sinaStockPrice.getM();
                StockPrice stockPrice = new StockPrice(stockId, completeDate, sinaStockPrice.getP(), queryType, sinaStockPrice.getAvg_p());
                stockPriceList.add(stockPrice);
            }
        }
        return stockPriceList;
    }
}
