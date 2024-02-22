package labelingStudy.nctu.boredom_detection.model.DataRecord;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
* Created by Lesley on 2020/6/14.
*
* ImageDateRecord stores information about all screenshot image data.
*/
@Entity
public class ImageDataRecord {
    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "isUpload")
    public int isUpload ;

    @ColumnInfo(name = "isReady")
    public int isReady ;

    @ColumnInfo(name = "createdTime")
    public long createdTime ;

    @ColumnInfo(name = "fileName")
    public String fileName ;

    @ColumnInfo(name = "doing")
    public String doing;

    @ColumnInfo(name = "grantUpload")
    public int grantUpload;

    @ColumnInfo(name = "grantUploadTime")
    public long grantUploadTime;

    @ColumnInfo(name = "label")
    public int label;

    @ColumnInfo(name = "labeltime")
    public long labeltime;

    @ColumnInfo(name = "labelTimeString")
    public String labelTimeString;

    @ColumnInfo(name = "confirmtime")
    public long confirmtime;

    @ColumnInfo(name = "confirmTimeString")
    public String confirmTimeString;

    @ColumnInfo(name = "group_id")
    public String group_id;


    public ImageDataRecord(String fileName, long createdTime) {
        this.isUpload = 0;
        this.fileName = fileName;
        this.createdTime = createdTime;
        this.grantUpload = -1;
        this.grantUploadTime = -1;
        this.label = -1;
        this.labeltime = -1;
        this.labelTimeString = "";
        this.confirmtime = -1;
        this.confirmTimeString = "";
        this.isReady = 0;
        this.group_id = "";
        this.doing = "";
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public long getLabeltime() {
        return labeltime;
    }

    public void setLabeltime(long labeltime) {
        this.labeltime = labeltime;
    }


    public int getIsReady() {
        return isReady;
    }

    public void setIsReady(int isReady) {
        this.isReady = isReady;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public long getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(long confirmtime) {
        this.confirmtime = confirmtime;
    }

    public String getLabelTimeString() {
        return labelTimeString;
    }

    public void setLabelTimeString(String labelTimeString) {
        this.labelTimeString = labelTimeString;
    }

    public String getConfirmTimeString() {
        return confirmTimeString;
    }

    public void setConfirmTimeString(String confirmTimeString) {
        this.confirmTimeString = confirmTimeString;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }

    public int getGrantUpload() {
        return grantUpload;
    }

    public void setGrantUpload(int grantUpload) {
        this.grantUpload = grantUpload;
    }

    public long getGrantUploadTime() {
        return grantUploadTime;
    }

    public void setGrantUploadTime(long grantUploadTime) {
        this.grantUploadTime = grantUploadTime;
    }

    public boolean isLabeled() {
        return this.label != -1;
    }
}
