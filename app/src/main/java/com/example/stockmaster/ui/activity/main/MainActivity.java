package com.example.stockmaster.ui.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.UIManager;
import com.example.stockmaster.ui.adapter.StockListAdapter;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_deal_point)
    public TextView tv_deal_point;
    @BindView(R.id.rv_stock_list)
    public RecyclerView rv_stock_list;

    private RecyclerView.Adapter mStockListAdapter;
    private BrainService mBrainService;
    private MainPresent mMainPresent;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BrainService.MyBinder myBinder = (BrainService.MyBinder) service;
            mBrainService = myBinder.getService();
            String welcome = mBrainService.getHello();
            Log.d("lwd", String.format("service return %s", welcome));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityUIManager mainActivityUIManager = new MainActivityUIManager();
        mMainPresent = new MainPresent(this, mainActivityUIManager);
        // 设置RecyclerView的布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_stock_list.setLayoutManager(linearLayoutManager);
        mStockListAdapter = new StockListAdapter(mMainPresent.getStockList(), this);
        rv_stock_list.setAdapter(mStockListAdapter);

        // 开启service
//        Intent intent = new Intent(this, BrainService.class);
//        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        mMainPresent.beginQueryAndAnalyse();

        // 设置手机震动
        Vibrator v = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    public void notifyStockListDataSetChanged(){
        mStockListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class MainActivityUIManager {
        private String CHANNEL_ID = "STOCK_MASTER_CHANNEL";
        private int notificationId = 0;
        public MainActivityUIManager(){
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            createNotificationChannel();
        }

        public void refreshUIWhenReceiveNewPrice(Stock stock){
        }

        /**
         * 创建通知频道
         */
        private void createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

        /**
         * 发送notification
         * @param notificationContent 发送的内容
         */
        public void sendNotification(String notificationContent){
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("交易通知")
                    .setVibrate(new long[]{0, 1000,1000,1000})
                    .setContentText(notificationContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId++, builder.build());
        }

        /**
         * 处理买卖点
         * @param dealString
         */
        public void refreshUIWhenGetNewDealPoint(String dealString) {
            tv_deal_point.setText(dealString);
//            sendNotification(dealString);
        }
    }

}

