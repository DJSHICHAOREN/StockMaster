package com.example.stockmaster.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.entity.StockPrice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealListAdapter extends RecyclerView.Adapter<DealListAdapter.DealListViewHolder> {
    List<StockPrice> mStockPriceList;
    public DealListAdapter(List<StockPrice> stockPriceList){
        mStockPriceList = stockPriceList;
    }

    @NonNull
    @Override
    public DealListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal_list_item, parent, false);
        DealListViewHolder stockListViewHolder = new DealListViewHolder(mLayoutView);
        return stockListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DealListViewHolder holder, int position) {
        holder.tv_deal_point.setText(mStockPriceList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mStockPriceList.size();
    }

    public static class DealListViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_deal_point)
        TextView tv_deal_point;
        public DealListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
