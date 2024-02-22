/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package labelingStudy.nctu.boredom_detection.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import labelingStudy.nctu.boredom_detection.BoredomApp;
import labelingStudy.nctu.boredom_detection.BuildConfig;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.MainActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.Receiver.IntentActionReceiver;
import labelingStudy.nctu.boredom_detection.Receiver.RestarterBroadcastReceiver;
import labelingStudy.nctu.boredom_detection.SurveyActivity;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.dao.ActionDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.MinukuDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationRemoveRecordDao;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.manager.InstanceManager;
import labelingStudy.nctu.boredom_detection.manager.RemindersIntentManager;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationEventRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.minuku.Utilities.CSVHelper;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.manager.MobilityManager;
import labelingStudy.nctu.minuku.manager.SessionManager;
import labelingStudy.nctu.minuku.model.DataRecord.AppUsageDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;
import labelingStudy.nctu.minuku.model.Session;
import labelingStudy.nctu.minuku.streamgenerator.AccessibilityStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.ActivityRecognitionStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.AppUsageStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.BatteryStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.ConnectivityStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.RingerStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.TelephonyStreamGenerator;
import labelingStudy.nctu.minuku.streamgenerator.TransportationModeStreamGenerator;

import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_1;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_10;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_17;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_2;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_20;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_21;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_7;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_9;


//import labelingStudy.nctu.boredom_detection.Receiver.WifiReceiver;

public class BackgroundService extends Service {


    private static final String TAG = "BackgroundService";
    private final boolean debugMode = true; // 20200531 add by Lesley for print all data in detail

    final static String CHECK_RUNNABLE_ACTION = "checkRunnable";
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";



//  WifiReceiver mWifiReceiver;
    IntentFilter intentFilter;
    IntentActionReceiver mIntentActionReceiver;

    MinukuStreamManager streamManager;

    AccessibilityStreamGenerator accessibilityStreamGenerator ;

    private ScheduledExecutorService mScheduledExecutorService;
    private ScheduledFuture<?> mScheduledUploadFirestore;
    private ScheduledFuture<?> mScheduledFuture;
    private ScheduledFuture<?> mScheduledScreenShot;
    private ScheduledFuture<?> mScheduledNotification;
    private ScheduledFuture<?> mScheduledNews;
    private ScheduledFuture<?> mScheduledSyncNotificationList;
    private ScheduledFuture<?> mScheduledUploadEsm;

    private MinukuDataRecordDAO MinukuDataRecordDAO;
    private NotificationEventRecordDAO NotificationEventRecordDAO;
    private NotificationDataRecordDAO NotificationDataRecordDAO;
    private ImageDataRecordDAO ImageDataRecordDAO;
    private AnswerDataRecordDAO AnswerDataRecordDAO;
    private NoteDataRecordDAO noteDataRecordDAO;
    private NotificationRemoveRecordDao notificationRemoveRecordDAO;
    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    private ActionDataRecordDAO actionDataRecordDAO;


    private labelingStudy.nctu.minuku.dao.AccessibilityDataRecordDAO AccessibilityDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.ActivityRecognitionDataRecordDAO ActivityRecognitionDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.AppUsageDataRecordDAO AppUsageDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.BatteryDataRecordDAO BatteryDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.ConnectivityDataRecordDAO ConnectivityDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.RingerDataRecordDAO RingerDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.TelephonyDataRecordDAO TelephonyDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.TransportationModeDataRecordDAO TransportationModeDataRecordDAO;
    private labelingStudy.nctu.minuku.dao.MobileAccessibilityDataRecordDAO MobileAccessibilityDataRecordDAO;


    private int ongoingNotificationID = 42;
    private int hintNotificationID = 32;
    private String ongoingNotificationText = "";


    NotificationManager mNotificationManager;
//    private Notification surveyNoti = new Notification();
    ActivityManager mActivityManager = null;
    UsageStatsManager mUsageStatsManager = null;
    PowerManager mPowerManager = null;
    ConnectivityManager mConnectivityManager = null;
    NetworkRequest.Builder builder;
    ConnectivityManager.NetworkCallback networkCallback;
    int MAX_UPLOAD_NUM_MINUKU = 1500;
    int MAX_UPLOAD_NUM_NOTI_RM = 200;
    int MAX_UPLOAD_NUM_ACC = 1000;
    int MAX_UPLOAD_NUM_INTENT = 100;
    int MAX_UPLOAD_NUM_IMAGE = 1000;

    public static boolean isBackgroundServiceRunning = false;
    public static boolean isBackgroundRunnableRunning = false;

    private SharedPreferences sharedPrefs;

    private static Intent resultIntentfromM;
    public String fileName = "NA";
    File saveFile;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;

    private ImageReader imageReader;
    private Handler handler;

    //private int notifyNotificationID = 1000;
    private int reminderNotificationCode = 300;
//    private int notifySurveyID = 77;
//    private int notifySurveyCode = 777;
    int hintFlag = 0;
    int hintCase = 3;

    private Boolean saveDataToCSV = false;

    Gson gson=new Gson();


    //firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private boolean pushToRealtimeDataBase_Minuku = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_Minuku;
    private boolean pushToRealtimeDataBase_Accessibility = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_Accessibility;
    private boolean pushToRealtimeDataBase_NotiRemove = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_NotiRemove;
    private boolean pushToRealtimeDataBase_ServiceStatus = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_ServiceStatus;
    private boolean pushToRealtimeDataBase_Image = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_Image;

    String versionName = BuildConfig.VERSION_NAME;

    // alarm
    private AlarmManager alarmManager;
    RemindersIntentManager remindersIntentManager;

    PackageManager pm ;


    Context mContext;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat formatter_date = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatter_full = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    SimpleDateFormat formmatter_minuku = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");


    public BackgroundService() {
        super();

    }

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = this;
        ongoingNotificationText = mContext.getResources().getString(R.string.ongoing_message, getResources().getString(R.string.app_name));
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        isBackgroundServiceRunning = false;
        isBackgroundRunnableRunning = false;

        streamManager = MinukuStreamManager.getInstance();
        mScheduledExecutorService = Executors.newScheduledThreadPool(Constants.NOTIFICATION_UPDATE_THREAD_SIZE);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mUsageStatsManager =  (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        builder = new NetworkRequest.Builder();

        MinukuDataRecordDAO = appDatabase.getDatabase(this).minukuDataRecordDAO();
        NotificationEventRecordDAO = appDatabase.getDatabase(this).notificationEventRecordDAO();
        NotificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();
        ImageDataRecordDAO = appDatabase.getDatabase(this).imageDataRecordDAO();
        AnswerDataRecordDAO = appDatabase.getDatabase(this).answerDataRecordDAO();
        noteDataRecordDAO = appDatabase.getDatabase(this).noteDataRecordDAO();
        notificationRemoveRecordDAO = appDatabase.getDatabase(this).notificationRemoveRecordDao();
        preferenceDataRecordDAO = appDatabase.getDatabase(this).preferenceDataRecordDAO();
        actionDataRecordDAO = appDatabase.getDatabase(this).actionDataRecordDAO();

        AccessibilityDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).accessibilityDataRecordDao();
        ActivityRecognitionDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).activityRecognitionDataRecordDao();
        AppUsageDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).appUsageDataRecordDao();
        BatteryDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).batteryDataRecordDao();
        ConnectivityDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).connectivityDataRecordDao();
        RingerDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).ringerDataRecordDao();
        TelephonyDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).telephonyDataRecordDao();
        TransportationModeDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).transportationModeDataRecordDao();
        MobileAccessibilityDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).mobileAccessibilityDataRecordDAO();

        intentFilter = new IntentFilter();
        //intentFilter.addAction(CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        //intentFilter.addAction(Constants.ACTION_CONNECTIVITY_CHANGE);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);


//        mWifiReceiver = new WifiReceiver();

        mIntentActionReceiver = new IntentActionReceiver();
        saveFile = getMainDirectoryName();


        initWindow();
        initHandler();
        createImageReader();
