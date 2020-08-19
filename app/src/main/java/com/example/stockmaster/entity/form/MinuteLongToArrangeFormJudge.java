package com.example.stockmaster.entity.form;

import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.ma.MaState;

import java.util.Date;
import java.util.List;

public class MinuteLongToArrangeFormJudge extends BaseFormJudge {
    public MinuteLongToArrangeFormJudge() {
        super(R.integer.formMinuteLongToArrange);
    }

    public MaState getMaStateByTime(List<MaState> maStateList, Date time){
        for(int i=maStateList.size()-1; i>=0; i--){
            if(maStateList.get(i).getTime() == time || maStateList.get(i).getTime().before(time)){
                return maStateList.get(i);
            }
        }
        return null;
    }

    @Override
    public StockForm judge(Stock stock, List<MaState> maStateList, int kLevel) {
        if(maStateList == null || maStateList.size() < kLevel + 11){
            return null;
        }

        // 判断最新的三条线是否是按序排列且上升的
        int maStateListLength = maStateList.size();
        MaState lastMaState1 = maStateList.get(maStateListLength-1);

        MaState lastMaState2 = getMaStateByTime(maStateList, lastMaState1.previousTime);
        if(lastMaState2 == null){
            return null;
        }
        MaState lastMaState3 = getMaStateByTime(maStateList, lastMaState2.previousTime);
        if(lastMaState3 == null){
            return null;
        }

        // 确保信息有效
        if(lastMaState3.getMa30() == 0){
            return null;
        }
        // 打印开始信息
        if(isPrintBeginJudgeTime){
            Log.d("lwd", String.format("MinuteLongToArrangeFormJudge 开始分析 time=%s，", lastMaState1.getTime().toString()));
            isPrintBeginJudgeTime = false;
        }

        boolean isSeriation = false; // 均线是否是呈梯子型排列
        boolean isRise = false; // 均线是否上升
        boolean biggerThanNMaStateBefore = true;
        boolean isHorizontalBefore = false; // 均线之前是否横盘
        boolean isHigherThanBeforeDays = false;
        boolean isMinutePriceUp = false;
        // 判断均线是否阶梯形排列
        if(lastMaState1.getMa5() >= lastMaState1.getMa10()
                && lastMaState1.getMa5() >= lastMaState1.getMa20()
                && lastMaState1.getMa5() >= lastMaState1.getMa30()
                && (lastMaState1.getMa10() > lastMaState1.getMa20() || lastMaState1.getMa10() > lastMaState1.getMa30())){
            isSeriation = true;
        }

        // 判断均线是否都在上升
        if(lastMaState2.getMa5() != 0
                && lastMaState2.getMa10() != 0
                && lastMaState2.getMa20() != 0
                && lastMaState2.getMa30() != 0){
            if(lastMaState1.getMa5() - lastMaState2.getMa5() > 0
                    && lastMaState1.getMa10() - lastMaState2.getMa10() > 0
                    && (lastMaState1.getMa20() - lastMaState2.getMa20() > 0 || lastMaState1.getMa30() - lastMaState2.getMa30() > 0)){
                isRise = true;
            }
        }

        // 判断是否超过十日收盘价
//        MaState beforeMaState = lastMaState2;
//        for(int i=0; i<10; i++){
//            if(lastMaState1.getPrice() < beforeMaState.getEndPrice()
//             || lastMaState1.getPrice() < beforeMaState.getBeginPrice()){
//                biggerThanNMaStateBefore = false;
//            }
//            beforeMaState = getMaStateByTime(maStateList, beforeMaState.previousTime);
//        }

        // 判断均线在之前是否横盘
//        if(lastMaState3.getMa5() != 0 && lastMaState3.getMa10() != 0 && lastMaState3.getMa20() != 0){
//            // 得到均线之前的斜率
//            float ma5SlopeBefore = (lastMaState2.getMa5() - lastMaState3.getMa5())/lastMaState3.getMa5();
//            float ma10SlopeBefore = (lastMaState2.getMa10() - lastMaState3.getMa10())/lastMaState3.getMa10();
////            float ma20Slope = (lastMaState2.getMa20() - lastMaState3.getMa20())/lastMaState3.getMa20();
//            float ma5SlopeNow = (lastMaState1.getMa5() - lastMaState2.getMa5())/lastMaState2.getMa5();
//            float ma10SlopeNow = (lastMaState1.getMa10() - lastMaState2.getMa10())/lastMaState2.getMa10();
//            if(ma5SlopeNow > ma5SlopeBefore && ma10SlopeNow > ma10SlopeBefore){
//                if(lastMaState3.getMaPriceDispersion() <= 0.01 && lastMaState2.getMaPriceDispersion() <= 0.01)
//                    isHorizontalBefore = true;
//            }
//        }

//        if(DateUtil.isDateEqual(lastMaState1.getTime(), 5, 15, 42)){
//            Log.d("lwd", "hello");
//        }


        if(isSeriation && isRise){
//            Log.d("lwd", String.format("%s 买他，价格:%s", lastMaState1.getTime(), lastMaState1.getPrice()));
            return new StockForm(stock.getId(), getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
        }


        return null;
    }
}
