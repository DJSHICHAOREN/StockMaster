package com.example.stockmaster.ui.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.ui.activity.detail.DetailActivity;
import com.example.stockmaster.ui.activity.detail.KMADetailActivity;
import com.example.stockmaster.util.DBUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockMonitorAdapter extends RecyclerView.Adapter<StockMonitorAdapter.StockListViewHolder> {
    private List<Stock> mStockList;
    private Fragment mStockMonitorFragment;

    public StockMonitorAdapter(List<Stock> stockList, Fragment stockMonitorFragment){
        mStockList = stockList;
        mStockMonitorFragment = stockMonitorFragment;
    }
    @NonNull
    @Override
    public StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_stock_stock_list_item, parent, false);
        StockListViewHolder stockListViewHolder = new StockListViewHolder(mLayoutView);
        return stockListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StockListViewHolder holder, int position) {
        final Stock stock = mStockList.get(position);
        holder.tv_stock_id.setText(stock.getId());
        holder.tv_stock_name.setText(stock.getName());
        holder.tv_deal_tip.setText(stock.getStrategyAnalyseDescribeString());
        holder.ll_stock_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mStockMonitorFragment.getActivity(), KMADetailActivity.class);
//                intent.putExtra("stockId", mStockList.get(position).id);
//                mStockMonitorFragment.startActivity(intent);

                AlertDialog alertDialog1 = new AlertDialog.Builder(mStockMonitorFragment.getContext())
                        .setTitle(stock.getId() + " " + stock.getName())//标题
                        .setMessage(stock.getStrategyAnalyseDescribeString())//内容
                        .create();
                alertDialog1.show();
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
        public ViewGroup ll_stock_item;

        @BindView(R.id.btn_monitor_type)
        public Button btn_monitor_type;

        public StockListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}