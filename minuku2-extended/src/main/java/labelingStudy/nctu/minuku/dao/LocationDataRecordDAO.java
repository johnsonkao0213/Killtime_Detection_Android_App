package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.LocationDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface LocationDataRecordDAO {
    @Query("SELECT COUNT(*) FROM LocationDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM LocationDataRecord")
    List<LocationDataRecord> getAll();

    @Query("SELECT * FROM LocationDataRecord WHERE creationTime BETWEEN :start AND :end")
    List<LocationDataRecord> getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM LocationDataRecord WHERE _id = :id  LIMIT 1")
    List<LocationDataRecord>  getById(long id);

    @Insert
    long insertAll(LocationDataRecord locationDataRecord);

    @Delete
    void deleteOne(LocationDataRecord locationDataRecord);
}
