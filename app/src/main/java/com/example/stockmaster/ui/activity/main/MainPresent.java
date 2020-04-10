package com.example.stockmaster.ui.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockmaster.entity.Stock;
import com.example.stockmaster.http.SinaDataQueryer;
import com.example.stockmaster.ui.activity.base.BasePresent;
import com.example.stockmaster.util.StockAnalyser;
import com.example.stockmaster.util.StockManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPresent extends BasePresent {
    private MainActivity mMainActivity;
    private MainActivity.MainActivityUIManager mMainActivityUIManager;
    private StockManager mStockManager;
    private SinaDataQueryer mSinaDataQueryer;
    private StockAnalyser mStockAnalyser;
    private Timer timer;
    final ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:{
                    Log.d("lwd","获取今天股票数据");
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    for(final String stockId : stockIdList) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                mSinaDataQueryer.queryStocksTodayPrice(stockId);
                            }
                        };

                        mCachedThreadPool.execute(runnable);
                    }
                    break;
                }
                case 2:{
                    Bundle bundle = msg.getData();
                    ArrayList<String> stockIdList = bundle.getStringArrayList("stockIdList");
                    String stockIdStr = "";
                    for(String stockId : stockIdList) {
                        stockIdStr = stockIdStr + "rt_" + stockId + ",";
                    }
                    mSinaDataQueryer.queryStocksNowPrice(stockIdStr);
//                    mMainActivity.notifyStockListDataSetChanged();
                    break;
                }

            }
        }
    };

    public MainPresent(AppCompatActivity view, MainActivity.MainActivityUIManager mainActivityUIManager) {
        super(view);
        mMainActivity = (MainActivity)view;
        mMainActivityUIManager = mainActivityUIManager;
        // 创建工具实例
        mStockManager = new StockManager();
        mStockManager.setMainActivityUIManager(mMainActivityUIManager);
        mSinaDataQueryer = new SinaDataQueryer(mMainActivity, mStockManager);
        // 实例化股票对象
        // 请求股票数据
//        ArrayList<String> stockIdList = new ArrayList<String>(Arrays.asList("hk02400", "hk06060", "hk09969"));
        ArrayList<String> stockIdList = new ArrayList<String>(Arrays.asList("00001", "00002", "00003", "00004", "00005", "00006", "00008", "00010", "00011", "00012", "00014", "00016", "00017", "00019", "00020", "00023", "00027", "00038", "00059", "00066", "00069", "00081", "00083", "00086", "00087", "00095", "00101", "00107", "00116", "00119", "00120", "00123", "00135", "00136", "00142", "00144", "00148", "00151", "00152", "00165", "00168", "00173", "00175", "00177", "00178", "00179", "00189", "00200", "00215", "00220", "00241", "00242", "00256", "00257", "00267", "00268", "00270", "00272", "00285", "00288", "00291", "00293", "00297", "00302", "00303", "00308", "00315", "00317", "00322", "00323", "00336", "00337", "00338", "00341", "00345", "00347", "00354", "00358", "00363", "00371", "00384", "00386", "00388", "00390", "00392", "00410", "00425", "00440", "00451", "00460", "00467", "00468", "00489", "00493", "00494", "00496", "00506", "00512", "00520", "00522", "00525", "00535", "00546", "00548", "00551", "00552", "00553", "00563", "00564", "00568", "00570", "00576", "00581", "00586", "00588", "00590", "00598", "00604", "00607", "00631", "00636", "00639", "00656", "00658", "00659", "00665", "00667", "00669", "00670", "00683", "00687", "00688", "00694", "00696", "00700", "00719", "00728", "00743", "00751", "00753", "00754", "00762", "00763", "00772", "00777", "00780", "00788", "00799", "00806", "00811", "00813", "00817", "00819", "00826", "00832", "00836", "00839", "00853", "00855", "00856", "00857", "00861", "00867", "00868", "00874", "00880", "00881", "00883", "00884", "00895", "00902", "00914", "00916", "00921", "00934", "00939", "00941", "00960", "00966", "00968", "00978", "00981", "00991", "00992", "00995", "00998", "01030", "01033", "01038", "01044", "01052", "01053", "01055", "01057", "01060", "01065", "01066", "01070", "01071", "01072", "01083", "01088", "01089", "01093", "01098", "01099", "01108", "01109", "01112", "01113", "01114", "01117", "01119", "01128", "01131", "01138", "01157", "01169", "01171", "01176", "01177", "01186", "01193", "01196", "01199", "01208", "01211", "01212", "01233", "01234", "01238", "01257", "01268", "01269", "01282", "01286", "01288", "01293", "01299", "01302", "01308", "01310", "01313", "01316", "01317", "01330", "01333", "01336", "01339", "01347", "01357", "01359", "01368", "01375", "01378", "01381", "01382", "01383", "01398", "01448", "01458", "01478", "01508", "01513", "01515", "01521", "01528", "01530", "01533", "01548", "01558", "01569", "01573", "01579", "01585", "01589", "01600", "01608", "01610", "01618", "01622", "01628", "01635", "01638", "01658", "01666", "01668", "01686", "01691", "01717", "01728", "01765", "01766", "01772", "01776", "01777", "01778", "01787", "01788", "01789", "01797", "01800", "01810", "01812", "01813", "01816", "01818", "01833", "01836", "01848", "01860", "01873", "01876", "01882", "01883", "01888", "01890", "01896", "01898", "01905", "01907", "01908", "01911", "01918", "01919", "01928", "01929", "01951", "01958", "01963", "01966", "01970", "01972", "01988", "01996", "01997", "01999", "02001", "02005", "02007", "02009", "02013", "02016", "02018", "02019", "02020", "02038", "02039", "02048", "02068", "02103", "02128", "02186", "02196", "02202", "02208", "02233", "02238", "02255", "02269", "02282", "02313", "02314", "02318", "02319", "02328", "02329", "02331", "02333", "02338", "02343", "02357", "02359", "02362", "02380", "02382", "02386", "02388", "02400", "02588", "02600", "02601", "02607", "02611", "02628", "02666", "02669", "02678", "02688", "02689", "02727", "02768", "02772", "02777", "02799", "02858", "02866", "02869", "02877", "02880", "02883", "02899", "02989", "03301", "03306", "03309", "03311", "03319", "03320", "03323", "03328", "03331", "03333", "03339", "03360", "03369", "03377", "03380", "03383", "03396", "03606", "03613", "03618", "03633", "03669", "03690", "03692", "03759", "03799", "03800", "03808", "03818", "03866", "03868", "03877", "03883", "03888", "03898", "03899", "03900", "03908", "03933", "03958", "03968", "03969", "03988", "03990", "03993", "03998", "06030", "06049", "06055", "06060", "06066", "06068", "06069", "06088", "06098", "06099", "06110", "06116", "06158", "06166", "06169", "06178", "06186", "06196", "06198", "06806", "06808", "06818", "06837", "06862", "06865", "06869", "06881", "06886"));
        mStockManager.createStocks(stockIdList);
    }

    public List<Stock> getStockList(){
        return mStockManager.getStockList();
    }

    public void beginQueryAndAnalyse(){
        // 获取从开盘到现在的股票数据
        Message todayPriceMessage = Message.obtain();
        todayPriceMessage.what = 1;
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("stockIdList", (ArrayList<String>) mStockManager.getStockIdList());
        todayPriceMessage.setData(bundle);
        handler.sendMessage(todayPriceMessage);
        // 获取每分钟的数据
        timer = new Timer("RefreshStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message minutePriceMessage = Message.obtain();
                minutePriceMessage.what = 2;
                minutePriceMessage.setData(bundle);
                handler.sendMessage(minutePriceMessage);
            }
        }, 0, 2000); // 1 seconds
    }




}
