package com.example.stockmaster.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.Timer;

public class BrainService extends Service {
    private Timer timer;
    private StockManager mStockManager;
    private SinaDataQueryer mSinaDataQueryer;
    private StockAnalyser mStockAnalyser;
    private RecyclerView.Adapter stockListAdapter;
    private BrainService mBrainService;

    public class MyBinder extends Binder {
        public BrainService getService(){
            return BrainService.this;
        }
    }
    private MyBinder serviceBinder = new MyBinder();

    public BrainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        MainActivity.MainActivityUIManager mainActivityUIManager = new MainActivity().MainActivityUIManager();
//        mStockAnalyser = new StockAnalyser(mainActivityUIManager);
//        mStockManager = new StockManager(mainActivityUIManager, mStockAnalyser);
//        mSinaDataQueryer = new SinaDataQueryer(BrainService.this, mStockManager);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public String getHello(){
        return "hello";
    }

    public void setUIManagerAndStartAnalyse(MainActivity.MainActivityUIManager mainActivityUIManager){

    }
}
