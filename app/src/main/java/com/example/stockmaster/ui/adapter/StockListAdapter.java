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
    private View mLayoutView;

    public StockListAdapter(List<Stock> stockList){
        mStockList = stockList;
    }

    @NonNull
    @Override
    public StockListAdapter.StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.stock_list_item, parent, false);
        if(mLayoutView == null){
            mLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_item, parent, false);
        }
        StockListViewHolder stockListViewHolder = new StockListViewHolder(mLayoutView);
        return stockListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StockListAdapter.StockListViewHolder holder, int position) {
        holder.mTextView.setText(mStockList.get(position).id);
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public static class StockListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_stock_id)
        public TextView mTextView;

        public StockListViewHolder(View view) {
            super(view);
//            mTextView = textView;
            ButterKnife.bind(this, view);
        }
    }
}
