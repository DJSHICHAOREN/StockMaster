package com.example.stockmaster.ui.activity.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.stockmaster.R;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.adapter.MonitorPanelAdapter;
import com.example.stockmaster.util.StockManager;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    @BindView(R.id.vp_monitor_panel)
    ViewPager vp_monitor_panel;
    @BindView(R.id.tab_layout)
    TabLayout tab_layout;
    @BindView(R.id.tv_load_progress)
    TextView tv_load_progress;

    MonitorPanelAdapter monitorPanelAdapter;
    private final int GET_WRITE_EXTERNAL_STORAGE = 1;

    public static File mDBFile = null;

    private static boolean isStartedService = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    Bundle bundle = msg.getData();
                    String content = bundle.getString("content");
                    tv_load_progress.setText(content);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 设置viewPager的tabLayout
        tab_layout.setupWithViewPager(vp_monitor_panel);
        // 设置viewPager的adapter
        monitorPanelAdapter = new MonitorPanelAdapter(getSupportFragmentManager());
        vp_monitor_panel.setAdapter(monitorPanelAdapter);

        // 请求存储权限
        requireWriteExternalStorage();

        StockManager.setMainActivityUIManager(new MainActivityUIManager());
    }

    public void initAllThing(){
        // 载入股票列表，需要操作数据库，必须在请求存储权限之后
        StockManager.initStockManager(this);

        // 开启service
        if(!isStartedService){
            Intent intent = new Intent(this, BrainService.class);
            startService(intent);

            isStartedService = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void getSDCardPath() {
        String dbPath = Environment.getExternalStorageDirectory() + "/" + "StockMaster";
//        File somePath = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dbDir = new File(dbPath);
        if (!dbDir.exists()) {
            if(dbDir.mkdirs()){
                mDBFile = dbDir;
            }
        }
        else{
            mDBFile = dbDir;
        }
    }


    @AfterPermissionGranted(GET_WRITE_EXTERNAL_STORAGE)
    private void requireWriteExternalStorage() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            getSDCardPath();
            initAllThing();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.write_external_storage_rational),
                    GET_WRITE_EXTERNAL_STORAGE, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("lwd", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("lwd", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        else{
            requireWriteExternalStorage();
        }
    }

    public class MainActivityUIManager extends UIManager {
        public void flushLoadProgress(String content){
            Message notifyTipsMsg = Message.obtain();
            notifyTipsMsg.what = 1;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            notifyTipsMsg.setData(bundle);

            handler.sendMessage(notifyTipsMsg);


        }
    }
}

