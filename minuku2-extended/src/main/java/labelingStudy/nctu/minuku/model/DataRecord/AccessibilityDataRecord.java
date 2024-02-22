package labelingStudy.nctu.minuku.model.DataRecord;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

/**
 * Created by Lawrence on 2017/9/6.
 */

/**
 * AccessibilityDataRecord stores information about events happen in the user interface.
 */
@Entity
public class AccessibilityDataRecord implements DataRecord {

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "pack")
    public String pack;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "extra")
    public String extra;

    @ColumnInfo(name = "app")
    public String app;


    @PrimaryKey(autoGenerate = true)
    private long _id;

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }
    public AccessibilityDataRecord(){
        this.creationTime = new Date().getTime();
    }

    public AccessibilityDataRecord(String app, String pack, String text, String type, String extra, long detectedTime){
        this.creationTime = detectedTime;

        this.pack = pack;
        this.text = text;
        this.type = type;
        this.extra = extra;
        this.app = app;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
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

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
