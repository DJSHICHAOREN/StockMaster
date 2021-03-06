package com.example.stockmaster.ui.fragment.stock_monitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.ui.adapter.StockMonitorAdapter;
import com.example.stockmaster.util.ClipBoardUtil;
import com.example.stockmaster.util.FileUtil;
import com.example.stockmaster.util.StockManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockMonitorFragment extends Fragment {
    @BindView(R.id.rv_stock_list)
    public RecyclerView rv_stock_list;

    private StockMonitorAdapter mStockMonitorAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    mStockMonitorAdapter.notifyDataSetChanged();
                    break;
                }
                case 2:{
                    Bundle bundle = msg.getData();
                    int itemIndex = bundle.getInt("itemIndex");
                    Log.d("lwd", "price notifyItemChanged itemIndex:" + itemIndex);
                    mStockMonitorAdapter.notifyItemChanged(itemIndex);
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_monitor, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_stock_list.setLayoutManager(linearLayoutManager);

        mStockMonitorAdapter = new StockMonitorAdapter(null, this);
        rv_stock_list.setAdapter(mStockMonitorAdapter);

        StockMonitorSortedListCallback stockMonitorSortedListCallback = new StockMonitorSortedListCallback(mStockMonitorAdapter);
        SortedList<Stock> stockSortedList = StockManager.setStockMonitorFragmentUIManager(new StockMonitorFragmentUIManager(), stockMonitorSortedListCallback);
        mStockMonitorAdapter.setStockList(stockSortedList);

        return view;
    }

    @OnClick(R.id.btn_copy_stock_id_list)
    public void onCopyStockIdListClick(View view){
//        ClipBoardUtil.CopyStringToClipBoard(getContext(), StockManager.getPickedStockIdListString());

        FileUtil.writeStringToFile(StockManager.getPickedStockIdListString(), MainActivity.mDBFile.getPath(), "股票id列表.txt");
        Toast.makeText(getContext(), "已存入txt中", Toast.LENGTH_LONG).show();
    }

    public class StockMonitorFragmentUIManager extends UIManager {
        public void notifyStockListDateSetChanged(){
            Message notifyListUpdateMsg = Message.obtain();
            notifyListUpdateMsg.what = 1;
            handler.sendMessage(notifyListUpdateMsg);
        }

        /**
         * 刷新首页买卖点列表
         */
        public void notifyStockListItemChanged(int itemIndex){
            Message notifyListUpdateMsg = Message.obtain();
            notifyListUpdateMsg.what = 2;

            Bundle bundle = new Bundle();
            bundle.putInt("itemIndex", itemIndex);
            notifyListUpdateMsg.setData(bundle);

            handler.sendMessage(notifyListUpdateMsg);

        }
    }


}
