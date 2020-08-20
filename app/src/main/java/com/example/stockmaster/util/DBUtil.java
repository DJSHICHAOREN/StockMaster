package com.example.stockmaster.util;

import android.util.Log;

import com.example.stockmaster.entity.k.K30Minutes;
import com.example.stockmaster.entity.stock.DealDate;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.stock.StockPrice;
import com.example.stockmaster.entity.form.StockForm;
import com.example.stockmaster.ui.activity.main.MainActivity;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBUtil {
    public static DbManager db;
    public static DbManager.DaoConfig daoConfig = null;

    public static void initDBUtil() throws DbException {
        if(daoConfig == null){
            daoConfig = new DbManager.DaoConfig()
                    .setDbName("test.db")
                    // 不设置dbDir时, 默认存储在app的私有目录.
                    .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                    .setDbVersion(2)
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL, 对写入加速提升巨大
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    })
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            // TODO: ...
                            // db.addColumn(...);
                            // db.dropTable(...);
                            // ...
                            // or
                            // db.dropDb();
                        }
                    });
            if(MainActivity.mDBFile != null){
                daoConfig = daoConfig.setDbDir(MainActivity.mDBFile);
                Log.d("lwd", "mDBFile != null");
            }
            else{
                Log.d("lwd", "mDBFile == null");
            }
        }
        if(db == null){
            db = x.getDb(daoConfig);
        }
    }

    /**
     * 只有在数据库中不存在这只股票时才将其加入数据库
     * 当数据库中存在这只股票时，返回数据库中的这只股票
     * 如果数据库中不存在，则加入数据库
     * @param stock
     */
    public static Stock saveStock(Stock stock){
        try {
            initDBUtil();

            Stock oldStock = db.selector(Stock.class)
                    .where("id", "=", stock.getId())
                    .findFirst();
            if(oldStock != null){
                return oldStock;
            }
            db.save(stock);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return stock;
    }

    public static void updateStock(Stock stock){
        try {
            initDBUtil();
            db.saveOrUpdate(stock);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List<Stock> getAllStocks(){
        try {
            initDBUtil();
            List<Stock> stockList = db.selector(Stock.class).orderBy("id").findAll();
            if(stockList == null){
                return new ArrayList<>();
            }
            return stockList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void saveStockPrice(StockPrice stockPrice){
        try {
            initDBUtil();

            StockPrice oldStockPrice = db.selector(StockPrice.class)
                    .where("stockId", "=", stockPrice.getStockId())
                    .and("time", "=", stockPrice.getTime())
                    .findFirst();
            // 之前没有存储价格，或价格有更新时，存储价格
            if(oldStockPrice == null){
                db.saveBindingId(stockPrice);
            }
            else if(oldStockPrice.getPrice() != stockPrice.getPrice()){
                db.saveOrUpdate(stockPrice);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List<StockPrice> getOneDayStockPriceList(String stockId, Date date){
        try {
            List<StockPrice> dealDateList = db.selector(StockPrice.class)
                    .where("stockId", "=", stockId)
                    .and("dealDate", "=", date)
                    .findAll();
            if(dealDateList != null){
                return dealDateList;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public static List<StockPrice> getStockPriceList(String stockId){
        try {
            initDBUtil();
            List<StockPrice> stockPriceList = db.selector(StockPrice.class)
                    .where("stockId", "=", stockId)
                    .orderBy("time").findAll();
            if(stockPriceList == null){
                return new ArrayList<>();
            }
            return stockPriceList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static DealDate saveDealDate(DealDate dealDate) {
        try {
            initDBUtil();
            DealDate oldDealStock = db.selector(DealDate.class)
                    .where("date", "=", dealDate.getDate())
                    .findFirst();
            if (oldDealStock != null) {
                return oldDealStock;
            }
            db.save(dealDate);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dealDate;
    }

    public static List<DealDate> getAllDealDate(){
        try {
            initDBUtil();
            List<DealDate> dealDateList = db.selector(DealDate.class).orderBy("date").findAll();
            if(dealDateList == null){
                return new ArrayList<>();
            }
            return dealDateList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }



    public static void  dropStockFormTable(){
        try {
            initDBUtil();
            db.dropTable(StockForm.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将符合K30Minutes的价格存入数据库
     * @param stockPrice
     */
    public static void saveOrUpdate30MinuteStockPrice(StockPrice stockPrice){
        String timeContent = "10:00:00, 10:30:00, " +
                "11:00:00, 11:30:00, " +
                "12:00:00, " +
                "13:30:00, " +
                "14:00:00, 14:30:00, " +
                "15:00:00, 15:30:00, " +
                "16:00:00 ";
        // 判断price是否是在K30Minute中
        if(DateUtil.isTimeInContent(timeContent, stockPrice.getTime())){
            saveStockPrice(stockPrice);
        }
    }

}
