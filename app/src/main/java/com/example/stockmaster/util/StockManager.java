package com.example.stockmaster.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.SortedList;

import com.example.stockmaster.R;
import com.example.stockmaster.entity.stock.DealDate;
import com.example.stockmaster.entity.stock.Stock;
import com.example.stockmaster.entity.stock.StockPrice;
import com.example.stockmaster.entity.ma.DayMaPrice;
import com.example.stockmaster.entity.strategy.StrategyResult;
import com.example.stockmaster.service.BrainService;
import com.example.stockmaster.ui.activity.base.UIManager;
import com.example.stockmaster.ui.fragment.stock_monitor.StockMonitorSortedListCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理股票类：新建股票类，建立新的价格
 */
public class StockManager {
    private static List<Stock> mStockList = new ArrayList<Stock>(); // 监控的全部股票列表
    private static List<String> mStockIdList = new ArrayList<String>();
    private static SortedList<Stock> mStockMonitorPickedStockList;
    private  static List<Stock> mPriceMonitorStockList = new ArrayList<>(); // 在价格监视的股票
    private  static List<String> mPriceMonitorStockIdList = new ArrayList<>(); // 在价格监视的股票
    private static UIManager mPriceMonitorUIManager;
    private static UIManager mStockMonitorUIManager;
    private static UIManager mMainActivityUIManager;
    private static BrainService mBrainService;
    private static ArrayList<String> DEFAULT_PRICE_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList());
