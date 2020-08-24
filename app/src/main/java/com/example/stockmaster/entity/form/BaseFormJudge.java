package com.example.stockmaster.entity.form;

import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.ma.MaState;

import java.util.Date;
import java.util.List;

public abstract class BaseFormJudge {
    private int formId;
    protected boolean isPrintBeginJudgeTime = true;
    public BaseFormJudge(int formId){
        this.formId = formId;
    }

    public abstract StockForm judge(Stock stock, List<MaState> maStateList, int kLevel);

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public MaState getMaStateByTime(List<MaState> maStateList, Date time){
        for(int i=maStateList.size()-1; i>=0; i--){
            if(maStateList.get(i).getTime() == time || maStateList.get(i).getTime().before(time)){
                return maStateList.get(i);
            }
        }
        return null;
    }
}
