package com.example.stockmaster.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.http.converter.ResponseStringToObject;
import com.example.stockmaster.util.StockManager;
import com.example.stockmaster.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class SinaDataQueryer {

    private Context mContext;
    private StockManager mStockManager;
    private RequestQueue mQueue;
    private ResponseStringToObject mResponseStringToObject = new ResponseStringToObject();
    private TextUtil mTextUtil = new TextUtil();
    public SinaDataQueryer (Context context, StockManager stockManager){
        mContext = context;
        mStockManager = stockManager;
    }

    /**
     * 获取股票的当前价格
     * @param list 股票代号列表
     */
    public void queryStocksNowPrice(final String list){
        // Instantiate the RequestQueue.
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);
        String url ="http://hq.sinajs.cn/list=" + list;
        //http://hq.sinajs.cn/list=sh600000,sh600536
        //https://hq.sinajs.cn/?_=0.011979296747612889&list=rt_hk02400,rt_hkHSI

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<StockPrice> stockPriceList = mResponseStringToObject.sinaMinutePriceResponseToObjectList(response);
                        mStockManager.addMinuteStockPrice(stockPriceList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryStocksNowPrice(list);
                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd","请求数据失败");
                        Log.e("lwd",error.getMessage());
                    }
                });

        mQueue.add(stringRequest);
        mQueue.start();
    }

    /**
     * 查询股票今天的价格
     * @param stockId
     */
    public void queryStocksTodayPrice(String stockId){
        queryStocksNDayPrice(stockId, 1);
    }

    /**
     * 查询股票的五日均价
     * @param stockId
     */
    public void queryStocksFiveDayAvgPrice(String stockId){
        queryStocksNDayPrice(stockId, 5);
    }



    /**
     * 查询股票N天的价格
     * https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=02400&day=1&callback=var%20hkT1=
     * @param stockId
     */
    public void queryStocksNDayPrice(final String stockId, final int dayCount){
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);

        String noHkStockId = stockId;
        if(stockId.contains("hk")){
            noHkStockId = stockId.replace("hk", "");
        }
        String url ="https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=" + noHkStockId + "&day="+ dayCount +"&callback=:::hk" + noHkStockId + ":::";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(dayCount == 1){
                            List<StockPrice> stockPriceList = mResponseStringToObject.sinaTodayPriceResponseToObjectList(response);
                            mStockManager.addTodayStockPrice(stockPriceList, stockId);
                        }
                        else if(dayCount == 5){
                            // 得到收盘价列表
                            ArrayList<String> closedPriceList = mTextUtil.getAllSatisfyStrings(response,
                                    "\"prevclose\":\"\\d*\\.\\d*\"");
                            int a = 1;
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryStocksTodayPrice(stockId);
                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd","请求数据失败");
                    }
                });

        mQueue.add(stringRequest);
        mQueue.start();
    }

}
