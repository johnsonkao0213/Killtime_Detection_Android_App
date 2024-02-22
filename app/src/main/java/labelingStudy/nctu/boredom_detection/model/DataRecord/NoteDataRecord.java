package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteDataRecord {
    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "createdTime")
    public long createdTime;

    // 1 : killtime start
    // 0 : killtime end
    // 2 : task
    @ColumnInfo(name = "mode")
    public int mode;

    // only for note
    @ColumnInfo(name = "type")
    public int type;

    @ColumnInfo(name = "text")
    public String text;

    // only for tasks
    @ColumnInfo(name = "isDone")
    public boolean isDone;


    // only for tasks
    // -1 : not yet
    // 0: some tasks is uploading
    // 1: all tasks are uploaded
    @ColumnInfo(name = "isUpload")
    public int isUpload;

    // only for tasks
    @ColumnInfo(name = "isGroup")
    public boolean isGroup;

    // only for tasks
    @ColumnInfo(name = "starttime")
    public long starttime;

    // only for tasks
    @ColumnInfo(name = "endtime")
    public long endtime;

    // the number of all tasks that will to be uploaded
    @ColumnInfo(name = "num_upload")
    public int num_upload;

    // only for tasks
    // the number of all tasks that skip to be uploaded
    @ColumnInfo(name = "num_skip_upload")
    public int num_skip_upload;


    public NoteDataRecord() {

    }

    public NoteDataRecord(long createdTime, int mode, int type, String text, boolean isDone) {
        this.createdTime = createdTime;
        this.mode = mode;
        this.type = type;
        this.text = text;
        this.isDone = isDone;
        this.isGroup = false;
        this.isUpload = -1;

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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public int getNum_upload() {
        return num_upload;
    }

    public void setNum_upload(int num_upload) {
        this.num_upload = num_upload;
    }

    public int getNum_skip_upload() {
        return num_skip_upload;
    }

    public void setNum_skip_upload(int num_skip_upload) {
        this.num_skip_upload = num_skip_upload;
    }
}
