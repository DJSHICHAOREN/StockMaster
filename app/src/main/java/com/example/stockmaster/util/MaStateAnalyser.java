package com.example.stockmaster.util;

import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.form.FallThroughSupportFormJudge;
import com.example.stockmaster.entity.form.LongToArrangeFormJudge;
import com.example.stockmaster.entity.form.MinuteLongToArrangeFormJudge;
import com.example.stockmaster.entity.form.MinuteRiseFormJudge;
import com.example.stockmaster.entity.form.SuddenUpFormJudge;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.entity.form.BaseFormJudge;
import com.example.stockmaster.entity.form.StockForm;

import java.util.ArrayList;
import java.util.List;

public class MaStateAnalyser {
    private static MaStateAnalyser instance = null;
    private List<BaseFormJudge> mBaseFormJudgeList = new ArrayList<>();
    public MaStateAnalyser(){
//        mBaseFormJudgeList.add(new UpEmanativeFormJudge());
        mBaseFormJudgeList.add(new LongToArrangeFormJudge());
        mBaseFormJudgeList.add(new FallThroughSupportFormJudge());
        mBaseFormJudgeList.add(new MinuteRiseFormJudge());
        mBaseFormJudgeList.add(new SuddenUpFormJudge());
        mBaseFormJudgeList.add(new MinuteLongToArrangeFormJudge());
    }

    public List<StockForm> analyse(Stock stock, List<MaState> maStateList, int kLevel){
        List<StockForm> stockFormList = new ArrayList<>();

        for(BaseFormJudge baseFormJudge : mBaseFormJudgeList){
            StockForm stockForm = baseFormJudge.judge(stock, maStateList, kLevel);
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
