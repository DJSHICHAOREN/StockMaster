package com.example.stockmaster.entity.form;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.List;

public class SuddenUpFormJudge extends BaseFormJudge {
    public SuddenUpFormJudge() {
        super(R.integer.formSuddenUp);
    }

    @Override
    public StockForm judge(Stock stock, List<MaState> maStateList, int kLevel) {
        int testNum = 5;
        if(maStateList == null || maStateList.size() < kLevel + testNum + 1){
            return null;
        }

        // 得到最后一个状态
        int maStateListLength = maStateList.size();
        MaState lastMaState = maStateList.get(maStateListLength-1);

        // 打印开始信息
        if(isPrintBeginJudgeTime){
            Log.d("lwd", String.format("MinuteLongToArrangeFormJudge 开始分析 time=%s，", lastMaState.getTime().toString()));
            isPrintBeginJudgeTime = false;
        }

        // 是否一个阶段的价格突然上升
        boolean isSuddenUp = false;
        boolean isGentlyBefore = true;
        // 存储前n个块的均价的波动幅度
        List<Float> increaseRateList = new ArrayList<>();
        for(int i=0; i<testNum; i++){
            float thisAvgPrice = (lastMaState.getHighestPrice() + lastMaState.getLowestPrice())/2;

            lastMaState = getMaStateByTime(maStateList, lastMaState.previousTime);
            float lastAvgPrice = (lastMaState.getHighestPrice() + lastMaState.getLowestPrice())/2;

            float increaseRate = Math.abs(thisAvgPrice - lastAvgPrice) / lastAvgPrice;
            increaseRateList.add(increaseRate);
        }

        if(increaseRateList.get(increaseRateList.size()-1) > 0.01){
            isSuddenUp = true;
        }

        for(int i=0; i<increaseRateList.size()-1; i++){
            if(increaseRateList.get(i) > 0.005){
                isGentlyBefore = false;
            }
        }

        if(isSuddenUp && isGentlyBefore){
            MaState lastMaState1 = maStateList.get(maStateListLength-1);
            return new StockForm(stock.getId(), getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
        }

        return null;
    }
}
