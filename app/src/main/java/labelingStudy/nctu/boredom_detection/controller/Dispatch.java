package labelingStudy.nctu.boredom_detection.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.multidex.MultiDex;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.MainActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.service.BackgroundService;

/**
 * Created by Lawrence on 2018/3/31.
 */

public class Dispatch extends Activity {

    private final String TAG = "Dispatch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispatcher);

        MultiDex.install(this);

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        Class<?> activityClass;

        boolean firstStartBackGround = sharedPrefs.getBoolean("firstStartBackGround", true);


        if(firstStartBackGround) {

            startBackgroundService();

            sharedPrefs.edit().putBoolean("firstStartBackGround", false).apply();
        }

        if(!firstStartBackGround && !BackgroundService.isBackgroundServiceRunning){

            startBackgroundService();
        }

        try {

            activityClass = Class.forName(
                    sharedPrefs.getString("lastActivity", MainActivity.class.getName()));
        } catch(ClassNotFoundException e) {

            activityClass = MainActivity.class;
        }

        Log.d(TAG, "Going to "+activityClass.getName());

        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        Dispatch.this.finish();
    }

    private void startBackgroundService(){

        Intent intentToStartBackground = new Intent(getBaseContext(), BackgroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentToStartBackground);
        } else {
            startService(intentToStartBackground);
        }
    }

}
