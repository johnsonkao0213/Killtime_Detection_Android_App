package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class PreferenceDataRecord {

    @ColumnInfo
    private String jsonString;

    @ColumnInfo(name = "isUpload")
    public int isUpload ;

    // 1: news
    // 2: recording
    // 3 : retry to upload image
    // 4 : private image record
    // 5 : background Service status
    // 6 : network
    @ColumnInfo(name = "type")
    public int type ;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    @ColumnInfo(name = "createTime")
    public long createTime;

    @PrimaryKey(autoGenerate = true)
    private long id;

    public PreferenceDataRecord() {
        this.isUpload = 0;
    }

    public PreferenceDataRecord(String preference, int type, long createdTime) {
        this.isUpload = 0;
        this.jsonString = preference;
        this.type = type;
        this.createTime = createdTime;
        Date curDate = new Date(createdTime); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        this.firebasePK = formatter.format(curDate);
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String answer) {
        this.jsonString = answer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public String getFirebasePK() {
        return firebasePK;
    }

    public void setFirebasePK(String firebasePK) {
        this.firebasePK = firebasePK;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
