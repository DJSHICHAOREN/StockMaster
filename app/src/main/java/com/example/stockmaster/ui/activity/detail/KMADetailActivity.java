package com.example.stockmaster.ui.activity.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.stockmaster.R;

import butterknife.ButterKnife;

public class KMADetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kma_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int stockId = intent.getIntExtra("stockId", 0);
    }
}
