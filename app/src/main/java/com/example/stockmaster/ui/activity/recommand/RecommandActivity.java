package com.example.stockmaster.ui.activity.recommand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.stockmaster.R;

import butterknife.ButterKnife;

public class RecommandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand);
        ButterKnife.bind(this);
    }
}
