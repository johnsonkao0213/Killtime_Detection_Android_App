package labelingStudy.nctu.minuku.streamgenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import labelingStudy.nctu.minuku.Data.appDatabase;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.dao.ConnectivityDataRecordDAO;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.model.DataRecord.ConnectivityDataRecord;
import labelingStudy.nctu.minuku.stream.ConnectivityStream;
import labelingStudy.nctu.minukucore.exception.StreamAlreadyExistsException;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;
import labelingStudy.nctu.minukucore.stream.Stream;


/**
 * Created by Lawrence on 2017/8/22.
 */

public class ConnectivityStreamGenerator extends AndroidStreamGenerator<ConnectivityDataRecord> {

    private final String TAG = "ConnectivityStreamGenerator";
    private final boolean debugMode = false; // 20200531 add by Lesley for print all data in detail

    private Context mContext;
    private ConnectivityDataRecordDAO connectivityDataRecordDAO;
    public String NETWORK_TYPE_WIFI = "Wifi";
    public String NETWORK_TYPE_MOBILE = "Mobile";
    public boolean mIsNetworkAvailable = false;
    public boolean mIsConnected = false;
    public boolean mIsWifiAvailable = false;
    public boolean mIsMobileAvailable = false;
    public boolean mIsWifiConnected = false;
    public boolean mIsMobileConnected = false;

    public static String mNetworkType = "NA";

    public static int mainThreadUpdateFrequencyInSeconds = 5;
    public static long mainThreadUpdateFrequencyInMilliseconds = mainThreadUpdateFrequencyInSeconds *Constants.MILLISECONDS_PER_SECOND;

    private static Handler mMainThread;

    private static ConnectivityManager mConnectivityManager;

    private ConnectivityStream mStream;

    private SharedPreferences sharedPrefs;

    private static long id;

    public static long getId() { return id; }

    public ConnectivityStreamGenerator(Context applicationContext){
        super(applicationContext);

        this.mContext = applicationContext;
        this.mStream = new ConnectivityStream(Constants.DEFAULT_QUEUE_SIZE);
        connectivityDataRecordDAO = appDatabase.getDatabase(applicationContext).connectivityDataRecordDao();

        sharedPrefs = mContext.getSharedPreferences(Constants.sharedPrefString,Context.MODE_PRIVATE);

        mConnectivityManager = (ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);

        this.register();
    }

