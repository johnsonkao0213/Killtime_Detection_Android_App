package labelingStudy.nctu.boredom_detection.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import labelingStudy.nctu.boredom_detection.MainActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

public class sendHintNotificationReceiver extends BroadcastReceiver {

    private int notifyNotificationID = 11;
    private int notifyNotificationCode = 300;

    @Override
    public void onReceive(Context context, Intent intent1) {
        if(intent1.getAction() != null ) {
            Log.i("sendHintNotificationReceiver", intent1.getAction());
            if (intent1.getAction().equalsIgnoreCase("HINT_ALARM")) {
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = getHintNotification(context);
                notification.defaults |= Notification.DEFAULT_VIBRATE;

                mNotificationManager.notify(notifyNotificationID, notification);
            }
        }else{
            Log.i("sendHintNotificationReceiver", "intent1.getAction() is null.");
        }
    }

    private Notification getHintNotification(Context context) {


        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        String text = context.getString(R.string.upload_reminder);


        bigTextStyle.setBigContentTitle(context.getString(R.string.reminder_title)); //Constants.SURVEY_CHANNEL_NAME
        bigTextStyle.bigText(text);

        Intent resultIntent = new Intent();
        resultIntent.setComponent(new ComponentName(context, MainActivity.class));

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        PendingIntent pending = PendingIntent.getActivity(context, notifyNotificationCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder noti = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
//                .setContentText(text)
                .setContentText(text)
                .setStyle(bigTextStyle)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .setChannelId(Constants.SURVEY_CHANNEL_ID)
                    .build();
        } else {
            return noti
                    .setSmallIcon(Utils.getNotificationIcon(noti))
                    .build();
        }

    }



}
