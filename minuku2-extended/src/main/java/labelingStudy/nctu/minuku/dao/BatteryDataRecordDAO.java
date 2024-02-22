package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.BatteryDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface BatteryDataRecordDAO {
    @Query("SELECT COUNT(*) FROM BatteryDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM BatteryDataRecord")
    List<BatteryDataRecord> getAll();

    @Query("SELECT * FROM BatteryDataRecord WHERE creationTime BETWEEN :start AND :end")
    List<BatteryDataRecord> getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM BatteryDataRecord WHERE _id = :id  LIMIT 1")
    List<BatteryDataRecord>  getById(long id);

    @Insert
    long insertAll(BatteryDataRecord batteryDataRecord);

    @Delete
    void deleteOne(BatteryDataRecord batteryDataRecord);
}
