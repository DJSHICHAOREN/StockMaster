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
        if(maStateList == null || maStateList.size() < 2){
            Log.d("lwd", "maStateList为空或者maStateList的长度小于2");
            return false;
        }
        // 判断最新的三条线是否是按序排列且上升的
        int maStateListLength = maStateList.size();
        MaState lastMaState1 = maStateList.get(maStateListLength-1);
        MaState lastMaState2 = maStateList.get(maStateListLength-2);

        boolean isSeriation = false; // 均线是否是呈梯子型排列
        boolean isRise = false; // 均线是否上升
        if(lastMaState1.getMa5() > lastMaState1.getMa10() && lastMaState1.getMa10() > lastMaState1.getMa20()){
            isSeriation = true;
        }
        if(lastMaState2.getMa5() != 0 && lastMaState2.getMa10() != 0 && lastMaState2.getMa20() != 0){
            if(lastMaState1.getMa5() - lastMaState2.getMa5() > 0
                    && lastMaState1.getMa10() - lastMaState2.getMa10() > 0
                    && lastMaState1.getMa20() - lastMaState2.getMa20() > 0){
                isRise = true;
            }
        }
        if(isSeriation && isRise){
            return true;
        }
        return false;

    }
}
