package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.AccessibilityDataRecord;

@Dao
public interface AccessibilityDataRecordDAO {
    @Query("SELECT COUNT(*) FROM AccessibilityDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM AccessibilityDataRecord")
    List<AccessibilityDataRecord> getAll();

    @Query("SELECT * FROM AccessibilityDataRecord WHERE _id = :id  LIMIT 1")
    List<AccessibilityDataRecord>  getById(long id);

    @Insert
    long insertAll(AccessibilityDataRecord accessibilityDataRecord);

    @Delete
    void deleteOne(AccessibilityDataRecord accessibilityDataRecord);
}