//        initMediaProjection();

        accessibilityStreamGenerator = new AccessibilityStreamGenerator(getApplicationContext());
        sharedPrefs.edit().putInt("notifyNotificationID", 1000).apply();

        insertBackgroundServiceStatusPreference("OnCreate");

        remindersIntentManager = RemindersIntentManager.getInstance( this );
        alarmManager = (AlarmManager) getSystemService( Context.ALARM_SERVICE );

        SetPeriodAlarm();

        pm = mContext.getPackageManager();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(saveDataToCSV) {
            CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundServiceRunning ? " + isBackgroundServiceRunning);
            CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundRunnableRunning ? " + isBackgroundRunnableRunning);

            String onStart = "BackGround, start service";
            CSVHelper.storeToCSV(CSVHelper.CSV_ESM, onStart);
            CSVHelper.storeToCSV(CSVHelper.CSV_CAR, onStart);
        }

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        createNotificationChannel(getResources().getString(R.string.survey_channel_name), Constants.SURVEY_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
        createNotificationChannel(getResources().getString(R.string.ongoing_channel_name), Constants.ONGOING_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        createNotificationChannel(getResources().getString(R.string.uploading_channel_name), Constants.UPLOAD_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
        createNotificationChannel(getResources().getString(R.string.uploading_background_channel_name), Constants.UPLOAD_DATA_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);


        //make the WifiReceiver start sending availSite to the server.
//        registerReceiver(mWifiReceiver, intentFilter);
        registerConnectivityNetworkMonitorForAPI21AndUp();

        registerReceiver(mIntentActionReceiver, intentFilter);

        IntentFilter checkRunnableFilter = new IntentFilter(CHECK_RUNNABLE_ACTION);
        registerReceiver(CheckRunnableReceiver, checkRunnableFilter);

        //building the ongoing notification to the foreground
        startForeground(ongoingNotificationID, getOngoingNotification(ongoingNotificationText));
        // add one ongoing by jj
        //startForeground(hintNotificationID, getHintNotification());

        initMediaProjection();

        if (!isBackgroundServiceRunning) {

            Log.i(TAG, "Initialize the Manager");

            isBackgroundServiceRunning = true;

            if(saveDataToCSV)
                CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Going to judge the condition is ? "+(!InstanceManager.isInitialized()));

            if (!InstanceManager.isInitialized()) {

                if(saveDataToCSV)
                    CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Going to start the runnable.");

                InstanceManager.getInstance(this);
                SessionManager.getInstance(this);
                MobilityManager.getInstance(this);

                updateNotificationAndStreamManagerThread();
                insertBackgroundServiceStatusPreference("ScheduledExecutorService");
            }
        }

        // read test file
//        FileHelper fileHelper = FileHelper.getInstance(getApplicationContext());
//        FileHelper.readTestFile();

        return START_REDELIVER_INTENT; //START_STICKY_COMPATIBILITY;
    }



    public void updateNotificationAndStreamManagerThread(){

        /*
        mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(
                updateStreamManagerRunnable,
                30,
                Constants.STREAM_UPDATE_FREQUENCY,
                TimeUnit.SECONDS);
                // original delay:10



        mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(
                updateAccessRunnable,
                30,
                10,
                TimeUnit.MILLISECONDS);

         */
        // original delay:10
        // 上傳FirestoreManager資料的間隔時間
        mScheduledUploadFirestore= mScheduledExecutorService.scheduleAtFixedRate(
                //uploadDataManagerRunnable
                uploadDataToFirestoreManagerRunnable,
                90,
                60, // 300
                TimeUnit.SECONDS);
                // original delay:60, 300



        mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(
                //uploadDataManagerRunnable
                checkUploadResultManagerRunnable,
                100,
                300,
                TimeUnit.SECONDS);
        // original delay:60, 300

        // 每次截圖的的間隔時間
        mScheduledScreenShot = mScheduledExecutorService.scheduleAtFixedRate(
                updateScreenShotRunnable,
                15, //TODO: should be longer:user need to register & login
//                Constants.GET_AVAILABILITY_FROM_SERVER_FREQUENCY, // 2 min
                5, // TODO: 測試時間會跟實際不同
                TimeUnit.SECONDS);
                // original delay:60/10

        mScheduledNotification = mScheduledExecutorService.scheduleAtFixedRate(
                updateNotificationStatusRunnable,
                45, //TODO: should be longer:user need to register & login
//                Constants.GET_AVAILABILITY_FROM_SERVER_FREQUENCY, // 2 min
                300, // TODO: 測試時間會跟實際不同
                TimeUnit.SECONDS);
                // original delay:30 /300

        /*   replace by active get news by crawlNews
        mScheduledNews = mScheduledExecutorService.scheduleAtFixedRate(
                updateNewsRunnable,
                10, //TODO: should be longer:user need to register & login
//                Constants.GET_AVAILABILITY_FROM_SERVER_FREQUENCY, // 2 min
                300, // TODO: 測試時間會跟實際不同
                TimeUnit.SECONDS);
                // original delay:10/ 14400

        */


        mScheduledSyncNotificationList = mScheduledExecutorService.scheduleAtFixedRate(
                syncNotificationQuestionsRunnable,
                0, //TODO: should be longer:user need to register & login
                1, // TODO: 測試時間會跟實際不同
                TimeUnit.HOURS);

        mScheduledUploadEsm = mScheduledExecutorService.scheduleAtFixedRate(
                uploadEsmDataRunnable,
                90,
                60, // 300
                TimeUnit.SECONDS);


        // test
//
        // test

    }
    private void removeRunnable(){
        mScheduledUploadFirestore.cancel(true);
        mScheduledFuture.cancel(true);
        mScheduledScreenShot.cancel(true);
        mScheduledNotification.cancel(true);
        mScheduledUploadEsm.cancel(true);
    }

    public Runnable uploadEsmDataRunnable= new Runnable() {
        @Override
        public void run() {

            String PID = sharedPrefs.getString("ParticipantID", "");
            List<DocumentReference> ans_rf_list = new ArrayList<DocumentReference>();
            List<Map<String, Object>> ans_object_list = new ArrayList<Map<String, Object>>();

            if(Utils.isSavingDataToWhere(sharedPrefs, "not")){
                Log.i(TAG, "It's not saving ESM data.");
            }

            if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs) && !Utils.isSavingDataToWhere(sharedPrefs, "not")) {
                List<AnswerDataRecord> answerDataRecordList = AnswerDataRecordDAO.getByIsUpload(0);
                if (answerDataRecordList.size() > 0) {
                    for (final AnswerDataRecord a : answerDataRecordList) {
                        String dd = a.getFirebasePK().substring(0, 8);

                        String jsonString = a.getJsonString();
                        Map<String, Object> record = null;
                        try {
                            record = new ObjectMapper().readValue(jsonString, HashMap.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final Map<String, Object> recordNew = new HashMap<>();
                        recordNew.put("survey", record);

                        if(Utils.isSavingDataToWhere(sharedPrefs, "local")){
                            try {
                                saveDataToLocal(record, dd, a.getFirebasePK(),"ESM Questionnaires");
                                Log.i(TAG, "ESM answer saves to local success");
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }



                        Log.i(TAG, "uploadEsmDataToFirestoreManager answer: " + record);

                        // Serialize recordNew to a JSON file in the local folder


                        DocumentReference ref = db.collection(PID).document(dd)
                                .collection("ESM").document(a.getFirebasePK());
                        a.setIsUpload(1);
                        ans_rf_list.add(ref);
                        ans_object_list.add(recordNew);
                        AnswerDataRecordDAO.updateOne(a);
                        //ref.update(record);
                    }
                }
            }
            if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                for (int k = 0; k < ans_rf_list.size(); k++) {
                    WriteBatch batch = db.batch();
                    batch.set(ans_rf_list.get(k), ans_object_list.get(k));
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.i(TAG, "ESM answer saves to firebase batch set success");
                            else
                                Log.e(TAG, task.getException().getMessage());
                        }
                    });
                }
            }

        }
    };



    public static void saveDataToLocal(Map<String, Object> record, String currentDate, String currentTime, String childFolderName) throws JsonProcessingException {
        // Serialize the record map to a JSON string
        String jsonString = new ObjectMapper().writeValueAsString(record);

        // Define the folder name and location in external storage
        String folderName = childFolderName +"/"+ currentDate;
        String fileName = currentTime + ".json";
        FileOutputStream fos = null;

        try {
            // Get the external storage directory
            File externalStorageDir = Environment.getExternalStoragePublicDirectory(
                    Constants.PACKAGE_DIRECTORY_PATH);

            // Create a directory for your folder
            File folder = new File(externalStorageDir, folderName);

            // Check if the folder already exists
            if (!folder.exists()) {
                // Create the folder
                if (folder.mkdirs()) {
                    // Folder created successfully
                } else {
                    // Failed to create the folder
                    // Handle the error
                    return;
                }
            }

            // Create a file in the folder
            File file = new File(folder, fileName);

            // Create a FileOutputStream for the file
            fos = new FileOutputStream(file);

            // Write the JSON string to the file
            fos.write(jsonString.getBytes());

            // The file has been saved successfully

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Runnable updateNotificationStatusRunnable = new Runnable() {
        @Override
        public void run() {
//            Log.i(TAG, "!!!!! Update Status !!!!!");

            Log.i(TAG, "updateNotificationStatusRunnable called ");

            android.util.Log.i(TAG, "notifyESM called");
            long lastNotiTime = sharedPrefs.getLong("lastNotiTime", -1);
            long gap = sharedPrefs.getLong("gap", -1);

            android.util.Log.i(TAG, "notifyESM lastNotiTime: " + ScheduleAndSampleManager.getTimeString(lastNotiTime));
            android.util.Log.i(TAG, "notifyESM gap: " + gap);
            if(gap !=-1)
            {
                android.util.Log.i(TAG, "notifyESM NotiTime + gap: " + ScheduleAndSampleManager.getTimeString(lastNotiTime + gap));

            }

            boolean send = false;

            long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
            if (Utils.isActiveTiming(sharedPrefs)) {
                if(gap == -1) //first notice
                {
                    send = true;
                }else {
                    if (checkScreenUnlocked(mContext) && currentTime > (lastNotiTime + gap)) { // at least 1 hr
                        send = true;
                    }
                }
            }else{
                sharedPrefs.edit()
                        .putLong("lastNotiTime", currentTime)
                        .commit();
            }

            if (send) {
//                Utils.notifyESM(mContext, sharedPrefs, debugMode, Utils.createID(), mNotificationManager, handler, NotificationDataRecordDAO, NotificationEventRecordDAO);
                Utils.setIntentionSurvey(mContext, sharedPrefs, mNotificationManager, handler);

            }

            Log.i(TAG, "updateNotificationStatusRunnable(sendNotification) called ");

        }
    };

    private Notification getHintNotification() {


        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        String text = "";
        if(hintCase == 1)
            text = "請記得標記所有圖片並完成上傳和刪除的動作哦！";
        else if(hintCase == 2)
            text = "上傳即將完成！請務必記得執行刪除動作";
        else if(hintCase == 3) {
            List<NotificationDataRecord> questions = NotificationDataRecordDAO.getUnsurveyed();
            String numUnsurveyed = Integer.toString(questions.size());
            text = "有" + numUnsurveyed + "個問卷等待你填寫";
        }

        bigTextStyle.setBigContentTitle("提醒："); //Constants.SURVEY_CHANNEL_NAME
        bigTextStyle.bigText(text);

        Intent resultIntent = new Intent();
        // by jj
        resultIntent.setComponent(new ComponentName(this, SurveyActivity.class));
        //resultIntent.setComponent(new ComponentName(this, MainActivity.class));

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pending = PendingIntent.getActivity(this, reminderNotificationCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(text)
                .setStyle(bigTextStyle)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .setChannelId(Constants.SURVEY_CHANNEL_ID)
                    .build();
        } else {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .build();
        }

    }

    Runnable syncNotificationQuestionsRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "syncNotificationQuestionsRunnable called ");
            if(Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                List<NotificationEventRecord> notificationEventRecordList = NotificationEventRecordDAO.getOne();
                if(notificationEventRecordList.size()>0)
                {
                    Log.i(TAG, "syncNotificationQuestionsRunnable ---  notification data existed  " );
                    long lastUpdateNotiTime = sharedPrefs.getLong("lastUpdateNotificationListTime", -1);
                    if((ScheduleAndSampleManager.getCurrentTimeInMillis() - lastUpdateNotiTime) > 86400000 ) // one day
                    {

                        Log.i(TAG, "syncNotificationQuestionsRunnable ---  disable invalid notification event  ");
                        db.collection("Notification")
                                .whereEqualTo("enable", false)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Log.i(TAG, document.getId() + " => " + document.getData());

                                                List<NotificationEventRecord> aList = NotificationEventRecordDAO.getbyNotificationId(document.getData().get("id").toString());
                                                for (NotificationEventRecord a : aList)
                                                {
                                                    if(!a.getIsUsed()) {
                                                        a.setIsEnable(false);
                                                        NotificationEventRecordDAO.updateOne(a);
                                                    }
                                                }

                                            }
                                        } else {
                                            Log.i(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                        Log.i(TAG, "syncNotificationQuestionsRunnable ---  update or insert valid notification event ");
                        db.collection("Notification")
                                .whereEqualTo("enable", true)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                //Log.i(TAG, document.getId() + " => " + document.getData());

                                                List<NotificationEventRecord> aList = NotificationEventRecordDAO.getbyNotificationId(document.getData().get("id").toString());
                                                if(aList.size() > 0) {
                                                    for (NotificationEventRecord a : aList) {
                                                        if (!a.getIsUsed()) {
                                                            a.setIsEnable(true);
                                                            a.setOptions(document.getData().get("options").toString());
                                                            a.setUrl(document.getData().get("url").toString());
                                                            a.setTitle(document.getData().get("title").toString());
                                                            NotificationEventRecordDAO.updateOne(a);
                                                        }
                                                    }
                                                } else{
                                                    NotificationEventRecord notificationEventRecord = new NotificationEventRecord(document.getData().get("id").toString(),
                                                            document.getData().get("title").toString(),document.getData().get("options").toString(),
                                                            document.getData().get("url").toString(),document.getData().get("type").toString());
                                                    NotificationEventRecordDAO.insertOne(notificationEventRecord);
                                                }

                                            }
                                        } else {
                                            Log.i(TAG, "syncNotificationQuestionsRunnable Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        sharedPrefs.edit()
                                .putLong("lastUpdateNotificationListTime", ScheduleAndSampleManager.getCurrentTimeInMillis())
                                .commit();
                    }

                } else{
                    Log.i(TAG, "syncNotificationQuestionsRunnable ---  No notification data existed  ");
                    db.collection("Notification")
                            .whereEqualTo("enable", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Log.i(TAG, document.getId() + " => " + document.getData());

                                            NotificationEventRecord notificationEventRecord = new NotificationEventRecord(document.getData().get("id").toString(),
                                                    document.getData().get("title").toString(),document.getData().get("options").toString(),
                                                    document.getData().get("url").toString(),document.getData().get("type").toString());
                                            NotificationEventRecordDAO.insertOne(notificationEventRecord);
                                        }
                                        sharedPrefs.edit()
                                                .putLong("lastUpdateNotificationListTime", ScheduleAndSampleManager.getCurrentTimeInMillis())
                                                .commit();
                                        Log.i(TAG, "syncNotificationQuestionsRunnable ---  download notification data success " + NotificationEventRecordDAO.getAll().size());

                                    } else {
                                        Log.i(TAG, "syncNotificationQuestionsRunnable Error getting documents: ", task.getException());
                                    }
                                }
                            });

                    //to do list !!!!!!!!
                    sharedPrefs.edit()
                            .putInt("countAd", 0)
                            .putInt("countCrowdsourcing", 0)
                            .putInt("countNews", 0)
                            .putInt("countQuestionnaire", 0)
                            .commit();

                }
                // get Participant_ID
                if(sharedPrefs.getString("ParticipantID", "").isEmpty())
                {
                    if(Constants.DEVICE_ID.equalsIgnoreCase("NA"))
                        getDeviceid();
                    if(Constants.DEVICE_ID.equalsIgnoreCase("NA"))
                    {
                        Log.i(TAG, "No Device ID , skip syncNotificationQuestionsRunnable - get participant id " );
                    }else{
                        final DocumentReference UserRef = db.collection("Users").document("Overview");
                        final DocumentReference DeviceRef = db.collection("Users").document(Constants.DEVICE_ID);
                        db.runTransaction(new Transaction.Function<String>() {
                            @Override
                            public String apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(DeviceRef);
                                if(snapshot.exists())
                                {
                                    String id = snapshot.getString("id");
                                    Double retry = snapshot.getDouble("retry");
                                    retry =  retry +1;
                                    transaction.update(DeviceRef, "retry", retry);
                                    transaction.update(DeviceRef, "timestamp", FieldValue.serverTimestamp());
                                    return id;

                                }else{
                                    DocumentSnapshot snapshot1 = transaction.get(UserRef);
                                    Double num = snapshot1.getDouble("Number");
                                    String id = "";
                                    num = num + 1;
                                    int value = num.intValue();
                                    if(value<10)
                                    {
                                        id =  "P0" + value;
                                    }else{
                                        id = "P" + value;
                                    }
                                    transaction.update(UserRef, "Number", num);
                                    final Map<String, Object> record = new HashMap<>();
                                    record.put("id" , id);
                                    record.put("retry", 0);
                                    record.put("timestamp", FieldValue.serverTimestamp());
                                    transaction.set(DeviceRef, record);
                                    return id;
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.i(TAG, "Transaction success: " + result);
                                sharedPrefs.edit()
                                        .putString("ParticipantID", result)
                                        .commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                            }
                        });

                        insertPreference();
                    }
                }
            } else {
                Log.i(TAG, "No Network, skip syncNotificationQuestionsRunnable ");
            }

            Log.i(TAG, "syncNotificationQuestionsRunnable end ");

        }
    };

    Runnable updateScreenShotRunnable = new Runnable() {
        @Override
        public void run() {
            startScreenShot();
            // update ongoing 問卷個數提醒 by jj (暫時寫在這，每10秒更新)
            //mNotificationManager.cancel(hintNotificationID);
            //startForeground(hintNotificationID, getHintNotification());
        }
    };

    Runnable updateAccessRunnable = new Runnable() {
        @Override
        public void run() {

            accessibilityStreamGenerator.updateStream();
        }
    };
    /*
    Runnable updateStreamManagerRunnable = new Runnable() {
        @Override
        public void run() {

            Calendar current = Calendar.getInstance();
            int currentHourIn24Format = current.get(Calendar.HOUR_OF_DAY);
            int currentMinute = current.get(Calendar.MINUTE);

            if ((currentHourIn24Format >= 10 && currentHourIn24Format < 22)) {

                try {

                    if(saveDataToCSV) {
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundServiceRunning ? " + isBackgroundServiceRunning);
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "isBackgroundRunnableRunning ? " + isBackgroundRunnableRunning);
                    }

                    Log.i(TAG, "updateStreamManager called");
                    streamManager.updateStreamGenerators();

                    MinukuDataRecord minukuDataRecord = new MinukuDataRecord(new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(new Date(System.currentTimeMillis())).replaceAll("\\D+", ""),
                            System.currentTimeMillis(), AccessibilityStreamGenerator.getId(), ActivityRecognitionStreamGenerator.getId(), AppUsageStreamGenerator.getId(),
                            BatteryStreamGenerator.getId(), ConnectivityStreamGenerator.getId(), RingerStreamGenerator.getId(), TelephonyStreamGenerator.getId(),
                            TransportationModeStreamGenerator.getId(),0, "");
                    MinukuDataRecordDAO.insertOne(minukuDataRecord);

                    if(debugMode) {
                        List<MinukuDataRecord> minukuDataRecordList = MinukuDataRecordDAO.getAll();
                        Log.e(TAG, "--------updateStreamManager----start------");
                        for (MinukuDataRecord a : minukuDataRecordList) {
                            Log.e(TAG, "id in db: " + a.get_id() );
                            Log.e(TAG, "isUpload in db: " + a.isUpload);
                            Log.e(TAG, "AccessibilityKey in db: " + a.AccessibilityKey);
                            Log.e(TAG, "ActivityRecognitionKey in db: " + a.ActivityRecognitionKey);
                            Log.e(TAG, "AppUsageKey in db: " + a.AppUsageKey);
                            Log.e(TAG, "BatteryKey in db: " + a.BatteryKey);
                            Log.e(TAG, "ConnectivityKey in db: " + a.ConnectivityKey);
                            Log.e(TAG, "RingerKey in db: " + a.RingerKey);
                            Log.e(TAG, "TelephonyKey in db: " + a.TelephonyKey);
                            Log.e(TAG, "TransportationModeKey in db: " + a.TransportationModeKey);
                        }
                        Log.e(TAG, "--------updateStreamManager----end------");

                    }
                } catch (Exception e) {

                    if (saveDataToCSV)
                    {
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "Background, service update, stream, Exception");
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, Utils.getStackTrace(e));
                    }
                    Log.i(TAG, "updateStreamManager error: " + e.getMessage()) ;
                    //e.printStackTrace();
                }

                Log.i(TAG, "DB connected (updateStream)");
            }
        }
    };

    */

    Runnable uploadDataToFirestoreManagerRunnable = new Runnable() {
        @Override
        public void run() {
            if(Utils.isSavingDataToWhere(sharedPrefs, "not")){
                Log.i(TAG, "It's not saving real-time data.");
            }

            if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs) && !Utils.isSavingDataToWhere(sharedPrefs, "not")) {

                try {
                    String PID = sharedPrefs.getString("ParticipantID", "");
                    String today = Utils.getUploadDay();
                    List<DocumentReference> doc_rf_list = new ArrayList<DocumentReference>();
                    List<Map<String, Object>> object_list = new ArrayList<Map<String, Object>>();
                    List<DocumentReference> ans_rf_list = new ArrayList<DocumentReference>();
                    List<Map<String, Object>> ans_object_list = new ArrayList<Map<String, Object>>();

                    int update_count_minuku = 0;


                    if (!PID.isEmpty()) {

                        List<MinukuDataRecord> minukuDataRecordList = MinukuDataRecordDAO.getByIsUpload(0, 0);
                        if (!pushToRealtimeDataBase_Minuku) {
                            Log.i(TAG, "batchuploadDataToFirestoreManager " + minukuDataRecordList.size() + " new minuku data need to upload. ");
                        } else {
                            Log.i(TAG, "UploadDataToRealtimeDataBaseManager " + minukuDataRecordList.size() + " new minuku data need to upload. ");
                        }
                        if (minukuDataRecordList.size() > 0) {
                            //Log.e(TAG, "--------uploadDataToFirestoreManager----start------");
                            for (final MinukuDataRecord a : minukuDataRecordList) {
                                if (!pushToRealtimeDataBase_Minuku) {
                                    String dd = a.getTimeString().substring(0, 8);
                                    DocumentReference ref = db.collection(PID).document(dd)
                                            .collection("MinukuData")
                                            .document(PID + "_" + a.getImageFileName());
                                    doc_rf_list.add(ref);
                                    object_list.add(Utils.getMinukuUploadMap(AccessibilityDataRecordDAO, ActivityRecognitionDataRecordDAO, AppUsageDataRecordDAO, BatteryDataRecordDAO, ConnectivityDataRecordDAO, RingerDataRecordDAO, TelephonyDataRecordDAO, TransportationModeDataRecordDAO, a, true));

                                    a.setIsUploading(1);
                                    MinukuDataRecordDAO.updateOne(a);
                                } else {
                                    if (!Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                        break;
                                    } else {
                                        Map<String, Object> minukuMap =  Utils.getMinukuUploadMap(AccessibilityDataRecordDAO, ActivityRecognitionDataRecordDAO, AppUsageDataRecordDAO, BatteryDataRecordDAO, ConnectivityDataRecordDAO, RingerDataRecordDAO, TelephonyDataRecordDAO, TransportationModeDataRecordDAO, a, false);
                                        if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                            saveDataToFirebase(database,"Logs", today, PID, "MinukuData", PID + "_" + a.getImageFileName(), minukuMap);
                                        }
                                        else{
                                            saveDataToLocal(minukuMap, today, a.getImageFileName(), "RealTime Data/MinukuData");
                                        }
                                        /*
                                        database.getReference("Logs")
                                                .child(today)
                                                .child(PID)
                                                .child("MinukuData")
                                                .child(PID + "_" + a.getImageFileName())
                                                .setValue(minukuMap)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Write failed
                                                        Log.e(TAG, "upload Minuku Fail " + e.getMessage());
                                                    }
                                                });
                                        */
                                        MinukuDataRecordDAO.deleteOne(a);
                                        update_count_minuku ++;
                                        if(update_count_minuku > MAX_UPLOAD_NUM_MINUKU)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // Notificagtion Data
                        // Upload new Notification Data


                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<NotificationDataRecord> noticeList = NotificationDataRecordDAO.getByIsUpload(0, 0);
                            Log.i(TAG, "uploadDataToFirestoreManager " + noticeList.size() + " new notification data need to upload. ");
                            if (noticeList.size() > 0) {
                                for (final NotificationDataRecord a : noticeList) {
                                    String dd = a.getFirebasePK().substring(0, 8);

                                    //Log.i(TAG, "uploadDataToFirestoreManager dd " + dd );
                                    final Map<String, Object> record = new HashMap<>();
                                    record.put("timestamp", FieldValue.serverTimestamp());
                                    record.put("title", a.getTitle());
                                    record.put("type", a.getType());
                                    record.put("url", a.getUrl());
                                    record.put("noticeTimeString", a.createdTimeString);
                                    record.put("noticeTime", a.getCreatedTime());
                                    //record.put("responseTimeString", a.responsedTimeString);
                                    //record.put("responseTime", a.responsedTime);
                                    record.put("response", a.response);
                                    record.put("responseCode", a.responseCode);
                                    record.put("comment", a.getComment());
                                    record.put("isExpired", a.getIsExpired());

                                    // add paused and resumed by jj
                                    record.put("clickedTimeString", a.getClickedTimeString());
                                    record.put("pausedTimeString", Arrays.asList(a.getPausedTimeString().split(",")));
                                    record.put("resumedTimeString", Arrays.asList(a.getResumedTimeString().split(",")));
                                    record.put("submitTimeString", a.getSubmitTimeString());
                                    record.put("destroyTimeString", a.getDestroyTimeString());
                                    record.put("removeTimeString", a.getRemoveTimeString());
                                    record.put("removeTime", a.removeTime);
                                    //TODO : set ID by ourselves
                                    DocumentReference ref = db.collection(PID).document(dd)
                                            .collection("Notification").document(a.getFirebasePK());
                                    doc_rf_list.add(ref);
                                    object_list.add(record);


                                    a.setIsUploading(1);
                                    a.setIsUpdated(0);
                                    NotificationDataRecordDAO.updateOne(a);

                    /*
                    // Add a new document with a generated ID
                    db.collection(Constants.DEVICE_ID).document(dd)
                            .collection("Notification")
                            .add(record)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i(TAG, "Notification uploadDataToFirestoreManager DocumentSnapshot added with ID: " + documentReference.getId());
                                    a.setIsUpload(1);
                                    a.setIsUpdated(0);
                                    a.setFirebasePK(documentReference.getId());
                                    try {
                                        NotificationDataRecordDAO.updateOne(a);
                                    } catch (Exception e) {

                                        Log.e(TAG, "uploadDataToFirestoreManager update room db error: " + e.getMessage());
                                        //e.printStackTrace();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Log.e(TAG, "upload Fail : " + a.get_id());
                                }
                            });
                     */
                                }

                            }
                        }

                        // Upload updated Notification Data
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {

                            List<NotificationDataRecord> updatenoticeList = NotificationDataRecordDAO.getByIsUpdated(1, 1, 0);
                            Log.i(TAG, "uploadDataToFirestoreManager " + updatenoticeList.size() + " updated notification data need to update. ");
                            if (updatenoticeList.size() > 0) {
                                for (final NotificationDataRecord a : updatenoticeList) {
                                    String dd = a.getFirebasePK().substring(0, 8);

                                    final Map<String, Object> record = new HashMap<>();
                                    record.put("timestamp", FieldValue.serverTimestamp());
                                    record.put("title", a.getTitle());
                                    record.put("type", a.getType());
                                    record.put("url", a.getUrl());
                                    record.put("noticeTimeString", a.createdTimeString);
                                    record.put("noticeTime", a.getCreatedTime());
                                    //record.put("responseTimeString", a.responsedTimeString);
                                    //record.put("responseTime", a.responsedTime);
                                    record.put("response", a.response);
                                    record.put("responseCode", a.responseCode);
                                    record.put("comment", a.getComment());
                                    record.put("isExpired", a.getIsExpired());

                                    // add paused and resumed by jj
                                    record.put("clickedTimeString", a.getClickedTimeString());
                                    record.put("pausedTimeString", Arrays.asList(a.getPausedTimeString().split(",")));
                                    record.put("resumedTimeString", Arrays.asList(a.getResumedTimeString().split(",")));
                                    record.put("submitTimeString", a.getSubmitTimeString());
                                    record.put("destroyTimeString", a.getDestroyTimeString());
                                    record.put("removeTimeString", a.getRemoveTimeString());
                                    record.put("removeTime", a.removeTime);
                                    Log.i(TAG, "uploadDataToFirestoreManager update id: " + a.getFirebasePK());

                                    DocumentReference ref = db.collection(PID).document(dd)
                                            .collection("Notification").document(a.getFirebasePK());
                                    doc_rf_list.add(ref);
                                    object_list.add(record);
                                    a.setIsUploading(1);
                                    NotificationDataRecordDAO.updateOne(a);

                    /*
                    // Add a new document with a generated ID
                    db.collection(Constants.DEVICE_ID).document(dd)
                            .collection("Notification")
                            .document(a.getFirebasePK())
                            .set(record)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "uploadDataToFirestoreManager DocumentSnapshot successfully written!");
                                    a.setIsUpload(1);
                                    a.setIsUpdated(0);
                                    NotificationDataRecordDAO.updateOne(a);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "uploadDataToFirestoreManager  Error writing document", e);
                                    Log.e(TAG, "uploadDataToFirestoreManager upload Fail : " + a.get_id());
                                }
                            });
                     */
                                }
                            }
                        }

                        // upload answer data by jj
                        /*if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<AnswerDataRecord> answerDataRecordList = AnswerDataRecordDAO.getByIsUpload(0);
                            if (answerDataRecordList.size() > 0) {
                                for (final AnswerDataRecord a : answerDataRecordList) {
                                    String dd = a.getFirebasePK().substring(0, 8);

                                    String jsonString = a.getJsonString();
                                    final Map<String, Object> record = new ObjectMapper().readValue(jsonString, HashMap.class);
                                    final Map<String, Object> recordNew = new HashMap<>();
                                    recordNew.put("survey", record);

                                    Log.i(TAG, "uploadDataToFirestoreManager answer: " + record);

                                    DocumentReference ref = db.collection(PID).document(dd)
                                            .collection("ESM").document(a.getFirebasePK());
                                    a.setIsUpload(1);
                                    ans_rf_list.add(ref);
                                    ans_object_list.add(recordNew);
                                    AnswerDataRecordDAO.updateOne(a);
                                    //ref.update(record);
                                }
                            }
                        }

                        */
                        int update_count_noti_rm = 0;
                        // Notification Remove Data
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<NotificationRemoveRecord> notificationRemoveList = notificationRemoveRecordDAO.getByIsUpload(0);
                            if (!pushToRealtimeDataBase_NotiRemove) {
                                Log.i(TAG, "batchuploadDataToFirestoreManager " + notificationRemoveList.size() + " new notification remove data need to upload. ");
                            } else {
                                Log.i(TAG, "UploadDataToRealtimeDataBaseManager " + notificationRemoveList.size() + " new notification remove data need to upload. ");
                            }
                            if (notificationRemoveList.size() > 0) {
                                for (final NotificationRemoveRecord a : notificationRemoveList) {

                                    if (!pushToRealtimeDataBase_NotiRemove) {
                                        String dd = a.firebasePK.substring(0, 8);

                                        //TODO : set ID by ourselves
                                        DocumentReference ref = db.collection(PID).document(dd)
                                                .collection("NotificationRemove").document(a.firebasePK);
                                        doc_rf_list.add(ref);
                                        object_list.add(Utils.getNotiRemoveUploadMap(a));
                                    } else {
                                        if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                            saveDataToFirebase(database,"Logs", today, PID, "NotificationRemove", a.firebasePK, Utils.getNotiRemoveUploadMap(a));
                                        }
                                        else{
                                            saveDataToLocal(Utils.getNotiRemoveUploadMap(a), today, a.firebasePK, "RealTime Data/NotificationRemove");
                                        }
                                        /*
                                        database.getReference("Logs")
                                                .child(today)
                                                .child(PID)
                                                .child("NotificationRemove")
                                                .child(a.firebasePK)
                                                .setValue(Utils.getNotiRemoveUploadMap(a))
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Write failed
                                                        Log.e(TAG, "upload NotificationRemove Fail " + e.getMessage());
                                                    }
                                                });
                                         */

                                        update_count_noti_rm ++;
                                        if(update_count_noti_rm > MAX_UPLOAD_NUM_NOTI_RM)
                                        {
                                            break;
                                        }
                                    }

                                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                        Log.d("NotiRemove", "1");

                                        // if remove Boredom's Notification, update Notification
                                        if ((a.appName.equals(getResources().getString(R.string.app_name))) &&
                                                (a.title.equals("News") || a.title.equals("Advertisement") ||
                                                        a.title.equals("Questionnaire") || a.title.equals("Crowdsourcing"))) {
                                            List<NotificationDataRecord> notificationDataRecordList = NotificationDataRecordDAO.getByNotifyNotificationID(a.notifyNotificationID);
                                            NotificationDataRecord b = notificationDataRecordList.get(0);
                                            String bb = b.getFirebasePK().substring(0, 8);
                                            final Map<String, Object> record2 = new HashMap<>();
                                            record2.put("removeTime", a.removeTime);
                                            DocumentReference ref2 = db.collection(PID).document(bb)
                                                    .collection("Notification").document(b.getFirebasePK());
                                            ans_rf_list.add(ref2);
                                            ans_object_list.add(record2);
                                            Log.d("NotiRemove", "2");
                                        }

                                        notificationRemoveRecordDAO.deleteOne(a);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        int update_count_acc = 0;
                        // Mobile Accessibility Data
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<MobileAccessibilityDataRecord> accessibilityList = MobileAccessibilityDataRecordDAO.getByIsUpload(0);
                            if (!pushToRealtimeDataBase_Accessibility) {
                                Log.i(TAG, "batchuploadDataToFirestoreManager " + accessibilityList.size() + " new Accessibility data need to upload. ");
                            } else {
                                Log.i(TAG, "UploadDataToRealtimeDataBaseManager " + accessibilityList.size() + " new Accessibility data need to upload. ");
                            }
                            if (accessibilityList.size() > 0) {
                                for (final MobileAccessibilityDataRecord a : accessibilityList) {
                                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                        if (!pushToRealtimeDataBase_Accessibility) {
                                            String dd = a.firebasePK.substring(0, 8);

                                            DocumentReference ref = db.collection(PID).document(dd)
                                                    .collection("Accessibility").document(a.firebasePK);
                                            doc_rf_list.add(ref);
                                            object_list.add(Utils.getAccessibilityUploadMap(a));
                                        } else {
                                            if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                                saveDataToFirebase(database,"Logs", today, PID, "Accessibility", a.firebasePK, Utils.getAccessibilityUploadMap(a));
                                            }else{
                                                saveDataToLocal(Utils.getAccessibilityUploadMap(a), today, a.firebasePK, "RealTime Data/Accessibility");

                                            }
                                            /*database.getReference("Logs")
                                                    .child(today)
                                                    .child(PID)
                                                    .child("Accessibility")
                                                    .child(a.firebasePK)
                                                    .setValue(Utils.getAccessibilityUploadMap(a))
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Write failed
                                                            Log.e(TAG, "upload Accessibility Fail " + e.getMessage());
                                                        }
                                                    });

                                             */
                                            update_count_acc ++;
                                            if(update_count_acc > MAX_UPLOAD_NUM_ACC)
                                            {
                                                break;
                                            }
                                        }
                                        MobileAccessibilityDataRecordDAO.deleteOne(a);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        int update_count_intent = 0;
                        // Intent Action Data
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<ActionDataRecord> actionList= actionDataRecordDAO.getAll();
                            Log.i(TAG, "UploadDataToRealtimeDataBaseManager " + actionList.size() + " new Action data need to upload. ");

                            if (actionList.size() > 0) {
                                for (final ActionDataRecord a : actionList) {
                                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                        if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                            saveDataToFirebase(database,"Logs", today, PID, "IntentAction", a.getFirebasePK(), Utils.getIntentActionUploadMap(a));
                                        }else{
                                            saveDataToLocal(Utils.getIntentActionUploadMap(a), today, a.getFirebasePK(), "RealTime Data/IntentAction");
                                        }

                                        /*
                                        database.getReference("Logs")
                                                .child(today)
                                                .child(PID)
                                                .child("IntentAction")
                                                .child(a.getFirebasePK())
                                                .setValue(Utils.getIntentActionUploadMap(a))
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Write failed
                                                        Log.e(TAG, "upload Action Fail " + e.getMessage());
                                                    }
                                                });

                                         */
                                        actionDataRecordDAO.deleteOne(a);
                                        update_count_intent ++;
                                        if(update_count_intent > MAX_UPLOAD_NUM_INTENT)
                                        {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }

                                }
                            }
                        }

                        int update_count_pref = 0;
                        // Preference Data
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            List<PreferenceDataRecord> pList = preferenceDataRecordDAO.getByIsUpload(0);
                            Log.i(TAG, "uploadDataToFirestoreManager " + pList.size() + " new Preference data need to upload. ");
                            if (pList.size() > 0) {
                                for (final PreferenceDataRecord a : pList) {
                                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                        if (a.getType() == 1 || a.getType() == 2 || a.getType() == 5 || a.getType() == 6) {

                                            if (a.getType() == 1) {
                                                String jsonString = a.getJsonString();
                                                final Map<String, Object> record = new ObjectMapper().readValue(jsonString, HashMap.class);
                                                record.put("settingTime", a.getCreateTime());
                                                DocumentReference ref = db.collection(PID).document("Preference")
                                                        .collection("NewsType").document(a.getFirebasePK());
                                                doc_rf_list.add(ref);
                                                object_list.add(record);
                                            } else if (a.getType() == 6) {
                                                String jsonString = a.getJsonString();
                                                final Map<String, Object> record = new ObjectMapper().readValue(jsonString, HashMap.class);
                                                record.put("settingTime", a.getCreateTime());
                                                DocumentReference ref = db.collection(PID).document("Preference")
                                                        .collection("Network").document(a.getFirebasePK());
                                                doc_rf_list.add(ref);
                                                object_list.add(record);
                                            } else if (a.getType() == 2) {
                                                String jsonString = a.getJsonString();
                                                final Map<String, Object> record = new ObjectMapper().readValue(jsonString, HashMap.class);
                                                record.put("settingTime", a.getCreateTime());
                                                DocumentReference ref = db.collection(PID).document("Preference")
                                                        .collection("RecordingTime").document(a.getFirebasePK());
                                                doc_rf_list.add(ref);
                                                object_list.add(record);
                                            } else if (a.getType() == 5) { // ServiceStatus
                                                String jsonString = a.getJsonString();
                                                String dd = a.getFirebasePK().substring(0, 8);
                                                final Map<String, Object> record = new ObjectMapper().readValue(jsonString, HashMap.class);
                                                if (!pushToRealtimeDataBase_ServiceStatus) {
                                                    DocumentReference ref = db.collection(PID).document(dd)
                                                            .collection("ServiceStatus").document(a.getFirebasePK());
                                                    doc_rf_list.add(ref);
                                                    object_list.add(record);
                                                } else {
                                                    if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                                        saveDataToFirebase(database,"Logs", today, PID, "ServiceStatus", a.getFirebasePK(), record);
                                                    }else {
                                                        saveDataToLocal(record, today, a.getFirebasePK(), "RealTime Data/ServiceStatus");
                                                    }
                                                    /*
                                                    database.getReference("Logs")
                                                            .child(today)
                                                            .child(PID)
                                                            .child("ServiceStatus")
                                                            .child(a.getFirebasePK())
                                                            .setValue(record)
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Write failed
                                                                    Log.e(TAG, "upload Accessibility Fail " + e.getMessage());
                                                                }
                                                            });

                                                     */
                                                }

                                            }

                                            //a.isUpload = 1;
                                            //MobileAccessibilityDataRecordDAO.updateOne(a);
                                            preferenceDataRecordDAO.deleteOne(a);
                                        } else if (a.getType() == 3 || a.getType() == 4) { // image data record
                                            String jsonString = a.getJsonString();
                                            ImageDataRecord imgDataRecord = new Gson().fromJson(jsonString, ImageDataRecord.class);
                                            imgDataRecord.setFileName(imgDataRecord.getFileName().replace(".jpg", ""));
                                            if (a.getType() == 3) {
                                                retryUploadImage(imgDataRecord);
                                            } else {
                                                if (!pushToRealtimeDataBase_Image) {
                                                    Log.d(TAG, "uploadDataToFirestoreManager " + imgDataRecord.getFileName() + " private Image data need to upload. ");
                                                    String dd = imgDataRecord.getFileName().substring(0, 8);
                                                    DocumentReference ref = db.collection(PID).document(dd)
                                                            .collection("ImageRecord")
                                                            .document(PID + "_" + imgDataRecord.getFileName());
                                                    doc_rf_list.add(ref);
                                                    object_list.add(Utils.getImageHashMap(sharedPrefs, imgDataRecord, true));
                                                } else {
                                                    Log.d(TAG, "UploadDataToRealtimeDataBaseManager " + imgDataRecord.getFileName() + " private Image data need to upload. ");
                                                    if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                                                        saveDataToFirebase(database, "Logs", today, PID, "ImageRecord", PID + "_" + imgDataRecord.getFileName(), Utils.getImageHashMap(sharedPrefs, imgDataRecord, false));
                                                    }else {
                                                        saveDataToLocal(Utils.getImageHashMap(sharedPrefs, imgDataRecord, false), today, a.getFirebasePK(), "RealTime Data/ImageRecord");
                                                    }
                                                    /*
                                                    database.getReference("Logs")
                                                            .child(today)
                                                            .child(PID)
                                                            .child("ImageRecord")
                                                            .child(PID + "_" + imgDataRecord.getFileName())
                                                            .setValue(Utils.getImageHashMap(sharedPrefs, imgDataRecord, false))
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Write failed
                                                                    Log.e(TAG, "upload Image Fail " + e.getMessage());
                                                                }
                                                            });

                                                     */

                                                }
                                            }
                                            preferenceDataRecordDAO.deleteOne(a);
                                            update_count_pref ++;
                                            if(update_count_pref > MAX_UPLOAD_NUM_IMAGE)
                                            {
                                                break;
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        int bound = 200;
                        for (int i = 0; i < doc_rf_list.size(); i = i + bound) {
                            // Get a new write batch
                            WriteBatch batch = db.batch();
                            int tmp = ((doc_rf_list.size() - i) < bound) ? (doc_rf_list.size() - i) : bound;
                            Log.i(TAG, "batch run operators # = " + tmp);
                            for (int j = 0; j < tmp; j++) {
                                batch.set(doc_rf_list.get(i + j), object_list.get(i + j));
                            }
//                            for (int k = 0; k < ans_rf_list.size(); k++) {
////                                batch.update(ans_rf_list.get(k), ans_object_list.get(k));
//                                batch.set(ans_rf_list.get(k), ans_object_list.get(k));
//                            }

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.i(TAG, "batch set success");
                                    else
                                        Log.e(TAG, task.getException().getMessage());
                                }
                            });
                        }
                        /*for (int k = 0; k < ans_rf_list.size(); k++) {
                            WriteBatch batch = db.batch();
                            batch.set(ans_rf_list.get(k), ans_object_list.get(k));
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.i(TAG, "answer batch set success");
                                    else
                                        Log.e(TAG, task.getException().getMessage());
                                }
                            });
                        }
                        */
                    } else {
                        Log.i(TAG, " No Participant ID , skip uploadDataToFirestoreManager - minuku ");
                    }
                } catch (Exception e) {
                    Log.i(TAG, "uploadDataToFirestoreManager error: " + e.getMessage());
                    //e.printStackTrace();
                }

                Log.i(TAG, "DB connected (uploadDataToFirestoreManager)");
            } else {
                Log.i(TAG, "no network, skip upload data (uploadDataToFirestoreManager)");
            }
        }

    };

    public static void saveDataToFirebase(FirebaseDatabase database, String databaseReference, String today, String pid, final String dataName, String dataFileName, Map<String, Object> dataMap) {
        database.getReference(databaseReference)
                .child(today)
                .child(pid)
                .child(dataName)
                .child(dataFileName)
                .setValue(dataMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.e(TAG, "upload Fail: " + dataName + e.getMessage());
                    }
                });
    }

    Runnable checkUploadResultManagerRunnable = new Runnable() {
        @Override
        public void run() {

            Calendar current = Calendar.getInstance();
            int currentHourIn24Format = current.get(Calendar.HOUR_OF_DAY);

            if(Utils.isNetworkAvailableforUpload(mContext, sharedPrefs) && !Utils.isSavingDataToWhere(sharedPrefs, "not")) {
                try {
                    /*
                    List<MinukuDataRecord> minukuDataRecordList = MinukuDataRecordDAO.getByIsUploading(1);

                    Log.i(TAG, "checkUploadResultManagerRunnable - minukuDataRecordList " + minukuDataRecordList.size());
                    if (minukuDataRecordList.size() > 0) {
                        for (final MinukuDataRecord m : minukuDataRecordList) {
                            String dd = m.getTimeString().substring(0, 8);

                            String PID = sharedPrefs.getString("ParticipantID", "");
                            DocumentReference ref = db.collection(PID).document(dd)
                                    .collection("MinukuData")
                                    .document(PID + "_"+ m.getImageFileName());
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        String PID = sharedPrefs.getString("ParticipantID", "");
                                        List<MinukuDataRecord> resultList = MinukuDataRecordDAO.getByImageFileName(document.getId().replace(PID+"_",""));
                                        if(resultList.size()>0) {
                                            MinukuDataRecord minuku = resultList.get(0);
                                            if (document.exists()) {

                                                //Log.i(TAG, "DocumentSnapshot data: " + document.getId());
                                                MinukuDataRecordDAO.deleteOne(minuku);

                                                List<AccessibilityDataRecord> accessibilityList = AccessibilityDataRecordDAO.getById(minuku.AccessibilityKey);
                                                if (accessibilityList.size() > 0) {
                                                    AccessibilityDataRecordDAO.deleteOne(accessibilityList.get(0));
                                                } else {
                                                    Log.e(TAG, "AccessibilityDataRecord " + minuku.AccessibilityKey + " is not found");
                                                }


                                                List<ActivityRecognitionDataRecord> activityrecognitionList = ActivityRecognitionDataRecordDAO.getById(minuku.ActivityRecognitionKey);
                                                if (activityrecognitionList.size() > 0) {
                                                    ActivityRecognitionDataRecordDAO.deleteOne(activityrecognitionList.get(0));
                                                } else {
                                                    Log.e(TAG, "ActivityRecognitionDataRecord " + minuku.ActivityRecognitionKey + " is not found");
                                                }


                                                List<AppUsageDataRecord> appusageList = AppUsageDataRecordDAO.getById(minuku.AppUsageKey);
                                                if (appusageList.size() > 0) {
                                                    AppUsageDataRecordDAO.deleteOne(appusageList.get(0));

                                                } else {
                                                    Log.e(TAG, "AppUsageDataRecord " + minuku.AppUsageKey + " is not found");
                                                }


                                                List<BatteryDataRecord> batteryList = BatteryDataRecordDAO.getById(minuku.BatteryKey);
                                                if (batteryList.size() > 0) {
                                                    BatteryDataRecordDAO.deleteOne(batteryList.get(0));
                                                } else {
                                                    Log.e(TAG, "BatteryDataRecord " + minuku.BatteryKey + " is not found");
                                                }


                                                List<ConnectivityDataRecord> connectivityList = ConnectivityDataRecordDAO.getById(minuku.ConnectivityKey);
                                                if (connectivityList.size() > 0) {
                                                    ConnectivityDataRecordDAO.deleteOne(connectivityList.get(0));
                                                } else {
                                                    Log.e(TAG, "ConnectivityDataRecord " + minuku.ConnectivityKey + " is not found");
                                                }


                                                List<RingerDataRecord> ringerList = RingerDataRecordDAO.getById(minuku.RingerKey);
                                                if (ringerList.size() > 0) {
                                                    RingerDataRecordDAO.deleteOne(ringerList.get(0));
                                                } else {
                                                    Log.e(TAG, "RingerDataRecord " + minuku.RingerKey + " is not found");
                                                }


                                                List<TelephonyDataRecord> telephonyList = TelephonyDataRecordDAO.getById(minuku.TelephonyKey);
                                                if (telephonyList.size() > 0) {
                                                    TelephonyDataRecordDAO.deleteOne(telephonyList.get(0));
                                                } else {
                                                    Log.e(TAG, "TelephonyDataRecord " + minuku.TelephonyKey + " is not found");
                                                }


                                                List<TransportationModeDataRecord> transportationmodeList = TransportationModeDataRecordDAO.getById(minuku.TransportationModeKey);
                                                if (transportationmodeList.size() > 0) {
                                                    TransportationModeDataRecordDAO.deleteOne(transportationmodeList.get(0));
                                                } else {
                                                    Log.e(TAG, "TransportationModeDataRecord " + minuku.TransportationModeKey + " is not found");

                                                }


                                            } else {
                                                minuku.setIsUploading(0);
                                                MinukuDataRecordDAO.updateOne(minuku);
                                                Log.i(TAG, "No such document: " + document.getId());
                                            }
                                        }
                                    } else {
                                        Log.i(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }

                    }
                    */

                    // Check Notification Data Record

                    List<NotificationDataRecord> notiDataRecordList = NotificationDataRecordDAO.getByIsUploading(1);

                    Log.i(TAG, "checkUploadResultManagerRunnable - notiDataRecordList " + notiDataRecordList.size());
                    if (notiDataRecordList.size() > 0) {
                        for (final NotificationDataRecord n : notiDataRecordList) {
                            String dd = n.getFirebasePK().substring(0, 8);

                            String PID = sharedPrefs.getString("ParticipantID", "");
                            DocumentReference ref =  db.collection(PID).document(dd)
                                    .collection("Notification").document(n.getFirebasePK());
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        List<NotificationDataRecord> resultList = NotificationDataRecordDAO.getByfirebasePK(document.getId());
                                        if(resultList.size()>0) {
                                            NotificationDataRecord noti = resultList.get(0);
                                            if (document.exists()) {
                                                //Log.i(TAG, "DocumentSnapshot data: " + document.getId());
                                                noti.setIsUploading(0);

                                                if(noti.getIsUpload()==0)
                                                {
                                                    noti.setIsUpload(1);
                                                }
                                                else{
                                                    //Log.i(TAG, "checkUploadResultManagerRunnable - test");
                                                    Map<String, Object> record =  document.getData();
                                                    if (record.get("noticeTimeString").toString().equalsIgnoreCase(noti.getCreatedTimeString())
                                                            && record.get("submitTimeString").toString().equalsIgnoreCase(noti.getSubmitTimeString())
                                                            && record.get("clickedTimeString").toString().equalsIgnoreCase(noti.getClickedTimeString())
                                                            && record.get("response").toString().equalsIgnoreCase(noti.getResponse())
                                                            && record.get("comment").toString().equalsIgnoreCase(noti.getComment())){
                                                        noti.setIsUpdated(0);
                                                    }else{
                                                        noti.setIsUpdated(1);
                                                    }
                                                    //Log.i(TAG, "checkUploadResultManagerRunnable - test1");
                                                }
                                                NotificationDataRecordDAO.updateOne(noti);
                                            } else {
                                                noti.setIsUploading(0);
                                                NotificationDataRecordDAO.updateOne(noti);
                                                Log.i(TAG, "No such document: " + document.getId());
                                            }
                                        }
                                    } else {
                                        Log.i(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }

                    }
                }catch (Exception e)
                {
                    Log.i(TAG, "checkUploadResultManagerRunnable error: " + e.getMessage());
                }
            }
        }

    };

    private void retryUploadImage(ImageDataRecord a) throws JsonProcessingException {
        Log.d(TAG, "retryUploadImage " + a.getFileName() + " retry to upload. ");
        String PID = sharedPrefs.getString("ParticipantID", "");

        if(!pushToRealtimeDataBase_Image) {
            String dd = a.getFileName().substring(0, 8);
            // Add a new document with a generated ID
            db.collection(PID).document(dd)
                    .collection("ImageRecord")
                    .document(PID +"_" +a.getFileName())
                    .set(Utils.getImageHashMap(sharedPrefs, a, true));
        }else{
            String today = Utils.getUploadDay();
            if(Utils.isSavingDataToWhere(sharedPrefs, "firebase")){
                saveDataToFirebase(database, "Logs", today, PID, "ImageRecord", PID +"_" +a.getFileName(), Utils.getImageHashMap(sharedPrefs, a, false));
            }else {
                saveDataToLocal(Utils.getImageHashMap(sharedPrefs, a, false), today, PID +"_" +a.getFileName(), "RealTime Data/ImageRecord");
            }
            /*database.getReference("Logs")
                    .child(today)
                    .child(PID)
                    .child("ImageRecord")
                    .child(PID +"_" +a.getFileName())
                    .setValue(Utils.getImageHashMap(sharedPrefs, a, false))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e(TAG, "upload Image Fail " + e.getMessage());
                        }
                    });

             */
        }

        File f = new File(saveFile + "/" + a.getFileName() +".jpg");
        if (f.exists() && !f.isDirectory()) {
            Uri file = Uri.fromFile(f);

            String day = Utils.getUploadDay();

            String path = day + "/" + PID + "/" + a.getLabel() + "/" + PID + "_" + a.getFileName();

            StorageReference storageRef = storageReference.child(path);

            UploadTask uploadTask = storageRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "uploadImage file " + taskSnapshot.getMetadata().getName() + " success.");

                    String filename = taskSnapshot.getMetadata().getName();

                    String docid = taskSnapshot.getMetadata().getName().split("\\.")[0];

                    if (filename.split("_").length > 1) {
                        filename = filename.split("_")[1];
                    }
                    String PID = sharedPrefs.getString("ParticipantID", "");
                    if(!pushToRealtimeDataBase_Image) {
                        String dd = filename.substring(0, 8);
                        final Map<String, Object> record = new HashMap<>();
                        record.put("timestamp", FieldValue.serverTimestamp());
                        record.put("isUploaded", 1);

                        db.collection(PID).document(dd)
                                .collection("ImageRecord")
                                .document(docid)
                                .update(record);
                    }else{
                        String today = Utils.getUploadDay();

                        database.getReference("Logs")
                                .child(today)
                                .child(PID)
                                .child("ImageRecord")
                                .child(docid)
                                .child("isUploaded")
                                .setValue(1)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Write failed
                                        Log.e(TAG, "update Image Fail " + e.getMessage());
                                    }
                                });
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    //Log.e(TAG, "addOnCompleteListener ");
                    String filename = task.getResult().getMetadata().getName() +".jpg";
                    Log.d(TAG, "addOnCompleteListener " + filename);
                    File fdelete = new File(saveFile + "/" + filename);
                    if (fdelete.exists()) {
                        fdelete.delete();
                    }
                }
            });
        } else {
            Log.i(TAG, "uploadImage file " + a.getFileName() + "is  not found, skip");
        }
    }


    private Notification getOngoingNotification(String text){

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(getResources().getString(R.string.app_name));
        bigTextStyle.bigText(text);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(text)
                .setStyle(bigTextStyle)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .setChannelId(Constants.ONGOING_CHANNEL_ID)
                    .build();
        } else {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .build();
        }
    }


    @Override
    public void onDestroy() {
//        super.onDestroy();
        insertBackgroundServiceStatusPreference("onDestroy");
        stopTheSessionByServiceClose();

        if(saveDataToCSV) {
            String onDestroy = "BackGround, onDestroy";
            CSVHelper.storeToCSV(CSVHelper.CSV_ESM, onDestroy);
            CSVHelper.storeToCSV(CSVHelper.CSV_CAR, onDestroy);
        }

        sendBroadcastToStartService();

//        checkingRemovedFromForeground();
//        removeRunnable();


        mNotificationManager.cancel(ongoingNotificationID);
        stopForeground(true);

        Log.i(TAG, "Destroying service. Your state might be lost!");

        sharedPrefs.edit().putInt("CurrentState", TransportationModeStreamGenerator.mCurrentState).apply();
        sharedPrefs.edit().putInt("ConfirmedActivityType", TransportationModeStreamGenerator.mConfirmedActivityType).apply();

//        unregisterReceiver(mWifiReceiver);

        if(virtualDisplay!=null)
        {
            virtualDisplay.release();
        }
        if(mediaProjection!=null)
        {
            mediaProjection.stop();
        }

        Intent restartServiceIntent = new Intent(getApplicationContext(), NotiListenerService.class);
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                this,
                1,
                restartServiceIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT
        );
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                System.currentTimeMillis() + 10000,
                restartServicePendingIntent
        );

        if (isBackgroundServiceRunning || isBackgroundRunnableRunning) {
            try {
                unregisterReceiver(CheckRunnableReceiver);
                unregisterReceiver(mIntentActionReceiver);
                mConnectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isBackgroundServiceRunning = false;
                isBackgroundRunnableRunning = false;
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent intent){
        super.onTaskRemoved(intent);
        insertBackgroundServiceStatusPreference("onTaskRemoved");

        sendBroadcastToStartService();

//        checkingRemovedFromForeground();
//        removeRunnable();

        isBackgroundServiceRunning = false;
        isBackgroundRunnableRunning = false;


        mNotificationManager.cancel(ongoingNotificationID);

        if(saveDataToCSV) {
            String onTaskRemoved = "BackGround, onTaskRemoved";
            CSVHelper.storeToCSV(CSVHelper.CSV_CheckService_alive, onTaskRemoved);
        }

        sharedPrefs.edit().putInt("CurrentState", TransportationModeStreamGenerator.mCurrentState).apply();
        sharedPrefs.edit().putInt("ConfirmedActivityType", TransportationModeStreamGenerator.mConfirmedActivityType).apply();
    }


    private void checkingRemovedFromForeground(){

        Log.i(TAG,"stopForeground");

        stopForeground(true);

        try {

            unregisterReceiver(CheckRunnableReceiver);
        }catch (IllegalArgumentException e){

        }

        mScheduledExecutorService.shutdown();
    }

    private void stopTheSessionByServiceClose(){

        //if the background service is killed, set the end time of the ongoing trip (if any) using the current timestamp
        if (SessionManager.getOngoingSessionIdList().size()>0){

            Session session = SessionManager.getSession(SessionManager.getOngoingSessionIdList().get(0)) ;

            //if we end the current session, we should update its time and set a long enough flag
            if (session.getEndTime()==0){
                long endTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
                session.setEndTime(endTime);
            }

            //end the current session
            SessionManager.endCurSession(session);

            sharedPrefs.edit().putInt("ongoingSessionid",session.getId()).apply();
        }
    }



    private void sendBroadcastToStartService(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            sendBroadcast(new Intent(this, RestarterBroadcastReceiver.class).setAction(Constants.CHECK_SERVICE_ACTION));
        } else {

            Intent checkServiceIntent = new Intent(Constants.CHECK_SERVICE_ACTION);
            sendBroadcast(checkServiceIntent);
        }
    }

    private void registerConnectivityNetworkMonitorForAPI21AndUp() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                        /*sendBroadcast(
                                getConnectivityIntent("onAvailable")
                        );*/

            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities){
                sendBroadcast(
                        getConnectivityIntent("onCapabilitiesChanged : "+networkCapabilities.toString())
                );
            }

            @Override
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                        /*sendBroadcast(
                                getConnectivityIntent("onLinkPropertiesChanged : "+linkProperties.toString())
                        );*/
            }

            @Override
            public void onLosing(Network network, int maxMsToLive) {
                        /*sendBroadcast(
                                getConnectivityIntent("onLosing")
                        );*/
            }

            @Override
            public void onLost(Network network) {
                        /*sendBroadcast(
                                getConnectivityIntent("onLost")
                        );*/
            }
        };

        mConnectivityManager.registerNetworkCallback(builder.build(), networkCallback);

    }

    private Intent getConnectivityIntent(String message) {

        Intent intent = new Intent();

        intent.setAction(Constants.ACTION_CONNECTIVITY_CHANGE);

        intent.putExtra("message", message);

        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel(CharSequence name, String channel_id, int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    BroadcastReceiver CheckRunnableReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(CHECK_RUNNABLE_ACTION)) {

                Log.i(TAG, "[check runnable] going to check if the runnable is running");

                if(saveDataToCSV) {
                    CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "going to check if the runnable is running");
                    CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "is the runnable running ? " + isBackgroundRunnableRunning);
                }
                if (!isBackgroundRunnableRunning) {

                    Log.i(TAG, "[check runnable] the runnable is not running, going to restart it.");

                    if(saveDataToCSV) {
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "the runnable is not running, going to restart it");
                    }
                    updateNotificationAndStreamManagerThread();

                    Log.i(TAG, "[check runnable] the runnable is restarted.");
                    if(saveDataToCSV) {
                        CSVHelper.storeToCSV(CSVHelper.CSV_RUNNABLE_CHECK, "the runnable is restarted");
                    }
                }

                PendingIntent pi = PendingIntent.getBroadcast(BackgroundService.this, 0, new Intent(CHECK_RUNNABLE_ACTION), 0);

                AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarm.set(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + Constants.PROMPT_SERVICE_REPEAT_MILLISECONDS,
                        pi
                );
            }
        }
    };


    private WindowManager windowManager =null;
    private ImageView igv = null;
    private WindowManager.LayoutParams params;

    private int screenWidth,screenHeight,screenDensity;

    public void initWindow(){

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //取得螢幕的各項參數
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


        Log.i(TAG,"density:"+screenDensity+", width:" + screenWidth + ", height:" + screenHeight);

        igv = new ImageView(this);
        igv.setImageResource(R.mipmap.ic_launcher);

    }

    public void initHandler(){
        handler = new Handler();
    }

    //建立imageReader
    public void createImageReader() {
//      if(imageReader == null)
        try {
            Log.d(TAG, "createImageReader called" );
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 4);
        }catch (Exception e){
            Log.e(TAG, "createImageReader " + e.getMessage() );
        }
    }

    public void initMediaProjection(){

        try {
            Log.i(TAG, "initMediaProjection called");
            //透過MediaProjectionManager取得MediaProjection
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mediaProjection = mediaProjectionManager.getMediaProjection(AppCompatActivity.RESULT_OK, resultIntentfromM);

            //呼叫mediaProjection.createVirtualDisplay()

            if(mediaProjection!=null) {
                virtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror-nctu",
                        screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(), null, null);
                if (virtualDisplay ==null)
                {
                    Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                return;
            }
        }catch (Exception e){
            Log.e(TAG, "initMediaProjection " + e.getMessage());
            Toast.makeText(this, "virtualDisplay is null", Toast.LENGTH_SHORT).show();
        }

    }

    public static void setResultIntent(Intent it){
        resultIntentfromM = it;
    }

    public void startScreenShot(){
        igv.setVisibility(View.GONE);

        boolean start = false;

        boolean lastCaptureType = sharedPrefs.getBoolean("lastCaptureType", false);
        long lastCapturetime = sharedPrefs.getLong("lastCapturetime", -1);

        if(Utils.isSavingDataToWhere(sharedPrefs, "not")){
            Log.i(TAG, "Do not store the data, so it's not capturing screenshot.");

        }
        if (Utils.isActiveTiming(sharedPrefs) && !Utils.isSavingDataToWhere(sharedPrefs, "not")){
//            if (lastNotiTime != 0 && ((userShowTime - lastNotiTime) > 3600000)) { // 1 hr
                start = true;
//            }
        }else{
            if(lastCapturetime!= -1)
                if(lastCaptureType)
                {
                    long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
                    int count_unlabeled = ImageDataRecordDAO.getUnLabeledLabelByTime(lastCapturetime, current);
                    int count_labeled = ImageDataRecordDAO.getLabeledLabelByTime(lastCapturetime, current);
                    Log.i(TAG, "[startScreenShot- overday] checkImageNum " + lastCapturetime + " -> " + current +" : " + (count_unlabeled + count_labeled));
                    if((count_unlabeled + count_labeled) > 0) {
                        insertOneNote(lastCapturetime, current);
                    }
                    sharedPrefs.edit()
                            .putLong("lastCapturetime",ScheduleAndSampleManager.getCurrentTimeInMillis())
                            .putBoolean("lastCaptureType", false)
                            .commit();
                }
        }
        if(start){
            //Log.i(TAG, "[startScreenShot] called");

            String screenStatus = getScreenStatus();
            //Log.e(TAG, "[startScreenShot 0] " + scrrenStatus);
            if(!screenStatus.equals("Screen_off")) {

                if(!BoredomApp.isActivityVisible()) {
                    long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
                    if(!lastCaptureType ) {
                        sharedPrefs.edit()
                                .putBoolean("lastCaptureType", true)
                                .putLong("lastCapturetime",current)
                                .commit();
                        Log.i(TAG, "[startScreenShot] reset lastCaptureType : " + current );

                    }else{
                        Date date_curr = new Date(current);
                        Date date_last = new Date(lastCapturetime);
                        String d_curr = formatter_date.format(date_curr);
                        String d_last = formatter_date.format(date_last);
                        if (!d_curr.equalsIgnoreCase(d_last))
                        {
                            try {
                                Date d = formatter_full.parse(d_last+"235959999");
                                Log.d(TAG, "[startScreenShot]  over Day!!!!" );
                                insertOneNote(lastCapturetime, d.getTime());
                                sharedPrefs.edit()
                                        .putLong("lastCapturetime",current)
                                        .commit();
                            } catch (ParseException e) {
                                Log.e(TAG, "[startScreenShot] " + e.getMessage().toString() );
                            }
                        }
                    }
                    lastCaptureType = true;
                    lastCapturetime = current;

                    startCapture();
                } else{
                    Log.i(TAG, "[startScreenShot] skip startScreenShot , because of using Boredom App");
                    try {
                        streamManager.updateStreamGenerators();
                    }catch (Exception e)
                    {
                        Log.e(TAG, "[startScreenShot] doInBackground updateStreamGenerators fail");
                    }
                    insertOneMinuku(getImagesFileName(System.currentTimeMillis()), -2);
                }
            } else{
                if(lastCapturetime!= -1)
                    if(lastCaptureType)
                    {
                        long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
                        int count_unlabeled = ImageDataRecordDAO.getUnLabeledLabelByTime(lastCapturetime, current);
                        int count_labeled = ImageDataRecordDAO.getLabeledLabelByTime(lastCapturetime, current);
                        Log.i(TAG, "[startScreenShot] checkImageNum " + lastCapturetime + " -> " + current +" : " + (count_unlabeled + count_labeled));
                        if((count_unlabeled + count_labeled) >= 100) {
                            insertOneNote(lastCapturetime, current);
                            sharedPrefs.edit()
                                    .putBoolean("lastCaptureType", false)
                                    .commit();
                            lastCaptureType = false;
                        }
                    }
                Log.i(TAG, "[startScreenShot] skip startScreenShot , because of " + screenStatus);

                try {
                    streamManager.updateStreamGenerators();
                }catch (Exception e)
                {
                    Log.e(TAG, "[startScreenShot] doInBackground updateStreamGenerators fail");
                }
                insertOneMinuku(getImagesFileName(System.currentTimeMillis()), -1);

            }

        }

    }

    int retry = 0;

    private void startCapture() {
        //Log.i(TAG, "startCapture called 1");
        Image image = null;
        //呼叫image.acquireLatestImage()，取得image
        try {
            if( imageReader != null) {
                image = imageReader.acquireLatestImage();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG,"ImageReader Exception: " + e.toString());
        }
        finally {
            if(image != null){
                retry = 0;
                int intScreenWidth = image.getWidth();
                int intScreenHeight = image.getHeight();

                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();

                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * intScreenWidth;

                Bitmap bitmap = Bitmap.createBitmap(intScreenWidth + rowPadding / pixelStride, intScreenHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, intScreenWidth, intScreenHeight);
                bitmap = Bitmap.createScaledBitmap(bitmap, intScreenWidth/2, intScreenHeight/2, true);

                buffer.clear();
                image.close();
                new SaveTask().execute(bitmap);
            }else{
                insertOneMinuku(getImagesFileName(System.currentTimeMillis()),  0);
                retry++;
                Log.d(TAG, "image is null, retry count : " + retry );
                if (retry >= 10)
                {
                    if(imageReader !=null)
                    {
                        imageReader.close();
                    }
                    createImageReader();

                    if(virtualDisplay !=null) {
                        Log.d(TAG, "virtualDisplay is not null " );
                        virtualDisplay.release();
                        if(mediaProjection!=null) {
                            mediaProjection.stop();
                            initMediaProjection();
                        }
                    }
                }

            }
        }
    }


    public class SaveTask extends AsyncTask<Bitmap, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Bitmap... params) {

            //Log.i(TAG, "startCapture called 3 ");
            if (params == null || params.length < 1 || params[0] == null) {
                Log.e(TAG, "imageReader.acquireLatestImage() fail ");
                return false;
            }
           // Log.i(TAG, "doInBackground start");
            Boolean success = false;

            Bitmap bitmap = params[0];
            //Log.i(TAG, "updateStreamManager called");
            boolean updateStreamSuccess = true;
            try {
                streamManager.updateStreamGenerators();
            }catch (Exception e)
            {
                updateStreamSuccess = false;
                Log.e(TAG, "doInBackground updateStreamGenerators fail");
            }
            File fileImage;
            if (bitmap != null) {
                try {
                    //Log.i(TAG, "doInBackground bitmap!= null");
                    long time = System.currentTimeMillis();
                    fileImage = getScreenShotsFile(getImagesFileName(time));
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        if (fileImage.exists()) {
                            success = true;

                            String filename = fileImage.getName().substring(0,14);
                            //long time = getTime(filename);

                            insertOneMinuku(filename, 1);

                            //insert data record to room database
                            ImageDataRecord imageDataRecord = new ImageDataRecord(fileImage.getName(), time);
                            long result = ImageDataRecordDAO.insertOne(imageDataRecord);
                            Log.i(TAG, "ImageDataRecordDAO.insertOne " + time);
                        }else{
                            Log.e(TAG, "doInBackground " +  "fileImage is not found.");
                        }
                    }
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "doInBackground " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground " + e.getMessage());
                }finally {
                    bitmap.recycle();
                }
            }else{
                Log.i(TAG, "doInBackground bitmap is null");
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);

            if (bool) {
//                Toast.makeText(getApplicationContext(),"Got it",Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    streamManager.updateStreamGenerators();
                }catch (Exception e)
                {
                    Log.e(TAG, "[startScreenShot] doInBackground updateStreamGenerators fail");
                }
                insertOneMinuku(getImagesFileName(System.currentTimeMillis()),  -3);

            }
            igv.setVisibility(View.VISIBLE);

        }
    }

    private void insertOneMinuku(String filename, int isScreenshotted)
    {
        // isScreenshotted 1 --> normal
        // isScreenshotted 0 --> no image captured
        // isScreenshotted -1 --> Sleep
        // isScreenshotted -2 --> Boredom App
        // isScreenshotted -3 --> onPostExecute


        //Log.i(TAG, "doInBackground insertOneMinuku " + filename);
        long AppUsageKey =  AppUsageStreamGenerator.getId();
        long time = getTime(filename);

        MinukuDataRecord minukuDataRecord = new MinukuDataRecord(formmatter_minuku.format(new Date(time)).replaceAll("\\D+", ""),
                time, AccessibilityStreamGenerator.getId(), ActivityRecognitionStreamGenerator.getId(), AppUsageKey,
                BatteryStreamGenerator.getId(), ConnectivityStreamGenerator.getId(), RingerStreamGenerator.getId(), TelephonyStreamGenerator.getId(),
                TransportationModeStreamGenerator.getId(),filename, isScreenshotted);
        MinukuDataRecordDAO.insertOne(minukuDataRecord);

        String screenStatus ="Screen_off";
        if(isScreenshotted != -1) {
            screenStatus = getScreenStatus();
        }
        if(isScreenshotted != -2) {
            String currentPackageName = "NA";
            String currentActivityName = "NA";


            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    currentPackageName = getProcessNew();
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground " + e.getMessage());
                }
            } else {
                ActivityManager.RunningTaskInfo currentTask = getCurrentTask();
                if (currentTask != null) {
                    currentPackageName = currentTask.topActivity.getPackageName();
                    currentActivityName = currentTask.topActivity.getClassName();
                }
            }

            List<AppUsageDataRecord> appusageList = AppUsageDataRecordDAO.getById(AppUsageKey);
            if (appusageList.size() > 0) {
                AppUsageDataRecord tmp = appusageList.get(0);
                tmp.setScreen_Status(screenStatus);
                tmp.setLatest_Foreground_Activity(currentActivityName);
                tmp.setLatest_Foreground_Package(currentPackageName);
                tmp.setLatest_Used_App(getAppNamebyPackageName(currentPackageName));
                AppUsageDataRecordDAO.updateOne(tmp);
            } else {
                Log.e(TAG, "doInBackground AppUsageDataRecord " + AppUsageKey + " is not found");
            }
        }else{
            List<AppUsageDataRecord> appusageList = AppUsageDataRecordDAO.getById(AppUsageKey);
            if (appusageList.size() > 0) {
                AppUsageDataRecord tmp = appusageList.get(0);
                tmp.setScreen_Status(screenStatus);
                tmp.setLatest_Foreground_Activity("NA");
                tmp.setLatest_Foreground_Package("labelingStudy.nctu.boredom_detection");
                tmp.setLatest_Used_App(getAppNamebyPackageName("labelingStudy.nctu.boredom_detection"));
                AppUsageDataRecordDAO.updateOne(tmp);
            } else {
                Log.e(TAG, "doInBackground AppUsageDataRecord " + AppUsageKey + " is not found");
            }
        }

    }

    private long getTime(String datetime )
    {
        long time = -1;

        try {


            Date d = formatter.parse(datetime);
            time = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.i(TAG, "getTime:" + time );
        return time;
    }

    public void getDeviceid(){
        Constants.DEVICE_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

//        TelephonyManager mngr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        int permissionStatus= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
//        if(permissionStatus== PackageManager.PERMISSION_GRANTED){
//            Constants.DEVICE_ID = mngr.getDeviceId();
//            if(Constants.DEVICE_ID == null){
//                Constants.DEVICE_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//            }
//
//            Log.e(TAG,"DEVICE_ID"+Constants.DEVICE_ID+" : "+mngr.getDeviceId());
//
//        }
    }

    public File getMainDirectoryName() {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        File mainDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Constants.PACKAGE_DIRECTORY_PATH), "Demo");

