package labelingStudy.nctu.minuku.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;


@Dao
public interface MobileAccessibilityDataRecordDAO {

    @Query("SELECT count(*) FROM MobileAccessibilityDataRecord WHERE isUpload = :isUpload")
    int getCountByIsUpload(int isUpload);

    @Query("SELECT COUNT(*) FROM MobileAccessibilityDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM MobileAccessibilityDataRecord")
    List<MobileAccessibilityDataRecord> getAll();

    @Query("SELECT * FROM MobileAccessibilityDataRecord WHERE isUpload =:isUpload")
    List<MobileAccessibilityDataRecord> getByIsUpload(int isUpload);

    @Query("SELECT * FROM MobileAccessibilityDataRecord WHERE id = :id")
    List<MobileAccessibilityDataRecord>  getByPKId(long id);

    @Insert
    long insertOne(MobileAccessibilityDataRecord mobileAccessibilityDataRecord);

    @Delete
    void deleteOne(MobileAccessibilityDataRecord... mobileAccessibilityDataRecord);

    @Query("DELETE FROM MobileAccessibilityDataRecord")
    void deleteAll();

    @Update
    void updateOne(MobileAccessibilityDataRecord mobileAccessibilityDataRecord);
}