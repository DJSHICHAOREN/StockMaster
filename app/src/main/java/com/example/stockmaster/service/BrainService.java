package com.example.stockmaster.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.stockmaster.R;
import com.example.stockmaster.http.DataQueryerManager;
import com.example.stockmaster.util.StockManager;

public class BrainService extends Service {

    private boolean IS_QUERY = true;
    private String CHANNEL_ID = "BRAIN_SERVICE_NOTIFICATION";
    private DataQueryerManager mDataQueryerManager;
    private StockManager mStockManager;

    public class MyBinder extends Binder {
        public BrainService getService(){
            return BrainService.this;
        }
    }
    private MyBinder serviceBinder = new MyBinder();

    public BrainService() {
        StockManager.setBrainService(this);
        mDataQueryerManager = DataQueryerManager.getInstance(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("lwd", "创建brainService");

        if(IS_QUERY){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createNotificationChannel();
                    // 如果在非交易时间
                    // 请求一次一日价格，为了计算短期买卖点
                    // 请求一次分时价格，为了得到股票名字
                    // 请求一次最近交易时间，均线需要用到
                    // 最开始时请求：分时数据、均价数据
                    mDataQueryerManager.queryBeginOnce();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 定时请求最近交易时间
                    mDataQueryerManager.beginQueryLastDealDate();
                    // 定时请求日均线数据
                    mDataQueryerManager.beginQueryMaPrice();
                    // 定时请求分时价格
                    mDataQueryerManager.beginQueryMinutePrice();
                    // 请求一次五日价格
                    mDataQueryerManager.queryFiveDayPrice();
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    // 定时请求今天价格
                    mDataQueryerManager.beginQueryTodayPrice();
                    // 结束时请求：今日价格
//                    mDataQueryerManager.queryEndOnce();

                    // 从数据库加载股票价格的均线
//                StockManager.loadStockPrice();
                }
            }).start();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public String getHello(){
        return "hello";
    }

    /**
     * 创建通知频道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.service_channel_name);
            String description = getString(R.string.service_channel_description);
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
    public void sendNotification(int notificationId, String notificationContent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BrainService.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Service交易通知")
                .setVibrate(new long[]{0, 1000,1000,1000})
                .setContentText(notificationContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(BrainService.this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
