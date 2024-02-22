package labelingStudy.nctu.minuku.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import labelingStudy.nctu.minuku.Data.appDatabase;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.dao.MobileAccessibilityDataRecordDAO;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.model.DataRecord.AccessibilityDataRecord;
import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;
import labelingStudy.nctu.minuku.streamgenerator.AccessibilityStreamGenerator;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;

/**
 * Created by Lawrence on 2017/9/3.
 */

public class MobileAccessibilityService extends AccessibilityService {

    private final String TAG = "MobileAccessibilitySrvc";

    private static AccessibilityStreamGenerator accessibilityStreamGenerator;

    private MobileAccessibilityDataRecordDAO mobileAccessibilityDataRecordDAO;
    private SharedPreferences sharedPrefs;
    Executor mExecutor = Executors.newSingleThreadExecutor();
    PackageManager pm ;


    public MobileAccessibilityService() {
        super();
    }

    public MobileAccessibilityService(AccessibilityStreamGenerator accessibilityStreamGenerator) {
        super();

        try {

            this.accessibilityStreamGenerator = (AccessibilityStreamGenerator) MinukuStreamManager.getInstance().getStreamGeneratorFor(AccessibilityDataRecord.class);
        } catch (StreamNotFoundException e) {

            this.accessibilityStreamGenerator = accessibilityStreamGenerator;
//            e.printStackTrace();
        }
    }

    @Override
    protected void onServiceConnected() {
        mobileAccessibilityDataRecordDAO = appDatabase.getDatabase(this).mobileAccessibilityDataRecordDAO();
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        pm = getApplicationContext().getPackageManager();

        Log.d("in access", "config success!");
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        accessibilityServiceInfo.notificationTimeout = 1000;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String pack = "";
        String text = "";
        String type = "";
        String extra = "";
        String app = "";
        long time = -1;

        //Log.d(TAG,"onAccessibilityEvent");

        if (accessibilityEvent.getPackageName() != null) {
            pack = accessibilityEvent.getPackageName().toString();
            //Log.d(TAG,"pack : "+ pack);
        }

        if (accessibilityEvent.getClassName() != null) {
            text = accessibilityEvent.getClassName().toString();
        }
        if (accessibilityEvent.getText() != null) {
            text += ":" + accessibilityEvent.getText().toString();
            //TODO testing the attribute.
            //Log.d(TAG,"text : "+ text);
        }
        if (accessibilityEvent.getContentDescription() != null) {
            extra = accessibilityEvent.getContentDescription().toString();
            //Log.d(TAG,"extra : "+ extra);
        }
        time = ScheduleAndSampleManager.getCurrentTimeInMillis();


        switch (eventType) {
                /*
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                type = "TYPE_WINDOW_STATE_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;

            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                type = "TYPE_ANNOUNCEMENT";
                //Log.d(TAG, "type : " + type);
                break;

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                type = "TYPE_NOTIFICATION_STATE_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;
                 */
                /*
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                type = "TYPE_WINDOW_CONTENT_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                type = "TYPE_WINDOWS_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;
                */
                /*
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                type = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                type = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                type = "TYPE_GESTURE_DETECTION_START";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                type = "TYPE_GESTURE_DETECTION_END";
                //Log.d(TAG, "type : " + type);
                break;
                */
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                type = "TYPE_TOUCH_INTERACTION_END";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                type = "TYPE_TOUCH_INTERACTION_START";
                //Log.d(TAG, "type : " + type);
                break;

            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                type = "TYPE_VIEW_FOCUSED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                type = "TYPE_VIEW_HOVER_ENTER";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                type = "TYPE_VIEW_HOVER_EXIT";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                type = "TYPE_VIEW_CLICKED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                type = "TYPE_VIEW_CONTEXT_CLICKED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                type = "TYPE_VIEW_LONG_CLICKED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                type = "TYPE_VIEW_SCROLLED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                type = "TYPE_VIEW_SELECTED";
                //Log.d(TAG, "type : " + type);
                break;

            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                type = "TYPE_VIEW_TEXT_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;
                /*
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                type = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                //Log.d(TAG, "type : " + type);
                break;

            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                type = "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                type = "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
                //Log.d(TAG, "type : " + type);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                type = "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
                //Log.d(TAG, "type : " + type);
                break;
                 */

        }
        //Log.d(TAG,"time : " + time);
        //Log.d(TAG, "type : " + type);
        //Log.d(TAG,"time : " + time + "pack = "+pack+" text = "+text+" type = "+type+" extra = "+extra);

        app = getAppNamebyPackageName(pack);

        if (isActiveTiming() && !type.isEmpty()) {

            final MobileAccessibilityDataRecord a = new MobileAccessibilityDataRecord(app, pack, text, type, extra, time);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mobileAccessibilityDataRecordDAO.insertOne(a);
                }
            });
        }

        //prevent the situation that the StreamGenerator is gone but the service is still running
        try {

            accessibilityStreamGenerator.setLatestInAppAction(app, pack, text, type, extra, time);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onInterrupt() {

    }

    public boolean isActiveTiming() {
        boolean condition = true;
        LocalTime now = LocalTime.now();
        int currentTime = now.getHour() * 60 + now.getMinute();

        int recording_end = sharedPrefs.getInt("recording_end", 1320);
        int recording_start = sharedPrefs.getInt("recording_start", 600);

        if (recording_start < recording_end) {

            if (currentTime < recording_start || currentTime > recording_end) {
                condition = false;
            }
        } else if (recording_start > recording_end) {

            if (currentTime < recording_start && currentTime > recording_end) {
                condition = false;
            }

        }
        return condition;
    }

    public String getAppNamebyPackageName(String packageName) {

        ApplicationInfo ai;
        try
        {
            ai = pm.getApplicationInfo(packageName,  PackageManager.GET_META_DATA);
        } catch(final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String)(ai !=null?pm.getApplicationLabel(ai):"unknown");

    }


}
