package com.example.stockmaster.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String ShIndex = "s_sh000001";
    private final static String SzIndex = "s_sz399001";
    private final static String ChuangIndex = "s_sz399006";
    private final static String Sh50Index = "s_sh000016";
    private final static String Sh300Index = "s_sh000300";
    private final static String ZXIndex = "s_sz399005";
    private final static String DqsIndex = "gb_$dji";
    private final static String NsdkIndex = "gb_ixic";
    private final static String HkIndex = "rt_hkHSI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this,"数据请求失败",Toast.LENGTH_LONG).show();
                        Log.e("lwd","请求数据失败");
                    }
                });

        queue.add(stringRequest);
        queue.start();
    }

    public List<Stock> sinaResponseToStocks(String response){
        response = response.replaceAll("\n", "");
        String[] stocks = response.split(";");

        List<Stock> stockBeanList = new ArrayList<>();
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

            Stock stockNow = new Stock();
            String[] lefts = left.split("_");
            stockNow.id_ = lefts[2]+"_"+lefts[3];

            String[] values = right.split(",");
            try{
                if(stockNow.id_.equals(ShIndex) || stockNow.id_.equals(SzIndex) || stockNow.id_.equals(ChuangIndex)){
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[2];
                    stockNow.percent = values[3];
                }else if(stockNow.id_.equals(DqsIndex) || stockNow.id_.equals(NsdkIndex)){
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[4];
                    stockNow.percent = values[2];
                }else if(stockNow.id_.equals(HkIndex)){
                    stockNow.name_ = values[1];
                    stockNow.now_ = values[6];
                    stockNow.increase = values[7];
                    stockNow.percent = values[8];
                }else {
                    stockNow.name_ = values[0];
                    stockNow.now_ = values[1];
                    stockNow.increase = values[2];
                    stockNow.percent = values[3];
                }

            }catch (ArrayIndexOutOfBoundsException e){
                Log.e("MainActivity",e.toString());
            }

            stockBeanList.add(stockNow);
        }

        return stockBeanList;
    }
}
