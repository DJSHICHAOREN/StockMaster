package com.example.stockmaster.entity.form;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.ma.MaState;

import java.util.Date;
import java.util.List;

public class LongToArrangeFormJudge extends BaseFormJudge {

    public LongToArrangeFormJudge() {
        super(R.integer.formLongToArrange);
    }

    public MaState getMaStateByTime(List<MaState> maStateList, Date time){
        for(int i=maStateList.size()-1; i>=0; i--){
            if(maStateList.get(i).getTime() == time || maStateList.get(i).getTime().before(time)){
                return maStateList.get(i);
            }
        }
        return null;
    }


    /**0号交易策略，高确定性
     * (1)
     * 5K: ma5 > ma10 > ma20 > ma30 > ma60
     * 15K: ma5 > ma10 > ma20 > ma30
     * 30K: ma5 > ma10 > ma20
     * 60K: ma5 > ma10
     * 日K: ma5向上
     * (2)
     * 为上升形态
     * @param
     * @param maStateList
     * @param kLevel
     * @return
     */
    @Override
    public StockForm judge(Stock stock, List<MaState> maStateList, int kLevel){
        if(maStateList == null || maStateList.size() < kLevel + 3){
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
//            Log.d("lwd", String.format("time=%s，ma60不等于0，开始分析", lastMaState1.getTime().toString()));
            isPrintBeginJudgeTime = false;
        }

        boolean isSeriation = false; // 均线是否是呈梯子型排列
        boolean isRise = false; // 均线是否上升
        boolean isHorizontalBefore = false; // 均线之前是否横盘
        boolean isDayMaUp = false;
        boolean isHigherThanBeforeDays = false;
        boolean isMinutePriceUp = false;
        // 判断均线是否阶梯形排列
        if(lastMaState1.getMa5() >= lastMaState1.getMa10()
                && lastMaState1.getMa10() > lastMaState1.getMa20()
                && lastMaState1.getMa20() > lastMaState1.getMa30()){
            isSeriation = true;
        }

        // 判断均线是否都在上升
        if(lastMaState2.getMa5() != 0
                && lastMaState2.getMa10() != 0
                && lastMaState2.getMa20() != 0
                && lastMaState2.getMa30() != 0){
            if(lastMaState1.getMa5() - lastMaState2.getMa5() > 0
                    && lastMaState1.getMa10() - lastMaState2.getMa10() > 0
                    && lastMaState1.getMa20() - lastMaState2.getMa20() > 0
                    && lastMaState1.getMa30() - lastMaState2.getMa30() > 0){
                isRise = true;
            }
        }


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


        // 判断日K线是否发散
        if(stock.getDayMaPrice() != null){
            if(stock.getMa5(lastMaState1.getPrice()) >= stock.getDayMaPrice().getMa10() &&
                    stock.getDayMaPrice().getMa10() >= stock.getDayMaPrice().getMa30()){
                isDayMaUp = true;
            }
        }

        // 判断是否比前四日的价格高
        if(lastMaState1.getPrice() > stock.getFiveDayHighestEndPrice() * 1.001){
            isHigherThanBeforeDays = true;
        }


        if(isSeriation && isRise && isDayMaUp && isHigherThanBeforeDays){
//            Log.d("lwd", String.format("%s 买他，价格:%s", lastMaState1.getTime(), lastMaState1.getPrice()));
            return new StockForm(stock.getId(), getFormId(), kLevel, lastMaState1.getTime(), 0, lastMaState1.getPrice());
        }
        return null;
    }
}
