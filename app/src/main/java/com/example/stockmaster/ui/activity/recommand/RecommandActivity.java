package com.example.stockmaster.ui.activity.recommand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.stockmaster.R;

public class RecommandActivity extends AppCompatActivity {
    private RecommandPresent mRecommandPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand);
        mRecommandPresent = new RecommandPresent(this);
    }
}