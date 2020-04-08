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
import com.example.stockmaster.entity.masina.MAResponseResult;
import com.example.stockmaster.entity.sina.SinaResponse;
import com.example.stockmaster.http.converter.ResponseStringToObject;
import com.example.stockmaster.util.StockManager;
import com.example.stockmaster.util.TextUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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
//                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd",String.format("请求分时数据失败"));
                    }
                });

        mQueue.add(stringRequest);
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
                        try{
                            if(dayCount == 1){
                                List<StockPrice> stockPriceList = mResponseStringToObject.sinaTodayPriceResponseToObjectList(response);
                                mStockManager.addTodayStockPrice(stockPriceList, stockId);
                                // 在收到股票分时数据并建立股票实例以后在请求股票的五日数据，计算五日均价
                                queryStocksFiveDayAvgPrice(stockId);
                                queryStocksMAPrice(stockId);
                            }
                            else if(dayCount == 5){
                                // 得到收盘价列表
                                List<String> closedPriceList = mTextUtil.getAllSatisfyStrings(response,
                                        "\"prevclose\":\"\\d*\\.\\d*\"");

                                List<Float> fiveDayPriceList = new ArrayList<>();
                                for(String closedPrice : closedPriceList){
                                    String stringPrice = closedPrice.split(":")[1].replaceAll("\"", "");
                                    fiveDayPriceList.add(Float.parseFloat(stringPrice));
                                }
                                if(fiveDayPriceList.size() == 5){
                                    mStockManager.setPreviousFourDayPriceList(fiveDayPriceList.subList(1, fiveDayPriceList.size()), stockId);
                                }
                            }
                        }
                        // 得到的时间为空字符串，则抛出异常
                        catch (NumberFormatException ex){
                            Log.e("lwd","得到空的时间字符串");
                            queryStocksTodayPrice(stockId);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryStocksTodayPrice(stockId);
//                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd",String.format("%s请求%s天数据失败", stockId, dayCount));
                        Log.e("lwd", "异常信息：" + error.getMessage());
                    }
                });

        mQueue.add(stringRequest);
    }

    /**
     * 请求10日、30日、50日、100日、250日均价
     * http://web.ifzq.gtimg.cn/appstock/hk/Hkinchot/averageVolatility?code=02400&_callback=jQuery112
     * @param stockId
     */
    public void queryStocksMAPrice(final String stockId){
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);

        String noHkStockId = stockId;
        if(stockId.contains("hk")){
            noHkStockId = stockId.replace("hk", "");
        }
        String url = "http://web.ifzq.gtimg.cn/appstock/hk/Hkinchot/averageVolatility?code="+ noHkStockId + "&callback=:::hk" + noHkStockId + ":::";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        MAResponseResult mAResponseResult = new Gson().fromJson(jsonObject, new TypeToken<MAResponseResult>(){}.getType());
                        Log.d("lwd", String.format("stockId:%s, ma10:%s", stockId, mAResponseResult.getData().getMA10()));
                        mStockManager.addMAPrice(stockId, mAResponseResult.getData().getMA10(), mAResponseResult.getData().getMA30(),
                                mAResponseResult.getData().getMA50(), mAResponseResult.getData().getMA100(),
                                mAResponseResult.getData().getMA250());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryStocksMAPrice(stockId);
                        Log.e("lwd","请求均线数据失败");
                    }
                });

        mQueue.add(stringRequest);
    }

}
