package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;

@Dao
public interface PreferenceDataRecordDAO {

    @Query("SELECT count(*) FROM PreferenceDataRecord WHERE isUpload = :isUpload")
    int getCountByIsUpload(int isUpload);

    @Query("SELECT COUNT(*) FROM PreferenceDataRecord")
    int  getAllRecordSize();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(PreferenceDataRecord p);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(PreferenceDataRecord p);

    @Query("SELECT * FROM PreferenceDataRecord")
    List<PreferenceDataRecord> getAll();

    @Query("SELECT * FROM PreferenceDataRecord WHERE id = :id")
    List<PreferenceDataRecord>  getByPKId(long id);

    @Query("DELETE FROM PreferenceDataRecord")
    void deleteAll();

    @Query("SELECT * FROM PreferenceDataRecord WHERE isUpload =:isUpload")
    List<PreferenceDataRecord> getByIsUpload(int isUpload);

    @Update
    void updateOne(PreferenceDataRecord p);

    @Delete
    void deleteOne(PreferenceDataRecord p);
}