package labelingStudy.nctu.boredom_detection.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;
import labelingStudy.nctu.minuku.config.Constants;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class UploadService extends Service {

    private static final String TAG = "UploadService";

    private ScheduledExecutorService mScheduledExecutorService;
    ScheduledFuture<?> mScheduledUpload;

    private NotificationManager mManager;
    private Notification.Builder mBuilder;
    private int notifyNotificationID = 13;
    private int notifyNotificationCode = 305;


    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    public static final String UPLOADED_ID = "uploaded_id";
    public static final String UPLOADED_STATUS = "uploaded_success";

    /** Intent Extras **/
    public static final String EXTRA_ID_LIST = "extra_upload_ids";
    public static final String EXTRA_ID_NOTE_LIST = "extra_upload_ids_note";
    public static final String EXTRA_UPLADE_SUCCESS = "extra_upload_success";
    public static final String EXTRA_NOTE_ID_LIST = "extra_note_ids";
    public static final String EXTRA_NON_PRIVATE = "extra_non_private";
    public static final String EXTRA_PRIVATE = "extra_private";


    // [START declare_ref]

    private boolean pushToRealtimeDataBase_Image = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_Image;
    private boolean pushToRealtimeDataBase_sys = labelingStudy.nctu.boredom_detection.config.Constants.pushToRealtimeDataBase_sys;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END declare_ref]

    private ImageDataRecordDAO imageDataRecordDAO;
    private NoteDataRecordDAO noteDataRecordDAO;
    private SharedPreferences sharedPrefs;


    ConnectivityManager mConnectivityManager = null;
    NetworkRequest.Builder builder;
    ConnectivityManager.NetworkCallback networkCallback;

    File imgFile;

    String PID = "";

    Gson gson = new Gson();

    // upload images max
    int UploadUploadedCount = 0;
    int UploadSuccessCount = 0;
    int PrivateCount = 0;
    int nonPrivateCount = 0;
    int UploadFailCount = 0;
    int UploadNoFileCount = 0;
    public List<String> WaitingUploadIdList =  new ArrayList<String>();
    public List<String> WaitingUploadIdNoteList =  new ArrayList<String>();
    public List<String> NoteIdList =  new ArrayList<String>();
    public List<String> UploadSuccessList =  new ArrayList<String>();

    private NoteDataRecord now_process_note = null;

    boolean RESTARTSERVICE = true;

    public static boolean isBackgroundServiceRunning = false;
    public static boolean isBackgroundRunnableRunning = false;
    Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        mScheduledExecutorService = Executors.newScheduledThreadPool(Constants.NOTIFICATION_UPDATE_THREAD_SIZE);
        isBackgroundServiceRunning = false;

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        builder = new NetworkRequest.Builder();

        // [START get_storage_ref]
        storageReference = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]

        imageDataRecordDAO = appDatabase.getDatabase(this).imageDataRecordDAO();
        noteDataRecordDAO = appDatabase.getDatabase(this).noteDataRecordDAO();

        imgFile = new File(Environment.getExternalStoragePublicDirectory(
                Constants.PACKAGE_DIRECTORY_PATH), "Demo");
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

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

            uploadImages();
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
                uploadImageRunnable,
                90,
                300,
                TimeUnit.SECONDS);

    }

    private void retrivesharedPreferences() {
        WaitingUploadIdList.clear();
        NoteIdList.clear();
        //UploadSuccessList.clear();
        WaitingUploadIdNoteList.clear();
        WaitingUploadIdList = Utils.getArrayList(sharedPrefs,gson,  EXTRA_ID_LIST);
        NoteIdList = Utils.getArrayList(sharedPrefs, gson, EXTRA_NOTE_ID_LIST);
        WaitingUploadIdNoteList = Utils.getArrayList(sharedPrefs, gson, EXTRA_ID_NOTE_LIST);
        PrivateCount = sharedPrefs.getInt(EXTRA_PRIVATE, 0);
        nonPrivateCount = sharedPrefs.getInt(EXTRA_NON_PRIVATE, 0);
        Log.i(TAG,"retrivesharedPreferences : " + WaitingUploadIdList);
    }


    private void resetUploadImageToSharePref() {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_ID_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_NOTE_ID_LIST);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_UPLADE_SUCCESS);
        Utils.resetStringIntoSharePref(sharedPrefs, EXTRA_ID_NOTE_LIST);

        editor.putInt(EXTRA_NON_PRIVATE, 0)
                .putInt(EXTRA_PRIVATE, 0)
                .apply();


        Log.i(TAG,"setUploadImageToSharePref ");
    }


    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadId(String id, String success) {

        Log.i(TAG, "broadcastUploadId " + id);
        Intent broadcast = new Intent(UPLOAD_COMPLETED)
                .putExtra(UPLOADED_ID, id)
                .putExtra(UPLOADED_STATUS, success);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    public void uploadImages()
    {
        Log.i(TAG, "upload images called");
        try {
            UploadUploadedCount = 0;
            UploadSuccessCount = 0;
            UploadFailCount = 0;
            UploadNoFileCount = 0;

            mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;

            mBuilder = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.uploading_images))
                    .setContentText(UploadUploadedCount + "/" + WaitingUploadIdList.size())
                    //.setStyle(bigTextStyle)
                    //.setContentIntent(pending)
                    //.setAutoCancel(true)
                    //.setDefaults(Notification.DEFAULT_ALL);
                    .setProgress(100,0,false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification =  mBuilder
                        .setSmallIcon(Utils.getNotificationIcon(mBuilder))
                        .setChannelId(Constants.UPLOAD_CHANNEL_ID)
                        .build();
            } else {
                notification =  mBuilder
                        .setSmallIcon(Utils.getNotificationIcon(mBuilder))
                        .build();
            }

            mManager.notify(notifyNotificationID, notification);

            uploadOneImage();
        }catch (Exception e)
        {
            labelingStudy.nctu.minuku.logger.Log.e(TAG, "upload images error:" + e.getMessage());
        }
    }

    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "uploadImageRunnable called ");

            if(isBackgroundRunnableRunning) {
                Log.i(TAG, "isBackgroundRunnableRunning : " + isBackgroundRunnableRunning);
                if (UploadUploadedCount < WaitingUploadIdList.size()) {

                    if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                        if(isBackgroundRunnableRunning) {
                            isBackgroundRunnableRunning = false;
                            uploadOneImage();
                        }
                    }
                }
            }
            Log.i(TAG, "uploadImageRunnable(sendNotification) called ");

        }
    };

    public void uploadOneImage()
    {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if(sharedPrefs.getInt(EXTRA_NON_PRIVATE, 0) != nonPrivateCount)
                        {
                            retrivesharedPreferences();
                        }
                        if( UploadUploadedCount < WaitingUploadIdList.size()) {
                            if(!Utils.isNetworkAvailableforUpload(mContext, sharedPrefs))
                            {
                                isBackgroundRunnableRunning = true;
                                mBuilder.setContentTitle("暫停上傳圖片，連上無線網路會自動繼續上傳")
                                        .setContentText(UploadUploadedCount + "/" + WaitingUploadIdList.size())
                                        .setProgress(WaitingUploadIdList.size(), UploadUploadedCount, false);
                                // Displays the progress bar for the first time.
                                mManager.notify(notifyNotificationID, mBuilder.build());

                            }else {
                                Log.i(TAG, "uploadOneImage :" + UploadUploadedCount + "/" + WaitingUploadIdList.size());
                                List<ImageDataRecord> list = imageDataRecordDAO.getByPKId(Long.parseLong(WaitingUploadIdList.get(UploadUploadedCount)));
                                if (!list.isEmpty()) {
                                    ImageDataRecord a = list.get(0);
                                    File f = new File(imgFile + "/" + a.getFileName());
                                    if (f.exists() && !f.isDirectory()) {
                                        Uri file = Uri.fromFile(f);

                                        String day = Utils.getUploadDay();
                                        String labelName = Utils.getLabelStringById(mContext, a.getLabel());
                                        String path = day + "/" + PID + "/" + labelName + "/" + PID + "_" + a.getFileName();

                                        StorageReference storageRef = storageReference.child(path);

                                        UploadTask uploadTask = storageRef.putFile(file);

                                        // Register observers to listen for when the download is done or if it fails
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                UploadUploadedCount++;
                                                UploadFailCount++;
                                                mBuilder.setContentTitle(getResources().getString(R.string.uploading_images))
                                                        .setContentText(UploadUploadedCount + "/" + WaitingUploadIdList.size())
                                                        .setProgress(WaitingUploadIdList.size(), UploadUploadedCount, false);
                                                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                                                // Displays the progress bar for the first time.
                                                mManager.notify(notifyNotificationID, mBuilder.build());
                                                uploadOneImage();
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.i(TAG, "uploadImage file " + taskSnapshot.getMetadata().getName() + " success.");

                                                String filename = taskSnapshot.getMetadata().getName();
                                                String docid = taskSnapshot.getMetadata().getName().split("\\.")[0];

                                                if (filename.split("_").length > 1) {
                                                    filename = filename.split("_")[1];
                                                }
                                                List<ImageDataRecord> list = imageDataRecordDAO.getByPKFileName(filename);
                                                if (list.size() > 0) {
                                                    ImageDataRecord a = list.get(0);
                                                    if(!pushToRealtimeDataBase_Image) {
                                                        // Add a new document with a generated ID
                                                        String dd = a.getFileName().substring(0, 8);
                                                        db.collection(PID).document(dd)
                                                                .collection("ImageRecord")
                                                                .document(docid)
                                                                .set(getImageUploadMap(a, docid, true))
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e(TAG, "Error writing Image document " + e.getMessage());
                                                                    }
                                                                });
                                                    }else{
                                                        String today = Utils.getUploadDay();
                                                        database.getReference("Logs")
                                                                .child(today)
                                                                .child(PID)
                                                                .child("ImageRecord")
                                                                .child(docid)
                                                                .setValue(getImageUploadMap(a, docid,false))
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Write failed
                                                                        Log.e(TAG, "upload Image Fail " + e.getMessage());
                                                                    }
                                                                });
                                                    }
                                                    a.setIsUpload(1);
                                                    imageDataRecordDAO.updateOne(a);
                                                    addOneSuccessForNote(get_Id_note(a.get_id() + "", UploadUploadedCount));
                                                }

                                                UploadUploadedCount++;
                                                UploadSuccessCount++;
                                                mBuilder.setContentTitle(getResources().getString(R.string.uploading_images))
                                                        .setContentText(UploadUploadedCount + "/" + WaitingUploadIdList.size())
                                                        .setProgress(WaitingUploadIdList.size(), UploadUploadedCount, false);
                                                // Displays the progress bar for the first time.
                                                mManager.notify(notifyNotificationID, mBuilder.build());
                                                uploadOneImage();
                                            }
                                        });


                                    } else {
                                        Log.i(TAG, "uploadImage file " + a.getFileName() + "is  not found, skip");
                                        a.setIsUpload(1);
                                        imageDataRecordDAO.updateOne(a);
                                        UploadNoFileCount++;
                                        UploadUploadedCount++;
                                        uploadOneImage();
                                    }
                                } else {
                                    Log.i(TAG, "uploadImage id " + WaitingUploadIdList.get(UploadUploadedCount).toString() + "is not found, skip");
                                    UploadUploadedCount++;
                                    UploadFailCount++;
                                    uploadOneImage();
                                }
                            }

                        } else {

                            mBuilder.setContentText(getResources().getString(R.string.upload_completed))
                                    // Removes the progress bar
                                    .setProgress(0, 0, false);
                            mManager.notify(notifyNotificationID, mBuilder.build());
                            String today = Utils.getUploadDay();

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                            String docId = formatter.format(new Date());

                            // Add a new document with a generated ID
                            if(!pushToRealtimeDataBase_sys) {
                                db.collection(PID).document(today)
                                        .collection("SystemData")
                                        .document(docId)
                                        .set(getSysUploadMap(WaitingUploadIdList.size(), PrivateCount, UploadSuccessCount, UploadFailCount, UploadNoFileCount, true))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding System document", e);
                                            }
                                        });
                            }else{
                                database.getReference("Logs")
                                        .child(today)
                                        .child(PID)
                                        .child("SystemData")
                                        .child(docId)
                                        .setValue(getSysUploadMap(WaitingUploadIdList.size(), PrivateCount, UploadSuccessCount, UploadFailCount, UploadNoFileCount, false))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Write failed
                                                Log.e(TAG, "upload System Fail " + e.getMessage());
                                            }
                                        });
                            }

                            for(int i=0; i < NoteIdList.size(); i++){
                                List<NoteDataRecord> list = noteDataRecordDAO.getByPKId(Long.parseLong(NoteIdList.get(i)));
                                if(!list.isEmpty())
                                {
                                    NoteDataRecord t = list.get(0);
                                    String str = t.getText();
                                    str = str.replace(getString(R.string.main_uploading),"");
                                    if(UploadSuccessList.size() == 0) {
                                        t.setText(getString(R.string.main_uploading_success) + str + "\n" + getString(R.string.uploaded_images, "0"));
                                    }else{
                                        t.setText(getString(R.string.main_uploading_success)+ str + "\n" + getString(R.string.uploaded_images, UploadSuccessList.get(i)));
                                    }

                                    t.setIsUpload(1);
                                    noteDataRecordDAO.updateOne(t);
                                }
                            }
                            String json = gson.toJson(NoteIdList);
                            String json1 = gson.toJson(UploadSuccessList);
                            broadcastUploadId(json, json1);

                            resetUploadImageToSharePref();
                            PrivateCount = 0;
                            nonPrivateCount = 0;
                            UploadUploadedCount = 0;
                            UploadSuccessCount = 0;
                            UploadFailCount = 0;
                            UploadNoFileCount = 0;
                            WaitingUploadIdList.clear();
                            WaitingUploadIdNoteList.clear();
                            NoteIdList.clear();
                            UploadSuccessList.clear();

                            Log.i(TAG, "stopping");
                            RESTARTSERVICE = false;
                            stopSelf();

                        }
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //service關閉後重開方式↓
        if(RESTARTSERVICE)
            startService(new Intent(this, UploadService.class));

        mConnectivityManager.unregisterNetworkCallback(networkCallback);
    }

    private Map<String, Object> getImageUploadMap(ImageDataRecord a, String docid,  boolean containTimestamp) {
        final Map<String, Object> record = new HashMap<>();
        if(containTimestamp) {
            record.put("timestamp", FieldValue.serverTimestamp());
        }
        String labelName = Utils.getLabelStringById(mContext, a.getLabel());
        record.put("fileName", docid);
        record.put("label", labelName);
        record.put("curr_act", a.getDoing());
        record.put("isUploaded", 1);
        record.put("labelTime", a.getLabeltime());
        record.put("labelTimeString", a.getLabelTimeString());
        record.put("uploadTime", a.getConfirmtime());
        record.put("uploadTimeString", a.getConfirmTimeString());
        return record;
    }

    private Map<String, Object> getSysUploadMap(int CategoryCount, int NoCategoryCount, int UploadSuccessCount, int  UploadFailCount, int UploadNoFileCount,  boolean containTimestamp) {
        final Map<String, Object> record = new HashMap<>();
        if(containTimestamp) {
            record.put("timestamp", FieldValue.serverTimestamp());
        }
        record.put("CategoryCount",  CategoryCount);
        record.put("NoCategoryCount", NoCategoryCount);
        record.put("upload_success", UploadSuccessCount);
        record.put("upload_fail", UploadFailCount);
        record.put("error", UploadNoFileCount);
        return record;
    }

    public void getDeviceid(){

        TelephonyManager mngr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        int permissionStatus= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if(permissionStatus== PackageManager.PERMISSION_GRANTED){
            Constants.DEVICE_ID = mngr.getDeviceId();
            if(Constants.DEVICE_ID == null){
                Constants.DEVICE_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

            labelingStudy.nctu.minuku.logger.Log.e(TAG,"DEVICE_ID"+Constants.DEVICE_ID+" : "+mngr.getDeviceId());

        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);

        return filter;
    }

    private String get_Id_note(String id, int idx)
    {
//        Log.d(TAG, "get_Id_note : " + id +" " + idx );
        if(id.equalsIgnoreCase(WaitingUploadIdList.get(idx)))
        {
            return WaitingUploadIdNoteList.get(idx);
        }

        for(int i = 0; i < WaitingUploadIdList.size(); i++)
        {
            if(id.equalsIgnoreCase(WaitingUploadIdList.get(i)))
            {

                return WaitingUploadIdNoteList.get(i);
            }
        }
        return "";
    }

    private void addOneSuccessForNote(String note_id)
    {
        Log.d(TAG, "addOneSuccesForNote : " + note_id);
        Log.d(TAG, "NoteIdList :" + NoteIdList);
//        Log.d(TAG, "WaitingUploadIdNoteList :" + WaitingUploadIdNoteList);
        Log.d(TAG, "UploadSuccessList :" + UploadSuccessList);
        if(NoteIdList.size()!=UploadSuccessList.size()) {

            if(sharedPrefs.getString(EXTRA_UPLADE_SUCCESS, null) == null)
            {
                UploadSuccessList.clear();
                for (int i = 0; i < NoteIdList.size(); i++) {
                    UploadSuccessList.add("0");
                }
            }else{
                UploadSuccessList.clear();
                UploadSuccessList = Utils.getArrayList(sharedPrefs, gson, EXTRA_UPLADE_SUCCESS);
                for (int i = 0; i < NoteIdList.size() - UploadSuccessList.size(); i++) {
                    UploadSuccessList.add("0");
                }
            }
            String json_status = gson.toJson(UploadSuccessList);

            Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_UPLADE_SUCCESS, json_status);

        }

        for(int i = 0; i < NoteIdList.size(); i++)
        {
            if(note_id.equalsIgnoreCase(NoteIdList.get(i)))
            {
                int tmp = Integer.parseInt(UploadSuccessList.get(i));
                tmp++;
                if(i < UploadSuccessList.size())
                    UploadSuccessList.set(i, tmp+"");
                String json_status = gson.toJson(UploadSuccessList);


                Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_UPLADE_SUCCESS, json_status);

                break ;
            }
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

                Log.i(TAG, "ConnectivityManager.onAvailable called ");

                if(isBackgroundRunnableRunning) {
                    Log.i(TAG, "isBackgroundRunnableRunning : " + isBackgroundRunnableRunning);
                    if (UploadUploadedCount < WaitingUploadIdList.size()) {
                        if (Utils.isNetworkAvailableforUpload(mContext, sharedPrefs)) {
                            if(isBackgroundRunnableRunning) {
                                isBackgroundRunnableRunning = false;
                                uploadOneImage();
                            }
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
}
