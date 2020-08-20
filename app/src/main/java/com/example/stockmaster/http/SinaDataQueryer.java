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
import com.example.stockmaster.entity.stock.StockPrice;
import com.example.stockmaster.entity.ma.DayMaPrice;
import com.example.stockmaster.entity.masina.MAResponseResult;
import com.example.stockmaster.http.converter.ResponseStringToObject;
import com.example.stockmaster.util.MAGenerator;
import com.example.stockmaster.util.StockManager;
import com.example.stockmaster.util.TextUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class SinaDataQueryer {

    private Context mContext;
    private RequestQueue mQueue;
    private ResponseStringToObject mResponseStringToObject = new ResponseStringToObject();
    MAGenerator mMaGenerator = new MAGenerator();
    private OkHttpClient mHttpClient = null;


    public SinaDataQueryer (Context context){
        mContext = context;

        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient.Builder()
                    .readTimeout(5, TimeUnit.SECONDS) //设置读超时
                    .writeTimeout(5,TimeUnit.SECONDS) //设置写超时
                    .connectTimeout(15,TimeUnit.SECONDS) //设置连接超时
                    .retryOnConnectionFailure(true) //是否自动重连
                    .build();
        }
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
                        StockManager.addMinuteStockPriceNew(stockPriceList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        queryStocksNowPrice(list);
                        Toast.makeText(mContext,"分时数据请求失败", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        mQueue.add(stringRequest);
    }



    /**
     * 查询股票的五日均价
     * @param stockId
     */
    public void queryStocksFiveDayPrice(String stockId, boolean isNewStock){
        queryStocksNDayPrice(stockId, 5);
//        queryStocksNDayPriceSync(stockId, 5);
    }


    /**
     * 查询股票N天的价格
     * https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=02400&day=1&callback=var%20hkT1=
     * @param stockIdCode
     */
    public void queryStocksNDayPrice(String stockIdCode, final int dayCount){
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);
        // 为stockId添加hk
        if(!stockIdCode.contains("hk")){
            stockIdCode = "hk" + stockIdCode;
        }
        final String stockId = stockIdCode;

        String url ="https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=" + stockId.replace("hk", "") + "&day="+ dayCount +"&callback=:::" + stockId + ":::";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            if(dayCount == 5){
                                List<List<StockPrice>> stockPriceEveryDayList = mResponseStringToObject.sinaNDaysPriceResponseToObjectList(response, false, StockPrice.QueryType.FIVEDAY);
                                List<Date> dateList = TextUtil.convertStringToDateList(response);
                                StockManager.setDealDateList(dateList);

                                // 为了求五日均线,得到收盘价列表
                                List<Float> fiveDayClosePriceList = mMaGenerator.generateDayMA5(response);
                                StockManager.setPreviousFourDayPriceList(fiveDayClosePriceList, stockId);

                                StockManager.addFiveDayStockPriceListNew(stockPriceEveryDayList, stockId);
                            }
                            Log.d("lwd", String.format("%s %d日数据添加完毕", stockId, dayCount));
                        }
                        // 得到的时间为空字符串，则抛出异常
                        catch (NumberFormatException ex){
                            Log.e("lwd","得到空的时间字符串");
//                            queryStocksNDayPrice(stockId, dayCount);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        queryStocksFiveDayPrice(stockId, false);
//                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd",String.format("%s请求%s天数据失败", stockId, dayCount));
                        Log.e("lwd", "异常信息：" + error.getMessage());
                    }
                });

        mQueue.add(stringRequest);
    }

    public void queryStocksNDayPriceSync(String stockIdCode, final int dayCount){
        // 为stockId添加hk
        if(!stockIdCode.contains("hk")){
            stockIdCode = "hk" + stockIdCode;
        }
        final String stockId = stockIdCode;

        String url ="https://quotes.sina.cn/hk/api/openapi.php/HK_MinlineService.getMinline?symbol=" + stockId.replace("hk", "") + "&day="+ dayCount +"&callback=:::" + stockId + ":::";

        okhttp3.Request request=new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        Call call=mHttpClient.newCall(request);
        try {
            okhttp3.Response response=call.execute();
            String responseStr = response.body().string();

            List<List<StockPrice>> stockPriceEveryDayList = mResponseStringToObject.sinaNDaysPriceResponseToObjectList(responseStr, false, StockPrice.QueryType.FIVEDAY);
            List<Date> dateList = TextUtil.convertStringToDateList(responseStr);

            // 为了求五日均线,得到收盘价列表
            List<Float> fiveDayClosePriceList = mMaGenerator.generateDayMA5(responseStr);
            StockManager.setPreviousFourDayPriceList(fiveDayClosePriceList, stockId);

            StockManager.addFiveDayStockPriceListNew(stockPriceEveryDayList, stockId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询股票今天的价格
     * https://stock.finance.sina.com.cn/hkstock/api/openapi.php/HK_StockService.getHKMinline?symbol=02400&random=1594780810111&callback=var%20t1hk02400=
     * @param stockIdCode
     */
    public void queryStocksTodayPrice(String stockIdCode){
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);
        // 为stockId添加hk
        final String stockId = addHKToStockId(stockIdCode);

        String url = "https://stock.finance.sina.com.cn/hkstock/api/openapi.php/HK_StockService.getHKMinline?symbol=" + stockId.replace("hk", "")
                + "" + "&callback=:::" + stockId + ":::";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            List<StockPrice> stockPriceList = mResponseStringToObject.sinaTodayPriceResponseToObjectList(response, StockPrice.QueryType.TODAY);
                            StockManager.addOneDayStockPriceListNew(stockPriceList, stockId);
                            Log.d("lwd", String.format("%s 今日最准数据添加完毕", stockId));
                        }
                        // 得到的时间为空字符串，则抛出异常
                        catch (NumberFormatException ex){
                            Log.e("lwd","得到空的时间字符串");
//                            queryStocksTodayPrice(stockId);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        queryStocksTodayPrice(stockId);
//                        Toast.makeText(mContext,"数据请求失败", Toast.LENGTH_LONG).show();
                        Log.e("lwd",String.format("%s今日数据请求失败", stockId));
                        error.printStackTrace();
                    }
                });

        mQueue.add(stringRequest);
    }

    /**
     * 请求10日、30日、50日、100日、250日均价
     * http://web.ifzq.gtimg.cn/appstock/hk/Hkinchot/averageVolatility?code=02400&_callback=jQuery112
     * @param stockIdCode
     */
    public void queryStocksMAPrice(String stockIdCode){
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);

        // 为stockId添加hk
        if(!stockIdCode.contains("hk")){
            stockIdCode = "hk" + stockIdCode;
        }
        final String stockId = stockIdCode;
        String url = "http://web.ifzq.gtimg.cn/appstock/hk/Hkinchot/averageVolatility?code="+ stockId.replace("hk", "") + "&callback=:::" + stockId + ":::";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                        MAResponseResult mAResponseResult = new Gson().fromJson(jsonObject, new TypeToken<MAResponseResult>(){}.getType());
