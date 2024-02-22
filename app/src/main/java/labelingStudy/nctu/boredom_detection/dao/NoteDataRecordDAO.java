package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;


@Dao
public interface NoteDataRecordDAO {

    @Query("SELECT COUNT(*) FROM NoteDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM NoteDataRecord")
    List<NoteDataRecord> getAll();

    @Query("SELECT * FROM NoteDataRecord WHERE mode == 2 AND isUpload < 0 ")
    List<NoteDataRecord> getAllIsNotUpdated();

    @Query("SELECT * FROM NoteDataRecord WHERE mode == 2 AND isUpload < 0 AND createdTime >= :start AND createdTime <= :end ORDER BY createdTime DESC")
    List<NoteDataRecord>  getAllIsNotUpdatedByCreatedTime(long start, long end);

    @Query("SELECT * FROM NoteDataRecord WHERE mode == 2 AND isUpload > -1 ")
    List<NoteDataRecord> getAllIsUpdated();

    @Query("SELECT * FROM NoteDataRecord WHERE createdTime >= :start AND createdTime <= :end ORDER BY createdTime DESC")
    List<NoteDataRecord>  getByCreatedTime(long start, long end);

    @Query("SELECT * FROM NoteDataRecord WHERE mode <> :mode ORDER BY createdTime DESC LIMIT 1")
    List<NoteDataRecord>  getByLastMode(int mode);

    @Query("SELECT * FROM NoteDataRecord WHERE _id = :id")
    List<NoteDataRecord>  getByPKId(long id);

    @Query("UPDATE ImageDataRecord SET isUpload = :isUpload  WHERE _id = :id")
    void updateIsUploadbyId(long id, int isUpload);

    @Insert
    long insertOne(NoteDataRecord noteDataRecord);

    @Update
    void updateOne(NoteDataRecord noteDataRecord);
}
