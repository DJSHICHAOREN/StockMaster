package com.example.stockmaster.ui.activity.recommand;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.List;

public class RecommandPresent extends BasePresent {

    private StockManager mStockManager = new StockManager();

    public RecommandPresent(AppCompatActivity view) {
        super(view);
    }

    public List<Stock> getLineUpStocks(){
        return StockManager.getLineUpStocks();
    }

}
