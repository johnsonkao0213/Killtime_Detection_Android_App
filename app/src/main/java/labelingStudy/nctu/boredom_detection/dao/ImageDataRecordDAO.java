package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;

@Dao
public interface  ImageDataRecordDAO {
    @Query("SELECT COUNT(*) FROM ImageDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM ImageDataRecord")
    List<ImageDataRecord> getAll();

    @Query("SELECT * FROM ImageDataRecord WHERE isReady = :isReady")
    List<ImageDataRecord> getAllIsReady(int isReady);

    @Query("SELECT * FROM ImageDataRecord WHERE  createdTime >= :start AND createdTime <= :end")
    List<ImageDataRecord> getAllByTime(long start, long end);

    @Query("SELECT * FROM ImageDataRecord WHERE isReady = :isReady AND createdTime >= :start AND createdTime <= :end")
    List<ImageDataRecord> getAllIsReadyByTime(int isReady, long start, long end);

    @Query("SELECT * FROM ImageDataRecord WHERE isReady = :isReady LIMIT 1")
    List<ImageDataRecord> getOneIsReady(int isReady);

    @Query("SELECT * FROM ImageDataRecord  WHERE label <> :label order by _id")
    List<ImageDataRecord> getAvailableData(int label);

    @Query("SELECT * FROM ImageDataRecord WHERE isUpload =:isUpload")
    List<ImageDataRecord> getByIsUpload(int isUpload);


    @Query("SELECT * FROM ImageDataRecord WHERE _id = :id")
    List<ImageDataRecord>  getByPKId(long id);

    @Query("SELECT * FROM ImageDataRecord WHERE _id > :id AND isReady = :isReady order by _id")
    List<ImageDataRecord>  getByLastId(long id, int isReady);

    @Query("SELECT * FROM ImageDataRecord WHERE fileName LIKE :fileName")
    List<ImageDataRecord>  getByPKFileName(String fileName);

    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE label = :label")
    int  getCountByLabel (int label);

    @Query("SELECT * FROM ImageDataRecord WHERE label = :label")
    List<ImageDataRecord> getByLabel (int label);

    @Query("UPDATE ImageDataRecord SET label = :label, labeltime = :labeltime, labelTimeString = :labeltimestr WHERE _id = :id")
    void updateLabelById(long id, int label, long labeltime, String labeltimestr);

    @Query("UPDATE ImageDataRecord SET grantUpload = :grantUpload, labeltime = :grantUploadTime WHERE _id = :id")
    void updatePermissionById(long id, int grantUpload, long grantUploadTime);

    @Query("UPDATE ImageDataRecord SET doing = :doing WHERE _id = :id")
    void updateDoingById(long id,  String doing);

    @Query("UPDATE ImageDataRecord SET isReady = :isReady, group_id = :group_id, confirmtime = :confirmtime, confirmTimeString = :confirmTimeString WHERE _id = :id")
    void updateIsReadyById(long id, int isReady, long confirmtime, String confirmTimeString, String group_id);

    @Query("UPDATE ImageDataRecord SET isReady = :isReady, confirmtime = :confirmtime, confirmTimeString = :confirmTimeString WHERE createdTime >= :start AND createdTime <= :end")
    void updateIsReadyByTime(int isReady, long confirmtime, String confirmTimeString, long start, long end);


    @Query("UPDATE ImageDataRecord SET isUpload = :isUpload WHERE fileName LIKE :fileName")
    void updateIsUploadbyFileName(String fileName, int isUpload);


    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE label = -1 AND createdTime >= :start AND createdTime <= :end")
    int  getUnLabeledLabelByTime ( long start, long end);

    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE label <> -1 AND createdTime >= :start AND createdTime <= :end")
    int  getLabeledLabelByTime ( long start, long end);

    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE grantUpload = :grantUpload AND createdTime >= :start AND createdTime <= :end")
    int  getGrantCountByTime ( int grantUpload, long start, long end);

    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE grantUpload = -1 AND createdTime >= :start AND createdTime <= :end")
    int  getUnconfirmGrantByTime ( long start, long end);

    @Query("SELECT COUNT(*) FROM ImageDataRecord WHERE grantUpload <> -1 AND createdTime >= :start AND createdTime <= :end")
    int  getConfirmGrantByTime ( long start, long end);

    @Insert
    long insertOne(ImageDataRecord imageDataRecord);

    @Update
    void updateOne(ImageDataRecord imageDataRecord);

    @Delete
    void deleteOne(ImageDataRecord imageDataRecord);
}
