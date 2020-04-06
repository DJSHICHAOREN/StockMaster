package com.example.stockmaster.ui.activity.recommand;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.base.BasePresent;

import java.util.ArrayList;

public class RecommandPresent extends BasePresent {
    private SinaDataQueryer mSinaDataQueryer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: {
                    Log.d("lwd", "获取今天股票数据");
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    for (final String stockId : stockIdList) {
                        new Thread() {
                            @Override
                            public void run() {
                                mSinaDataQueryer.queryStocksTodayPrice(stockId);
//                                mSinaDataQueryer.queryStocksFiveDayAvgPrice(stockId);
                            }
                        }.start();
                    }
                    break;
                }
            }
        }
    };



    public RecommandPresent(AppCompatActivity view) {
        super(view);
    }


}