//        File mainDir = new File(Constants.PACKAGE_DIRECTORY_PATH, "Demo");

        Log.i(TAG, "Demo File is presented at " + mainDir );
//        mainDir.mkdirs();
//        //If File is not present create directory
        if (!mainDir.exists()) {
            mainDir.mkdirs();
            Log.i(TAG, "Directory not created");
        }
        return mainDir;
    }

    public final void SetPeriodAlarm(){
        // Set the alarm to start at 22:05
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date date = calendar.getTime();
        labelingStudy.nctu.minuku.logger.Log.i(TAG,"SetPeriodAlarm : " + formmatter_minuku.format(date));

        //daily
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 60 * 24 * 1000, remindersIntentManager.getChristmasIntent() );
    }

    public File getScreenShotsFile(String Name){
        fileName = Name;
        File file = new File(saveFile.getAbsolutePath(), fileName+".jpg");
        return file;
    }

    public String getImagesFileName(long time){
        Date curDate = new Date(time); // 獲取當前時間
        return formatter.format(curDate);
    }


    private ActivityManager.RunningTaskInfo getCurrentTask()
    {
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (taskInfo.isEmpty())
        {
            return null;
        }else {

            return taskInfo.get(0);
        }

    }



    //API 21 and above
    private String getProcessNew() throws Exception {
        String topPackageName = null;

        long time = System.currentTimeMillis();
        int[] intTimingArray = new int[]{ 10, 30, 60, 100, 300, 500, 800,1000 };
        for(int i : intTimingArray)
        {
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * i, time);
            if (stats != null && !stats.isEmpty()) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    return null;
                }
                topPackageName =  runningTask.get(runningTask.lastKey()).getPackageName();
                break;
            }
        }
        return topPackageName;
    }


    public String getScreenStatus() {
        String Screen_Status = "NA";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

            //use isInteractive after api 20

            if (mPowerManager.isInteractive())
                Screen_Status = "Interactive";
            else
                Screen_Status = "Screen_off";

        }
        //before API20, we use screen on or off
        else {
            if(mPowerManager.isScreenOn())
                Screen_Status = "Screen_on";
            else
                Screen_Status = "Screen_off";

        }
        return Screen_Status;
    }

    public Boolean checkScreenUnlocked(Context context) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return !myKM.inKeyguardRestrictedInputMode();
    }


    public void insertOneNote(long start, long end)
    {
        Log.i(TAG, "insertOneNote : " + start + " -> " + end);
        NoteDataRecord noteDataRecord1 = new NoteDataRecord(end - 1, 2, -1, "", false);
        noteDataRecord1.setStarttime(start);
        noteDataRecord1.setEndtime(end);
        noteDataRecordDAO.insertOne(noteDataRecord1);
    }

    private void insertPreference() {

        List<String> type = Arrays.asList(NEWS_1, NEWS_2, NEWS_9, NEWS_10, NEWS_20, NEWS_17, NEWS_7, NEWS_21);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("News",String.join(",", type));
        hashMap.put("IsDefault","1");
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap), 1, System.currentTimeMillis()));

        int start = sharedPrefs.getInt("recording_start", 600);
        int end = sharedPrefs.getInt("recording_end", 1320);

        int start_hour = start/60;
        int start_min = start%60;
        int end_hour = end/60;
        int end_min = end%60;

        String start_h = start_hour<10?"0"+start_hour:""+start_hour;
        String start_m = start_min<10?"0"+start_min:""+start_min;
        String end_h = end_hour<10?"0"+end_hour:""+end_hour;
        String end_m = end_min<10?"0"+end_min:""+end_min;

        HashMap<String,String> hashMap1=new HashMap<>();
        hashMap1.put("StartTime",start_h +":" + start_m);
        hashMap1.put("EndTime", end_h + ":" +end_m);
        hashMap1.put("IsDefault","1");
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap1), 2, System.currentTimeMillis()));


    }


    private void insertBackgroundServiceStatusPreference(String status) {

        Log.e(TAG, status);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("action",status);
        hashMap.put("version", versionName);
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap), 5, System.currentTimeMillis()));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Your handling
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        Log.d(TAG, "onConfigurationChanged : "+ screenWidth +", " + screenHeight +", "+ screenDensity);
        if (virtualDisplay == null) {
            // Capturer is stopped, the virtual display will be created in startCaptuer().
            return;
        }
        // Create a new virtual display on the surfaceTextureHelper thread to avoid interference
        // with frame processing, which happens on the same thread (we serialize events by running
        // them on the same thread).
        if(imageReader !=null)
        {
            imageReader.close();
        }
        createImageReader();

        if(virtualDisplay !=null) {
            virtualDisplay.release();
            if(mediaProjection!=null) {
                mediaProjection.stop();
                initMediaProjection();
            }
            //createVirtualDisplay();
        }

    }

    public String getAppNamebyPackageName(String packageName) {
        ApplicationInfo ai;
        try
        {
            ai = pm.getApplicationInfo(packageName,  PackageManager.GET_META_DATA);
        } catch(final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String)(ai !=null?pm.getApplicationLabel(ai):"NA");
    }

}
