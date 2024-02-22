package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Lesley on 2020/5/31.
 *
 * MinukuDataRecord stores information about all minuku data primary key at the same time.
 */
@Entity
public class MinukuDataRecord {

    @ColumnInfo(name = "TimeMillis")
    public long TimeMillis;

    @ColumnInfo(name = "TimeString")
    public String TimeString;

    @ColumnInfo(name = "isUpload")
    public int isUpload ;

    @ColumnInfo(name = "isUploading")
    public int isUploading ;

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "AccessibilityKey")
    public long AccessibilityKey;

    @ColumnInfo(name = "ActivityRecognitionKey")
    public long ActivityRecognitionKey;

    @ColumnInfo(name = "AppUsageKey")
    public long AppUsageKey;

    @ColumnInfo(name = "BatteryKey")
    public long BatteryKey;

    @ColumnInfo(name = "ConnectivityKey")
    public long ConnectivityKey;

    @ColumnInfo(name = "RingerKey")
    public long RingerKey;

    @ColumnInfo(name = "TelephonyKey")
    public long TelephonyKey;

    @ColumnInfo(name = "TransportationModeKey")
    public long TransportationModeKey;

    @ColumnInfo(name = "ImageFileName")
    public String ImageFileName;

    @ColumnInfo(name = "isScreenShotted")
    public int isScreenShotted;

    public MinukuDataRecord(){ }

    public MinukuDataRecord(String timeString, long timeMillis, long accessibilityKey,
                            long activityRecognitionKey, long appUsageKey, long batteryKey,
                            long connectivityKey, long ringerKey, long  telephonyKey, long transportationModeKey, String filename, int isScreenShotted) {
        this.TimeString = timeString;
        this.TimeMillis = timeMillis;
        this.AccessibilityKey = accessibilityKey;
        this.ActivityRecognitionKey = activityRecognitionKey;
        this.AppUsageKey = appUsageKey;
        this.BatteryKey = batteryKey;
        this.ConnectivityKey = connectivityKey;
        this.RingerKey = ringerKey;
        this.TelephonyKey = telephonyKey;
        this.TransportationModeKey = transportationModeKey;
        this.isUpload = 0;
        this.ImageFileName =  filename;
        this.isUploading = 0;
        this.isScreenShotted = isScreenShotted;
    }

    public long getTimeMillis() {
        return TimeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        TimeMillis = timeMillis;
    }

    public String getTimeString() {
        return TimeString;
    }

    public void setTimeString(String timeString) {
        TimeString = timeString;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getAccessibilityKey() {
        return AccessibilityKey;
    }

    public void setAccessibilityKey(long accessibilityKey) {
        AccessibilityKey = accessibilityKey;
    }

    public long getActivityRecognitionKey() {
        return ActivityRecognitionKey;
    }

    public void setActivityRecognitionKey(long activityRecognitionKey) {
        ActivityRecognitionKey = activityRecognitionKey;
    }

    public long getAppUsageKey() {
        return AppUsageKey;
    }

    public void setAppUsageKey(long appUsageKey) {
        AppUsageKey = appUsageKey;
    }

    public long getBatteryKey() {
        return BatteryKey;
    }

    public void setBatteryKey(long batteryKey) {
        BatteryKey = batteryKey;
    }

    public long getConnectivityKey() {
        return ConnectivityKey;
    }

    public void setConnectivityKey(long connectivityKey) {
        ConnectivityKey = connectivityKey;
    }

    public long getRingerKey() {
        return RingerKey;
    }

    public void setRingerKey(long ringerKey) {
        RingerKey = ringerKey;
    }

    public long getTelephonyKey() {
        return TelephonyKey;
    }

    public void setTelephonyKey(long telephonyKey) {
        TelephonyKey = telephonyKey;
    }

    public long getTransportationModeKey() {
        return TransportationModeKey;
    }

    public void setTransportationModeKey(long transportationModeKey) {
        TransportationModeKey = transportationModeKey;
    }

    public String getImageFileName() {
        return ImageFileName;
    }

    public void setImageFileName(String imageFileName) {
        ImageFileName = imageFileName;
    }

    public int getIsUploading() {
        return isUploading;
    }

    public void setIsUploading(int isUploading) {
        this.isUploading = isUploading;
    }

    public int getIsScreenShotted() {
        return isScreenShotted;
    }

    public void setIsScreenShotted(int isScreenShotted) {
        this.isScreenShotted = isScreenShotted;
    }
}
