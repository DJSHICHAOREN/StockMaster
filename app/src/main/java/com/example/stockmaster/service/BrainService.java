package com.example.stockmaster.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.Timer;

public class BrainService extends Service {
    private Timer timer;
    private StockManager mStockManager;
    private SinaDataQueryer mSinaDataQueryer;
    private StockAnalyser mStockAnalyser;
    private RecyclerView.Adapter stockListAdapter;
    private BrainService mBrainService;
    private String CHANNEL_ID = "BRAIN_SERVICE_NOTIFICATION";

    public class MyBinder extends Binder {
        public BrainService getService(){
            return BrainService.this;
        }
    }
    private MyBinder serviceBinder = new MyBinder();

    public BrainService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("lwd", "创建brainService");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //fetching notifications from server
                //if there is notifications then call this method
//                createNotificationChannel();
                sendNotification(1, "from Brain Service");
            }
        }).start();



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
//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
