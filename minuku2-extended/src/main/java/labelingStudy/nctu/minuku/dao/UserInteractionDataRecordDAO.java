package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.UserInteractionDataRecord;

/**
 * Created by tingwei on 2018/9/10.
 */
@Dao
public interface UserInteractionDataRecordDAO {
    @Query("SELECT COUNT(*) FROM UserInteractionDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM UserInteractionDataRecord")
    List<UserInteractionDataRecord> getAll();

    @Query("SELECT * FROM UserInteractionDataRecord WHERE _id = :id  LIMIT 1")
    List<UserInteractionDataRecord>  getById(long id);

    @Insert
    long insertAll(UserInteractionDataRecord userInteractionDataRecord);

    @Delete
    void deleteOne(UserInteractionDataRecord userInteractionDataRecord);
}
