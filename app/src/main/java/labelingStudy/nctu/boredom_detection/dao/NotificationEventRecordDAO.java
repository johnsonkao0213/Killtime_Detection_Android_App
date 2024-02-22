package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationEventRecord;

@Dao
public interface NotificationEventRecordDAO {
    @Query("SELECT COUNT(*) FROM NotificationEventRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM NotificationEventRecord")
    List<NotificationEventRecord> getAll();

    @Query("SELECT * FROM NotificationEventRecord LIMIT 1")
    List<NotificationEventRecord> getOne();

    @Query("SELECT * FROM NotificationEventRecord WHERE notificationId = :notificationId")
    List<NotificationEventRecord> getbyNotificationId(String notificationId);

    @Query("SELECT * FROM NotificationEventRecord WHERE isEnable = :isEnable AND type LIKE :type AND isUsed = :isUsed ")
    List<NotificationEventRecord>  findValidNotifications(String type, boolean isEnable, boolean isUsed);

    @Query("SELECT * FROM NotificationEventRecord WHERE isEnable = :isEnable AND type LIKE :type ")
    List<NotificationEventRecord>  findValidNotifications(String type, boolean isEnable);

    @Query("UPDATE NotificationEventRecord SET isUsed = :isUsed WHERE type LIKE :type")
    void updateIsUsedByType(String type, boolean isUsed);

    @Insert
    long insertOne(NotificationEventRecord notificationEventRecord);

    @Update
    void updateOne(NotificationEventRecord notificationEventRecord);

    @Delete
    void deleteOne(NotificationEventRecord notificationEventRecord);


}
