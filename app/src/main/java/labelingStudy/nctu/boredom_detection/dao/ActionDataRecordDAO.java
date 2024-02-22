package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;


@Dao
public interface ActionDataRecordDAO {

    @Query("SELECT COUNT(*) FROM ActionDataRecord")
    int  getAllRecordSize();

    @Query("SELECT count(*) FROM ActionDataRecord")
    int getAllCount();

    @Query("SELECT * FROM ActionDataRecord")
    List<ActionDataRecord> getAll();

    @Query("SELECT * FROM ActionDataRecord WHERE createdTime >= :start AND createdTime <= :end")
    List<ActionDataRecord>  getByCreatedTime(long start, long end);

    @Query("SELECT * FROM ActionDataRecord WHERE _id = :id")
    List<ActionDataRecord>  getByPKId(long id);

    @Insert
    long insertOne(ActionDataRecord actionDataRecord);

    @Update
    void updateOne(ActionDataRecord actionDataRecord);

    @Delete
    void deleteOne(ActionDataRecord actionDataRecord);
}