//                        Log.d("lwd", String.format("stockId:%s, ma10:%s", stockId, mAResponseResult.getData().getMA10()));
                        DayMaPrice dayMaPrice = new DayMaPrice(stockId, StockManager.getLastDealDate(), mAResponseResult.getData().getMA10(), mAResponseResult.getData().getMA30(),
                                mAResponseResult.getData().getMA50(), mAResponseResult.getData().getMA100(),
                                mAResponseResult.getData().getMA250());
                        StockManager.setStockDayMaPrice(stockId, dayMaPrice);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        queryStocksMAPrice(stockId);
                        Log.e("lwd","请求均线数据失败");
                    }
                });

        mQueue.add(stringRequest);
    }

    public void queryLastDealDate(){
        // Instantiate the RequestQueue.
        if(mQueue ==null)
            mQueue = Volley.newRequestQueue(mContext);
        String url = "http://hq.sinajs.cn/list=rt_hk09988";
        //http://hq.sinajs.cn/list=rt_hk09988

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        String dateString = TextUtil.searchDateString(response);
                        Date time = TextUtil.searchDate(response);
                        StockManager.setLastDealDate(time);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 不能在出错之后立刻请求，否则会一直出错
//                        queryLastDealDate();
                        Log.e("lwd",String.format("请求最近交易日期失败"));
                    }
                });

        mQueue.add(stringRequest);
    }

    private String addHKToStockId(String stockId){
        if(!stockId.contains("hk")){
            stockId = "hk" + stockId;
        }
        return stockId;
    }

}
