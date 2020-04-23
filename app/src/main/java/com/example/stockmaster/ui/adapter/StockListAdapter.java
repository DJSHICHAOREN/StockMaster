package com.example.stockmaster.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.ui.activity.detail.DetailActivity;
import com.example.stockmaster.ui.activity.main.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.StockListViewHolder> {
    private List<Stock> mStockList;
    private MainActivity mMainActivity;
    public StockListAdapter(List<Stock> stockList, MainActivity mainActivity){
        mStockList = stockList;
        mMainActivity = mainActivity;
    }

    @NonNull
    @Override
    public StockListAdapter.StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // mLayoutView
        View mLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item, parent, false);
        StockListViewHolder stockListViewHolder = new StockListViewHolder(mLayoutView);
        return stockListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StockListAdapter.StockListViewHolder holder, int position) {
        holder.tv_stock_id.setText(mStockList.get(position).getId());
        holder.tv_stock_name.setText(mStockList.get(position).getName());
        holder.tv_deal_tip.setText(mStockList.get(position).getRecentDealTips());
        holder.ll_stock_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, DetailActivity.class);
                intent.putExtra("stockIndex", position);
                mMainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public static class StockListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_stock_id)
        public TextView tv_stock_id;

        @BindView(R.id.tv_stock_name)
        public TextView tv_stock_name;

        @BindView(R.id.tv_deal_tip)
        public TextView tv_deal_tip;

        @BindView(R.id.ll_stock_item)
        public LinearLayout ll_stock_item;

        public StockListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
