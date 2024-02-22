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

import android.content.Context;

import com.firebase.client.Config;
import com.google.firebase.database.FirebaseDatabase;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.Instabug;

import labelingStudy.nctu.minuku.config.UserPreferences;
import labelingStudy.nctu.minuku.logger.Log;

//import com.bugfender.sdk.Bugfender;
//import com.google.firebase.crashlytics.FirebaseCrashlytics;
//import com.crashlytics.android.ndk.CrashlyticsNdk;
//import io.fabric.sdk.android.Fabric;

/**
 * Created by neerajkumar on 7/18/16.
 */
public class BoredomApp extends android.app.Application {
    private static String TAG = "BoredomApp";

    private static BoredomApp instance;
    private static Context mContext;

    public static BoredomApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean MainActivityVisible;
    public static boolean LabelActivityVisible;
    public static boolean PreferenceActivityVisible;
    public static boolean GrantUploadActivityVisible;
    public static boolean SurveyActivityVisible;
    public static boolean WelcomeActivityVisible;
    public static boolean AdvertisementActivityVisible;
    public static boolean CrowdsourcingActivityVisible;
    public static boolean NewsActivityVisible;
    public static boolean QuestionnaireActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "BoredomApp onCreate");
        //Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        // OPTIONAL: If crash reporting has been explicitly disabled previously, add:
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Config mConfig = new Config();
        mConfig.setPersistenceEnabled(true);
        long cacheSizeOfHundredMB = 100 * 1024 * 1024;
        mConfig.setPersistenceCacheSizeBytes(cacheSizeOfHundredMB);
        mConfig.setPersistenceEnabled(true);
        mConfig.setAndroidContext(this);
        /*
        Firebase.setDefaultConfig(mConfig);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        */
        UserPreferences.getInstance().Initialize(getApplicationContext());

        //Bugfender.init(this, "N7pdXEGbmKhK9k8YtpFPyXORtsAwgZa5", false);
        //Bugfender.setForceEnabled(true);

        new Instabug.Builder(this, "2be6d236d601237a17e9c6314455930a")
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventShake)
                .build();

        MainActivityVisible = false;
        LabelActivityVisible = false;
        PreferenceActivityVisible = false;
        GrantUploadActivityVisible = false;
        SurveyActivityVisible = false;
        WelcomeActivityVisible = false;
        AdvertisementActivityVisible = false;
        CrowdsourcingActivityVisible = false;
        NewsActivityVisible = false;
        QuestionnaireActivityVisible = false;
    }


    public static boolean isActivityVisible() {
        String tmp = "";
        if(MainActivityVisible){
            tmp = tmp + ", Main";
        }
        if(LabelActivityVisible){
            tmp = tmp + ", Label";
        }
        if(PreferenceActivityVisible){
            tmp = tmp + ", Preference";
        }
        if(GrantUploadActivityVisible){
            tmp = tmp + ", GrantUpload";
        }
        if(SurveyActivityVisible){
            tmp = tmp + ", Survey";
        }
        if(WelcomeActivityVisible){
            tmp = tmp + ", Welcome";
        }
        if(AdvertisementActivityVisible){
            tmp = tmp + ", Advertisement";
        }
        if(CrowdsourcingActivityVisible){
            tmp = tmp + ", Crowdsourcing";
        }
        if(NewsActivityVisible){
            tmp = tmp + ", News";
        }
        if(QuestionnaireActivityVisible){
            tmp = tmp + ", Questionnaire";
        }

        if(tmp.length()>0)
        {
            Log.d( TAG,tmp + " is running ...");
        }else{
            Log.d( TAG,"activities paused ...");
        }

        return MainActivityVisible || LabelActivityVisible || PreferenceActivityVisible || GrantUploadActivityVisible || SurveyActivityVisible || WelcomeActivityVisible || AdvertisementActivityVisible || CrowdsourcingActivityVisible || NewsActivityVisible || QuestionnaireActivityVisible;
    }

    public static void setActivityVisibility(String act, Boolean visible) {
        switch(act){
            case "MainActivity":
                MainActivityVisible = visible;
                break;
            case "LabelActivity":
                LabelActivityVisible = visible;
                break;
            case "PreferenceActivity":
                PreferenceActivityVisible = visible;
                break;
            case "GrantUploadActivity":
                GrantUploadActivityVisible = visible;
                break;
            case " SurveyActivity":
                SurveyActivityVisible = visible;
                break;
            case "WelcomeActivity":
                WelcomeActivityVisible = visible;
                break;
            case "AdvertisementActivity":
                AdvertisementActivityVisible = visible;
                break;
            case "CrowdsourcingActivity":
                CrowdsourcingActivityVisible = visible;
                break;
            case "NewsActivit":
                NewsActivityVisible = visible;
                break;
            case "QuestionnaireActivity":
                QuestionnaireActivityVisible = visible;
                break;
        }
//        if (visible)
//            Log.d( TAG,act + " go back ...");
//        else
//            Log.d( TAG,act + " paused ...");
    }

}
