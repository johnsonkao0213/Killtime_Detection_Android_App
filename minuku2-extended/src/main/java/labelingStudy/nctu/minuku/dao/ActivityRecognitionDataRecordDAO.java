package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.ActivityRecognitionDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface ActivityRecognitionDataRecordDAO {
    @Query("SELECT COUNT(*) FROM ActivityRecognitionDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM ActivityRecognitionDataRecord")
    List<ActivityRecognitionDataRecord> getAll();

    @Query("SELECT * FROM ActivityRecognitionDataRecord WHERE _id = :id  LIMIT 1")
    List<ActivityRecognitionDataRecord>  getById(long id);

    @Insert
    long insertAll(ActivityRecognitionDataRecord activityRecognitionDataRecord);

    @Delete
    void deleteOne(ActivityRecognitionDataRecord activityRecognitionDataRecord);
}
