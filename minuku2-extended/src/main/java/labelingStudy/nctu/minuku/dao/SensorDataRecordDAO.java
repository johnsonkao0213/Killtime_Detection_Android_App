package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.SensorDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface SensorDataRecordDAO {
    @Query("SELECT COUNT(*) FROM SensorDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM SensorDataRecord")
    List<SensorDataRecord> getAll();

    @Query("SELECT * FROM SensorDataRecord WHERE _id = :id  LIMIT 1")
    List<SensorDataRecord>  getById(long id);

    @Insert
    long insertAll(SensorDataRecord sensorDataRecord);

    @Delete
    void deleteOne(SensorDataRecord sensorDataRecord);
}
