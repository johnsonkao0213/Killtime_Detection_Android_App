package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.RingerDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface RingerDataRecordDAO {
    @Query("SELECT COUNT(*) FROM RingerDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM RingerDataRecord")
    List<RingerDataRecord> getAll();

    @Query("SELECT * FROM RingerDataRecord WHERE creationTime BETWEEN :start AND :end")
    List<RingerDataRecord> getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM RingerDataRecord WHERE _id = :id  LIMIT 1")
    List<RingerDataRecord>  getById(long id);

    @Insert
    long insertAll(RingerDataRecord ringerDataRecord);

    @Delete
    void deleteOne(RingerDataRecord ringerDataRecord);

}
