package labelingStudy.nctu.minuku.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class MobileAccessibilityDataRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "pack")
    public String pack;

    @ColumnInfo(name = "app")
    public String app;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "extra")
    public String extra;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    @ColumnInfo(name = "isUpload")
    public int isUpload;


    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public MobileAccessibilityDataRecord(){
    }

    public MobileAccessibilityDataRecord(String app, String pack, String text, String type, String extra, long detectedTime){
        this.creationTime = detectedTime;

        this.app = app;
        this.pack = pack;
        this.text = text;
        this.type = type;
        this.extra = extra;
        this.isUpload = 0;

        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        this.firebasePK = formatter.format(curDate);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebasePK() {
        return firebasePK;
    }

    public void setFirebasePK(String firebasePK) {
        this.firebasePK = firebasePK;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPack(){
        return pack;
    }

    public String getText(){
        return text;
    }

    public String getType(){
        return type;
    }

    public String getExtra(){
        return extra;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
