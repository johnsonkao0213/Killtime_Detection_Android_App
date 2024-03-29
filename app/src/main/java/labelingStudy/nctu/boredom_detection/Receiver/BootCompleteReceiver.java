package labelingStudy.nctu.boredom_detection.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import labelingStudy.nctu.boredom_detection.MainActivity;
import labelingStudy.nctu.minuku.Data.DBHelper;
import labelingStudy.nctu.boredom_detection.service.BackgroundService;

/**
 * Created by Lawrence on 2017/7/19.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";
    private  static DBHelper dbhelper = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            /*
            Log.i(TAG,"boot_complete in first");

            try{

                dbhelper = new DBHelper(context);
                dbhelper.getWritableDatabase();
                Log.i(TAG,"db is ok");



                SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.sharedPrefString, context.MODE_PRIVATE);

                //recover the ongoing session
                int ongoingSessionId = sharedPrefs.getInt("ongoingSessionid", -1);

//                if(ongoingSessionId != -1){

//                    SessionManager.getInstance(context).addOngoingSessionid(ongoingSessionId);
//                }

            }finally {

                Log.i(TAG, "Successfully receive reboot request");

                //here we start the service

                startBackgroundService(context);

                Log.i(TAG,"BackgroundService is ok");

            }*/

            Log.i(TAG, "the BootCompleteReceiver is going to start the Activity");

            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }

    }

    private void startBackgroundService(Context context){

        Intent intentToStartBackground = new Intent(context, BackgroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentToStartBackground);
        } else {
            context.startService(intentToStartBackground);
        }
    }

}
