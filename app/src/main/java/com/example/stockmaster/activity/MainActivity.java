package com.example.stockmaster.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_test)
    public TextView tv_test;
    private Timer timer;
    private StockManager mStockManager;
    private SinaDataQueryer mSinaDataQueryer;
    private StockAnalyser mStockAnalyser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityUIManager mainActivityUIManager = new MainActivityUIManager();
        mStockAnalyser = new StockAnalyser(mainActivityUIManager);
        mStockManager = new StockManager(mainActivityUIManager, mStockAnalyser);
        mSinaDataQueryer = new SinaDataQueryer(MainActivity.this, mStockManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<String> stockIdList = new ArrayList<String>(Arrays.asList("hk02400", "hk06060", "hk09969"));

        // 实例化股票对象
        mStockManager.createStocks(stockIdList);
        // 获取从开盘到现在的股票数据
        Message todayPriceMessage = Message.obtain();
        todayPriceMessage.what = 1;
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("stockIdList", stockIdList);
        todayPriceMessage.setData(bundle);
        handler.sendMessage(todayPriceMessage);
        // 获取每分钟的数据
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message minutePriceMessage = Message.obtain();
                minutePriceMessage.what = 2;
                minutePriceMessage.setData(bundle);
                handler.sendMessage(minutePriceMessage);
            }
        }, 0, 2000); // 1 seconds
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    Log.d("lwd","获取今天股票数据");
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    for(final String stockId : stockIdList) {
                        new Thread() {
                            @Override
                            public void run() {
                                mSinaDataQueryer.queryStocksTodayPrice(stockId);
                            }
                        }.start();
                    }
                    break;
                }
                case 2:{
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    String stockIdStr = "";
                    for(String stockId : stockIdList) {
                        stockIdStr = stockIdStr + "rt_" + stockId + ",";
                    }
                    mSinaDataQueryer.queryStocksNowPrice(stockIdStr);
                    break;
                }

            }
        }
    };

    public class MainActivityUIManager implements UIManager{
        @Override
        public void refreshUIWhenReceiveNewPrice(Stock stock){
            tv_test.setText(stock.toString());
        }

        @Override
        public void refreshUIWhenGetNewBuyPoint() {

        }
    }

}

