package com.example.stockmaster.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.StockListViewHolder> {
    private List<Stock> mStockList;

    public StockListAdapter(List<Stock> stockList){
        mStockList = stockList;
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
        holder.tv_stock_id.setText(mStockList.get(position).id);
        holder.tv_stock_price.setText(mStockList.get(position).getCurrentPrice()+"");
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public static class StockListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_stock_id)
        public TextView tv_stock_id;

        @BindView(R.id.tv_stock_price)
        public TextView tv_stock_price;

        public StockListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