//    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("03347", "01521", "01772", "09989", "09983", "00175", "09939", "09926", "09969", "09996", "02018", "01385", "01070", "00826", "01873", "06060", "00257", "00981", "01801", "00185", "01231", "01931", "01003", "00034", "00488", "01813", "02103", "09983", "01558", "02100", "00697", "09923", "00832", "06098", "00241", "02001", "02772", "02231", "01951", "00586", "00119", "01995", "01755", "01858", "01859", "02013", "00772", "00535", "03709", "03883", "00978", "01478", "09968", "01935", "01579", "01030", "02269", "06865", "06862", "09928", "01610", "01789", "01873", "01330", "01176", "00095", "03319", "01833", "00081", "01157", "02400", "03301", "03690", "02163", "00152", "01268", "02382", "03662", "01908", "01753", "00123", "02138", "01769", "02606", "09988", "01773", "00754", "01821", "02883", "09922", "06049", "01501", "02168", "02020", "00780", "00257", "01349", "00650", "03316", "00467", "03990", "01777", "00445", "01918", "03868", "01257", "09909", "01830", "02669", "01238", "06186", "02331", "06068", "00960", "00813", "03380", "06055", "00182", "01765", "00884", "03759", "01576", "02007", "01600", "00631", "02727", "01141", "01387", "01919", "01717", "01093", "01898", "01890", "01638", "02202", "06100", "01233", "02186", "02233", "01686", "02282", "01381", "00968", "01585", "00700", "03918", "00691", "01252", "00853", "03813", "02380", "01033", "00667", "01665", "02686", "00345", "01857", "06110", "00658", "01119", "02768", "01066", "01432", "01599", "00819", "00371", "09999", "03323", "02688", "06820", "00881", "00777", "02196", "01929", "00992", "02319", "00384", "00337", "01691", "01317", "01186", "00327", "01896", "00317", "01138", "03669", "02005", "02798", "00512", "00588", "00200", "02588"));
//    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("09983"));
    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("00001", "00002", "00003", "00004", "00005", "00006", "00008", "00010", "00011", "00012", "00014", "00016", "00017", "00019", "00020", "00023", "00027", "00038", "00059", "00066", "00069", "00081", "00083", "00086", "00087", "00095", "00101", "00107", "00116", "00119", "00120", "00123", "00135", "00136", "00142", "00144", "00148", "00151", "00152", "00165", "00168", "00173", "00175", "00177", "00178", "00179", "00189", "00200", "00215", "00220", "00241", "00242", "00256", "00257", "00267", "00268", "00270", "00272", "00285", "00288", "00291", "00293", "00297", "00302", "00303", "00308", "00315", "00317", "00322", "00323", "00336", "00337", "00338", "00341", "00345", "00347", "00354", "00358", "00363", "00371", "00384", "00386", "00388", "00390", "00392", "00410", "00425", "00440", "00451", "00460", "00467", "00468", "00489", "00493", "00494", "00496", "00506", "00512", "00520", "00522", "00525", "00535", "00546", "00548", "00551", "00552", "00553", "00563", "00564", "00568", "00570", "00576", "00581", "00586", "00588", "00590", "00598", "00604", "00607", "00631", "00636", "00639", "00656", "00658", "00659", "00665", "00667", "00669", "00670", "00683", "00687", "00688", "00694", "00696", "00700", "00719", "00728", "00743", "00751", "00753", "00754", "00762", "00763", "00772", "00777", "00780", "00788", "00799", "00806", "00811", "00813", "00817", "00819", "00826", "00832", "00836", "00839", "00853", "00855", "00856", "00857", "00861", "00867", "00868", "00874", "00880", "00881", "00883", "00884", "00895", "00902", "00914", "00916", "00921", "00934", "00939", "00941", "00960", "00966", "00968", "00978", "00981", "00991", "00992", "00995", "00998", "01030", "01033", "01038", "01044", "01052", "01053", "01055", "01057", "01060", "01065", "01066", "01070", "01071", "01072", "01083", "01088", "01089", "01093", "01098", "01099", "01108", "01109", "01112", "01113", "01114", "01117", "01119", "01128", "01131", "01138", "01157", "01169", "01171", "01176", "01177", "01186", "01193", "01196", "01199", "01208", "01211", "01212", "01233", "01234", "01238", "01257", "01268", "01269", "01282", "01286", "01288", "01293", "01299", "01302", "01308", "01310", "01313", "01316", "01317", "01330", "01333", "01336", "01339", "01347", "01357", "01359", "01368", "01375", "01378", "01381", "01382", "01383", "01398", "01448", "01458", "01478", "01508", "01513", "01515", "01521", "01528", "01530", "01533", "01548", "01558", "01569", "01573", "01579", "01585", "01589", "01600", "01608", "01610", "01618", "01622", "01628", "01635", "01638", "01658", "01666", "01668", "01686", "01691", "01717", "01728", "01765", "01766", "01772", "01776", "01777", "01778", "01787", "01788", "01789", "01797", "01800", "01810", "01812", "01813", "01816", "01818", "01833", "01836", "01848", "01860", "01873", "01876", "01882", "01883", "01888", "01890", "01896", "01898", "01905", "01907", "01908", "01911", "01918", "01919", "01928", "01929", "01951", "01958", "01963", "01966", "01970", "01972", "01988", "01996", "01997", "01999", "02001", "02005", "02007", "02009", "02013", "02016", "02018", "02019", "02020", "02038", "02039", "02048", "02068", "02103", "02128", "02186", "02196", "02202", "02208", "02233", "02238", "02255", "02269", "02282", "02313", "02314", "02318", "02319", "02328", "02329", "02331", "02333", "02338", "02343", "02357", "02359", "02362", "02380", "02382", "02386", "02388", "02400", "02588", "02600", "02601", "02607", "02611", "02628", "02666", "02669", "02678", "02688", "02689", "02727", "02768", "02772", "02777", "02799", "02858", "02866", "02869", "02877", "02880", "02883", "02899", "02989", "03301", "03306", "03309", "03311", "03319", "03320", "03323", "03328", "03331", "03333", "03339", "03360", "03369", "03377", "03380", "03383", "03396", "03606", "03613", "03618", "03633", "03669", "03690", "03692", "03759", "03799", "03800", "03808", "03818", "03866", "03868", "03877", "03883", "03888", "03898", "03899", "03900", "03908", "03933", "03958", "03968", "03969", "03988", "03990", "03993", "03998", "06030", "06049", "06055", "06060", "06066", "06068", "06069", "06088", "06098", "06099", "06110", "06116", "06158", "06166", "06169", "06178", "06186", "06196", "06198", "06806", "06808", "06818", "06837", "06862", "06865", "06869", "06881", "06886"));
    private static int mAllStockListSize = 0;
    private static int mNowLoadedStockListSize = 0;
    private static Date mLastDealDate = null;
    private static List<Date> mDealDateList = null;
    private static List<Integer> mDealDayList = new ArrayList<>();
    private static Context mContext;
    private static final ExecutorService mDBThreadPool = Executors.newCachedThreadPool(); // 存储股票价格线程池
    private static List<DealDate> mALLDealDateList = new ArrayList<>(); // 数据库存储的所有的交易日期
    private static List<DealDate> mBeforeNDealDateList = new ArrayList<>(); // 最近五日交易的前N天
    private static int mBeforeNDealDay = 2;
    public static void initStockManager(Context context){
        mContext = context;
        List<String> ALL_STOCK_ID_LIST = new ArrayList<>();
        ALL_STOCK_ID_LIST.addAll(DEFAULT_STOCK_MONITOR_STOCK_ID_LIST);
        ALL_STOCK_ID_LIST.addAll(DEFAULT_PRICE_MONITOR_STOCK_ID_LIST);
        mAllStockListSize = ALL_STOCK_ID_LIST.size();
        createStocks(ALL_STOCK_ID_LIST, false);
        mALLDealDateList = DBUtil.getAllDealDate();

        Timer timer = new Timer("MinuteStocks");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mStockMonitorUIManager != null){
                    mStockMonitorUIManager.notifyStockListDateSetChanged();
                }
            }
        }, 0, 1000 * 20); // 1 seconds
    }

    /**
     * 根据股票id列表创建股票实例
     * @param stockIdList
     */
    public static void createStocks(List<String> stockIdList, boolean isMonitorBuyPoint){
        for(String stockId : stockIdList){
            if(stockId.length() > 2 && !stockId.substring(0,2).equals("hk")){
                stockId = "hk" + stockId;
            }
            Stock stock = new Stock(stockId, "", isMonitorBuyPoint ? 1 : 0);
            // 将股票实例存入数据库
            stock = DBUtil.saveStock(stock);
            mStockList.add(stock);
            mStockIdList.add(stockId);
            // 添加价格监控列表
            if(stock.getMonitorType() != 0 || DEFAULT_PRICE_MONITOR_STOCK_ID_LIST.indexOf(stockId) != -1){
                mPriceMonitorStockList.add(stock);
                mPriceMonitorStockIdList.add(stockId);
            }
        }
    }

    /**
     * 添加价格监控股票
     * 判断股票id的长度是否合规
     * 请求分时价格，查看是否存在这只股票，并得到股票名称
     * 若已观察列表中没有这只股票，则将股票加入价格观察列表
     * @param stockId
     */
    public static void addPriceMonitorStock(String stockId){
        if(stockId.length() > 2 && !stockId.substring(0,2).equals("hk")){
            stockId = "hk" + stockId;
        }
        if(stockId.length() != 7){
            Toast.makeText(mContext,"股票id长度不正确", Toast.LENGTH_SHORT).show();
        }

        Stock stock = new Stock(stockId, "", 1);

        // 现在股票监控列表中找
        int stockIndex = mStockIdList.indexOf(stock.getId());
        if(stockIndex != -1){
            stock = mStockList.get(stockIndex);
        }
        else{
            // 在网络中查找股票

            // 在数据库中查找股票
            stock = DBUtil.saveStock(stock);
        }
        stock.setMonitorType(1);

        // 若在当前价格监控列表中没有
        if(mPriceMonitorStockIdList.indexOf(stockId) == -1){
            mPriceMonitorStockList.add(stock);
            mPriceMonitorStockIdList.add(stock.getId());

            if(mPriceMonitorUIManager != null){
                mPriceMonitorUIManager.notifyStockListDateSetChanged();
            }
        }

    }


    /**
     * 添加分钟股票价格
     * 在添加了从开盘到现在的数据之后，再添加实时的每分钟的数据
     * @param stockPriceList
     */
    public static void addMinuteStockPriceNew(List<StockPrice> stockPriceList){
        for(StockPrice stockPrice : stockPriceList){
            if(stockPrice.getTime() == null){
                continue;
            }
            Log.d("lwd", "addMinuteStockPriceNew：" + stockPrice.toString());
            int stockIndex = mStockIdList.indexOf(stockPrice.stockId);
            if(stockIndex == -1){
                Log.e("lwd", String.format("没有找到价格对应的股票id：%s", stockPrice.stockId));
                return;
            }
            Stock stock = mStockList.get(stockIndex);
            // 设置股票名称
            if(stockPrice.getName() != null && stock.getName().equals("")){
                stock.setName(stockPrice.getName());
                DBUtil.updateStock(stock);
            }

            if(stock != null && stock.isReceiveTodayData){
                List<StrategyResult> strategyResultList = stock.addStockPrice(stockPrice);
                // 处理策略结果
                for(StrategyResult strategyResult : strategyResultList){
                    if(isResultNotify(stock, strategyResult)){
                        mBrainService.sendNotification(strategyResult.getNotificationId(),
                                stock.getName() + " " + strategyResult.toNotificationString());
                    }
                }
            }
        }
    }

    /**
     * 判断是否将得到的策略结果通知
     * @param stock
     * @param strategyResult
     * @return
     */
    public static boolean isResultNotify(Stock stock, StrategyResult strategyResult){
        switch (strategyResult.getStrategyId()){
            case R.integer.strategyMinuteLongToArrange:{
                // 当出现均线向上的提醒时，监控分时价格买点提醒
                if(stock.getMonitorType() == 0){
                    stock.ringMonitorType();
                }
                return true;
            }
            case R.integer.strategyMinuteRise:{
                if(stock.getMonitorType() == 1 && strategyResult.getType() == 0){
                    return true;
                }
                if(stock.getMonitorType() == 2 && strategyResult.getType() == 1){
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * 添加一天股票价格
     * 一天的价格策略结果虽然与五日的有部分冲突了，但是在mlta策略的具体分析中会自动过滤掉重复的，因为他们的时间是相同的，间隔肯定小于2.5个小时
     * @param stockPriceList
     * @return stockId 获取成功的股票Id
     */
    public static void addOneDayStockPriceListNew(List<StockPrice> stockPriceList, String stockId){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null && stockPriceList.size() > 0){
            Log.d("lwd", "addOneDayStockPriceListNew:" + stockPriceList.get(stockPriceList.size()-1).toString());

            // 在添加今天的数据之前要清空之前的价格数据
//            if(stock.isReceiveTodayData){
//                List<StrategyResult> strategyResultList = stock.addStockPriceList(stockPriceList);
//                stock.setLastExactStockPriceIndex(stock.getStockPriceList().size()-1);
//                // 处理策略结果
//                if(strategyResultList.size() > 0){
//                    updateDataAndFlushStockMonitorStockList(stock);
//                }
//            }

            // 刷新UI
            if(mMainActivityUIManager != null){
                mMainActivityUIManager.flushLoadProgress(String.format("获取一日准确数据，time：%s",
                        stockPriceList.get(stockPriceList.size()-1).getTime()));
            }
        }
    }


    /**
     * 添加五天股票价格
     * @param stockPriceEveryDayList
     * @param stockId
     */
    public static void addFiveDayStockPriceListNew(List<List<StockPrice>> stockPriceEveryDayList, String stockId){
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){

//            if(mBeforeNDealDateList.size() > 0) {
//                for (int i = 0; i < mBeforeNDealDateList.size(); i++) {
//                    List<StockPrice> beforeStockPriceList = DBUtil.getOneDayStockPriceList(stock.getId(), mBeforeNDealDateList.get(i).getDate());
//                    stockPriceEveryDayList.add(i, beforeStockPriceList);
//                }
//            }
//
//            List<StrategyResult> strategyResultList = stock.addStockPriceListList(stockPriceEveryDayList);
//            SuccessRateAnalyser.analyse(strategyResultList);
//            stock.calFiveDayHighestAndLowestPrice(stockPriceEveryDayList);
//            stock.calFiveDayHighestEndPrice(stockPriceEveryDayList);
//            stock.setLastExactStockPriceIndex(stock.getStockPriceList().size()-1);
//
//            // 更新UI
//            if(mStockMonitorUIManager != null){
//                if(stock.getStrategyResultMap().get(R.integer.strategyMinuteLongToArrange).size() > 0){
//                    mStockMonitorPickedStockList.add(stock);
//                }
//            }

            // 首页提示
            mNowLoadedStockListSize++;
            if(mMainActivityUIManager != null){
                mMainActivityUIManager.flushLoadProgress(String.format("初始化进度：%d/%d", mNowLoadedStockListSize, mAllStockListSize));
            }

            Log.d("lwd", String.format("%s 五日关键数据加载完毕 进度：%d/%d", stockId, mNowLoadedStockListSize, mAllStockListSize));

            // 存储StockPrice，
            // 由于使用多线程，在遍历时不能改变list，所以将遍历放在最后
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for(List<StockPrice> stockPriceList : stockPriceEveryDayList){
                        for(StockPrice stockPrice : stockPriceList){
                            DBUtil.saveOrUpdate30MinuteStockPrice(stockPrice);
                        }
                    }
                }
            };
            mDBThreadPool.execute(runnable);
        }
        // 设置获取之前数据完毕，开始获取分时数据
        stock.receiveTodayData();
    }


    /**
     * 设置前四日结束价格
     * @param fiveDayClosePriceList
     * @param stockId
     */
    public static void setPreviousFourDayPriceList(List<Float> fiveDayClosePriceList, String stockId) {
        // 得到前四日收盘价
        List<Float> previousFourDayPriceList = null;
        if(fiveDayClosePriceList.size() == 5){
            previousFourDayPriceList = fiveDayClosePriceList.subList(1, fiveDayClosePriceList.size());
        }
        else{
            previousFourDayPriceList = fiveDayClosePriceList;
        }

        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setPreviousFourDayPriceList(previousFourDayPriceList);
            Log.d("lwd", String.format("%s 加载前四日收盘数据", stockId));
        }
