package labelingStudy.nctu.boredom_detection.Receiver;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Date;
import java.util.Map;

import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;


public class NotiButtonsReceiver extends BroadcastReceiver{
    String TAG ="NotiButtonsReceiver";
    private long id;
    Handler handler = new Handler();
    private long stateGap;
    private long lastStateNotiTime;


    private String firebasePk;
    private static Notification surveyNoti = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, TAG + " onReceive");
        String action = intent.getAction();
        final SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        id = intent.getLongExtra("_id",-1);
        firebasePk = intent.getStringExtra("_firebasePK");
        long lastStateNotiTime = sharedPrefs.getLong("lastStateNotiTime", -1);
        long stateGap = sharedPrefs.getLong("stateGap", -1);
        long countsESMAllIntentionSurvey = sharedPrefs.getLong("countsESMAllIntentionSurvey", 0);
        sharedPrefs.edit()
                .putLong("countsESMAllIntentionSurvey", countsESMAllIntentionSurvey+1)
                .commit();


        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        Log.i(TAG, action);
        int notificationId = intent.getIntExtra("notification_id", 0); // Retrieve the notification ID from the intent




// Cancel the notification by its ID
        mNotificationManager.cancel(notificationId);
        Answers answer = Answers.getIntentionInstance();
        answer.put_answer("Intention", action);
        Utils.updateAnswerData(context, answer, id, firebasePk);
        Utils.closeIntentionSurveyWindow(context);
      
        Bundle linkingBundle = new Bundle();
        linkingBundle.putString(IntentionSurveyActivity.LINKING_INTENTION, action);
        linkingBundle.putString(IntentionSurveyActivity.LINKING_TIME, Utils.getCurrentTimeString());


        if(Utils.isSendSurvey(stateGap,"lastStateNotiTime", sharedPrefs)){
            Utils.setStateSurvey(context, sharedPrefs,(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE),handler, linkingBundle);

        }

    }
}

