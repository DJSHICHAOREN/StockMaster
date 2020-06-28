package com.example.stockmaster.ui.fragment.stock_monitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.adapter.StockMonitorAdapter;
import com.example.stockmaster.util.ClipBoardUtil;
import com.example.stockmaster.util.StockManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockMonitorFragment extends Fragment {
    @BindView(R.id.rv_stock_list)
    public RecyclerView rv_stock_list;

    private RecyclerView.Adapter mStockMonitorAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    mStockMonitorAdapter.notifyDataSetChanged();
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

        StockManager.setStockMonitorFragmentUIManager(new StockMonitorFragmentUIManager());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_stock_list.setLayoutManager(linearLayoutManager);

        mStockMonitorAdapter = new StockMonitorAdapter(StockManager.getStockMonitorPickedStockList(), this);
        rv_stock_list.setAdapter(mStockMonitorAdapter);

//        File dbFile = getActivity().getDatabasePath("English");

        return view;
    }

    public class StockMonitorFragmentUIManager extends UIManager {
        public void notifyStockListDateSetChanged(){
            Message notifyListUpdateMsg = Message.obtain();
            notifyListUpdateMsg.what = 1;
            handler.sendMessage(notifyListUpdateMsg);
        }
    }

    @OnClick(R.id.btn_copy_stock_id_list)
    public void onCopyStockIdListClick(View view){
        ClipBoardUtil.CopyStringToClipBoard(getContext(), StockManager.getPickedStockIdListString());
    }
}
