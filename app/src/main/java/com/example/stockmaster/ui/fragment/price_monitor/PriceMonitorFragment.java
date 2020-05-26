package com.example.stockmaster.ui.fragment.price_monitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmaster.R;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.activity.main.MainActivity;
import com.example.stockmaster.ui.activity.recommand.RecommandActivity;
import com.example.stockmaster.ui.adapter.StockListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PriceMonitorFragment extends Fragment {


    @BindView(R.id.tv_deal_point)
    public TextView tv_deal_point;
    @BindView(R.id.rv_stock_list)
    public RecyclerView rv_stock_list;


    private RecyclerView.Adapter mStockListAdapter;
    private BrainService mBrainService;
    private MainPresent mMainPresent;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    Bundle bundle = msg.getData();
                    String dealString = bundle.getString("dealString");
                    tv_deal_point.setText(dealString);
                    break;
                }
                case 2:{
                    Bundle bundle = msg.getData();
                    int itemIndex = bundle.getInt("itemIndex");
                    mStockListAdapter.notifyItemChanged(itemIndex);
                    break;
                }
                case 3:{
                    mStockListAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_price_monitor, container, false);
        ButterKnife.bind(this, view);

        MainActivityUIManager mainActivityUIManager = new MainActivityUIManager();
        mMainPresent = new MainPresent(this, mainActivityUIManager);
        // 设置RecyclerView的布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_stock_list.setLayoutManager(linearLayoutManager);
        mStockListAdapter = new StockListAdapter(mMainPresent.getStockList(), this);
        rv_stock_list.setAdapter(mStockListAdapter);


        return view;
    }

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



    @OnClick(R.id.btn_goto_command_stocks)
    public void onGotoCommandStocksClick(View view){
        Intent intent = new Intent(getContext(), RecommandActivity.class);
        startActivity(intent);
    }



    public class MainActivityUIManager extends UIManager {
        private String CHANNEL_ID = "STOCK_MASTER_CHANNEL";
//        private int notificationId = 0;
        public MainActivityUIManager(){
            createNotificationChannel();
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
                NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }


        /**
         * 发送notification
         * @param notificationContent 发送的内容
         */
//        public void sendNotification(int notificationId, String notificationContent){
//            // Create an explicit intent for an Activity in your app
//            Intent intent = new Intent(getContext(), MainActivity.this);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("交易通知")
//                    .setVibrate(new long[]{0, 1000,1000,1000})
//                    .setContentText(notificationContent)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
//            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(notificationId, builder.build());
//        }

        /**
         * 更新最上方的买卖点，并发出通知
         * @param dealString
         */
        public void refreshUIWhenGetNewDealPoint(String dealString, int notificationId, String notificationContent) {
            Message changeTopTip = Message.obtain();
            changeTopTip.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("dealString", dealString);
            changeTopTip.setData(bundle);
            handler.sendMessage(changeTopTip);

//            sendNotification(notificationId, notificationContent);
        }

        /**
         * 刷新首页买卖点列表
         */
        public void notifyStockListItemChanged(int itemIndex){
            Message notifyListUpdateMsg = Message.obtain();
            notifyListUpdateMsg.what = 2;

            Bundle bundle = new Bundle();
            bundle.putInt("itemIndex", itemIndex);
            notifyListUpdateMsg.setData(bundle);

            handler.sendMessage(notifyListUpdateMsg);

        }

        public void notifyStockListDateSetChanged(){
            Message notifyListUpdateMsg = Message.obtain();
            notifyListUpdateMsg.what = 3;
            handler.sendMessage(notifyListUpdateMsg);
        }
    }
}
