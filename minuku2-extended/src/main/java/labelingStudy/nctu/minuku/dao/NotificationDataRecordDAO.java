package labelingStudy.nctu.minuku.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import labelingStudy.nctu.minuku.model.DataRecord.NotificationDataRecord;


/**
 * Created by chiaenchiang on 27/10/2018.
 */
@Dao
public interface NotificationDataRecordDAO {
    @Query("SELECT COUNT(*) FROM NotificationDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM NotificationDataRecord")
    List<NotificationDataRecord> getAll();

    @Query("SELECT * FROM NotificationDataRecord WHERE creationTime BETWEEN :start AND :end")
    Cursor getRecordBetweenTimes(long start, long end);

    @Query("SELECT * FROM NotificationDataRecord WHERE _id = :id  LIMIT 1")
    List<NotificationDataRecord>  getById(long id);

    @Insert
    long insertAll(NotificationDataRecord notificationDataRecord);

    @Delete
    void deleteOne(NotificationDataRecord notificationDataRecord);

}
