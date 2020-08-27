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

    // 净利润大于10%的股票
    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("03347", "01521", "01772", "09989", "09983", "00175", "09939", "09926", "09969", "09996", "02018", "01385", "01070", "00826", "01873", "06060", "00257", "00981", "01801", "00185", "01231", "01931", "01003", "00034", "00488", "01813", "02103", "09983", "01558", "02100", "00697", "09923", "00832", "06098", "00241", "02001", "02772", "02231", "01951", "00586", "00119", "01995", "01755", "01858", "01859", "02013", "00772", "00535", "03709", "03883", "00978", "01478", "09968", "01935", "01579", "01030", "02269", "06865", "06862", "09928", "01610", "01789", "01873", "01330", "01176", "00095", "03319", "01833", "00081", "01157", "02400", "03301", "03690", "02163", "00152", "01268", "02382", "03662", "01908", "01753", "00123", "02138", "01769", "02606", "09988", "01773", "00754", "01821", "02883", "09922", "06049", "01501", "02168", "02020", "00780", "00257", "01349", "00650", "03316", "00467", "03990", "01777", "00445", "01918", "03868", "01257", "09909", "01830", "02669", "01238", "06186", "02331", "06068", "00960", "00813", "03380", "06055", "00182", "01765", "00884", "03759", "01576", "02007", "01600", "00631", "02727", "01141", "01387", "01919", "01717", "01093", "01898", "01890", "01638", "02202", "06100", "01233", "02186", "02233", "01686", "02282", "01381", "00968", "01585", "00700", "03918", "00691", "01252", "00853", "03813", "02380", "01033", "00667", "01665", "02686", "00345", "01857", "06110", "00658", "01119", "02768", "01066", "01432", "01599", "00819", "00371", "09999", "03323", "02688", "06820", "00881", "00777", "02196", "01929", "00992", "02319", "00384", "00337", "01691", "01317", "01186", "00327", "01896", "00317", "01138", "03669", "02005", "02798", "00512", "00588", "00200", "02588"));
    // 股价大于1的股票
