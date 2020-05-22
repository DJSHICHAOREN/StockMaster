package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.ma.MaState;

import java.util.Date;
import java.util.List;

public class MaStateAnalyser {
    private List<MaState> maStateList;

    public MaStateAnalyser(List<MaState> maStateList){
        this.maStateList = maStateList;
    }

    public boolean analyse(){
        if(maStateList == null || maStateList.size() < 3){
            Log.d("lwd", "maStateList为空或者maStateList的长度小于3");
            return false;
        }
        // 判断最新的三条线是否是按序排列且上升的
        int maStateListLength = maStateList.size();
        MaState lastMaState1 = maStateList.get(maStateListLength-1);
        MaState lastMaState2 = maStateList.get(maStateListLength-2);
        MaState lastMaState3 = maStateList.get(maStateListLength-3);

        boolean isSeriation = false; // 均线是否是呈梯子型排列
        boolean isRise = false; // 均线是否上升
        boolean isHorizontalBefore = false; // 均线之前是否横盘
        // 判断均线是否阶梯形排列
        if(lastMaState1.getMa5() > lastMaState1.getMa10() && lastMaState1.getMa10() > lastMaState1.getMa20()){
            isSeriation = true;
        }
        // 判断均线是否都在上升
        if(lastMaState2.getMa5() != 0 && lastMaState2.getMa10() != 0 && lastMaState2.getMa20() != 0){
            if(lastMaState1.getMa5() - lastMaState2.getMa5() > 0
                    && lastMaState1.getMa10() - lastMaState2.getMa10() > 0
                    && lastMaState1.getMa20() - lastMaState2.getMa20() > 0){
                isRise = true;
            }
        }
        // 判断均线在之前是否横盘
        if(lastMaState3.getMa5() != 0 && lastMaState3.getMa10() != 0 && lastMaState3.getMa20() != 0){
            // 得到均线之前的斜率
            float ma5SlopeBefore = (lastMaState2.getMa5() - lastMaState3.getMa5())/lastMaState3.getMa5();
            float ma10SlopeBefore = (lastMaState2.getMa10() - lastMaState3.getMa10())/lastMaState3.getMa10();
//            float ma20Slope = (lastMaState2.getMa20() - lastMaState3.getMa20())/lastMaState3.getMa20();
            float ma5SlopeNow = (lastMaState1.getMa5() - lastMaState2.getMa5())/lastMaState2.getMa5();
            float ma10SlopeNow = (lastMaState1.getMa10() - lastMaState2.getMa10())/lastMaState2.getMa10();
            if(ma5SlopeBefore < 0.01 && ma10SlopeBefore < 0.01){

            }
        }
        if(isSeriation && isRise){
            return true;
        }
        return false;

    }
}
