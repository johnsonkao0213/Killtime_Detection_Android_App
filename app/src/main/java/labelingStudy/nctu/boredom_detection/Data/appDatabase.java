package labelingStudy.nctu.boredom_detection.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import labelingStudy.nctu.boredom_detection.dao.ActionDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.MinukuDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationRemoveRecordDao;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationEventRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;

@Database(entities = {MinukuDataRecord.class, NotificationDataRecord.class, NotificationEventRecord.class , ImageDataRecord.class, AnswerDataRecord.class, NoteDataRecord.class, NotificationRemoveRecord.class, PreferenceDataRecord.class, ActionDataRecord.class},version =1 )
public abstract class appDatabase extends RoomDatabase {
    public abstract MinukuDataRecordDAO minukuDataRecordDAO();
    public abstract NotificationDataRecordDAO notificationDataRecordDAO();
    public abstract NotificationEventRecordDAO notificationEventRecordDAO();
    public abstract ImageDataRecordDAO imageDataRecordDAO();
    public abstract NoteDataRecordDAO noteDataRecordDAO();
    public abstract AnswerDataRecordDAO answerDataRecordDAO();
    public abstract NotificationRemoveRecordDao notificationRemoveRecordDao();
    public abstract PreferenceDataRecordDAO preferenceDataRecordDAO();
    public abstract ActionDataRecordDAO actionDataRecordDAO();

    private static appDatabase INSTANCE;
    public static appDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, appDatabase.class,"appDataCollection")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destoryInstance(){
        if (INSTANCE != null)
            INSTANCE.close();
        INSTANCE = null;
    }
}
