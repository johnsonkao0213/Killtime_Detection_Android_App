package labelingStudy.nctu.boredom_detection.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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



public class CrowdsourcingActivity extends AppCompatActivity {

    TextView overview, question;
    RadioGroup rg;
    RadioButton rb1, rb2, rb3, rb4, rb5;
    Button submit;

    private static final String TAG = "CrowdsourcingActivity";

    private NotificationDataRecordDAO NotificationDataRecordDAO;
    NotificationDataRecord notice  =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdsourcing);
        //BoredomApp.setActivityVisibility(TAG, true);


        Log.i(TAG, "CrowdsourcingActivity onCreate");


        overview = (TextView)findViewById(R.id.tv_overview);
        question = (TextView)findViewById(R.id.tv_question);
        rg = (RadioGroup)findViewById(R.id.rg);
        rb1 = (RadioButton)findViewById(R.id.radioButton);
        rb2 = (RadioButton)findViewById(R.id.radioButton2);
        rb3 = (RadioButton)findViewById(R.id.radioButton3);
        rb4 = (RadioButton)findViewById(R.id.radioButton4);
        rb5 = (RadioButton)findViewById(R.id.radioButton5);
        submit = (Button)findViewById(R.id.btn_submit);
        submit.setOnClickListener(doClick);

        rg.setOnCheckedChangeListener(doCheck);

        overview.setText("");


        NotificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();
        onNewIntent(getIntent());

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            long _Id = extras.getLong("_id", -1);
            Log.i(TAG, "_id : " + _Id);
            List<NotificationDataRecord> List = NotificationDataRecordDAO.getByNotifyNotificationID(_Id);

            if (List.size() > 0) {
                notice = List.get(0);

                question.setText(notice.getTitle());

                setRadioButton(notice.getOptions());

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

            } else {
                Log.e(TAG, "_Id is not found");
            }
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
            NotificationDataRecordDAO.updateOne(notice);
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
            NotificationDataRecordDAO.updateOne(notice);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        // BoredomApp.setActivityVisibility(TAG, false);
        if (notice!=null) {
            notice.setIsUpdated(1);
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setDestroyTimeString(currentTime);
            NotificationDataRecordDAO.updateOne(notice);
        }
    }


    private RadioGroup.OnCheckedChangeListener doCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

        }
    };

    private void setRadioButton(String options)
    {
        //Log.i(TAG, "setRadioButton " + options);
        String[] optionArr = options.split("\\|");
        //Log.i(TAG, "setRadioButton optionArr.length " + optionArr.length);
        if(optionArr.length == 5){
            rb1.setText(optionArr[0]);
            rb2.setText(optionArr[1]);
            rb3.setText(optionArr[2]);
            rb4.setText(optionArr[3]);
            rb5.setText(optionArr[4]);
        }else if(optionArr.length == 2) {
            rb1.setVisibility(View.INVISIBLE);
            rb2.setText(optionArr[0]);
            rb3.setVisibility(View.INVISIBLE);
            rb4.setText(optionArr[1]);
            rb5.setVisibility(View.INVISIBLE);
        }
        else {
            Log.e(TAG, "setRadioButton does not support the case: the num of options != 2 or !=5 ");
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
