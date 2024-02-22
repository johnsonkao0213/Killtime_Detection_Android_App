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

package labelingStudy.nctu.boredom_detection;





import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.javatuples.Tuple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.Receiver.NotiButtonsReceiver;
import labelingStudy.nctu.boredom_detection.Receiver.NotificationDismissedReceiver;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationEventRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.boredom_detection.service.BackgroundService;
import labelingStudy.nctu.boredom_detection.view.AdvertisementActivity;
import labelingStudy.nctu.boredom_detection.view.CrowdsourcingActivity;
import labelingStudy.nctu.boredom_detection.view.NewsActivity;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.model.DataRecord.AccessibilityDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.ActivityRecognitionDataRecord;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.AppUsageDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.BatteryDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.ConnectivityDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.RingerDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.TelephonyDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.TransportationModeDataRecord;
import labelingStudy.nctu.minuku.streamgenerator.TransportationModeStreamGenerator;

/**
 * Created by neera_000 on 3/26/2016.
 *
 * modified by shriti 07/22/2016
 */
public class Utils {

    public static String TAG = "Utils";
    /**
     * Given an email Id as string, encodes it so that it can go into the firebase object without
     * any issues.
     * @param unencodedEmail
     * @return
     */


    private static Notification surveyNoti = null;
    private static long expiredIntentionTime = 30 * 1 * 1000; //Intention Survey過幾秒後取消
    private static long expiredStateTime = 15 * 60 * 1000;  //State Survey過幾分鐘後取消
    public static int notifySurveyID = 77;
    private static int notifySurveyCode = 777;
    private static int notifyStateSurveyID = 88;
    private static NotificationManager mNotificationManager;
    private static Handler handler;
    private static NotificationDataRecordDAO NotificationDataRecordDAO;
    private static AnswerDataRecordDAO answerDataRecordDAO;
    private static labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO NotificationEventRecordDAO;
    static SimpleDateFormat simpledateFormat = new SimpleDateFormat("yyyyMMddHH");
    private static long surveyId;
    private static String surveyFirebasePK;
    private static long surveyStateId;
    private static String surveyStateFirebasePK;

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("setSurvey", "notify");
            if(surveyNoti!=null) {
                mNotificationManager.notify(notifySurveyID, surveyNoti);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    removeNotification(notifySurveyID);
                }
            }

        }
    };

    private static void removeNotification(final int id) {
        Handler handler = new Handler();
        long delayInMilliseconds = expiredIntentionTime; //Survey過幾分鐘後取消
        handler.postDelayed(new Runnable() {
            public void run() {
                mNotificationManager.cancel(id);
            }
        }, delayInMilliseconds);
    }


    public static final String encodeEmail(String unencodedEmail) {
        if (unencodedEmail == null) return null;
        return unencodedEmail.replace(".", ",");
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String toPythonTuple(Tuple tuple){

        String pythontuple = "("+tuple.toString().replace("[","").replace("]","")+")";

        return pythontuple;
    }

    public static boolean isEnglish(String charaString){

        return charaString.matches("^[a-zA-Z]*");

    }

    public static String tupleConcat(Tuple tuple1, Tuple tuple2){

        String pythontuple = "(" +tuple1.toString().replace("[","").replace("]","")+
                ", "+tuple2.toString().replace("[","").replace("]","")+
                ")";

        return pythontuple;
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String getActivityStringType(String activityString){

        switch (activityString){

            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_ON_FOOT:
                return "walk";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_ON_BICYCLE:
                return "bike";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_IN_VEHICLE:
                return "car";
            case TransportationModeStreamGenerator.TRANSPORTATION_MODE_NAME_NO_TRANSPORTATION:
                return "static";
            default:
                return "";
        }
    }

    public static void setBoredomAppRunningStatus (Context context, boolean status) {
        Class<?> serviceClass = BackgroundService.class;



    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        Log.i(TAG, "isMyServiceRunning called");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG, "isMyServiceRunning true");
                return true;
            }
        }
        Log.i(TAG, "isMyServiceRunning false");
        return false;
    }


    public static boolean isNetworkConneted(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static boolean isNetworkAvailableforUpload(Context context, SharedPreferences sharedPrefs) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if(activeNetworkInfo != null) {
                String n = sharedPrefs.getString("NetworkBuffet", "");
//                android.util.Log.i(TAG, "isNetworkAvailable -> Network Buffet : " + n);
                if (!n.isEmpty()) {

                    if (n.equalsIgnoreCase("y")) {
                        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
                    } else {

                        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                            android.util.Log.i(TAG, "isNetworkAvailable -> activeNetworkInfo.getType() : WIFI");
                            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
                        } else {
//                            android.util.Log.i(TAG, "isNetworkAvailable -> activeNetworkInfo.getType() : " + activeNetworkInfo.getType());
                            return false;
                        }
                    }
                }
            }
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if(activeNetworkInfo!=null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                    android.util.Log.i(TAG, "isNetworkAvailable -> activeNetworkInfo.getType() : WIFI");
                    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
                } else {
//                    android.util.Log.i(TAG, "isNetworkAvailable -> activeNetworkInfo.getType() : " + activeNetworkInfo.getType());
                    return false;
                }
            }
        }

        return false;
    }

    public static boolean isNetworkBuffet(SharedPreferences sharedPrefs)
    {
        String n = sharedPrefs.getString("NetworkBuffet","");

        if( n.equalsIgnoreCase("y"))
            return true;

        return false;
    }

    public static boolean isSavingDataToWhere(SharedPreferences sharedPrefs, String where){
        //TODO
        String n = sharedPrefs.getString("isSavingData","not");

        if( n.equalsIgnoreCase(where))
            return true;

        return false;
    }




    public static boolean isActiveTiming(SharedPreferences sharedPrefs)
    {
        boolean condition =  true;

        LocalTime now =  LocalTime.now();
        int currentTime =  now.getHour() * 60 + now.getMinute();

        int recording_end = sharedPrefs.getInt("recording_end", 1320);
        int recording_start = sharedPrefs.getInt("recording_start", 600);

        //Log.i(TAG, "isActiveTiming recording_start " + recording_start);
        //Log.i(TAG, "isActiveTiming recording_end " + recording_end);
        //Log.i(TAG, "isActiveTiming now " + currentTime);
        if(recording_start < recording_end){

            if(currentTime< recording_start || currentTime>recording_end)
            {
                condition = false;
            }
        } else if(recording_start > recording_end){

            if(currentTime< recording_start && currentTime>recording_end)
            {
                condition = false;
            }

        }
        //Log.i(TAG, "isActiveTiming result " + condition  + " " + currentTime);
        return condition;
    }

    public static String getUploadDay() {
        /*
        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.HOUR_OF_DAY) < 6)
        {
            now.add(Calendar.DATE, -1);
            return String.format("%d%02d%02d", now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
        }
        return String.format("%d%02d%02d", now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
         */
        return simpledateFormat.format(new Date());
    }


    public static Map<String, Object> getAccessibilityUploadMap(MobileAccessibilityDataRecord a)
    {
        final Map<String, Object> record = new HashMap<>();
        record.put("app", a.getApp());
        record.put("pack", a.getPack());
        record.put("text", a.getText());
        record.put("type", a.getType());
        record.put("extra", a.getExtra());
        record.put("detectedtime", a.creationTime);

        return record;
    }


    public static  Map<String, Object> getNotiRemoveUploadMap(NotificationRemoveRecord a) {
        final Map<String, Object> record = new HashMap<>();
        record.put("appName", a.appName);
        record.put("title", a.title);
        record.put("postTime", a.postTime);
        record.put("removeTime", a.removeTime);
        record.put("reason", a.reason);

        return record;
    }



    public static  Map<String, Object> getMinukuUploadMap(labelingStudy.nctu.minuku.dao.AccessibilityDataRecordDAO AccessibilityDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.ActivityRecognitionDataRecordDAO ActivityRecognitionDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.AppUsageDataRecordDAO AppUsageDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.BatteryDataRecordDAO BatteryDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.ConnectivityDataRecordDAO ConnectivityDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.RingerDataRecordDAO RingerDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.TelephonyDataRecordDAO TelephonyDataRecordDAO,
                                                          labelingStudy.nctu.minuku.dao.TransportationModeDataRecordDAO TransportationModeDataRecordDAO,
                                                          MinukuDataRecord a, boolean containTimestamp) {

        final Map<String, Object> record = new HashMap<>();
        //record.put("PrimaryKey", a.get_id());
        if (containTimestamp) {
            record.put("timestamp", FieldValue.serverTimestamp());
        }

        List<AccessibilityDataRecord> accessibilityList = AccessibilityDataRecordDAO.getById(a.AccessibilityKey);
        if (accessibilityList.size() > 0) {
            record.put("Accessibility_app", accessibilityList.get(0).getApp());
            record.put("Accessibility_pack", accessibilityList.get(0).getPack());
            record.put("Accessibility_text", accessibilityList.get(0).getText());
            record.put("Accessibility_type", accessibilityList.get(0).getType());
            record.put("Accessibility_extra", accessibilityList.get(0).getExtra());
            record.put("Accessibility_detectedtime", accessibilityList.get(0).getCreationTime());
        } else {
            Log.e(TAG, "AccessibilityDataRecord " + a.AccessibilityKey + " is not found");
        }


        List<ActivityRecognitionDataRecord> activityrecognitionList = ActivityRecognitionDataRecordDAO.getById(a.ActivityRecognitionKey);
        if (activityrecognitionList.size() > 0) {
            record.put("ActivityRecognition_ProbableActivities", activityrecognitionList.get(0).getProbableActivities());
            record.put("ActivityRecognition_MostProbableActivity", activityrecognitionList.get(0).getMostProbableActivity());
            record.put("ActivityRecognition_LatestDetectionTime", activityrecognitionList.get(0).getDetectedtime());

        } else {
            Log.e(TAG, "ActivityRecognitionDataRecord " + a.ActivityRecognitionKey + " is not found");
        }


        List<AppUsageDataRecord> appusageList = AppUsageDataRecordDAO.getById(a.AppUsageKey);
        if (appusageList.size() > 0) {
            record.put("AppUsage_LastestForegroundActivity", appusageList.get(0).getLatest_Foreground_Activity());
            record.put("AppUsage_LastestForegroundPackage", appusageList.get(0).getLatest_Foreground_Package());
            record.put("AppUsage_LastestForegroundApp", appusageList.get(0).getLatest_Used_App());
            record.put("AppUsage_sScreen_Status", appusageList.get(0).getScreen_Status());


        } else {
            Log.e(TAG, "AppUsageDataRecord " + a.AppUsageKey + " is not found");
        }


        List<BatteryDataRecord> batteryList = BatteryDataRecordDAO.getById(a.BatteryKey);
        if (batteryList.size() > 0) {
            record.put("Battery_mBatteryLevel", batteryList.get(0).getBatteryLevel());
            record.put("Battery_mBatteryPercentage", batteryList.get(0).getBatteryPercentage());
            record.put("Battery_mBatteryChargingState", batteryList.get(0).getBatteryChargingState());
            record.put("Battery_isCharging", batteryList.get(0).isCharging());

        } else {
            Log.e(TAG, "BatteryDataRecord " + a.BatteryKey + " is not found");
        }


        List<ConnectivityDataRecord> connectivityList = ConnectivityDataRecordDAO.getById(a.ConnectivityKey);
        if (connectivityList.size() > 0) {
            record.put("Connectivity_isWifiConnected", connectivityList.get(0).isIsWifiConnected());
            record.put("Connectivity_NetworkType", connectivityList.get(0).getNetworkType());
            record.put("Connectivity_isNetworkAvailable", connectivityList.get(0).isNetworkAvailable());
            record.put("Connectivity_isConnected", connectivityList.get(0).IsConnected);
            record.put("Connectivity_isWifiAvailable", connectivityList.get(0).isIsWifiAvailable());
            record.put("Connectivity_isMobileAvailable", connectivityList.get(0).isIsMobileAvailable());
            record.put("Connectivity_isMobileConnected", connectivityList.get(0).isIsMobileConnected());

        } else {
            Log.e(TAG, "ConnectivityDataRecord " + a.ConnectivityKey + " is not found");
        }


        List<RingerDataRecord> ringerList = RingerDataRecordDAO.getById(a.RingerKey);
        if (ringerList.size() > 0) {
            record.put("Ringer_RingerMode", ringerList.get(0).getRingerMode());
            record.put("Ringer_AudioMode", ringerList.get(0).getAudioMode());
            record.put("Ringer_StreamVolumeMusic", ringerList.get(0).getStreamVolumeMusic());
            record.put("Ringer_StreamVolumeNotification", ringerList.get(0).getStreamVolumeNotification());
            record.put("Ringer_StreamVolumeRing", ringerList.get(0).getStreamVolumeRing());
            record.put("Ringer_StreamVolumeVoicecall", ringerList.get(0).getStreamVolumeVoicecall());
            record.put("Ringer_StreamVolumeSystem", ringerList.get(0).getStreamVolumeSystem());

        } else {
            Log.e(TAG, "RingerDataRecord " + a.RingerKey + " is not found");
        }


        List<TelephonyDataRecord> telephonyList = TelephonyDataRecordDAO.getById(a.TelephonyKey);
        if (telephonyList.size() > 0) {
            record.put("Telephony_mNetworkOperatorName", telephonyList.get(0).getNetworkOperatorName());
            record.put("Telephony_mCallState", telephonyList.get(0).getCallState());
            record.put("Telephony_mPhoneSignalType", telephonyList.get(0).getPhoneSignalType());
            record.put("Telephony_mGsmSignalStrength", telephonyList.get(0).getGsmSignalStrength());
            record.put("Telephony_mLTESignalStrength_dbm", telephonyList.get(0).getLTESignalStrength());
            record.put("Telephony_mCdmaSignalStrengthLevel", telephonyList.get(0).getCdmaSignalStrengthLevel());

        } else {
            Log.e(TAG, "TelephonyDataRecord " + a.TelephonyKey + " is not found");
        }


        List<TransportationModeDataRecord> transportationmodeList = TransportationModeDataRecordDAO.getById(a.TransportationModeKey);
        if (transportationmodeList.size() > 0) {
            record.put("TransportationMode_ConfirmedActivityString", transportationmodeList.get(0).getConfirmedActivityString());
            record.put("TransportationMode_SuspectTime", transportationmodeList.get(0).getSuspectedTime());
            record.put("TransportationMode_suspectedStartActivity", transportationmodeList.get(0).getSuspectedStartActivityString());
            record.put("TransportationMode_suspectedEndActivity", transportationmodeList.get(0).getSuspectedStopActivityString());


        } else {
            Log.e(TAG, "TransportationModeDataRecord " + a.TransportationModeKey + " is not found");

        }

        record.put("TimeString", a.getTimeString());
        record.put("TimeMillis", a.getTimeMillis());
        record.put("isScreenShotted", a.getIsScreenShotted());

        return record;
    }

    private Map<String, Object> getImageUploadMap(ImageDataRecord a, String docid,  boolean containTimestamp) {
        final Map<String, Object> record = new HashMap<>();
        if(containTimestamp) {
            record.put("timestamp", FieldValue.serverTimestamp());
        }
        record.put("fileName", docid);
        record.put("label", a.getLabel());
        record.put("curr_act", a.getDoing());
        record.put("isUploaded", 1);
        record.put("labelTime", a.getLabeltime());
        record.put("labelTimeString", a.getLabelTimeString());
        record.put("uploadTime", a.getConfirmtime());
        record.put("uploadTimeString", a.getConfirmTimeString());
        return record;
    }

    public static  Map<String, Object> getImageHashMap(SharedPreferences sharedPrefs, ImageDataRecord a, boolean containTimeStamp) {
        String PID = sharedPrefs.getString("ParticipantID", "");
        final Map<String, Object> record = new HashMap<>();
        if (containTimeStamp) {
            record.put("timestamp", FieldValue.serverTimestamp());
        }
        record.put("fileName", PID + "_" + a.getFileName());
        record.put("label", a.getLabel());
        record.put("curr_act", a.getDoing());
        record.put("isUploaded", 0);
        record.put("labelTime", a.getLabeltime());
        record.put("labelTimeString", a.getLabelTimeString());
        record.put("uploadTime", a.getConfirmtime());
        record.put("uploadTimeString", a.getConfirmTimeString());

        return record;
    }

    public static  Map<String, Object> getIntentActionUploadMap(ActionDataRecord a)
    {
        final Map<String, Object> record = new HashMap<>();
        record.put("action", a.getAction());
        record.put("detectedtime", a.getCreatedTime());

        return record;
    }

    public static void saveStringIntoSharePref(SharedPreferences sharedPrefs, String key, String value)
    {

        if( value.length() > 0)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            List<String> ret  = splitEqually(value,  8000);

            //Log.d(TAG, "saveStringIntoSharePref key: " + key + "  size : "+ret.size());
            editor.putInt(key +"_size", ret.size() );
            for(int i = 0; i<  ret.size(); i++)
            {
                editor.putString(key+"_"+i, ret.get(i));
            }
            editor.commit();
        }
    }

    public static void resetStringIntoSharePref(SharedPreferences sharedPrefs, String key)
    {
        int key_size = sharedPrefs.getInt(key+"_size", 0);
        if (key_size > 0)
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(key +"_size", 0 );
            for(int i = 0; i<  key_size; i++)
            {
                editor.putString(key+"_"+i, "");
            }
            editor.commit();
        }
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }


    public static ArrayList<String> getArrayList(SharedPreferences sharedPrefs, Gson gson, String key){

        String json = "";
        int key_size = sharedPrefs.getInt(key+"_size", 0);
        if (key_size > 0)
        {
            for(int i =0; i< key_size; i++)
            {
                json = json + sharedPrefs.getString(key+"_"+i, "");
            }
        }


        return getArrayListFromJson(gson, json);
    }

    public static ArrayList<String> getArrayListFromJson(Gson gson, String json){

        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> out = gson.fromJson(json, type);

        if(out == null)
            return new ArrayList<String>();
        return out;
    }

    public static void setIntentionSurvey(Context context, SharedPreferences sharedPrefs, NotificationManager notification_manager, Handler hndlr) {

        // init
        //surveyNoti = survey_noti;
        mNotificationManager = notification_manager;
        handler = hndlr;

        long lastNotiTime = sharedPrefs.getLong("lastNotiTime", -1);
        long gap = sharedPrefs.getLong("gap", -1);
        Random random = new Random();
        int rndm = random.nextInt(60) ;
        android.util.Log.i(TAG, "notifyESM rndm:" + rndm);
        gap =  30*60*1*1000 + Long.valueOf(rndm) * 1* 1000;  // 30mins
//        gap =  60*1*1000 + Long.valueOf(rndm) * 1* 1000;  //test 1 ~ 2mins

        long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();

        android.util.Log.i(TAG, "getIntention:" + gap);
        sharedPrefs.edit()
                .putLong("gap", gap)
                .putLong("lastNotiTime", currentTime)
                .commit();

        android.util.Log.i(TAG, "getIntentionESM next NotiTime: " + ScheduleAndSampleManager.getTimeString(currentTime + gap));
        android.util.Log.i(TAG, "to send notification");

        surveyId = createLongTypeID();
        String notiTime = getCurrentTimeString();
        surveyFirebasePK = Utils.createStringTypeID();
        Answers answer = Answers.getIntentionInstance();
        answer.reset_answer();
        answer.put_answer("notiTimeString", notiTime);
        answer.put_answer("notiSurveyType", "Intentions");
        Utils.updateAnswerData(context,answer,surveyId, surveyFirebasePK);

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        if (keyguardManager.inKeyguardRestrictedInputMode())
//            showIntentionSurveyNotification(context);

        if (Settings.canDrawOverlays(context))
            showIntentionSurveyWindow(context);
    }

    private static void showIntentionSurveyNotification(Context context) {
        RemoteViews customNotificationLayout = new RemoteViews(context.getPackageName(), R.layout.intention_notification_layout);
        // Set unique tags for each button
        String button1Text = context.getString(R.string.intention_mustdo_short);
        String button2Text = context.getString(R.string.intention_emotions_short);
        String button3Text = context.getString(R.string.intention_productivity_short);
        String button4Text = context.getString(R.string.intention_time_short);
        String button5Text = context.getString(R.string.intention_curiosity_short);
        String button6Text = context.getString(R.string.intention_habits_short);

        customNotificationLayout.setTextViewText(R.id.button1, button1Text);
        customNotificationLayout.setTextViewText(R.id.button2, button2Text);
        customNotificationLayout.setTextViewText(R.id.button3, button3Text);
        customNotificationLayout.setTextViewText(R.id.button4, button4Text);
        customNotificationLayout.setTextViewText(R.id.button5, button5Text);
        customNotificationLayout.setTextViewText(R.id.button6, button6Text);

        customNotificationLayout.setOnClickPendingIntent(R.id.button1, getPendingIntent(context, button1Text, notifySurveyID));
        customNotificationLayout.setOnClickPendingIntent(R.id.button2, getPendingIntent(context, button2Text, notifySurveyID));
        customNotificationLayout.setOnClickPendingIntent(R.id.button3, getPendingIntent(context, button3Text, notifySurveyID));
        customNotificationLayout.setOnClickPendingIntent(R.id.button4, getPendingIntent(context, button4Text, notifySurveyID));
        customNotificationLayout.setOnClickPendingIntent(R.id.button5, getPendingIntent(context, button5Text, notifySurveyID));
        customNotificationLayout.setOnClickPendingIntent(R.id.button6, getPendingIntent(context, button6Text, notifySurveyID));

        // fullScreenIntent makes noti stay longer on the screen
        PendingIntent fullScreenIntent = PendingIntent.getActivity(context, notifySurveyCode, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder noti = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentIntent(null)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setFullScreenIntent(fullScreenIntent, true)
                .setCustomContentView(customNotificationLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti.setTimeoutAfter(expiredIntentionTime); //Survey過幾分鐘後取消
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).setChannelId(Constants.SURVEY_CHANNEL_ID).build();
            mNotificationManager.notify(notifySurveyID, surveyNoti);
//            handler.postDelayed(runnable, 5 * 60 * 1000); //推薦內容後過幾分鐘發Survey

        } else {
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).build();
            mNotificationManager.notify(notifySurveyID, surveyNoti);
//            handler.postDelayed(runnable, 5 * 60 * 1000);
        }
    }

    private static PendingIntent getPendingIntent(Context context, String tag, int notifySurveyID) {
        Intent intent = new Intent(context, NotiButtonsReceiver.class);
        intent.setAction(tag); // Use the tag as the action
        intent.putExtra("notification_id", notifySurveyID);

        Bundle bundle = new Bundle();
        bundle.putLong("_id", Utils.getSurveyID());
        bundle.putString("_firebasePK", Utils.getSurveyFirebasePK());
        intent.putExtras(bundle);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static final View[] floatingViewContainer = new View[1];

    private static void showIntentionSurveyWindow(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        floatingViewContainer[0] = layoutInflater.inflate(R.layout.intention_notification_layout, null);

        String button1Text = context.getString(R.string.intention_mustdo_short);
        String button2Text = context.getString(R.string.intention_emotions_short);
        String button3Text = context.getString(R.string.intention_productivity_short);
        String button4Text = context.getString(R.string.intention_time_short);
        String button5Text = context.getString(R.string.intention_curiosity_short);
        String button6Text = context.getString(R.string.intention_habits_short);

        Button button1 = (Button) floatingViewContainer[0].findViewById(R.id.button1);
        button1.setText(button1Text);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button1Text, notifySurveyID));
            }
        });
        Button button2 = (Button) floatingViewContainer[0].findViewById(R.id.button2);
        button2.setText(button2Text);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button2Text, notifySurveyID));
            }
        });
        Button button3 = (Button) floatingViewContainer[0].findViewById(R.id.button3);
        button3.setText(button3Text);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button3Text, notifySurveyID));
            }
        });
        Button button4 = (Button) floatingViewContainer[0].findViewById(R.id.button4);
        button4.setText(button4Text);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button4Text, notifySurveyID));
            }
        });
        Button button5 = (Button) floatingViewContainer[0].findViewById(R.id.button5);
        button5.setText(button5Text);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button5Text, notifySurveyID));
            }
        });
        Button button6 = (Button) floatingViewContainer[0].findViewById(R.id.button6);
        button6.setText(button6Text);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(getIntent(context, button6Text, notifySurveyID));
            }
        });

        // Step 3: Set up the layout parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        // Position the window on the screen
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.y = -100;

        // Step 4: Add the view to the window
        windowManager.addView(floatingViewContainer[0], params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                View floatingView = floatingViewContainer[0];
                if (floatingView != null && floatingView.getParent() != null) {
                    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    windowManager.removeView(floatingView);
                    floatingViewContainer[0] = null;
                }
            }
        }, expiredIntentionTime);
    }

    public static void closeIntentionSurveyWindow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (floatingViewContainer[0] != null && floatingViewContainer[0].getParent() != null) {
            windowManager.removeView(floatingViewContainer[0]);
            floatingViewContainer[0] = null;
        }
    }

    private static Intent getIntent(Context context, String tag, int notifySurveyID) {
        Intent intent = new Intent(context, NotiButtonsReceiver.class);
        intent.setAction(tag); // Use the tag as the action
        intent.putExtra("notification_id", notifySurveyID);

        Bundle bundle = new Bundle();
        bundle.putLong("_id", Utils.getSurveyID());
        bundle.putString("_firebasePK", Utils.getSurveyFirebasePK());
        intent.putExtras(bundle);

        return intent;
    }
    public static boolean isSendSurvey(long gap, String lastNotiTimeString, SharedPreferences sharedPrefs){
        boolean send = false;

        long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
        long lastNotiTime = sharedPrefs.getLong(lastNotiTimeString, -1);

        if (Utils.isActiveTiming(sharedPrefs)) {
            if(gap == -1) {
                //first notice
                send = true;
            }else {
                if (currentTime > (lastNotiTime + gap)) {
                    // at least 1 hr
                    send = true;
                }
                else{
                    android.util.Log.i(TAG, lastNotiTimeString+ ":" + ScheduleAndSampleManager.getTimeString(lastNotiTime));
                    android.util.Log.i(TAG, lastNotiTimeString+ "+gap:" + ScheduleAndSampleManager.getTimeString(lastNotiTime+gap));

                }
            }
        }else{
            sharedPrefs.edit()
                    .putLong(lastNotiTimeString, currentTime)
                    .commit();
            android.util.Log.i(TAG, "It is not the active time.");
        }



        return send;
    }
    public static void setStateSurvey(Context context, SharedPreferences sharedPrefs, NotificationManager notification_manager, Handler hndlr, Bundle linkingBundle) {


        mNotificationManager = notification_manager;
        handler = hndlr;
        long lastStateNotiTime = sharedPrefs.getLong("lastStateNotiTime", -1);
        long stateGap = sharedPrefs.getLong("stateGap", -1);
        Random random = new Random();
        int rndm = random.nextInt(60) ;
        android.util.Log.i(TAG, "notifyESM rndm:" + rndm);
        stateGap =  60*60*1000 + Long.valueOf(rndm) * 60* 1000;  // 1hr ~ 2hrs
//        stateGap = 3*60*1*1000 + Long.valueOf(rndm) * 1* 1000;  //test 3 ~ 4mins


        long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();

        android.util.Log.i(TAG, "getState:" + stateGap);
        sharedPrefs.edit()
                .putLong("stateGap", stateGap)
                .putLong("lastStateNotiTime", currentTime)
                .commit();

        android.util.Log.i(TAG, "getStateESM next NotiTime: " + ScheduleAndSampleManager.getTimeString(currentTime + stateGap));
        android.util.Log.i(TAG, "to send notification");

        surveyStateId = createLongTypeID();
        String notiTime = getCurrentTimeString();
        surveyStateFirebasePK = Utils.createStringTypeID();
        Answers answer = Answers.getStateInstance();
        answer.reset_answer();
        answer.put_answer("notiTimeString", notiTime);
        answer.put_answer("notiSurveyType", "States");
        Utils.updateAnswerData(context,answer,surveyStateId, surveyStateFirebasePK);


        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        Intent resultIntent = new Intent();
        resultIntent.setComponent(new ComponentName(context, IntentionSurveyActivity.class));
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putLong("_id", surveyStateId);
        bundle.putString("_firebasePK", surveyStateFirebasePK);
        bundle.putBundle(IntentionSurveyActivity.LINKING_BUNDLE, linkingBundle);

        resultIntent.putExtras(bundle);
        PendingIntent pending = PendingIntent.getActivity(context, notifySurveyCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.notifyStateSurveyTitle))
                .setContentIntent(pending)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti.setTimeoutAfter(expiredStateTime); //stateSurvey過幾分鐘後取消
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).setChannelId(Constants.SURVEY_CHANNEL_ID).build();
            mNotificationManager.notify(notifyStateSurveyID, surveyNoti);
