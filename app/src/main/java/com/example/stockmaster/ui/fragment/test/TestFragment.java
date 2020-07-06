package com.example.stockmaster.ui.fragment.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stockmaster.R;
import com.example.stockmaster.http.DataQueryerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestFragment extends Fragment {
    @BindView(R.id.et_stock_id)
    EditText et_stock_id;

    private DataQueryerManager mDataQueryerManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);

        mDataQueryerManager = DataQueryerManager.getInstance(this.getContext());
        return view;
    }

    @OnClick(R.id.btn_analyse_stock)
    public void onAnalyseStockClick(View view){
        String stockId = et_stock_id.getText().toString().trim();
        if(!stockId.equals("")){
            if(!stockId.contains("hk")){
                stockId = "hk" + stockId;
            }

            mDataQueryerManager.queryOneStockMaOnce(stockId);
            mDataQueryerManager.queryOneStockFiveDayPrice(stockId, true);
        }
    }
}
