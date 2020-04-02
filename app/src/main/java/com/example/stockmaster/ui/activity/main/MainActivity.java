package com.example.stockmaster.ui.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.UIManager;
import com.example.stockmaster.ui.adapter.StockListAdapter;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_deal_point)
    public TextView tv_deal_point;
    @BindView(R.id.rv_stock_list)
    public RecyclerView rv_stock_list;

    private RecyclerView.Adapter mStockListAdapter;
    private BrainService mBrainService;
    private MainPresent mMainPresent;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BrainService.MyBinder myBinder = (BrainService.MyBinder) service;
            mBrainService = myBinder.getService();
            String welcome = mBrainService.getHello();
            Log.d("lwd", String.format("service return %s", welcome));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityUIManager mainActivityUIManager = new MainActivityUIManager();
        mMainPresent = new MainPresent(this, mainActivityUIManager);
        // 设置RecyclerView的布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_stock_list.setLayoutManager(linearLayoutManager);
        mStockListAdapter = new StockListAdapter(mMainPresent.getStockList(), this);
        rv_stock_list.setAdapter(mStockListAdapter);

        // 开启service
//        Intent intent = new Intent(this, BrainService.class);
//        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        mMainPresent.beginQueryAndAnalyse();
    }

    public void notifyStockListDataSetChanged(){
        mStockListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class MainActivityUIManager {
        public void refreshUIWhenReceiveNewPrice(Stock stock){
        }

        public void refreshUIWhenGetNewDealPoint(String dealString) {
            tv_deal_point.setText(dealString);
        }
    }

}

