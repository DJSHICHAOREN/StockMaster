package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 股票均价计算器
 */
public class MaCalculater {
    private static List<Integer> mCountedDayList = new ArrayList<>(Arrays.asList(5, 10, 20, 30, 60));

    public static MaState calMaState(List<StockPrice> stockPriceList){
        if(stockPriceList == null || stockPriceList.size() < 2){
            return null;
        }
        // 创建均价对象
        int priceListLength = stockPriceList.size();
        StockPrice lastStockPrice = stockPriceList.get(priceListLength-1);
        MaState maState = new MaState(lastStockPrice.getTime(), lastStockPrice.getPrice(), stockPriceList.get(priceListLength-2).getTime());
        // 计算均价
        for(int countedDay : mCountedDayList){
            // 计算均价
            if(priceListLength >= countedDay){
                float sum = 0;
                for(int i=priceListLength-1; i >= priceListLength-countedDay; i--){
                    sum += stockPriceList.get(i).getPrice();
                }
                sum /= countedDay;
                maState.setMaPrice(sum, countedDay);
            }
        }
//        Log.d("lwd", maState.toString());
        return maState;
    }

    public static int getMinCountedDay(){
        return mCountedDayList.get(0);
    }

}
