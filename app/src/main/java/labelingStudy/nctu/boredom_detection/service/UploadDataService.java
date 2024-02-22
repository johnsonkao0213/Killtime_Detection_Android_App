package labelingStudy.nctu.boredom_detection.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.dao.ActionDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationRemoveRecordDao;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;

/**
 * Service to handle uploading files to Firebase Realtime & firestore.
 * IT IS AN OLD VERSION. Winston 2023/11
 */
public class UploadDataService extends Service {

    private static final String TAG = "UploadDataService";

    private ScheduledExecutorService mScheduledExecutorService;
    ScheduledFuture<?> mScheduledUpload;

    private NotificationManager mManager;
    private Notification.Builder mBuilder;
    private int notifyNotificationID = 17;
    private int notifyNotificationCode = 307;

    /**
     * Intent Actions
     **/
    public static final String ACTION_UPLOAD = "action_upload_data";
    public static final String UPLOAD_COMPLETED = "upload_data_completed";
    public static final String UPLOAD_ERROR = "upload_data_error";

    /**
     * Intent Extras
     **/
    public static final String EXTRA_MINUKU_LIST = "extra_upload_minuku";
    public static final String EXTRA_NOTIREMOVE_LIST = "extra_upload_noti_remove";
    public static final String EXTRA_ACC_LIST = "extra_upload_accessibility";
    public static final String EXTRA_ACTION_LIST = "extra_upload_intent_action";
    public static final String EXTRA_PREF_LIST = "extra_upload_preference";
    public static final String EXTRA_LAST_TIME = "extra_last_time";


    private labelingStudy.nctu.boredom_detection.dao.MinukuDataRecordDAO MinukuDataRecordDAO;
    private labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO NotificationEventRecordDAO;
    private labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO NotificationDataRecordDAO;
    private ImageDataRecordDAO ImageDataRecordDAO;
    private labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO AnswerDataRecordDAO;
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

