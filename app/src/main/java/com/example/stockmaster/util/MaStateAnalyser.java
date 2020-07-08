package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.form.FallThroughSupportFormJudge;
import com.example.stockmaster.entity.form.LongToArrangeFormJudge;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.form.BaseFormJudge;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.form.UpEmanativeFormJudge;

import java.util.ArrayList;
import java.util.List;

public class MaStateAnalyser {
    private static MaStateAnalyser instance = null;
    private List<BaseFormJudge> mBaseFormJudgeList = new ArrayList<>();
    public MaStateAnalyser(){
//        mBaseFormJudgeList.add(new UpEmanativeFormJudge());
        mBaseFormJudgeList.add(new LongToArrangeFormJudge());
        mBaseFormJudgeList.add(new FallThroughSupportFormJudge());
    }

    public List<StockForm> analyse(String stockId, List<MaState> maStateList, int kLevel, String keyStockPriceTimeString,
                                   Stock stock, List<StockPrice> stockPriceList){
        List<StockForm> stockFormList = new ArrayList<>();
        for(BaseFormJudge baseFormJudge : mBaseFormJudgeList){
            StockForm stockForm = baseFormJudge.judge(stockId, maStateList, kLevel, stock, stockPriceList);
            if(stockForm != null){
//                DBUtil.saveStockForm(stockForm);
                stockFormList.add(stockForm);
            }
        }
        return stockFormList;
    }

    public static MaStateAnalyser getInstance(){
        if(instance == null){
            instance = new MaStateAnalyser();
        }
        return instance;
    }

}
