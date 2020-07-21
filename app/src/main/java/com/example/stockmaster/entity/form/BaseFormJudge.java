package com.example.stockmaster.entity.form;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.ma.MaState;

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
}