//    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("00700", "00388", "09618", "09988", "03690", "02269", "06185", "00158", "09999", "06160", "02313", "02378", "01579", "00011", "03347", "02382", "00945", "02359", "01833", "00016", "00053", "00669", "03759", "00026", "02688", "02318", "00522", "02020", "01211", "01299", "00002", "00373", "00168", "02500", "06049", "01044", "00027", "09923", "01501", "00914", "00941", "02588", "06826", "06098", "01801", "06060", "00291", "02606", "00001", "01877", "02018", "06862", "02696", "00881", "00772", "06288", "00303", "00357", "03888", "00006", "01743", "01113", "00019", "01858", "00212", "03319", "01038", "00066", "00017", "00960", "02888", "01772", "02163", "06078", "02400", "03968", "01513", "01797", "01193", "01109", "00813", "06969", "02319", "00853", "03692", "06855", "01112", "00586", "00316", "02331", "01918", "00005", "01928", "02196", "01997", "01336", "01913", "00012", "09926", "02168", "03331", "00708", "01169", "01347", "01763", "09996", "00285", "00345", "01876", "01268", "01477", "00981", "09909", "03606", "00392", "06919", "09997", "00067", "00777", "00014", "00247", "02202", "00425", "01755", "03898", "01769", "00148", "00688", "00763", "02601", "09990", "02388", "03808", "00440", "01821", "00101", "01787", "00384", "01810", "01972", "00874", "01789", "00683", "06100", "03990", "01882", "00010", "01099", "00268", "03316", "00052", "02628", "00241", "03333", "00819", "06030", "03908", "01066", "00041", "00341", "01151", "00289", "09922", "01911", "00023", "00667", "01093", "00200", "00590", "09966", "01995", "02338", "02120", "09989", "01727", "00175", "00839", "06186", "00780", "00322", "06055", "01896", "00696", "00179", "00004", "01813", "01385", "01548", "03668", "00062", "00754", "01158", "03380", "01128", "02128", "01310", "09939", "00105", "06869", "06886", "02607", "00868", "01908", "09969", "00029", "00071", "00973", "00636", "01717", "00152", "00551", "00966", "01588", "00270", "01088", "01970", "02611", "02013", "02180", "00165", "00863", "00411", "00363", "01233", "01313", "01249", "00225", "06066", "06865", "09928", "01502", "03323", "06978", "06823", "00921", "00003", "03869", "01675", "01691", "09983", "01558", "06190", "01951", "03383", "01999", "00880", "06808", "00032", "00377", "09908", "02282", "01098", "03396", "02869", "01308", "01478", "06110", "00025", "02099", "00968", "02777", "06099", "02007", "01137", "00034", "00836", "00133", "02616", "00358", "01873", "01530", "01776", "01111", "03900", "00799", "01177", "03883", "00083", "00520", "00144", "00867", "01818", "03933", "03669", "03709", "00317", "00883", "01475", "00287", "02048", "00116", "00656", "03613", "02689", "00511", "00098", "01910", "01458", "06889", "01929", "01125", "01992", "01585", "03918", "00743", "01888", "01809", "00251", "01448", "01157", "00177", "00191", "00488", "01252", "01773", "06178", "02333", "02638", "06169", "06069", "00051", "02039", "03306", "01967", "01836", "01922", "02218", "03308", "00267", "02356", "03848", "00220", "02238", "03633", "01848", "01030", "00548", "00100", "01114", "02066", "02208", "06837", "00512", "00336", "00087", "00878", "03662", "01839", "00194", "02183", "02669", "01971", "01525", "01212", "03301", "03360", "00861", "02355", "00288", "01890", "00884", "00659", "06958", "00069", "00855", "00045", "00877", "02001", "01651", "02883", "01186", "03311", "03309", "00293", "01600", "00050", "02552", "02328", "01533", "01171", "00564", "03948", "01657", "00950", "01666", "00762", "08051", "00382", "01238", "01894", "02678", "01846", "00939", "00354", "01070", "01817", "01686", "00135", "00278", "01515", "00658", "00576", "00753", "06111", "06868", "00151", "00489", "02877", "00694", "02337", "02899", "01378", "02666", "00776", "01528", "03738", "00811", "00081", "00552", "00900", "01316", "00266", "00895", "02772", "00992", "00817", "01349", "01119", "00088", "00553", "00127", "03958", "00916", "01061", "02186", "00008", "02357", "06198", "01988", "09668", "01981", "00257", "00277", "03799", "02005", "06881", "00327", "06139", "02289", "03866", "02314", "06158", "01860", "01820", "00190", "01052", "02299", "08516", "06933", "01398", "03636", "01800", "06819", "01271", "01234", "01570", "01835", "09977", "03320", "01072", "02003", "00856", "01196", "01963", "01875", "02138", "01799", "02798", "03798", "01199", "00315", "02278", "03328", "08496", "00620", "00719", "00089", "00028", "01658", "01966", "00390", "01611", "01055", "02103", "00369", "06968", "00837", "01521", "01778", "01596", "06068", "00610", "00831", "00951", "01895", "06128", "00631", "02303", "02239", "01919", "01638", "01958", "00222", "01542", "01723", "02016", "01735", "08236", "00302", "00995", "01138", "02768", "01456", "01622", "06820", "00113", "01589", "03321", "06858", "01330", "01610", "00376", "01628", "00173", "00806", "02386", "03836", "01766", "01382", "01753", "00386", "00832", "00131", "01257", "01652", "01993", "00252", "01083", "01812", "00662", "01381", "00902", "00570", "03601", "00189", "00998", "01916", "00486", "01935", "03618", "03969", "00906", "01551", "03978", "01006", "02342", "01578", "06118", "01681", "01415", "02488", "00371", "01126", "03681", "01089", "00480", "02231", "00038", "00468", "00670", "01427", "08170", "03993", "00086", "00331", "00095", "02118", "00934", "03899", "09979", "00484", "01672", "06166", "02019", "01565", "01255", "01317", "02060", "06885", "00967", "02368", "00746", "01608", "06818", "00242", "01756", "00612", "08069", "00184", "01851", "00078", "08139", "00506", "03689", "00593", "06088", "00374", "02226", "01108", "00112", "01286", "01629", "08401", "01568", "01806", "01430", "03868", "00797", "00160", "01288", "01476", "02708", "01983", "01996", "00751", "01866", "01883", "01931", "00857", "01218", "01036", "01065", "00546", "00728", "01373", "01609", "01759", "00604", "01339", "02738", "03966", "00826", "03988", "00699", "00737", "03698", "06093", "01990", "01133", "00337", "02011", "02779", "08039", "01005", "01368", "01902", "00119", "01830", "00408", "00816", "01696", "03603", "00410", "01765", "00035", "00306", "01566", "01760", "09968", "02030", "00375", "01184", "03608", "06122", "01221", "01085", "03336", "06199", "03339", "00226", "00710", "01302", "01343", "01417", "01635", "00198", "01739", "06806", "02727", "00984", "01907", "01583", "01599", "01708", "03680", "01656", "03998", "09936", "00581", "01045", "03393", "06123", "00517", "01387", "00099", "06033", "00046", "00347", "01071", "00255", "00462", "02199", "06838", "00596", "00691", "00854", "08227", "01561", "02283", "00142", "01987", "00075", "02377", "01593", "01915", "00323", "00665", "02232", "00579", "00956", "02198", "01853", "02302", "03639", "01509", "01862", "03335", "03399", "00294", "01886", "00693", "01785", "00416", "06090", "00398", "00697", "01665", "02360", "02600", "03718", "01898", "00626", "01859", "02858", "01298", "00056", "03377", "03737", "00432", "00846", "01160", "01208", "01662", "01428", "01571", "00875", "01263", "06196", "00239", "01297", "03768", "09906", "09916", "00617", "01829", "01847", "08513", "00908", "01861", "01712", "00639", "01899", "03358", "09911", "00107", "01702", "01612", "01090", "01673", "00598", "01159", "00057", "01258", "00580", "01816", "03366", "01224", "01689", "03318", "00557", "01357", "01576", "01856", "01285", "02698", "03813", "00216", "00588", "00589", "01333", "01698", "08247", "00338", "01953", "09958", "00882", "02009", "08363", "01292", "00258", "00259", "01337", "01962", "00733", "01553", "06188", "08083", "08439", "00326", "01480", "02012", "03788", "06896", "01372", "01777", "03939", "00256", "01375", "01359", "00525", "01280", "01885", "02002", "00329", "00365", "00529", "01969", "08037", "00123", "00608", "00219", "01358", "00533", "00933", "03315", "00068", "00788", "00983", "02225", "02380", "08425", "01370", "01748", "02868", "01725", "03839", "08049", "00187", "01003", "08500", "01986", "00467", "00400", "00535", "02068", "02286", "00980", "01122", "01941", "02300", "09900", "01538", "01713", "01761", "01811", "01863", "01463", "02233", "03330", "01449", "02025", "08328", "00722", "01105", "01198", "02308", "02558", "01543", "02166", "08371", "01010", "02136", "00613", "00679", "01989", "02006", "02528", "00276", "00528", "00825", "02608", "00684", "00709", "01183", "01617", "01618", "01857", "01985", "02633", "06811", "00178", "01483", "02341", "03700", "01905", "03600", "00771", "02181", "03369", "06189", "08540", "00538", "01577", "00695", "02281", "00215", "00366", "00978", "02111", "06822", "00125", "00591", "02886", "00108", "00308", "00912", "01569", "03623", "00163", "01329", "02383", "02393", "02448", "01293", "01649", "02100", "03348", "00146", "00271", "00296", "01345", "01527", "01865", "03983", "01107", "02660", "03938", "06138", "00321", "01606", "01788", "02662", "06839", "00493", "01117", "01921", "00120", "02322", "02329", "08025", "00807", "01050", "02023", "02262", "02343", "03688", "08418", "00124", "01060", "01361", "01660", "00096", "02686", "02892", "00272", "00814", "02083", "02280", "09986", "01216", "00213", "03877", "06918", "08223", "00732", "01086", "01586", "00224", "00264", "00543", "01140", "01176", "01388", "01798", "02139", "00991", "00218", "00301", "00458", "00896", "01639", "01741", "03329", "03818", "00450", "01082", "01301", "01626", "02699", "03390", "08140", "00818", "00999", "01977", "01979"));
//    private static ArrayList<String> DEFAULT_STOCK_MONITOR_STOCK_ID_LIST = new ArrayList<String>(Arrays.asList("08238"));

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
            if(stock.isReceiveTodayData){
                List<StrategyResult> strategyResultList = stock.addStockPriceList(stockPriceList);
                stock.setLastExactStockPriceIndex(stock.getStockPriceList().size()-1);
                // 处理策略结果
                if(strategyResultList.size() > 0){
                    updateDataAndFlushStockMonitorStockList(stock);
                }
            }
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

            if(mBeforeNDealDateList.size() > 0) {
                for (int i = 0; i < mBeforeNDealDateList.size(); i++) {
                    List<StockPrice> beforeStockPriceList = DBUtil.getOneDayStockPriceList(stock.getId(), mBeforeNDealDateList.get(i).getDate());
                    stockPriceEveryDayList.add(i, beforeStockPriceList);
                }
            }

            List<StrategyResult> strategyResultList = stock.addStockPriceListList(stockPriceEveryDayList);
            SuccessRateAnalyser.analyse(strategyResultList);
            stock.calFiveDayHighestAndLowestPrice(stockPriceEveryDayList);
            stock.calFiveDayHighestEndPrice(stockPriceEveryDayList);
            stock.setLastExactStockPriceIndex(stock.getStockPriceList().size()-1);

            // 更新UI
            if(mStockMonitorUIManager != null){
                if(stock.getStrategyResultMap().get(R.integer.strategyMinuteLongToArrange).size() > 0){
                    mStockMonitorPickedStockList.add(stock);
                }
            }
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
