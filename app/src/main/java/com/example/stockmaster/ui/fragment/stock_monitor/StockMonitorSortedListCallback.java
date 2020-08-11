package com.example.stockmaster.ui.fragment.stock_monitor;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.strategy.StrategyResult;

public class StockMonitorSortedListCallback extends SortedListAdapterCallback<Stock> {
    /**
     * Creates a {@link SortedList} that will forward data change events to the provided
     * Adapter.
     *
     * @param adapter The Adapter instance which should receive events from the SortedList.
     */
    public StockMonitorSortedListCallback(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override
    public int compare(Stock stock1, Stock stock2) {
        StrategyResult strategyResult1 = stock1.getLastStrategyResult(R.integer.strategyMinuteLongToArrange);
        StrategyResult strategyResult2 = stock2.getLastStrategyResult(R.integer.strategyMinuteLongToArrange);

        if(strategyResult1 != null || strategyResult2 != null){
            return -1 * strategyResult1.getTime().compareTo(strategyResult2.getTime());
        }
        return -1;
    }

    @Override
    public boolean areContentsTheSame(Stock oldItem, Stock newItem) {
        if(oldItem.getMonitorType() != newItem.getMonitorType()){
            return false;
        }
        if(oldItem.getLastStrategyResult(R.integer.strategyMinuteLongToArrange).getTime().compareTo(
                newItem.getLastStrategyResult(R.integer.strategyMinuteLongToArrange).getTime()) != 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean areItemsTheSame(Stock item1, Stock item2) {
        return item1.getId().equals(item2.getId());
    }
}
