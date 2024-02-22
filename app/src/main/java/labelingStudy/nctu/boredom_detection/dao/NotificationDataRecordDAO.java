package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;

@Dao
public interface NotificationDataRecordDAO {

    @Query("SELECT COUNT(*) FROM NotificationDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM NotificationDataRecord")
    List<NotificationDataRecord> getAll();

    @Query("SELECT * FROM NotificationDataRecord WHERE isUpload =:isUpload  AND isUploading = :isUploading")
    List<NotificationDataRecord> getByIsUpload(int isUpload, int isUploading);

    @Query("SELECT * FROM NotificationDataRecord WHERE isUpdated =:isUpdated AND isUpload =:isUpload AND isUploading = :isUploading")
    List<NotificationDataRecord> getByIsUpdated(int isUpdated, int isUpload, int isUploading);

    @Query("SELECT * FROM NotificationDataRecord WHERE isUploading = :isUploading")
    List<NotificationDataRecord>  getByIsUploading(int isUploading);

    @Query("SELECT * FROM NotificationDataRecord WHERE firebasePK LIKE :firebasePK")
    List<NotificationDataRecord>  getByfirebasePK(String firebasePK);

    @Query("SELECT * FROM NotificationDataRecord WHERE responseCode =:code1 OR responseCode =:code2")
    List<NotificationDataRecord> getBYResponseCode(int code1, int code2);

    @Query("SELECT * FROM NotificationDataRecord WHERE (responseCode = 0 OR responseCode = -1) AND isSurveyed = 0 AND isExpired = 1")
    List<NotificationDataRecord> getUnsurveyed();

    @Query("SELECT * FROM NotificationDataRecord WHERE _id = :id")
    List<NotificationDataRecord>  getByPKId(long id);

    @Query("SELECT * FROM NotificationDataRecord WHERE notifyNotificationID = :notifyNotificationID")
    List<NotificationDataRecord>  getByNotifyNotificationID(long notifyNotificationID);

    @Insert
    long insertOne(NotificationDataRecord notificationDataRecord);

    @Update
    void updateOne(NotificationDataRecord notificationDataRecord);
}
