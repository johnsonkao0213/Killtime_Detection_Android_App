package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.AppUsageDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface AppUsageDataRecordDAO {
    @Query("SELECT COUNT(*) FROM AppUsageDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM AppUsageDataRecord")
    List<AppUsageDataRecord> getAll();

    @Query("SELECT * FROM AppUsageDataRecord WHERE _id = :id  LIMIT 1")
    List<AppUsageDataRecord>  getById(long id);

    @Insert
    long insertAll(AppUsageDataRecord appUsageDataRecord);

    @Delete
    void deleteOne(AppUsageDataRecord appUsageDataRecord);

    @Update
    void updateOne(AppUsageDataRecord appUsageDataRecord);
}
