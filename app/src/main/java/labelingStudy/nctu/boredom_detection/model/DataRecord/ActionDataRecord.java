package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class ActionDataRecord {
    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "createdTime")
    public long createdTime;

    @ColumnInfo(name = "action")
    public String action;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    public ActionDataRecord() {

    }

    public ActionDataRecord( String action, long createdTime) {
        this.action = action;
        this.createdTime = createdTime;

        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        this.firebasePK = formatter.format(curDate);
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFirebasePK() {
        return firebasePK;
    }

    public void setFirebasePK(String firebasePK) {
        this.firebasePK = firebasePK;
    }
}
