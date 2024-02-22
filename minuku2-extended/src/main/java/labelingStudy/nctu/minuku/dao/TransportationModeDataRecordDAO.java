package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.TransportationModeDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface TransportationModeDataRecordDAO {
    @Query("SELECT COUNT(*) FROM TransportationModeDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM TransportationModeDataRecord")
    List<TransportationModeDataRecord> getAll();

    @Query("SELECT * FROM TransportationModeDataRecord WHERE creationTime BETWEEN :start AND :end")
    List<TransportationModeDataRecord> getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM TransportationModeDataRecord WHERE _id = :id  LIMIT 1")
    List<TransportationModeDataRecord>  getById(long id);

    @Insert
    long insertAll(TransportationModeDataRecord transportationModeDataRecord);

    @Delete
    void deleteOne(TransportationModeDataRecord transportationModeDataRecord);
}
