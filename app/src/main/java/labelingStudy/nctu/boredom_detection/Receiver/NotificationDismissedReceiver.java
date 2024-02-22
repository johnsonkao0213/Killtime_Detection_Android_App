package labelingStudy.nctu.boredom_detection.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    String TAG = "NotificationDismissedReceiver";
    NotificationDataRecordDAO NotificationDataRecordDAO;
    @Override
    public void onReceive(Context context, Intent intent) {
        Long _id = intent.getExtras().getLong("labelingStudy.nctu.boredom_detection._id");
        /* Your code to handle the event here */
        Log.i(TAG, "labelingStudy.nctu.boredom_detection._id " + _id);

        NotificationDataRecordDAO = appDatabase.getDatabase(context).notificationDataRecordDAO();

        List<NotificationDataRecord> List = NotificationDataRecordDAO.getByNotifyNotificationID(_id);

        if(List.size()>0)
        {
            NotificationDataRecord notice = List.get(0);

            notice.setIsUpdated(1);
            notice.setResponseCode(-1);

            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setResponsedTime(current);
            notice.setResponsedTimeString(currentTime);
            notice.setRemoveTimeString(currentTime);
            notice.setRemoveTime(current);

            NotificationDataRecordDAO.updateOne(notice);

        }else{
            Log.e(TAG, "_id : " + _id + " is not found");
        }
    }
}