//            handler.postDelayed(runnable, 5 * 60 * 1000); //推薦內容後過幾分鐘發Survey

        } else {
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).build();
            mNotificationManager.notify(notifyStateSurveyID, surveyNoti);
//            handler.postDelayed(runnable, 5 * 60 * 1000);
        }


    }


    public static void notifyESM(Context context, SharedPreferences sharedPrefs, boolean debugMode, int notifyNotificationCode, NotificationManager notification_manager, Handler hndlr, NotificationDataRecordDAO notificationDataRecordDAO, labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO notificationEventRecordDAO) {

        // init
        //surveyNoti = survey_noti;
        mNotificationManager = notification_manager;
        handler = hndlr;

        NotificationDataRecordDAO = notificationDataRecordDAO;
        NotificationEventRecordDAO = notificationEventRecordDAO;
        ////////////

        long lastNotiTime = sharedPrefs.getLong("lastNotiTime", -1);
        long gap = sharedPrefs.getLong("gap", -1);
        long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();

//        Random random = new Random();
//        int rndm = random.nextInt(120) ;
//        android.util.Log.i(TAG, "notifyESM rndm:" + rndm);
//        gap =  60*60*1000 + Long.valueOf(rndm) * 60* 1000;  // 1hr ~ 3hr
//        gap = 60*60*1000; // 推薦內容間隔多久
        android.util.Log.i(TAG, "notifyESM gap:" + gap);
        sharedPrefs.edit()
                .putLong("gap", gap)
                .putLong("lastNotiTime", currentTime)
                .commit();

        android.util.Log.i(TAG, "notifyESM next NotiTime: " + ScheduleAndSampleManager.getTimeString(currentTime + gap));
        android.util.Log.i(TAG, "to send notification");


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        getNotification(context, sharedPrefs, debugMode, notifyNotificationCode, notifySurveyCode);


    }


    private static void getNotification(Context context, SharedPreferences sharedPrefs, boolean debugMode, int notifyNotificationCode, int notifySurveyCode) {
        android.util.Log.i(TAG, "getNotification called");

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();

        Notification result_noti = null;

        Intent resultIntent = new Intent();

        String notitype = decideNotificationType(sharedPrefs, debugMode);
        NotificationEventRecord notiQuestionRecord = getNotificationEvent(context, sharedPrefs, notitype);

        long new_noti_id = createLongTypeID();

        if(notiQuestionRecord !=null)
        {

            bigTextStyle.setBigContentTitle(notitype); //Constants.SURVEY_CHANNEL_NAME
            String text = notiQuestionRecord.getTitle();
            bigTextStyle.bigText(text);
            switch (notitype)
            {
                case "Advertisement":
                    resultIntent.setComponent(new ComponentName(context, AdvertisementActivity.class));
                    break;
                case "Crowdsourcing":
                case "Questionnaire":
                    resultIntent.setComponent(new ComponentName(context, CrowdsourcingActivity.class));
                    break;
                case "News":
                    resultIntent.setComponent(new ComponentName(context, NewsActivity.class));
                    break;
                default:
                    resultIntent.setComponent(new ComponentName(context, AdvertisementActivity.class));
                    break;

            }


            resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Bundle bundle = new Bundle();
            bundle.putLong("_id", new_noti_id);
            resultIntent.putExtras(bundle);

            // update isExpired by jj
            updateIsExpired(new_noti_id);

            PendingIntent pending = PendingIntent.getActivity(context, notifyNotificationCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder noti = new Notification.Builder(context)
                    // set title to type by jj
                    .setContentTitle(notitype)
                    .setContentText(text)
                    .setStyle(bigTextStyle)
                    .setContentIntent(pending)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setDeleteIntent(createOnDismissedIntent(context, new_noti_id));

            // set survey by jj
            android.util.Log.i("setSurvey", "setSurvey");
            setSurvey(context, notifySurveyCode, new_noti_id);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result_noti =  noti
                        .setSmallIcon(getNotificationIcon(noti))
                        .setChannelId(Constants.SURVEY_CHANNEL_ID)
                        .build();

                if(result_noti!=null) {
                    long current = System.currentTimeMillis();
                    //int notifyNotificationID = sharedPrefs.getInt("notifyNotificationID", -1);
                    NotificationDataRecord noticeData = new NotificationDataRecord(
                            notiQuestionRecord.getNotificationId(), notiQuestionRecord.getTitle(), notiQuestionRecord.getUrl(),
                            notiQuestionRecord.getOptions(), ScheduleAndSampleManager.getTimeString(current),
                            current,  notiQuestionRecord.getType(), new_noti_id);
                    NotificationDataRecordDAO.insertOne(noticeData);


                    result_noti.defaults |= Notification.DEFAULT_VIBRATE;
                    mNotificationManager.notify((int)new_noti_id, result_noti);
                }

            } else {
                result_noti = noti
                        .setSmallIcon(getNotificationIcon(noti))
                        .build();
                if(result_noti!=null) {
                    long current = System.currentTimeMillis();
                    //int notifyNotificationID = sharedPrefs.getInt("notifyNotificationID", -1);
                    NotificationDataRecord noticeData = new NotificationDataRecord(
                            notiQuestionRecord.getNotificationId(), notiQuestionRecord.getTitle(), notiQuestionRecord.getUrl(),
                            notiQuestionRecord.getOptions(), ScheduleAndSampleManager.getTimeString(current),
                            current,  notiQuestionRecord.getType(), new_noti_id);
                    NotificationDataRecordDAO.insertOne(noticeData);
                    android.util.Log.i(TAG, "getNotificationq1 :" + notiQuestionRecord.getTitle() + " getNotification _id :" + (int)new_noti_id);
                    result_noti.defaults |= Notification.DEFAULT_VIBRATE;

                    mNotificationManager.notify((int)new_noti_id, result_noti);
                }
            }
        }else{
            android.util.Log.e(TAG, "setNotification fail");
        }

        return;

    }


    private static void setSurvey(Context context,  int notifySurveyCode, long id) {
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        Intent resultIntent = new Intent();
        resultIntent.setComponent(new ComponentName(context, SurveyActivity.class));
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putLong("_id", id);
        resultIntent.putExtras(bundle);
        PendingIntent pending = PendingIntent.getActivity(context, notifySurveyCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder noti = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("請填寫問卷")
                .setStyle(bigTextStyle)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti.setTimeoutAfter(30*60*1000); //Survey過幾分鐘後取消
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).setChannelId(Constants.SURVEY_CHANNEL_ID).build();
            //mNotificationManager.notify(notifySurveyID, surveyNoti);
            handler.postDelayed(runnable, 1 * 60 * 1000); //推薦內容後過幾分鐘發Survey
            // test
            //handler.postDelayed(runnable, 15);
        } else {
            surveyNoti = noti.setSmallIcon(getNotificationIcon(noti)).build();
            //mNotificationManager.notify(notifySurveyID, surveyNoti);
            handler.postDelayed(runnable, 1 * 60 * 1000);
            // test
            //handler.postDelayed(runnable, 15);
        }
    }


    private static void updateIsExpired(final long id) {
        android.util.Log.i("updateIsExpired", "updateIsExpired" + id);
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        NotificationDataRecord notificationDataRecord = NotificationDataRecordDAO.getByNotifyNotificationID(id).get(0);
                        if(notificationDataRecord !=null) {
                            notificationDataRecord.setIsExpired(1);
                            NotificationDataRecordDAO.updateOne(notificationDataRecord);
                        }
                    }
                },
                900000
        );
    }

    public static int getNotificationIcon(Notification.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            notificationBuilder.setColor(Color.TRANSPARENT);
            return R.drawable.muilab_icon_noti;

        }
        return R.drawable.muilab_icon;
    }

    // decide the notification
    // output: notitype ->Advertisement, Crowdsourcing, Questionnaire, News
    private static String decideNotificationType(SharedPreferences sharedPrefs, boolean debugMode)
    {
        android.util.Log.i(TAG, "decideNotificationType called");
        String notitype = "Advertisement";
        int BASE = 20;
        int countAd = sharedPrefs.getInt("countAd", 0);
        int countCrowdsourcing = sharedPrefs.getInt("countCrowdsourcing", 0);
        int countQuestionnaire = sharedPrefs.getInt("countQuestionnaire", 0);
        int countNews = sharedPrefs.getInt("countNews", 0);

        if(debugMode) {
            android.util.Log.i(TAG, "countAd : " + countAd);
            android.util.Log.i(TAG, "countCrowdsourcing : " + countCrowdsourcing);
            android.util.Log.i(TAG, "countQuestionnaire : " + countQuestionnaire);
            android.util.Log.i(TAG, "countNews : " + countNews);
        }

        android.util.Log.i(TAG, "decideNotificationType tetst");
        int sum = countAd + countCrowdsourcing + countQuestionnaire + countNews;
        int PAd = (BASE/4) - countAd;
        int PCrowdsourcing = (BASE/4) - countCrowdsourcing;
        int PQuestionnaire = (BASE/4) - countQuestionnaire;
        int PNewsd = (BASE/4) - countNews;

        if(Math.round(sum/BASE) > 0) {
            int tmp = (BASE / 4) * Math.round(sum / BASE);
            PAd += tmp;
            PCrowdsourcing += tmp;
            PQuestionnaire  += tmp;
            PNewsd += tmp;
        }
        if(PAd < 0)
        {
            PAd = 0;
        }
        if(PCrowdsourcing < 0)
        {
            PCrowdsourcing = 0;
        }
        if(PQuestionnaire < 0)
        {
            PQuestionnaire = 0;
        }
        if(PNewsd < 0)
        {
            PNewsd = 0;
        }
        sum =  PAd + PCrowdsourcing + PQuestionnaire + PNewsd;
        if(debugMode) {
            android.util.Log.i(TAG, "P Ad : " + PAd);
            android.util.Log.i(TAG, "P Crowdsourcing : " + PCrowdsourcing);
            android.util.Log.i(TAG, "P Questionnaire : " + PQuestionnaire);
            android.util.Log.i(TAG, "P News : " + PNewsd);
        }

        Random r = new Random();
        int idx = r.nextInt(sum);
        //Log.i(TAG, "decideNotificationType idx : " + idx);

        if(idx < PAd)
        {
            notitype = "Advertisement";
        } else if(PAd <= idx && idx < (PAd+PCrowdsourcing)){
            notitype = "Crowdsourcing";
        } else if((PAd+PCrowdsourcing) <= idx && idx < (PAd+PCrowdsourcing+PQuestionnaire)){
            notitype = "Questionnaire";
        } else if((PAd+PCrowdsourcing+PQuestionnaire) <= idx && idx < sum){
            notitype = "News";
        }

        android.util.Log.i(TAG, "decideNotificationType result : " + notitype);

        // only test
        //if(notitype.equalsIgnoreCase("News"))
        //{
        //    notitype = "Advertisement";
        //}

        return notitype;
    }

    public static int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    public static long createLongTypeID(){
        Date now = new Date();
        long id = (long)(Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now)));
        return id;
    }

    public static String createStringTypeID(){
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String stringID = formatter.format(curDate);
        return stringID;
    }

    public static String getCurrentTimeString(){
        long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
        String currentTime = ScheduleAndSampleManager.getTimeString(current);
        return currentTime;
    }
    private static NotificationEventRecord getNotificationEvent(Context context, SharedPreferences sharedPrefs, String notitype)
    {
        android.util.Log.i(TAG, "getNotificationEvent called");

        if (notitype.equals("News")) {
            //   wait to finish
            NotificationEventRecord output = crawlNews(context, sharedPrefs);
            if(output!=null) {
                int countNews = sharedPrefs.getInt("countNews", 0);
                sharedPrefs.edit()
                        .putInt("countNews", ++countNews)
                        .commit();
                return output;
            } else {
                return null;
            }
        }
        List<NotificationEventRecord> List = NotificationEventRecordDAO.findValidNotifications(notitype, true, false);

        if(List.size() > 0){
            Random r = new Random();
            NotificationEventRecord output = List.get(r.nextInt(List.size()));
            output.setIsUsed(true);
            NotificationEventRecordDAO.updateOne(output);

            switch(notitype)
            {
                case "Advertisement":
                    int countAd = sharedPrefs.getInt("countAd", 0);
                    sharedPrefs.edit()
                            .putInt("countAd", ++countAd)
                            .commit();
                    break;
                case "Crowdsourcing":
                    int countCrowdsourcing = sharedPrefs.getInt("countCrowdsourcing", 0);
                    sharedPrefs.edit()
                            .putInt("countCrowdsourcing", ++countCrowdsourcing)
                            .commit();
                    break;
                case "Questionnaire":
                    int countQuestionnaire = sharedPrefs.getInt("countQuestionnaire", 0);
                    sharedPrefs.edit()
                            .putInt("countQuestionnaire", ++countQuestionnaire)
                            .commit();
                    break;
                case "News":
                    int countNews = sharedPrefs.getInt("countNews", 0);
                    sharedPrefs.edit()
                            .putInt("countNews", ++countNews)
                            .commit();
                    break;
            }
            return output;

        }else{
            NotificationEventRecordDAO.updateIsUsedByType(notitype, false);
            List<NotificationEventRecord> List1 = NotificationEventRecordDAO.findValidNotifications(notitype, true);

            if(List1.size() > 0){
                Random r = new Random();
                NotificationEventRecord output = List1.get(r.nextInt(List1.size()));
                output.setIsUsed(true);
                NotificationEventRecordDAO.updateOne(output);

                switch(notitype)
                {
                    case "Advertisement":
                        int countAd = sharedPrefs.getInt("countAd", 0);
                        sharedPrefs.edit()
                                .putInt("countAd", ++countAd)
                                .commit();
                        break;
                    case "Crowdsourcing":
                        int countCrowdsourcing = sharedPrefs.getInt("countCrowdsourcing", 0);
                        sharedPrefs.edit()
                                .putInt("countCrowdsourcing", ++countCrowdsourcing)
                                .commit();
                        break;
                    case "Questionnaire":
                        int countQuestionnaire = sharedPrefs.getInt("countQuestionnaire", 0);
                        sharedPrefs.edit()
                                .putInt("countQuestionnaire", ++countQuestionnaire)
                                .commit();
                        break;
                    case "News":
                        int countNews = sharedPrefs.getInt("countNews", 0);
                        sharedPrefs.edit()
                                .putInt("countNews", ++countNews)
                                .commit();
                        break;
                }
                return output;

            }else{
                return null;
            }
        }
    }

    private static PendingIntent createOnDismissedIntent(Context context, Long pk) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        Bundle bundle = new Bundle();
        intent.putExtra("labelingStudy.nctu.boredom_detection._id", pk);

        //Log.d("createOnDismissedIntent", "labelingStudy.nctu.boredom_detection._id " + pk);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static NotificationEventRecord crawlNews(Context context, SharedPreferences sharedPrefs) {
        // By jeff 2020.06.07
        /*
        政治：1
        國際：2
        娛樂：9
        體育：10
        科技：20
        財經：17
        地方：7
        健康：21
         */

        android.util.Log.i(TAG, "crawlNews called");
        NotificationEventRecord noticeEventData = null;
        Set<String> newsSet = new HashSet<String>(sharedPrefs.getStringSet("news_preference", new HashSet<String>()));

        if(newsSet.size() == 0)
        {
            newsSet.add("1");
            newsSet.add("2");
            newsSet.add("9");
            newsSet.add("10");
            newsSet.add("20");
            newsSet.add("17");
            newsSet.add("7");
            newsSet.add("21");
        }

        try {

            if(isNetworkConneted(context)) {
                String title = "";
                String uri = "";
                String time = "";
                Calendar time_cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN);
                for (String categoryString : newsSet) {
                    String todayString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    Document doc = Jsoup.connect("https://www.ettoday.net/news/news-list-" + todayString + "-" + categoryString + ".htm").get();
                    Elements elements = doc.select("div.part_list_2");
                    String title_tmp = elements.get(0).selectFirst("a").text();
                    String category_tmp = elements.get(0).selectFirst("em").attr("class");
                    String uri_tmp = "https://www.ettoday.net/news/news-list-" + elements.get(0).selectFirst("a").attr("href");
                    String time_tmp = elements.get(0).selectFirst("span").text();
                    if (time.equalsIgnoreCase("")) {
                        title = title_tmp;
                        uri = uri_tmp;
                        time = time_tmp;
                        time_cal.setTime(sdf.parse(time));
                    } else {
                        Calendar cal_tmp = Calendar.getInstance();
                        cal_tmp.setTime(sdf.parse(time_tmp));
                        if (cal_tmp.getTime().after(time_cal.getTime())) {
                            title = title_tmp;
                            uri = uri_tmp;
                            time = time_tmp;
                            time_cal = cal_tmp;
                        }
                    }
                    //Log.i(TAG, "crawlNews title + " + title_tmp);
                    //Log.i(TAG, "crawlNews time + " + time_tmp);
                }

                android.util.Log.i(TAG, "crawlNews title + " + title);
                android.util.Log.i(TAG, "crawlNews time + " + time);
                android.util.Log.i(TAG, "crawlNews uri + " + uri);

                if (!uri.equalsIgnoreCase("")) {
                    long current = System.currentTimeMillis();
                    //int notifyNotificationID = sharedPrefs.getInt("notifyNotificationID", -1);

                    noticeEventData = new NotificationEventRecord(
                            "4000", title, "", uri, "News");
                } else {
                    android.util.Log.i(TAG, "crawlNews news not found.");
                }
            }else{
                android.util.Log.i(TAG, "Network is not connected.");
            }
        } catch (IOException e) {
            android.util.Log.e(TAG, "crawlNews IOException" + e.getMessage());
        } catch (ParseException e) {
            android.util.Log.e(TAG, "crawlNews ParseException" + e.getMessage());
        }
        return noticeEventData;
    }

    public static void updateAnswerData(Context context, Answers instance, long id, String firebasePK){
        answerDataRecordDAO =  appDatabase.getDatabase(context).answerDataRecordDAO();
        AnswerDataRecord answerDataRecord = new AnswerDataRecord();
        answerDataRecord.setIsUpload(0);
        answerDataRecord.setId(id);
        answerDataRecord.setFirebasePK(firebasePK);
        answerDataRecord.setJsonString(instance.get_json_object());


//
        // store to db
        long id2 = answerDataRecordDAO.insert(answerDataRecord);
        if (id2 == -1) {
            answerDataRecordDAO.update(answerDataRecord);
        }

        List<AnswerDataRecord> notuploadanswers = answerDataRecordDAO.getByIsUpload(0);

        for (AnswerDataRecord a: notuploadanswers) {
//            Log.i(TAG, a.getJsonString());
        }



    }
    public static String getLabelStringById(Context mContext, int labelId){
        return mContext.getResources().getString(labelingStudy.nctu.boredom_detection.config.Constants.labelResourceStings[labelId]);
    }
    public static int getNotifySurveyID(){
        return notifySurveyID;
    }
    public static int getNotifyStateSurveyID(){
        return notifyStateSurveyID;
    }
    public static long getSurveyID(){
        return surveyId;
    }
    public static String getSurveyFirebasePK(){
        return surveyFirebasePK;
    }
    public static long getStateSurveyID(){
        return surveyStateId;
    }
    public static String getStateSurveyFirebasePK(){
        return surveyStateFirebasePK;
    }

}