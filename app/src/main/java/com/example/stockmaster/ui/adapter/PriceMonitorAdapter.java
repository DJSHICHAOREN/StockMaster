package com.example.stockmaster.ui.adapter;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.ui.activity.detail.DetailActivity;
import com.example.stockmaster.ui.fragment.price_monitor.PriceMonitorFragment;
import com.example.stockmaster.util.DBUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PriceMonitorAdapter extends RecyclerView.Adapter<PriceMonitorAdapter.StockListViewHolder> {
    private List<Stock> mStockList;
    private PriceMonitorFragment mPriceMonitorFragment;
    public PriceMonitorAdapter(List<Stock> stockList, PriceMonitorFragment priceMonitorFragment){
        mStockList = stockList;
        mPriceMonitorFragment = priceMonitorFragment;
        Log.d("lwd", mStockList.toString());
    }

    @NonNull
    @Override
    public PriceMonitorAdapter.StockListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // mLayoutView
        View mLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitor_price_stock_list_item, parent, false);
        StockListViewHolder stockListViewHolder = new StockListViewHolder(mLayoutView);
        return stockListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PriceMonitorAdapter.StockListViewHolder holder, int position) {
        final Stock stock = mStockList.get(position);
        holder.tv_stock_id.setText(stock.getId());
        holder.tv_stock_name.setText(stock.getName());
        holder.tv_deal_tip.setText(stock.getRecentDealTips());
//        Log.d("lwd", "onBindViewHolder， position:" + position + ",stockId："
//                + stock.getId()  + ",tips:" + stock.getRecentDealTips());
        holder.ll_stock_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mPriceMonitorFragment.getActivity(), DetailActivity.class);
                intent.putExtra("stockIndex", position);
                mPriceMonitorFragment.startActivity(intent);
            }
        });
        holder.btn_monitor_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock.ringMonitorType();
                DBUtil.updateStock(stock);
//                setBtnSuperviseTypeColor(holder.btn_monitor_type, stock);
            }
        });
        setBtnSuperviseTypeColor(holder.btn_monitor_type, stock);
    }

    /**
     * 根据监控状态改变监控按钮的颜色
     * @param btn_monitor_type
     * @param stock
     */
    public void setBtnSuperviseTypeColor(Button btn_monitor_type, Stock stock){
        int monitorType = stock.getMonitorType();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        switch (monitorType){
            case 0:
                drawable.setColor(mPriceMonitorFragment.getResources().getColor(R.color.colorSuperviseNull));
                break;
            case 1:
                drawable.setColor(mPriceMonitorFragment.getResources().getColor(R.color.colorSuperviseBuy));
                break;
            case 2:
                drawable.setColor(mPriceMonitorFragment.getResources().getColor(R.color.colorSuperviseSale));
                break;
        }
        btn_monitor_type.setBackground(drawable);

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
