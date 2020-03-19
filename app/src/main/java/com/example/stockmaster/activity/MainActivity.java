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
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_test)
    public TextView tv_test;
    private Timer timer;
    private StockManager mStockManager = new StockManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, 1000); // 1 seconds
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    querySinaHeadStocks("sh600000");
                    Log.d("lwd","获取股票数据");
                    break;
                }
                case 2:{

                }


            }
        }
    };


    RequestQueue queue;
    public void querySinaHeadStocks(String list){
        // Instantiate the RequestQueue.
        if(queue==null)
            queue = Volley.newRequestQueue(this);
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        updateStockHeader(sinaResponseToStocks(response));
                        List<StockPrice> stockPriceList = sinaResponseToStocks(response);
                        tv_test.setText(stockPriceList.get(0).id + " " + stockPriceList.get(0).name + stockPriceList.get(0).current_price);

                        mStockManager.add(stockPriceList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd","请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    public List<StockPrice> sinaResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<StockPrice> stockPriceBeanList = new ArrayList<>();
        for(String stock : stocks) {
            String[] leftRight = stock.split("=");
            if (leftRight.length < 2)
                continue;

            String right = leftRight[1].replaceAll("\"", "");
            if (right.isEmpty())
                continue;

            String left = leftRight[0];
            if (left.isEmpty())
                continue;

            StockPrice stockPriceNow = new StockPrice();
            String[] lefts = left.split("_");
            stockPriceNow.id = lefts[2];

            String[] values = right.split(",");
            try{
                stockPriceNow.name = values[0];
                stockPriceNow.current_price = values[3];
                stockPriceNow.time = values[31];

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockPriceBeanList.add(stockPriceNow);
        }

        return stockPriceBeanList;
    }
}
