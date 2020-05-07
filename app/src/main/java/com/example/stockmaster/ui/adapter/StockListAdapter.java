package com.example.stockmaster.ui.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.ui.activity.detail.DetailActivity;
import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.util.DBUtil;

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
        Log.d("lwd", mStockList.toString());
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
        final Stock stock = mStockList.get(position);
        holder.tv_stock_id.setText(stock.getId());
        holder.tv_stock_name.setText(stock.getName());
        holder.tv_deal_tip.setText(stock.getRecentDealTips());
        holder.ll_stock_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, DetailActivity.class);
                intent.putExtra("stockIndex", position);
                mMainActivity.startActivity(intent);
            }
        });
        holder.btn_monitor_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock.ringMonitorType();
                DBUtil.updateStock(stock);
                setBtnSuperviseTypeColor(holder.btn_monitor_type, stock);
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
                drawable.setColor(mMainActivity.getResources().getColor(R.color.colorSuperviseNull));
                break;
            case 1:
                drawable.setColor(mMainActivity.getResources().getColor(R.color.colorSuperviseBuy));
                break;
            case 2:
                drawable.setColor(mMainActivity.getResources().getColor(R.color.colorSuperviseSale));
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
