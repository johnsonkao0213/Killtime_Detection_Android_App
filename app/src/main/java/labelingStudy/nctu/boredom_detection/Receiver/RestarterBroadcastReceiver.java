package labelingStudy.nctu.boredom_detection.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.boredom_detection.MainActivity;

/**
 * Created by Lawrence on 2018/8/17.
 */
public class RestarterBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "RestarterBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Constants.CHECK_SERVICE_ACTION)) {
            Log.i(TAG, "the RestarterBroadcastReceiver is going to start the Activity");

            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            /*
            Log.i(TAG, "the RestarterBroadcastReceiver is going to start the BackgroundService");


            Intent intentToStartBackground = new Intent(context, BackgroundService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentToStartBackground);
            } else {
                context.startService(intentToStartBackground);
            }

             */
        }
    }
}