//        Log.d("lwd", String.format("stockId:%s, ma5:%f", stock.id, stock.getMa5()));
    }

    /**
     * 设置日均线数据
     * @param stockId
     * @param dayMaPrice
     */
    public static void setStockDayMaPrice(String stockId, DayMaPrice dayMaPrice) {
        int stockIndex = mStockIdList.indexOf(stockId);
        Stock stock = mStockList.get(stockIndex);
        if(stock != null){
            stock.setDayMaPrice(dayMaPrice);
            Log.d("lwd", String.format("%s 加载均线数据数据", stockId));
        }

        if(mMainActivityUIManager != null){
            mMainActivityUIManager.flushLoadProgress(String.format("获取日均线数据，stockId：%s", stockId));
        }
    }

    public static void notifyPriceMonitorStockListChange(String stockId){
        if(mPriceMonitorUIManager != null){
            mPriceMonitorUIManager.notifyStockListItemChanged(mPriceMonitorStockIdList.indexOf(stockId));
        }
    }

    /**
     * 刷新股票日线价格监控列表
     * @param stock
     */
    public static void updateDataAndFlushStockMonitorStockList(Stock stock){
        int stockIndex = mStockMonitorPickedStockList.indexOf(stock);
        if(stockIndex != -1){
            mStockMonitorPickedStockList.updateItemAt(stockIndex, stock);
        }
        else{
            // 第一次添加股票监控，将股票加入mStockMonitorPickedStockList
            mStockMonitorPickedStockList.add(stock);
//            int a = mStockMonitorPickedStockList.indexOf(stock);
        }
    }

    /**
     * 刷新股票价格监控列表
     * 若monitorType为0，则在监控列表中去掉这个stock
     * 若monitorType为1或2，则在列表中添加这个stock
     */
    public static void flushPriceMonitorStockList(Stock stock){
        if(stock.getMonitorType() == 0){
            // 在监控列表中去除
            for(int i=0; i<mPriceMonitorStockList.size(); i++){
                if(mPriceMonitorStockList.get(i).getId() == stock.getId()){
//                    mPriceMonitorStockList.remove(i);

                    if(mPriceMonitorUIManager != null){
                        mPriceMonitorUIManager.notifyStockListDateSetChanged();
                    }
                    break;
                }
            }
        }
        else if(stock.getMonitorType() == 1){
            // 在监控列表中加入
            boolean isExited = false;
            for(int i=0; i<mPriceMonitorStockList.size(); i++){
                if(mPriceMonitorStockList.get(i).getId() == stock.getId()){
                    isExited = true;
                }
            }
            if(!isExited){
                mPriceMonitorStockList.add(stock);
                mPriceMonitorStockIdList.add(stock.getId());
            }
            if(mPriceMonitorUIManager != null){
                mPriceMonitorUIManager.notifyStockListDateSetChanged();
            }
        }
        else if(stock.getMonitorType() == 2){
            // 在监控列表中改变
            for(int i=0; i<mPriceMonitorStockList.size(); i++){
                if(mPriceMonitorStockList.get(i).getId() == stock.getId()){

                    if(mPriceMonitorUIManager != null){
                        mPriceMonitorUIManager.notifyStockListItemChanged(i);
                    }
                    break;
                }
            }
        }
    }

    public static boolean isStockInPriceMonitorList(String stockId){
        if(mPriceMonitorStockIdList.indexOf(stockId) != -1){
            return true;
        }
        return false;
    }


    public static Date getLastDealDate() {
        return mLastDealDate;
    }

    public static void setLastDealDate(Date mLastDealDate) {
        StockManager.mLastDealDate = mLastDealDate;
    }

    public static String getPickedStockIdListString(){
        String stockIdListString = "";
        for(int i = 0; i < mStockMonitorPickedStockList.size(); i++){
            stockIdListString += mStockMonitorPickedStockList.get(i).getId().substring(2);
            stockIdListString += "\r\n";
            stockIdListString += "\r\n";
        }
        return stockIdListString;
    }

    public static void setBrainService(BrainService brainService){
        mBrainService = brainService;
    }

    public static void setPriceMonitorFragmentUIManager(UIManager mainActivityUIManager) {
        mPriceMonitorUIManager = mainActivityUIManager;
    }

    public static SortedList<Stock> setStockMonitorFragmentUIManager(UIManager stockMonitorUIManager, StockMonitorSortedListCallback stockMonitorSortedListCallback){
        mStockMonitorUIManager = stockMonitorUIManager;
        mStockMonitorPickedStockList = new SortedList<>(Stock.class, stockMonitorSortedListCallback);

        return mStockMonitorPickedStockList;
    }

    public static void setMainActivityUIManager(UIManager mainActivityUIManager){
        mMainActivityUIManager = mainActivityUIManager;
    }

    /**
     * 从数据库中读取全部的股票
     */
    public static void getStocksFromDB(){
        mStockList = DBUtil.getAllStocks();
        for(Stock stock : mStockList){
            mStockIdList.add(stock.getId());
        }
        // 如果数据库为空，则添加默认股票
        if(mStockIdList.size() == 0){
            createStocks(DEFAULT_STOCK_MONITOR_STOCK_ID_LIST, false);
        }
    }

    public static List<Stock> getPriceMonitorStockList() {
        return mPriceMonitorStockList;
    }

    public static List<String> getThisStockMinuteRiseStrategyResultList(int stockIndex){
        return mPriceMonitorStockList.get(stockIndex).getAllMinuteRiseStrategyResult();
    }

    public static List<String> getDefaultStockMonitorStockIdList(){
        return mStockIdList;
    }

    public static List<Date> getDealDateList() {
        return mDealDateList;
    }

    public static void setDealDateList(List<Date> dealDateList) {
        // 当mDealDateList为null时才更新交易时间，所以到了新的交易日应该将mDealDateList设置为null，
        // 或者每次进入此函数时比较dealDateList与老的是否相同
        if(mDealDateList == null && dealDateList != null){
            mDealDateList = dealDateList;

            // 将新的DealDate存入数据库
            for(Date date : dealDateList){
                DealDate dealDate = new DealDate(date);
                DBUtil.saveDealDate(dealDate);
            }

            // 寻找前一天的dealDate
            if(dealDateList.size() > 0 && mALLDealDateList.size() > 0){
                // 找到请求五日的第一天在数据库存储价格天数中的位置
                int dealDateIndex = -1;
                for(int i=0; i<mALLDealDateList.size(); i++){
                    if(mALLDealDateList.get(i).getDate().equals(dealDateList.get(0))){
                        dealDateIndex = i;
                    }
                }

                int beforeIndex = dealDateIndex - 1;
                for(int i=0; i<mBeforeNDealDay; i++){
                    if(beforeIndex >= 0){
                        // 将前一个交易日存储
                        mBeforeNDealDateList.add(mALLDealDateList.get(beforeIndex));
                        beforeIndex--;
                    }
                    else{
                        break;
                    }
                }


            }

        }
    }

    public static List<Integer> getDealDayList() {
        if(mDealDayList.size() == 0){
            for(Date dealDate : getDealDateList()){
                mDealDayList.add(dealDate.getDate());
            }
        }
        return mDealDayList;
    }

}
