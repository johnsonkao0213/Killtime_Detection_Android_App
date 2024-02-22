package labelingStudy.nctu.boredom_detection.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;

@Dao
public interface MinukuDataRecordDAO {

    @Query("SELECT COUNT(*) FROM MinukuDataRecord")
    int  getAllRecordSize();

    @Query("SELECT * FROM MinukuDataRecord")
    List<MinukuDataRecord> getAll();

    @Query("SELECT * FROM MinukuDataRecord WHERE _id = :id")
    List<MinukuDataRecord>  getByPKId(long id);

    @Query("SELECT * FROM MinukuDataRecord WHERE isUpload = :isUpload AND isUploading = :isUploading")
    List<MinukuDataRecord>  getByIsUpload(int isUpload, int isUploading);

    @Query("SELECT count(*) FROM MinukuDataRecord WHERE isUpload = :isUpload AND isUploading = :isUploading")
    int getCountByIsUpload(int isUpload, int isUploading);

    @Query("SELECT * FROM MinukuDataRecord WHERE isUploading = :isUploading")
    List<MinukuDataRecord>  getByIsUploading( int isUploading);

    @Query("SELECT * FROM MinukuDataRecord WHERE ImageFileName LIKE :ImageFileName")
    List<MinukuDataRecord>  getByImageFileName( String ImageFileName);

    @Insert
    long insertOne(MinukuDataRecord minukuDataRecord);

    @Update
    void updateOne(MinukuDataRecord minukuDataRecord);

    @Delete
    void deleteOne(MinukuDataRecord minukuDataRecord);
}
