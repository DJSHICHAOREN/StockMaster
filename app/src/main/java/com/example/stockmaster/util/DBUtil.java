package com.example.stockmaster.util;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

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

    public static void saveStock(Stock stock){
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

    public static void clearStockPrice(){
        try {
            if(db == null){
                db = x.getDb(daoConfig);
            }
            List<StockPrice> stockPriceList = db.selector(StockPrice.class).findAll();
            for(StockPrice stockPrice : stockPriceList){

            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

}
