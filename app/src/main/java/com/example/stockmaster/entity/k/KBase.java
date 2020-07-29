package com.example.stockmaster.entity.k;

import android.util.Log;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.entity.ma.MaState;
import com.example.stockmaster.util.DateUtil;
import com.example.stockmaster.util.MaCalculater;
import com.example.stockmaster.util.MaStateAnalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.stockmaster.util.DateUtil.convertDateToShortMinuteString;

public class KBase {
    public String TIME_POINT_STRING = "";
    public List<String> TIME_POINT_STRING_LIST;
    private MaStateAnalyser maStateAnalyser = MaStateAnalyser.getInstance();
    private List<StockPrice> qualifiedPricePointList = new ArrayList<>();

    private List<MaState> maStateList = new ArrayList<>();
    private List<StockPrice> mKeyStockPriceList = new ArrayList<>();

    private int mKLevel = 0; // K线的级别
    private String mStockId;
    private Stock mStock;

    public KBase(Stock stock, String TIME_POINT_STRING, int kLevel){
        this.TIME_POINT_STRING = TIME_POINT_STRING;
        TIME_POINT_STRING_LIST = Arrays.asList(TIME_POINT_STRING.split(","));
        mKLevel = kLevel;
        mStockId = stock.getId();
        mStock = stock;
    }

    /**
     * 清除之前的状态
     * @param time
     */
    public void clearAdvanceState(Date time){
        int maStateListIndex = maStateList.size() -1;
        while (maStateListIndex >= 0){
            if(DateUtil.isDateAfter(maStateList.get(maStateListIndex).getTime(), time)
                    || DateUtil.isDateEqual(maStateList.get(maStateListIndex).getTime(), time) ){
                maStateList.remove(maStateListIndex);
                maStateListIndex--;
            }
            else{
                break;
            }
        }

        int mKeyStockPriceListIndex = mKeyStockPriceList.size() -1;
        while (mKeyStockPriceListIndex >= 0){
            if(DateUtil.isDateAfter(mKeyStockPriceList.get(mKeyStockPriceListIndex).getTime(), time)
                    || DateUtil.isDateEqual(mKeyStockPriceList.get(mKeyStockPriceListIndex).getTime(), time) ){
                mKeyStockPriceList.remove(mKeyStockPriceListIndex);
                mKeyStockPriceListIndex--;
            }
            else{
                break;
            }
        }
    }

    /**
     * 添加新的股票价格
     * @param stockPrice
     * @return
     */
    public List<StockForm> addStockPrice(StockPrice stockPrice){
        // 在分时请求中，删除之前分时请求的重叠状态
        // 删除要添加的stockPrice之前的state和keyPrice
        while (maStateList.size() > 0){
            // 得到最后一个状态
            MaState lastMaState = maStateList.get(maStateList.size() - 1);
            if(DateUtil.isDateAfter(lastMaState.getTime(), stockPrice.getTime()) ||
            DateUtil.isDateEqual(lastMaState.getTime(), stockPrice.getTime())){
                maStateList = maStateList.subList(0, maStateList.size()-1);
            }
            else{
                break;
            }
        }
        while (mKeyStockPriceList.size() > 0){
            StockPrice lastKeyStockPrice = mKeyStockPriceList.get(mKeyStockPriceList.size() - 1);
            if(DateUtil.isDateAfter(lastKeyStockPrice.getTime(), stockPrice.getTime()) ||
                    DateUtil.isDateEqual(lastKeyStockPrice.getTime(), stockPrice.getTime())){
                mKeyStockPriceList = mKeyStockPriceList.subList(0, mKeyStockPriceList.size()-1);
            }
            else{
                break;
            }
        }

        // 计算Form
        // 求关键价格列表
        List<StockPrice> filteredStockPriceList = filterKeyStockPrice(stockPrice);
        MaState maState = MaCalculater.calMaState(filteredStockPriceList);

        if(maState != null && maState.getMa5() != 0){
            maStateList.add(maState);
        }

        calCandleArgs(maStateList);

        List<StockForm> stockFormList = maStateAnalyser.analyse(mStock, maStateList, mKLevel);

        for(StockForm stockForm : stockFormList){
            Log.d("lwd", stockForm.toString());
        }

        return stockFormList;
    }

    /**
     * 得到关键价格列表，总以最新价格结尾
     * 关键价格列表+最新价格
     * @param stockPrice
     */
    private List<StockPrice> filterKeyStockPrice(StockPrice stockPrice){
        if(isDateTheKeyTime(stockPrice.getTime())){
            mKeyStockPriceList.add(stockPrice);
            return mKeyStockPriceList;
        }
        else{
            List<StockPrice> stockPriceList = new ArrayList<>();
            stockPriceList.addAll(mKeyStockPriceList);
            stockPriceList.add(stockPrice);
            return stockPriceList;
        }
    }

    private void calCandleArgs(List<MaState> maStateList){
        if(maStateList.size() < 1){
            return;
        }
        MaState lastMaState = maStateList.get(maStateList.size()-1);
        if(maStateList.size() == 1){
            lastMaState.setCandleArgs(lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice());
            lastMaState.setSupportPrice(-1);
        }
        else{
            MaState previousMaState = maStateList.get(maStateList.size()-2);

            // 如果前一个是关键点价格点
            // 支撑价是上一个前一段的最低价
            if(isDateTheKeyTime(previousMaState.getTime())){
                lastMaState.setCandleArgs(lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice(), lastMaState.getPrice());
                lastMaState.setSupportPrice(previousMaState.getLowestPrice());
            }
            else{
                // 得到最高价
                if(lastMaState.getPrice() > previousMaState.getHighestPrice()){
                    lastMaState.setHighestPrice(lastMaState.getPrice());
                }
                else{
                    lastMaState.setHighestPrice(previousMaState.getHighestPrice());
                }

                // 得到最低价
                if(lastMaState.getPrice() < previousMaState.getLowestPrice()){
                    lastMaState.setLowestPrice(lastMaState.getPrice());
//                    Log.d("lwd", String.format("new lowest price：%f, time:%s", lastMaState.getPrice(),
//                            lastMaState.getTime().toString()));
                }
                else{
                    lastMaState.setLowestPrice(previousMaState.getLowestPrice());
                }

                // 得到开盘价
                lastMaState.setBeginPrice(previousMaState.getBeginPrice());
                // 得到收盘价
                lastMaState.setEndPrice(lastMaState.getPrice());
                // 得到支撑价
                lastMaState.setSupportPrice(previousMaState.getSupportPrice());
            }
        }

        // 蜡烛图日志
//            if(isDateTheKeyTime(maState.getTime())){
//                Log.d("lwd", String.format("level:%d %s", mKLevel, maState.toCandleString()));
//            }
    }

    private boolean isDateTheKeyTime(Date time){
        String minuteTime = convertDateToShortMinuteString(time);
        if(TIME_POINT_STRING.indexOf(minuteTime) != -1){
            return true;
        }
        return false;
    }


    public void setTIME_POINT_STRING(String TIME_POINT_STRING) {
        this.TIME_POINT_STRING = TIME_POINT_STRING;
    }

    public List<StockPrice> getQualifiedPricePointList() {
        return qualifiedPricePointList;
    }
}
