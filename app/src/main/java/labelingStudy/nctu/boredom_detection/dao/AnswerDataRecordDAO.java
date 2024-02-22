package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;

@Dao
public interface AnswerDataRecordDAO {

    @Query("SELECT COUNT(*) FROM AnswerDataRecord")
    int  getAllRecordSize();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(AnswerDataRecord answerJson);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(AnswerDataRecord answerJson);

    @Query("SELECT * FROM AnswerDataRecord")
    List<AnswerDataRecord> getAll();

    @Query("DELETE FROM AnswerDataRecord")
    void deleteAll();

    @Query("SELECT * FROM AnswerDataRecord WHERE isUpload =:isUpload")
    List<AnswerDataRecord> getByIsUpload(int isUpload);

    @Update
    void updateOne(AnswerDataRecord notificationDataRecord);

    @Delete
    void deleteOne(AnswerDataRecord notificationDataRecord);
}