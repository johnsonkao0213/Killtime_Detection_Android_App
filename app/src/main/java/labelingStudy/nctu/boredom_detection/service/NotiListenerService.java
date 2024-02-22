package labelingStudy.nctu.boredom_detection.service;

import android.app.Notification;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.dao.NotificationRemoveRecordDao;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

public class NotiListenerService extends NotificationListenerService {
    private NotificationRemoveRecordDao notificationRemoveRecordDao;
    private SharedPreferences sharedPrefs;
    Executor mExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        notificationRemoveRecordDao = appDatabase.getDatabase(this).notificationRemoveRecordDao();
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap, int reason) {
        Log.i("onRemoved ", sbn.getPackageName() + " " + sbn.getNotification().extras.get("android.title") + " " + sbn.getId() + " " + sbn.getPostTime() + " " + reason);
        //if (sbn.isClearable() && Utils.isActiveTiming(sharedPrefs)) {
        Answers answer = null;

        if (sbn.getId() == Utils.getNotifySurveyID()){
            answer = Answers.getIntentionInstance();
        }
        else if(sbn.getId() == Utils.getNotifyStateSurveyID()){
            answer = Answers.getStateInstance();
        }

        String removedTime = Utils.getCurrentTimeString();


        if (sbn.getId() == Utils.getNotifySurveyID() || sbn.getId() == Utils.getNotifyStateSurveyID()){
            answer.put_answer("removedTimeString", removedTime);
            Notification notification = sbn.getNotification();
            if (reason == NotificationListenerService.REASON_TIMEOUT){
                Log.i("onRemoved", "TIMEOUT:" + removedTime);
                answer.put_answer("removedReason", "TIMEOUT");

            }else if(reason == NotificationListenerService.REASON_CLICK){
                Log.i("onRemoved", "CLICK:" +removedTime);
                answer.put_answer("removedReason", "CLICK");

            }else if(reason == NotificationListenerService.REASON_CANCEL){
                Log.i("onRemoved", "CANCEL:" + removedTime);
                answer.put_answer("removedReason", "CANCEL");

            }else if(reason == NotificationListenerService.REASON_CANCEL_ALL){
                Log.i("onRemoved", "CANCEL_ALL:" +removedTime);
                answer.put_answer("removedReason", "CANCEL_ALL");

            }else if(reason == NotificationListenerService.REASON_APP_CANCEL){
                Log.i("onRemoved", "APP_CANCEL:" +removedTime);
                answer.put_answer("removedReason", "APP_CANCEL");

            }else{
                Log.i("onRemoved", "Others:" + reason + " " + removedTime);
                answer.put_answer("removedReason", "Others:" + reason );
            }
            if (sbn.getId() == Utils.getNotifySurveyID()){
                Utils.updateAnswerData(this, answer, Utils.getSurveyID(), Utils.getSurveyFirebasePK());
            }
            else if(sbn.getId() == Utils.getNotifyStateSurveyID()){
                Utils.updateAnswerData(this, answer, Utils.getStateSurveyID(), Utils.getStateSurveyFirebasePK());
            }


        }
            Notification notification = sbn.getNotification();
            final NotificationRemoveRecord notificationRemoveRecord = setNotificationRemoveRecord(sbn, Calendar.getInstance().getTimeInMillis(), reason);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    notificationRemoveRecordDao.insertNotiRemove(notificationRemoveRecord);
                }
            });
        //}
    }

    private NotificationRemoveRecord setNotificationRemoveRecord(StatusBarNotification notification, Long removeTime, int reason) {
        String packageName = "null";
        String title = "";
        String content = "null";
        String appName = "null";
        long noti_id = -1;
        int notifyNotificationID = 0;

        Long postTime = notification.getPostTime();

        try {
            packageName = notification.getPackageName();

        } catch (Exception e) {
//            Log.e("NotiListenerService", "package name failed", e);
        }
        try {
            title = notification.getNotification().extras.get("android.title").toString();

        } catch (Exception e) {
//            Log.i("NotiListenerService", "title failed");
        }
        try {
            content = notification.getNotification().extras.get("android.text").toString();
        } catch (Exception e) {
//            Log.i("NotiListenerService", "content failed");
        }
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appName = (String) (applicationInfo != null ?
                    packageManager.getApplicationLabel(applicationInfo) : "(unknown)");

        } catch (Exception e) {
//            Log.e("NotiListenerService", "appName failed", e);
        }
        try {
            notifyNotificationID = notification.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((appName.equals("殺時間標記") || appName.equals("BoredomDetection")))
            Log.d("NotiRemove", "_id " + notifyNotificationID + "");
        NotificationRemoveRecord notificationRemoveRecord = new NotificationRemoveRecord(appName, title, content, postTime, removeTime, reason, notifyNotificationID);


        return notificationRemoveRecord;
    }

}
