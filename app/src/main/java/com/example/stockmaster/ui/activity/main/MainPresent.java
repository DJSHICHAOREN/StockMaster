package com.example.stockmaster.ui.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.http.DataQueryerManager;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPresent extends BasePresent {
    public MainPresent(AppCompatActivity view) {
        super(view);
    }


}
