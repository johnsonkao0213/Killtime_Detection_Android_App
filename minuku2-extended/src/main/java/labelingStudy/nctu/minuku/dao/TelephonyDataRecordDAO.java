package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.TelephonyDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface TelephonyDataRecordDAO {
    @Query("SELECT COUNT(*) FROM TelephonyDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM TelephonyDataRecord")
    List<TelephonyDataRecord> getAll();

    @Query("SELECT * FROM TelephonyDataRecord WHERE creationTime BETWEEN :start AND :end")
    List<TelephonyDataRecord> getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM TelephonyDataRecord WHERE _id = :id  LIMIT 1")
    List<TelephonyDataRecord>  getById(long id);

    @Insert
    long insertAll(TelephonyDataRecord telephonyDataRecord);

    @Delete
    void deleteOne(TelephonyDataRecord telephonyDataRecord);
}