    // [START declare_ref]


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END declare_ref]

    private SharedPreferences sharedPrefs;
    File saveFile;


    int UploadUploadedCount = 0;
    int UploadFailCount = 0;

    long lastTime = 0;

    public List<String> WaitingUploadList_minuku = new ArrayList<String>();
    public List<String> WaitingUploadList_notiRemove = new ArrayList<String>();
    public List<String> WaitingUploadList_acc = new ArrayList<String>();
    public List<String> WaitingUploadList_action = new ArrayList<String>();
    public List<String> WaitingUploadList_pref = new ArrayList<String>();

    int last_noti = -1;


    String PID = "";

    Gson gson = new Gson();
    boolean RESTARTSERVICE = true;

    public static boolean isBackgroundServiceRunning = false;
    public static boolean isBackgroundRunnableRunning = false;
    Context mContext;

    ConnectivityManager mConnectivityManager = null;
    NetworkRequest.Builder builder;
    ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        mScheduledExecutorService = Executors.newScheduledThreadPool(Constants.NOTIFICATION_UPDATE_THREAD_SIZE);
        isBackgroundServiceRunning = false;

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

        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
        saveFile = getMainDirectoryName();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerConnectivityNetworkMonitorForAPI21AndUp();

        PID = sharedPrefs.getString("ParticipantID", "");
        Log.i(TAG, "onStartCommand:" + intent + ":" + startId);
        if (ACTION_UPLOAD.equals(intent.getAction())) {
            retrivesharedPreferences();

            uploadData();
        }

        if (!isBackgroundServiceRunning) {

            isBackgroundServiceRunning = true;
            updateNotificationAndStreamManagerThread();
        }

        return START_REDELIVER_INTENT;
    }

    private void updateNotificationAndStreamManagerThread() {
        mScheduledUpload = mScheduledExecutorService.scheduleAtFixedRate(
                //uploadDataManagerRunnable
                uploadDataRunnable,
                90,
                300,
                TimeUnit.SECONDS);

    }

    private void retrivesharedPreferences() {
        uploadNoti();
        last_noti = -1;
        WaitingUploadList_minuku.clear();
        WaitingUploadList_minuku = Utils.getArrayList(sharedPrefs, gson, EXTRA_MINUKU_LIST);
        WaitingUploadList_notiRemove.clear();
        WaitingUploadList_notiRemove = Utils.getArrayList(sharedPrefs, gson, EXTRA_NOTIREMOVE_LIST);
        WaitingUploadList_acc.clear();
        WaitingUploadList_acc = Utils.getArrayList(sharedPrefs, gson, EXTRA_ACC_LIST);
        WaitingUploadList_action.clear();
        WaitingUploadList_action = Utils.getArrayList(sharedPrefs, gson, EXTRA_ACTION_LIST);
        WaitingUploadList_pref.clear();
        WaitingUploadList_pref = Utils.getArrayList(sharedPrefs, gson, EXTRA_PREF_LIST);
        lastTime = sharedPrefs.getLong(EXTRA_LAST_TIME, 0);
    }

    public void uploadData() {
        Log.i(TAG, "upload data called");
        try {
            UploadUploadedCount = 0;
            UploadFailCount = 0;

            mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;

            mBuilder = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.uploading_background))
                    .setContentText(getProcessStr())
                    //.setStyle(bigTextStyle)
                    //.setContentIntent(pending)
                    //.setAutoCancel(true)
                    //.setDefaults(Notification.DEFAULT_ALL);
                    .setProgress(getAllUploadCount(), UploadUploadedCount, false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = mBuilder
                        .setSmallIcon(Utils.getNotificationIcon(mBuilder))
                        .setChannelId(Constants.UPLOAD_DATA_CHANNEL_ID)
                        .build();
            } else {
                notification = mBuilder
                        .setSmallIcon(Utils.getNotificationIcon(mBuilder))
                        .build();
            }

            mManager.notify(notifyNotificationID, notification);

            uploadOneData();
        } catch (Exception e) {
            labelingStudy.nctu.minuku.logger.Log.e(TAG, "upload images error:" + e.getMessage());
        }
    }


    public void uploadOneData() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (sharedPrefs.getLong(EXTRA_LAST_TIME, 0) != lastTime) {
                            retrivesharedPreferences();
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (checkNetwork()) {

                            String today = Utils.getUploadDay();
                            if (UploadUploadedCount < WaitingUploadList_minuku.size()) {
                                // upload minuku
                                if (checkNetwork()) {
                                    int idx = UploadUploadedCount;

                                    List<MinukuDataRecord> minukuDataRecordList = MinukuDataRecordDAO.getByPKId(Long.parseLong(WaitingUploadList_minuku.get(idx)));
                                    if (minukuDataRecordList.size() > 0) {
                                        //Log.d(TAG, "--------Manual uplaod Minuku----start------");
                                        MinukuDataRecord a = minukuDataRecordList.get(0);
                                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                            database.getReference("Logs")
                                                    .child(today)
                                                    .child(PID)
                                                    .child("MinukuData")
                                                    .child(PID + "_" + a.getImageFileName())
                                                    .setValue(Utils.getMinukuUploadMap(AccessibilityDataRecordDAO, ActivityRecognitionDataRecordDAO, AppUsageDataRecordDAO, BatteryDataRecordDAO, ConnectivityDataRecordDAO, RingerDataRecordDAO, TelephonyDataRecordDAO, TransportationModeDataRecordDAO, a, false))
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Write failed
                                                            Log.e(TAG, "upload Minuku Fail " + e.getMessage());
                                                            UploadFailCount++;
                                                        }
                                                    });
                                            MinukuDataRecordDAO.deleteOne(a);
                                            updateResutl();
                                        }
                                    } else {
                                        updateResutl();
                                    }
                                }
                                uploadOneData();
                            } else if ((UploadUploadedCount - WaitingUploadList_minuku.size()) < WaitingUploadList_notiRemove.size()) {
                                // upload notiRemove
                                if (checkNetwork()) {
                                    int idx = UploadUploadedCount - WaitingUploadList_minuku.size();

                                    List<NotificationRemoveRecord> notificationRemoveList = notificationRemoveRecordDAO.getByPKId(Integer.parseInt(WaitingUploadList_notiRemove.get(idx)));
                                    if (notificationRemoveList.size() > 0) {
                                        NotificationRemoveRecord a = notificationRemoveList.get(0);
                                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
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
                                                            UploadFailCount++;
                                                        }
                                                    });
                                            Log.d("NotiRemove", "1");

                                            // if remove Boredom's Notification, update Notification
                                            if ((a.appName.equals("殺時間標記") || a.appName.equals("BoredomDetection")) &&
                                                    (a.title.equals("News") || a.title.equals("Advertisement") ||
                                                            a.title.equals("Questionnaire") || a.title.equals("Crowdsourcing"))) {
                                                List<NotificationDataRecord> notificationDataRecordList = NotificationDataRecordDAO.getByPKId(a.notifyNotificationID);
                                                NotificationDataRecord b = notificationDataRecordList.get(0);
                                                String bb = b.getFirebasePK().substring(0, 8);
                                                final Map<String, Object> record2 = new HashMap<>();
                                                record2.put("removeTime", a.removeTime);
                                                DocumentReference ref2 = db.collection(PID).document(bb)
                                                        .collection("Notification").document(b.getFirebasePK());

                                                WriteBatch batch = db.batch();
                                                batch.update(ref2, record2);
                                                batch.commit();

                                                Log.d("NotiRemove", "2");
                                            }

                                            notificationRemoveRecordDAO.deleteOne(a);
                                            updateResutl();
                                        }

                                    } else {
                                        updateResutl();
                                    }
                                }
                                uploadOneData();
                            } else if ((UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size()) < WaitingUploadList_acc.size()) {
                                // upload acc
                                if (checkNetwork()) {
                                    int idx = UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size();

                                    List<MobileAccessibilityDataRecord> accessibilityList = MobileAccessibilityDataRecordDAO.getByPKId(Long.parseLong(WaitingUploadList_acc.get(idx)));
                                    if (accessibilityList.size() > 0) {
                                        final MobileAccessibilityDataRecord a = accessibilityList.get(0);
                                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                                            database.getReference("Logs")
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
                                                            UploadFailCount++;
                                                        }
                                                    });
                                            MobileAccessibilityDataRecordDAO.deleteOne(a);
                                            updateResutl();
                                        }

                                    } else {
                                        updateResutl();
                                    }
                                }
                                uploadOneData();
                            } else if ((UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size() - WaitingUploadList_acc.size()) < WaitingUploadList_pref.size()) {
                                // upload preference
                                Log.i(TAG, "uploadDataToFirestoreManager " + WaitingUploadList_pref.size() + " new Preference data need to upload. ");
                                if (checkNetwork()) {
                                    int idx = UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size() - WaitingUploadList_acc.size();

                                    List<PreferenceDataRecord> pList = preferenceDataRecordDAO.getByPKId(Long.parseLong(WaitingUploadList_pref.get(idx)));

                                    if (pList.size() > 0) {
                                        PreferenceDataRecord a = pList.get(0);
                                        Log.i(TAG, "pref type:  " + a.getType() );
                                        if (a.getType() == 5) { // ServiceStatus
                                            String jsonString = a.getJsonString();

                                            Type mapType = new TypeToken<Map<String, Object>>() {
                                            }.getType();
                                            Map<String, Object> record = gson.fromJson(jsonString, mapType);

                                            if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
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
                                                                UploadFailCount++;
                                                            }
                                                        });
                                                preferenceDataRecordDAO.deleteOne(a);
                                                updateResutl();
                                            }
                                        } else if (a.getType() == 3 || a.getType() == 4) { // image data record
                                            String jsonString = a.getJsonString();
                                            ImageDataRecord imgDataRecord = new Gson().fromJson(jsonString, ImageDataRecord.class);
                                            imgDataRecord.setFileName(imgDataRecord.getFileName().replace(".jpg", ""));
                                            if (a.getType() == 3) {
                                                retryUploadImage(imgDataRecord);
                                            } else {
                                                Log.d(TAG, "UploadDataToRealtimeDataBaseManager " + imgDataRecord.getFileName() + " private Image data need to upload. ");
                                                if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
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
                                                                    UploadFailCount++;
                                                                }
                                                            });
                                                    preferenceDataRecordDAO.deleteOne(a);
                                                    updateResutl();
                                                }
                                            }
                                        }

                                    } else {
                                        updateResutl();
                                    }
                                }
                                uploadOneData();
                            } else if ((UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size() - WaitingUploadList_acc.size() - WaitingUploadList_pref.size()) < WaitingUploadList_action.size()) {
                                // upload action
                                if (checkNetwork()) {
                                    int idx = UploadUploadedCount - WaitingUploadList_minuku.size() - WaitingUploadList_notiRemove.size() - WaitingUploadList_acc.size() - WaitingUploadList_pref.size();

                                    List<ActionDataRecord> aList = actionDataRecordDAO.getByPKId(Long.parseLong(WaitingUploadList_action.get(idx)));
                                    //Log.i(TAG, "uploadDataToFirestoreManager " + aList.size() + " new Action data need to upload. ");
                                    if (aList.size() > 0) {
                                        ActionDataRecord a = aList.get(0);
                                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
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
                                                            android.util.Log.e(TAG, "upload Action Fail " + e.getMessage());
                                                        }
                                                    });
                                            actionDataRecordDAO.deleteOne(a);
                                            updateResutl();
                                        }
                                    } else {
                                        updateResutl();
                                    }
                                }
                                uploadOneData();
                            } else {
                                // finish upload

                                mBuilder.setContentText("上傳完成！")
                                        // Removes the progress bar
                                        .setProgress(0, 0, false);
                                mManager.notify(notifyNotificationID, mBuilder.build());

                                UploadUploadedCount = 0;
                                UploadFailCount = 0;
                                WaitingUploadList_minuku.clear();
                                WaitingUploadList_notiRemove.clear();
                                WaitingUploadList_acc.clear();
                                WaitingUploadList_pref.clear();
                                WaitingUploadList_action.clear();

                                resetUploadDataToSharePref();

                                Log.i(TAG, "stopping");
                                RESTARTSERVICE = false;
                                stopSelf();
                            }

                        }
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }



    private void retryUploadImage(ImageDataRecord a) {
        Log.d(TAG, "retryUploadImage " + a.getFileName() + " retry to upload. ");
        String PID = sharedPrefs.getString("ParticipantID", "");

        String today = Utils.getUploadDay();
        database.getReference("Logs")
                .child(today)
                .child(PID)
                .child("ImageRecord")
                .child(PID + "_" + a.getFileName())
                .setValue(Utils.getImageHashMap(sharedPrefs, a, false))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.e(TAG, "upload Image Fail " + e.getMessage());
                    }
                });


        File f = new File(saveFile + "/" + a.getFileName() + ".jpg");
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
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    //Log.e(TAG, "addOnCompleteListener ");
                    String filename = task.getResult().getMetadata().getName() + ".jpg";
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

    private boolean checkNetwork() {
        if (!Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
            isBackgroundRunnableRunning = true;
            mBuilder.setContentTitle("暫停上傳背景資料，連上無線網路會自動繼續上傳")
                    .setContentText(getProcessStr())
                    .setProgress(getAllUploadCount(), UploadUploadedCount, false);
            // Displays the progress bar for the first time.
            mManager.notify(notifyNotificationID, mBuilder.build());
            return false;
        } else {
            //Log.i(TAG, "WiFI is Connecting.");
            return true;
        }
    }

    private String getProcessStr() {
        double d = (double) UploadUploadedCount / getAllUploadCount() * 100.0;
        d = (double) Math.round(d * 100) / 100;

        Log.i(TAG, "" + d + " %");
        return d + " %";
    }

    private int getAllUploadCount() {
        return (WaitingUploadList_minuku.size() + WaitingUploadList_notiRemove.size() + WaitingUploadList_acc.size() + WaitingUploadList_pref.size() + WaitingUploadList_action.size());
    }

    private void uploadNoti() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        List<DocumentReference> doc_rf_list = new ArrayList<DocumentReference>();
                        List<Map<String, Object>> object_list = new ArrayList<Map<String, Object>>();
                        List<DocumentReference> ans_rf_list = new ArrayList<DocumentReference>();
                        List<Map<String, Object>> ans_object_list = new ArrayList<Map<String, Object>>();
                        // Notificagtion Data
                        // Upload new Notification Data

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
                                record.put("clickedTimeString", a.clickedTimeString);
                                record.put("pausedTimeString", Arrays.asList(a.getPausedTimeString().split(",")));
                                record.put("resumedTimeString", Arrays.asList(a.getResumedTimeString().split(",")));
                                record.put("submitTimeString", a.submitTimeString);

                                //TODO : set ID by ourselves
                                DocumentReference ref = db.collection(PID).document(dd)
                                        .collection("Notification").document(a.getFirebasePK());
                                doc_rf_list.add(ref);
                                object_list.add(record);

                                a.setIsUpdated(1);
                                NotificationDataRecordDAO.updateOne(a);

                            }

                        }

                        // Upload updated Notification Data

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
                                record.put("clickedTimeString", a.clickedTimeString);
                                record.put("pausedTimeString", Arrays.asList(a.getPausedTimeString().split(",")));
                                record.put("resumedTimeString", Arrays.asList(a.getResumedTimeString().split(",")));
                                record.put("submitTimeString", a.submitTimeString);

                                Log.i(TAG, "uploadDataToFirestoreManager update id: " + a.getFirebasePK());

                                DocumentReference ref = db.collection(PID).document(dd)
                                        .collection("Notification").document(a.getFirebasePK());
                                doc_rf_list.add(ref);
                                object_list.add(record);
                                a.setIsUpdated(1);
                                NotificationDataRecordDAO.updateOne(a);


                            }
                        }


                        // upload answer data by jj

                        List<AnswerDataRecord> answerDataRecordList = AnswerDataRecordDAO.getByIsUpload(0);
                        if (answerDataRecordList.size() > 0) {
                            for (final AnswerDataRecord a : answerDataRecordList) {
                                String dd = a.getFirebasePK().substring(0, 8);

                                String jsonString = a.getJsonString();
                                Type mapType = new TypeToken<Map<String, Object>>() {
                                }.getType();
                                Map<String, Object> record = gson.fromJson(jsonString, mapType);
                                final Map<String, Object> recordNew = new HashMap<>();
                                recordNew.put("survey", record);
                                //record.put("timestamp", FieldValue.serverTimestamp());

                                Log.i(TAG, "uploadDataToFirestoreManager answer: " + record);

                                DocumentReference ref = db.collection(PID).document(dd)
                                        .collection("Notification").document(a.getFirebasePK());

                                a.setIsUpload(1);
                                ans_rf_list.add(ref);
                                ans_object_list.add(recordNew);
                                AnswerDataRecordDAO.updateOne(a);
                                //ref.update(record);
                            }
                        }


                        // Preference Data

                        List<PreferenceDataRecord> pList = preferenceDataRecordDAO.getByIsUpload(0);
                        Log.i(TAG, "uploadDataToFirestoreManager " + pList.size() + " new Preference data need to upload. ");
                        if (pList.size() > 0) {
                            for (final PreferenceDataRecord a : pList) {
                                if (a.getType() == 1 || a.getType() == 2 || a.getType() == 6) {

                                    if (a.getType() == 1) {
                                        String jsonString = a.getJsonString();
                                        Type mapType = new TypeToken<Map<String, Object>>() {
                                        }.getType();
                                        Map<String, Object> record = gson.fromJson(jsonString, mapType);
                                        record.put("settingTime", a.getCreateTime());
                                        DocumentReference ref = db.collection(PID).document("Preference")
                                                .collection("NewsType").document(a.getFirebasePK());
                                        doc_rf_list.add(ref);
                                        object_list.add(record);
                                        preferenceDataRecordDAO.deleteOne(a);
                                    } else if (a.getType() == 6) {
                                        String jsonString = a.getJsonString();
                                        Type mapType = new TypeToken<Map<String, Object>>() {
                                        }.getType();
                                        Map<String, Object> record = gson.fromJson(jsonString, mapType);
                                        record.put("settingTime", a.getCreateTime());
                                        DocumentReference ref = db.collection(PID).document("Preference")
                                                .collection("Network").document(a.getFirebasePK());
                                        doc_rf_list.add(ref);
                                        object_list.add(record);
                                        preferenceDataRecordDAO.deleteOne(a);
                                    } else if (a.getType() == 2) {
                                        String jsonString = a.getJsonString();
                                        Type mapType = new TypeToken<Map<String, Object>>() {
                                        }.getType();
                                        Map<String, Object> record = gson.fromJson(jsonString, mapType);
                                        record.put("settingTime", a.getCreateTime());
                                        DocumentReference ref = db.collection(PID).document("Preference")
                                                .collection("RecordingTime").document(a.getFirebasePK());
                                        doc_rf_list.add(ref);
                                        object_list.add(record);
                                        preferenceDataRecordDAO.deleteOne(a);
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
                            for (int k = 0; k < ans_rf_list.size(); k++) {
                                batch.update(ans_rf_list.get(k), ans_object_list.get(k));
                            }

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
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    private void updateResutl() {

        UploadUploadedCount++;
        int now = Math.round((UploadUploadedCount * 100 / getAllUploadCount()));
        //Log.d(TAG, "now = "+ now);
        if (last_noti != now) {
            last_noti = now;
            mBuilder.setContentTitle(getResources().getString(R.string.uploading_background))
                    .setContentText(getProcessStr())
                    .setProgress(getAllUploadCount(), UploadUploadedCount, false);
            // Displays the progress bar for the first time.
            mManager.notify(notifyNotificationID, mBuilder.build());
        }

    }

    Runnable uploadDataRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
            Log.i(TAG, "uploadDataRunnable called ");

            if (isBackgroundRunnableRunning) {
                Log.i(TAG, "isBackgroundRunnableRunning : " + isBackgroundRunnableRunning);
                if (UploadUploadedCount < getAllUploadCount()) {

                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                        isBackgroundRunnableRunning = false;
                        uploadOneData();
                    }
                }
            }
            Log.i(TAG, "uploadDataRunnable called ");

        }
    };

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

                android.util.Log.i(TAG, "ConnectivityManager.onAvailable called ");

                if (isBackgroundRunnableRunning) {
                    Log.i(TAG, "isBackgroundRunnableRunning : " + isBackgroundRunnableRunning);
                    if (UploadUploadedCount < getAllUploadCount()) {

                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            isBackgroundRunnableRunning = false;
                            uploadOneData();
                        }
                    }
                }
                //Log.i(TAG, "ConnectivityManager.onAvailable called called ");

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
    public void onDestroy() {
        super.onDestroy();
        //service關閉後重開方式↓
        if (RESTARTSERVICE)
            startService(new Intent(this, UploadDataService.class));

        mConnectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public File getMainDirectoryName() {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        File mainDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Constants.PACKAGE_DIRECTORY_PATH), "Demo");

//        File mainDir = new File(Constants.PACKAGE_DIRECTORY_PATH, "Demo");

        Log.i(TAG, "Demo File is presented at " + mainDir);
//        mainDir.mkdirs();
//        //If File is not present create directory
        if (!mainDir.exists()) {
            mainDir.mkdirs();
            Log.i(TAG, "Directory not created");
        }
        return mainDir;
    }

    private void resetUploadDataToSharePref() {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_MINUKU_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_NOTIREMOVE_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_ACC_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_PREF_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_ACTION_LIST);

        editor.putInt(EXTRA_LAST_TIME, 0)
                .commit();


        android.util.Log.i(TAG, "resetUploadDataToSharePref ");
    }
}

