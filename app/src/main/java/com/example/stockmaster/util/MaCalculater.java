package com.example.stockmaster.util;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 股票均价计算器
 */
public class MaCalculater {
    private static List<Integer> mCountedDayList = new ArrayList<>(Arrays.asList(5, 10, 20, 30));

    public static MaState calMaState(List<StockPrice> stockPriceList){
        MaState maState = new MaState();
        for(int countedDay : mCountedDayList){

        }
        return maState;
    }

}
