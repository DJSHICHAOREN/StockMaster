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
import android.util.Log;

import com.example.stockmaster.R;
import com.example.stockmaster.service.BrainService;
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

    MonitorPanelAdapter monitorPanelAdapter;
    private final int GET_WRITE_EXTERNAL_STORAGE = 1;

    public static File mDBFile = null;

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

    }


    public void initAllThing(){
        // 载入股票列表，需要操作数据库，必须在请求存储权限之后
        StockManager.initStockManager();

        // 开启service
        Intent intent = new Intent(this, BrainService.class);
        startService(intent);
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
}

