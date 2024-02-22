package labelingStudy.nctu.boredom_detection.Receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.dao.ActionDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class IntentActionReceiver extends BroadcastReceiver {
    String TAG = "IntentActionReceiver";
    ActionDataRecordDAO actionDataRecordDAO;

    private boolean  debugmode = false;

    Handler handler = new Handler();
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        actionDataRecordDAO = appDatabase.getDatabase(context).actionDataRecordDAO();

        //Log.d(TAG, "IntentActionReceiver onReceive() called " + intent.getAction());
        long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
        String action = intent.getAction();
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //End service when user phone screen off
            action = "ACTION_SCREEN_OFF";
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //End service when user phone screen off
            action = "ACTION_SCREEN_ON";
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //Start service when user phone unlocked
            action = "ACTION_USER_PRESENT";

            mContext = context;


            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {

                            final SharedPreferences sharedPrefs = mContext.getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
                            android.util.Log.i(TAG, "notifyESM called");

                            // Retrieve values from shared preferences
                            long lastNotiTime = sharedPrefs.getLong("lastNotiTime", -1);
                            long stateGap = sharedPrefs.getLong("stateGap", -1);
                            long gap = sharedPrefs.getLong("gap", -1);

//                            // For testing purposes, set 'gap' to 60 seconds (remove this in production)
                            if (debugmode){
                                gap = 30*1000; // test
                                stateGap = 30*1000; // test
                                sharedPrefs.edit()
                                        .putLong("stateGap", stateGap)
                                        .commit();
                            }

//                            // test

                            android.util.Log.i(TAG, "notifyESM lastNotiTime: " + ScheduleAndSampleManager.getTimeString(lastNotiTime));
                            android.util.Log.i(TAG, "notifyESM gap: " + gap);

                            if(gap !=-1){
                                android.util.Log.i(TAG, "notifyESM NotiTime + gap: " + ScheduleAndSampleManager.getTimeString(lastNotiTime + gap));
                            }

//                            boolean send = false;
//
//                            long currentTime = ScheduleAndSampleManager.getCurrentTimeInMillis();
//
//                            if (Utils.isActiveTiming(sharedPrefs)) {
//
//
//                                if(gap == -1) {
//                                    //first notice
//                                    send = true;
//                                }else {
//                                    if (currentTime > (lastNotiTime + gap)) {
//                                        // at least 1 hr
//                                        send = true;
//                                    }
//                                }
//                            }else{
//                                sharedPrefs.edit()
//                                        .putLong("lastNotiTime", currentTime)
//                                        .commit();
//                                android.util.Log.i(TAG, "It is not the active time.");
//
//                            }

                            if (Utils.isSendSurvey(gap,"lastNotiTime", sharedPrefs)) {

                                final NotificationEventRecordDAO NotificationEventRecordDAO;
                                final NotificationDataRecordDAO NotificationDataRecordDAO;

                                NotificationEventRecordDAO = appDatabase.getDatabase(mContext).notificationEventRecordDAO();
                                NotificationDataRecordDAO = appDatabase.getDatabase(mContext).notificationDataRecordDAO();
//                                Utils.notifyESM(mContext, sharedPrefs, false,310, (NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE),handler , NotificationDataRecordDAO, NotificationEventRecordDAO);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.setIntentionSurvey(mContext, sharedPrefs, (NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE),handler);
                                    }
                                }, 5*1000); // 5000 milliseconds (5 seconds)
//                                Utils.setIntentionSurvey(mContext, sharedPrefs, (NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE),handler);

//                                Utils.setStateSurvey(mContext, sharedPrefs,(NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE),handler );

                            }
                        }
                    }).start();

        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            action = "ACTION_BATTERY_LOW";
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            action = "ACTION_BATTERY_OKAY";
        } else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            action = "ACTION_LOCALE_CHANGED";
        } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
            action = "ACTION_TIME_CHANGED";
        } else if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
            if (isAirplaneModeOn(context)) {
                action = "AIRPLANE_MODE_ON";
            } else {
                action = "AIRPLANE_MODE_OFF";
            }
        } else if(intent.getAction().equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if(mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALARMS) {
                //do your stuff
                action = "INTERRUPTION_FILTER_ALARMS";
            } else if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE) {
                action = "INTERRUPTION_FILTER_NONE";
            } else if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL) {
                action = "INTERRUPTION_FILTER_ALL";
            } else if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
                action = "INTERRUPTION_FILTER_PRIORITY";
            } else if (mNotificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_UNKNOWN) {
                action = "INTERRUPTION_FILTER_UNKNOWN";
            }
        }
        Log.d(TAG, "IntentActionReceiver onReceive() called " + action);
        actionDataRecordDAO.insertOne(new ActionDataRecord(action, current));


    }

    @SuppressLint("NewApi")
    public boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

}