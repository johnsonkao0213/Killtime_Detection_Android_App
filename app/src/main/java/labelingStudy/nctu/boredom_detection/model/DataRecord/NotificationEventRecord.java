package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Lesley on 2020/5/31.
 *
 * NotificationDataRecord stores information about all esm notification data.
 */
@Entity
public class NotificationEventRecord {

    @PrimaryKey(autoGenerate = true)
    private long _id;


    @ColumnInfo(name = "notificationId")
    public String notificationId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "options")
    public String options;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "isEnable")
    public boolean isEnable;

    @ColumnInfo(name = "isUsed")
    public boolean isUsed;

    public NotificationEventRecord(){ }


    public NotificationEventRecord(String notificationId, String title, String options, String url, String type ) {
        this.notificationId = notificationId;
        this.title = title;
        this.options = options;
        this.url = url;
        this.type = type;
        this.isEnable = true;
        this.isUsed =  false;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_id() {
        return _id;
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

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }
}
