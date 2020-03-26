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

public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.StockListViewHolder> {
    private List<Stock> mStockList;

    public static class StockListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public StockListViewHolder(TextView textView) {
            super(textView);
            mTextView = textView;
        }
    }

    public StockListAdapter(List<Stock> stockList){
        mStockList = stockList;
    }

    @NonNull
    @Override
    public StockListAdapter.StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_item, parent, false);
        LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item, null);
        StockListViewHolder vh = new StockListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StockListAdapter.StockListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
