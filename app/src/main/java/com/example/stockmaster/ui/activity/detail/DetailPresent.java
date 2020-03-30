package com.example.stockmaster.ui.activity.detail;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockManager;

import java.util.List;

public class DetailPresent extends BasePresent {

    private int mStockIndex;
    private StockManager mStockManager;

    public DetailPresent(AppCompatActivity view, int stockIndex) {
        super(view);

        mStockIndex = stockIndex;
        mStockManager = new StockManager();
    }

    public List<StockPrice> getDealPriceList(){
        return mStockManager.getThisStockDealPriceList(mStockIndex);
    }


}