    @Override
    public void register() {
        Log.d(TAG, "Registering with StreamManager.");
        try {
            MinukuStreamManager.getInstance().register(mStream, ConnectivityDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which ConnectivityDataRecord depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExistsException) {
            Log.e(TAG, "Another stream which provides ConnectivityDataRecord is already registered.");
        }
    }

    @Override
    public Stream<ConnectivityDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {

        Log.d(TAG, "updateStream called");

//        int session_id = SessionManager.getOngoingSessionId();

        int session_id = sharedPrefs.getInt("ongoingSessionid", Constants.INVALID_INT_VALUE);

        //TODO get service data
        ConnectivityDataRecord connectivityDataRecord =
                new ConnectivityDataRecord(mNetworkType,mIsNetworkAvailable, mIsConnected, mIsWifiAvailable,
                        mIsMobileAvailable, mIsWifiConnected, mIsMobileConnected);
        mStream.add(connectivityDataRecord);
        Log.d(TAG, "CheckFamiliarOrNot to be sent to event bus" + connectivityDataRecord);
        // also post an event.
        EventBus.getDefault().post(connectivityDataRecord);
        try {
//            appDatabase db;
//            db = Room.databaseBuilder(mContext,appDatabase.class,"dataCollection")
//                    .allowMainThreadQueries()
//                    .build();
            id = connectivityDataRecordDAO.insertAll(connectivityDataRecord);
            if(debugMode){
                List<ConnectivityDataRecord> connectivityDataRecords = connectivityDataRecordDAO.getAll();
                for (ConnectivityDataRecord c : connectivityDataRecords) {
                    Log.e(TAG, " isIsWifiConnected: "+String.valueOf(c.isIsWifiConnected()));
                    Log.e(TAG," NetworkType: "+c.getNetworkType());
                    Log.e(TAG, " isNetworkAvailable: "+String.valueOf(c.isNetworkAvailable()));
                    Log.e(TAG, " isIsConnected: "+ String.valueOf(c.isIsConnected()));
                    Log.e(TAG, " isIsWifiAvailable: "+String.valueOf(c.IsWifiAvailable));
                    Log.e(TAG, " isIsMobileAvailable: "+String.valueOf(c.IsMobileAvailable));
                    Log.e(TAG, " isIsMobileConnected: "+ String.valueOf(c.IsMobileConnected));

                }
            }
        } catch (NullPointerException e){ //Sometimes no data is normal
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public long getUpdateFrequency() {
        return 1;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    @Override
    public void onStreamRegistration() {
        Log.e(TAG,"onStreamRegistration");

        runPhoneStatusMainThread();

    }

    public void runPhoneStatusMainThread(){

        Log.d(TAG, "runPhoneStatusMainThread") ;

        mMainThread = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                getNetworkConnectivityUpdate();

                mMainThread.postDelayed(this, mainThreadUpdateFrequencyInMilliseconds);
            }
        };

        mMainThread.post(runnable);
    }

    private void getNetworkConnectivityUpdate(){

        mIsNetworkAvailable = false;
        mIsConnected = false;
        mIsWifiAvailable = false;
        mIsMobileAvailable = false;
        mIsWifiConnected = false;
        mIsMobileConnected = false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Network[] networks = mConnectivityManager.getAllNetworks();

            NetworkInfo activeNetwork;

            for (Network network : networks) {
                activeNetwork = mConnectivityManager.getNetworkInfo(network);

                //if there is no default network
                if(activeNetwork == null){

                    break;
                }

                if (activeNetwork.getType()== ConnectivityManager.TYPE_WIFI){
                    mIsWifiAvailable = activeNetwork.isAvailable();
                    mIsWifiConnected = activeNetwork.isConnected();
                } else if (activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
                    mIsMobileAvailable = activeNetwork.isAvailable();
                    mIsMobileConnected = activeNetwork.isConnected();
                }

            }

            if (mIsWifiConnected) {
                mNetworkType = NETWORK_TYPE_WIFI;
            }
            else if (mIsMobileConnected) {
                mNetworkType = NETWORK_TYPE_MOBILE;
            }

            mIsNetworkAvailable = mIsWifiAvailable | mIsMobileAvailable;
            mIsConnected = mIsWifiConnected | mIsMobileConnected;


            Log.d(TAG, "[test save records] connectivity change available? WIFI: available " + mIsWifiAvailable  +
                    "  mIsConnected: " + mIsWifiConnected + " Mobile: available: " + mIsMobileAvailable + " mIs connected: " + mIsMobileConnected
                    +" network type: " + mNetworkType + ",  mIs connected: " + mIsConnected + " mIs network available " + mIsNetworkAvailable);


        } else{

            Log.d(TAG, "[test save records] api under lollipop " );


            if (mConnectivityManager!=null) {

                NetworkInfo activeNetworkWifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo activeNetworkMobile = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                boolean isWiFi = activeNetworkWifi.getType() == ConnectivityManager.TYPE_WIFI;
                boolean isMobile = activeNetworkMobile.getType() == ConnectivityManager.TYPE_MOBILE;

                Log.d(TAG, "[test save records] connectivity change available? " + isWiFi);


                if(activeNetworkWifi !=null) {

                    mIsWifiConnected = activeNetworkWifi != null &&
                            activeNetworkWifi.isConnected();
                    mIsMobileConnected = activeNetworkWifi != null &&
                            activeNetworkMobile.isConnected();

                    mIsConnected = mIsWifiConnected | mIsMobileConnected;

                    mIsWifiAvailable = activeNetworkWifi.isAvailable();
                    mIsMobileAvailable = activeNetworkMobile.isAvailable();

                    mIsNetworkAvailable = mIsWifiAvailable | mIsMobileAvailable;


                    if (mIsWifiConnected) {
                        mNetworkType = NETWORK_TYPE_WIFI;
                    }

                    else if (mIsMobileConnected) {
                        mNetworkType = NETWORK_TYPE_MOBILE;
                    }


                    //assign value
//
                    Log.d(TAG, "[test save records] connectivity change available? WIFI: available " + mIsWifiAvailable  +
                            "  mIsConnected: " + mIsWifiConnected + " Mobile: available: " + mIsMobileAvailable + " mIs connected: " + mIsMobileConnected
                            +" network type: " + mNetworkType + ",  mIs connected: " + mIsConnected + " mIs network available " + mIsNetworkAvailable);

                }
            }

        }
    }

    @Override
    public void offer(ConnectivityDataRecord dataRecord) {

    }
}
