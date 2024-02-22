package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lesley on 2020/5/31.
 *
 * NotificationDataRecord stores information about all esm notification data.
 */
@Entity
public class NotificationDataRecord {

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "isUpload")
    public int isUpload ;

    @ColumnInfo(name = "isUploading")
    public int isUploading ;


    @ColumnInfo(name = "isUpdated")
    public int isUpdated ;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    @ColumnInfo(name = "notificationId")
    public String notificationId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "options")
    public String options;

    @ColumnInfo(name = "createdTimeString")
    public String createdTimeString;

    @ColumnInfo(name = "responsedTimeString")
    public String responsedTimeString;

    @ColumnInfo(name = "createdTime")
    public long createdTime;

    @ColumnInfo(name = "responsedTime")
    public long responsedTime;

    @ColumnInfo(name = "response")
    public String response;

    @ColumnInfo(name = "responseCode")
    public int responseCode;

    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "isExpired")
    public int isExpired;

    @ColumnInfo(name = "isSurveyed")
    public int isSurveyed;

    @ColumnInfo(name = "pausedTimeString")
    public String pausedTimeString;

    @ColumnInfo(name = "resumedTimeString")
    public String resumedTimeString;

    @ColumnInfo(name = "clickedTimeString")
    public String clickedTimeString;

    @ColumnInfo(name = "submitTimeString")
    public String submitTimeString;

    @ColumnInfo(name = "destroyTimeString")
    public String destroyTimeString;

    @ColumnInfo(name = "removeTimeString")
    public String removeTimeString;

    @ColumnInfo(name = "removeTime")
    public long removeTime;


    @ColumnInfo(name = "notifyNotificationID")
    public long notifyNotificationID;


    public NotificationDataRecord(){ }


    public NotificationDataRecord(String notificationId, String title, String url, String options, String createdTimeString,  long createdTime,   String type, long notifyNotificationID) {
        this.isUpload = 0;
        this.isUpdated = 0;

        this.notificationId = notificationId;
        this.title = title;
        this.url = url;
        this.options = options;
        this.createdTimeString = createdTimeString;
        this.responsedTimeString = "";
        this.createdTime = createdTime;
        this.responsedTime = -1;
        setResponseCode(0);
        this.comment = "";
        this.type = type;
        this.isExpired = -1;
        this.isSurveyed = 0;
        this.pausedTimeString = "";
        this.resumedTimeString = "";
        this.clickedTimeString = "";
        this.submitTimeString = "";
        this.destroyTimeString = "";
        this.removeTimeString = "";
        this.removeTime = -1;


        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String uploadID = formatter.format(curDate);
        this.firebasePK = uploadID;
        this.isUploading = 0;

        this.notifyNotificationID = notifyNotificationID;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public int getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(int isUpdated) {
        this.isUpdated = isUpdated;
    }

    public String getFirebasePK() {
        return firebasePK;
    }

    public void setFirebasePK(String firebasePK) {
        this.firebasePK = firebasePK;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getCreatedTimeString() {
        return createdTimeString;
    }

    public void setCreatedTimeString(String createdTimeString) {
        this.createdTimeString = createdTimeString;
    }

    public String getResponsedTimeString() {
        return responsedTimeString;
    }

    public void setResponsedTimeString(String responsedTimeString) {
        this.responsedTimeString = responsedTimeString;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getResponsedTime() {
        return responsedTime;
    }

    public void setResponsedTime(long responsedTime) {
        this.responsedTime = responsedTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;

        switch (responseCode)
        {
            case -1:
                this.response ="Remove";
                break;
            case 0:
                this.response ="No Response";
                break;
            case 1:
                this.response ="View";
                break;
            case 2:
                this.response ="Answer";
                break;
            default:
                this.response ="Others";
                break;

        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(int isExpired) {
        this.isExpired = isExpired;
    }

    public void setSurveyed() {
        this.isSurveyed = 1;
    }

    public int getIsUploading() {
        return isUploading;
    }

    public void setIsUploading(int isUploading) {
        this.isUploading = isUploading;
    }

    public String getPausedTimeString() {
        return pausedTimeString;
    }

    public void setPausedTimeString(String pausedTimeString) {
        this.pausedTimeString = pausedTimeString;
    }

    public String getResumedTimeString() {
        return resumedTimeString;
    }

    public void setResumedTimeString(String resumedTimeString) {
        this.resumedTimeString = resumedTimeString;
    }

    public String getClickedTimeString() {
        return clickedTimeString;
    }

    public void setClickedTimeString(String clickedTimeString) {
        this.clickedTimeString = clickedTimeString;
    }

    public String getSubmitTimeString() {
        return submitTimeString;
    }

    public void setSubmitTimeString(String submitTimeString) {
        this.submitTimeString = submitTimeString;
    }

    public String getDestroyTimeString() {
        return destroyTimeString;
    }

    public void setDestroyTimeString(String destroyTimeString) {
        this.destroyTimeString = destroyTimeString;
    }

    public String getRemoveTimeString() {
        return removeTimeString;
    }

    public long getNotifyNotificationID() {
        return notifyNotificationID;
    }

    public void setRemoveTimeString(String removeTimeString) {
        this.removeTimeString = removeTimeString;
    }

    public void setRemoveTime(long removeTime) {
        this.removeTime = removeTime;
    }

}
