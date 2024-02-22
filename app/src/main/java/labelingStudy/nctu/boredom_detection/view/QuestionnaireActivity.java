package labelingStudy.nctu.boredom_detection.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import labelingStudy.nctu.boredom_detection.BoredomApp;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;


public class QuestionnaireActivity extends AppCompatActivity {



    private static final String TAG = "QuestionnaireActivity";

    TextView textView;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    Button submit;
    private NotificationDataRecordDAO NotificationDataRecordDAO;
    NotificationDataRecord notice ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        //BoredomApp.setActivityVisibility(TAG, true);

        Log.i(TAG, "QuestionnaireActivity onCreate");

        textView = (TextView)findViewById(R.id.textView);
        checkBox1 = (CheckBox)findViewById(R.id.checkBox);
        checkBox2 = (CheckBox)findViewById(R.id.checkBox1);
        checkBox3 = (CheckBox)findViewById(R.id.checkBox2);
        checkBox4 = (CheckBox)findViewById(R.id.checkBox3);
        checkBox5 = (CheckBox)findViewById(R.id.checkBox4);
        submit = (Button)findViewById(R.id.button3);
        submit.setOnClickListener(doClick);


        NotificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();

        Intent intent = getIntent();
        long _Id = intent.getLongExtra("_id",-1);
        Log.i(TAG, "_id : " + _Id);
        List<NotificationDataRecord> List = NotificationDataRecordDAO.getByNotifyNotificationID(_Id);

        if(List.size()>0)
        {
            notice = List.get(0);

            textView.setText(notice.getTitle());
            setCheckBox(notice.getOptions());
            //notice.setIsUpdated(1);
            notice.setResponseCode(1);

            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setResponsedTime(current);
            notice.setResponsedTimeString(currentTime);
            notice.setClickedTimeString(currentTime);

            /*
            if(ScheduleAndSampleManager.getCurrentTimeInMillis() - notice.getCreatedTime() > 900000)
                notice.setIsExpired(1);
            else
                notice.setIsExpired(0);

             */

            NotificationDataRecordDAO.updateOne(notice);

        }else{
            Log.e(TAG, "_Id is not found");
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");
        //BoredomApp.setActivityVisibility(TAG, true);
        if (notice!=null) {
            String resumedTimeString = notice.getResumedTimeString();
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setResumedTimeString(resumedTimeString + "," + currentTime);
            if (resumedTimeString.equals("")) notice.setResumedTimeString(currentTime);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause");
        //BoredomApp.setActivityVisibility(TAG, false);
        if (notice!=null) {
            String pausedTimeString = notice.getPausedTimeString();
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setPausedTimeString(pausedTimeString + "," + currentTime);
            if (pausedTimeString.equals("")) notice.setPausedTimeString(currentTime);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        //BoredomApp.setActivityVisibility(TAG, false);
        if (notice!=null) {
            notice.setIsUpdated(1);
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setDestroyTimeString(currentTime);
            NotificationDataRecordDAO.updateOne(notice);
        }
    }

    private void setCheckBox(String options)
    {
        //Log.i(TAG, "setCheckBox " + options);
        String[] optionArr = options.split("\\|");

        if(optionArr.length == 5){
            checkBox1.setText(optionArr[0]);
            checkBox2.setText(optionArr[1]);
            checkBox3.setText(optionArr[2]);
            checkBox4.setText(optionArr[3]);
            checkBox5.setText(optionArr[4]);
        }else {
           Log.e(TAG, "setCheckBox does not support the case: the num of options != 5 ");
        }

    }

    Button.OnClickListener doClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(notice != null)
            {
                notice.setIsUpdated(1);
                notice.setResponseCode(2);

                long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
                String currentTime = ScheduleAndSampleManager.getTimeString(current);
                //notice.setResponsedTime(current);
                //notice.setResponsedTimeString(currentTime);
                notice.setSubmitTimeString(currentTime);

                if(ScheduleAndSampleManager.getCurrentTimeInMillis() - notice.getCreatedTime() > 900000)
                    notice.setIsExpired(1);
                else
                    notice.setIsExpired(0);

                NotificationDataRecordDAO.updateOne(notice);
            }
            //BoredomApp.setActivityVisibility(TAG, false);
            finish();
        }
    };
}
