package labelingStudy.nctu.minuku.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import labelingStudy.nctu.minukucore.model.DataRecord;

/**
 * Created by Lawrence on 2017/7/22.
 */

/**
 * ConnectivityDataRecord stores information about network connecting condition and status
 */
@Entity
public class ConnectivityDataRecord implements DataRecord {

    @PrimaryKey(autoGenerate = true)
    private long _id;

    @ColumnInfo(name = "creationTime")
    public long creationTime;

    @ColumnInfo(name = "NetworkType")
    public String NetworkType = "NA";

    @ColumnInfo(name = "IsNetworkAvailable")
    public boolean IsNetworkAvailable = false;

    @ColumnInfo(name = "IsConnected")
    public boolean IsConnected = false;

    @ColumnInfo(name = "IsWifiAvailable")
    public boolean IsWifiAvailable = false;

    @ColumnInfo(name = "IsMobileAvailable")
    public boolean IsMobileAvailable = false;

    @ColumnInfo(name = "IsWifiConnected")
    public boolean IsWifiConnected = false;

    @ColumnInfo(name = "IsMobileConnected")
    public boolean IsMobileConnected = false;



    public ConnectivityDataRecord(String NetworkType,boolean IsNetworkAvailable, boolean IsConnected, boolean IsWifiAvailable,
                                  boolean IsMobileAvailable, boolean IsWifiConnected, boolean IsMobileConnected){
        this.creationTime = new Date().getTime();
        this.NetworkType = NetworkType;
        this.IsNetworkAvailable = IsNetworkAvailable;
        this.IsConnected = IsConnected;
        this.IsWifiAvailable = IsWifiAvailable;
        this.IsMobileAvailable = IsMobileAvailable;
        this.IsWifiConnected = IsWifiConnected;
        this.IsMobileConnected = IsMobileConnected;

    }
    public ConnectivityDataRecord() {}

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getNetworkType() {
        return NetworkType;
    }

    public void setNetworkType(String networkType) {
        NetworkType = networkType;
    }

    public boolean isNetworkAvailable() {
        return IsNetworkAvailable;
    }

    public void setIsNetworkAvailable(boolean isNetworkAvailable) {
        IsNetworkAvailable = isNetworkAvailable;
    }

    public boolean isIsConnected() {
        return IsConnected;
    }

    public void setIsConnected(boolean isConnected) {
        IsConnected = isConnected;
    }

    public boolean isIsWifiAvailable() {
        return IsWifiAvailable;
    }

    public void setIsWifiAvailable(boolean isWifiAvailable) {
        IsWifiAvailable = isWifiAvailable;
    }

    public boolean isIsMobileAvailable() {
        return IsMobileAvailable;
    }

    public void setIsMobileAvailable(boolean isMobileAvailable) {
        IsMobileAvailable = isMobileAvailable;
    }

    public boolean isIsWifiConnected() {
        return IsWifiConnected;
    }

    public void setIsWifiConnected(boolean isWifiConnected) {
        IsWifiConnected = isWifiConnected;
    }

    public boolean isIsMobileConnected() {
        return IsMobileConnected;
    }

    public void setIsMobileConnected(boolean isMobileConnected) {
        IsMobileConnected = isMobileConnected;
    }

}
