package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;
import com.example.stockmaster.entity.form.StockForm;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class DBUtil {
    public static DbManager db;
    public static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("test.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
//            .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
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

    /**
     * 只有在数据库中不存在这只股票时才将其加入数据库
     * 当数据库中存在这只股票时，返回数据库中的这只股票
     * 如果数据库中不存在，则加入数据库
     * @param stock
     */
    public static Stock saveStock(Stock stock){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
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
            if(db == null){
                db = x.getDb(daoConfig);
            }
            db.saveOrUpdate(stock);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveStockPrice(StockPrice stockPrice){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }

            StockPrice oldStockPrice = db.selector(StockPrice.class)
                    .where("stockId", "=", stockPrice.getStockId())
                    .and("time", "=", stockPrice.getTime())
                    .findFirst();
            // 之前没有存储价格，或价格有更新时，存储价格
            if(oldStockPrice == null || oldStockPrice.getPrice() != stockPrice.getPrice()){
                db.saveBindingId(stockPrice);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List<Stock> getAllStocks(){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
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

    public static List<StockPrice> getStockPriceList(String stockId){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
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


    public static void saveStockForm(StockForm stockForm){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
            db.save(stockForm);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static List<StockForm> getAllStrategyAnalyseResult(){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
            List<StockForm> stockFormList = db.selector(StockForm.class).findAll();
            if(stockFormList == null){
                return new ArrayList<>();
            }
            return stockFormList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<StockForm> getStockFormByStockId(String stockId){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
            List<StockForm> stockFormList = db.selector(StockForm.class)
                    .where("stockId", "=", stockId)
                    .findAll();

            return stockFormList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
