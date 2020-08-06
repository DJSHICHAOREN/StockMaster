package com.example.stockmaster.ui.activity.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.stockmaster.R;
import com.example.stockmaster.ui.adapter.DealListAdapter;
import com.example.stockmaster.util.StockManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.rv_deal_list)
    RecyclerView rv_deal_list;

    private RecyclerView.Adapter mDealListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int stockIndex = intent.getIntExtra("stockIndex", 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_deal_list.setLayoutManager(linearLayoutManager);

        mDealListAdapter = new DealListAdapter(StockManager.getThisStockMinuteRiseStrategyResultList(stockIndex));
        rv_deal_list.setAdapter(mDealListAdapter);
    }
}
