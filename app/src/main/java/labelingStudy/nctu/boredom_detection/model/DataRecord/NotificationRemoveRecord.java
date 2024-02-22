package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class NotificationRemoveRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "app_name")
    public String appName;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @NonNull
    @ColumnInfo(name = "post_time")
    public long postTime;

    @NonNull
    @ColumnInfo(name = "remove_time")
    public long removeTime;

    @NonNull
    @ColumnInfo(name = "reason")
    public int reason = -1;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    @ColumnInfo(name = "isUpload")
    public int isUpload;

    @ColumnInfo(name = "notifyNotificationID")
    public int notifyNotificationID;

    public NotificationRemoveRecord(String appName, String title, String content, long postTime, long removeTime, int reason, int notifyNotificationID) {
        this.appName = appName;
        this.title = title;
        this.content = content;
        this.postTime = postTime;
        this.removeTime = removeTime;
        this.reason = reason;
        this.isUpload = 0;
        this.notifyNotificationID = notifyNotificationID;

        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        this.firebasePK = formatter.format(curDate);
    }

}
