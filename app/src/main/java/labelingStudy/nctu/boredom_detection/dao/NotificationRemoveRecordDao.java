package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;

@Dao
public interface NotificationRemoveRecordDao {

    @Query("SELECT count(*) FROM NotificationRemoveRecord WHERE isUpload = :isUpload")
    int getCountByIsUpload(int isUpload);

    @Query("SELECT COUNT(*) FROM NotificationRemoveRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM NotificationRemoveRecord")
    List<NotificationRemoveRecord> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNotiRemove(NotificationRemoveRecord notificationRemoveRecord);

    @Query("SELECT * FROM NotificationRemoveRecord WHERE id = :id")
    List<NotificationRemoveRecord>  getByPKId(int id);

    @Delete
    void deleteNotiRemove(NotificationRemoveRecord... notificationRemoveRecords);

    @Query("DELETE FROM NotificationRemoveRecord")
    void deleteAll();

    @Query("SELECT * FROM NotificationRemoveRecord WHERE isUpload =:isUpload")
    List<NotificationRemoveRecord> getByIsUpload(int isUpload);

    @Update
    void updateOne(NotificationRemoveRecord notificationRemoveRecord);

    @Delete
    void deleteOne(NotificationRemoveRecord notificationRemoveRecord);
}
