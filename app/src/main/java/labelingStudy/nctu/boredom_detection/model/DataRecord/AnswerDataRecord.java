package labelingStudy.nctu.boredom_detection.model.DataRecord;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AnswerDataRecord {

    @ColumnInfo
    private String jsonString;

    @ColumnInfo(name = "isUpload")
    public int isUpload ;

    @ColumnInfo(name = "firebasePK")
    public String firebasePK;

    @PrimaryKey(autoGenerate = true)
    private long id;

    public AnswerDataRecord() {
        this.isUpload = 0;
    }

    public AnswerDataRecord(String answer) {
        this.isUpload = 0;
        this.jsonString = answer;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String answer) {
        this.jsonString = answer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public String getFirebasePK() {
        return firebasePK;
    }

    public void setFirebasePK(String firebasePK) {
        this.firebasePK = firebasePK;
    }
}
