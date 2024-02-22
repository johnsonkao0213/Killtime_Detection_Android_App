package labelingStudy.nctu.boredom_detection;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.adapters.AdapterFragmentQ;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.fragment.FragmentEnd;
import labelingStudy.nctu.boredom_detection.fragment.FragmentSurvey;
import labelingStudy.nctu.boredom_detection.fragment.FragmentSurveyContent;
import labelingStudy.nctu.boredom_detection.fragment.FragmentSurveyContext;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.SurveyPojo;

public class SurveyActivity extends AppCompatActivity {
    private static final String TAG = "SurveyActivity";

    private SurveyPojo mSurveyPojo;
    private ViewPager mPager;
    private String style_string = null;
    private NotificationDataRecordDAO notificationDataRecordDAO;
    private AnswerDataRecordDAO answerDataRecordDAO;
    private List<NotificationDataRecord> notificationDataRecords;
    private NotificationDataRecord notificationDataRecord;
    private int hintNotificationID = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_survey);
        //BoredomApp.setActivityVisibility(TAG, true);


        final ArrayList<Fragment> arraylist_fragments = new ArrayList<>();

        // survey by jj
        Log.i("SurveyActivity", "onCreate");
        notificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();
        answerDataRecordDAO =  appDatabase.getDatabase(this).answerDataRecordDAO();
        /*
        notificationDataRecords = notificationDataRecordDAO.getUnsurveyed();
        if(notificationDataRecords.size()>0) {
            int count = 0;
            for (NotificationDataRecord notificationDataRecord: notificationDataRecords) {
                Log.i("SurveyActivity", notificationDataRecord.getTitle());
                long id = notificationDataRecord.get_id();
                String title = notificationDataRecord.getType();
                String content = notificationDataRecord.getTitle();
                String time = notificationDataRecord.getCreatedTimeString();
                time = time.substring(5,16);
                String firebasePK = notificationDataRecord.getFirebasePK();
                // general
                FragmentSurvey frag = new FragmentSurvey();
                Bundle xBundle = new Bundle();
                xBundle.putLong("id", id);
                xBundle.putInt("count", count);
                xBundle.putString("title", title);
                xBundle.putString("content", content);
                xBundle.putString("time", time);
                xBundle.putString("firebasePK", firebasePK);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);

                // content
                FragmentSurveyContent frag2 = new FragmentSurveyContent();
                Bundle xBundle2 = new Bundle();
                xBundle2.putLong("id", id);
                xBundle2.putInt("count", count);
                xBundle2.putString("title", title);
                xBundle2.putString("content", content);
                xBundle2.putString("time", time);
                xBundle2.putString("firebasePK", firebasePK);
                frag2.setArguments(xBundle2);
                arraylist_fragments.add(frag2);

                // context
                FragmentSurveyContext frag3 = new FragmentSurveyContext();
                Bundle xBundle3 = new Bundle();
                xBundle3.putLong("id", id);
                xBundle3.putInt("count", count);
                xBundle3.putString("title", title);
                xBundle3.putString("content", content);
                xBundle3.putString("time", time);
                xBundle3.putString("firebasePK", firebasePK);
                frag3.setArguments(xBundle3);
                arraylist_fragments.add(frag3);
            }
        }
         */

        // new survey by jj
        Intent intent = getIntent();
        long id = intent.getLongExtra("_id",-1);
        List<NotificationDataRecord> notificationDataRecords = notificationDataRecordDAO.getByNotifyNotificationID(id);
        if(notificationDataRecords.size()>0) {
            notificationDataRecord = notificationDataRecords.get(0);
            String title = notificationDataRecord.getType();
            String content = notificationDataRecord.getTitle();
            String time = notificationDataRecord.getCreatedTimeString();
            time = time.substring(5, 16);
            String firebasePK = notificationDataRecord.getFirebasePK();

            // general
            FragmentSurvey frag = new FragmentSurvey();
            Bundle xBundle = new Bundle();
            xBundle.putLong("id", id);
            xBundle.putString("title", title);
            xBundle.putString("content", content);
            xBundle.putString("time", time);
            xBundle.putString("firebasePK", firebasePK);
            frag.setArguments(xBundle);
            arraylist_fragments.add(frag);

            // content
            FragmentSurveyContent frag2 = new FragmentSurveyContent();
            Bundle xBundle2 = new Bundle();
            xBundle2.putLong("id", id);
            xBundle2.putString("title", title);
            xBundle2.putString("content", content);
            xBundle2.putString("time", time);
            xBundle2.putString("firebasePK", firebasePK);
            frag2.setArguments(xBundle2);
            arraylist_fragments.add(frag2);

            // context
            FragmentSurveyContext frag3 = new FragmentSurveyContext();
            Bundle xBundle3 = new Bundle();
            xBundle3.putLong("id", id);
            xBundle3.putString("title", title);
            xBundle3.putString("content", content);
            xBundle3.putString("time", time);
            xBundle3.putString("firebasePK", firebasePK);
            frag3.setArguments(xBundle3);
            arraylist_fragments.add(frag3);

            //- END -
            FragmentEnd frag_end = new FragmentEnd();
            arraylist_fragments.add(frag_end);


            mPager = (ViewPager) findViewById(R.id.pager);
            AdapterFragmentQ mPagerAdapter = new AdapterFragmentQ(getSupportFragmentManager(), arraylist_fragments);
            mPager.setAdapter(mPagerAdapter);
        }else{
            finish();
        }


    }

    public void go_to_next() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void event_survey_completed(Answers instance) {
        // save to db
        Log.i("SurveyActivity", "event_survey_completed");


        // update NotificationDataRecord
        //for (NotificationDataRecord notificationDataRecord: notificationDataRecords){
            notificationDataRecord.setSurveyed();
            notificationDataRecordDAO.updateOne(notificationDataRecord);
        //}

        // change notification
        List<NotificationDataRecord> questions = notificationDataRecordDAO.getUnsurveyed();
        String numUnsurveyed = Integer.toString(questions.size());
        String text = "有" + numUnsurveyed + "個問卷等待你填寫";
        Notification.Builder noti = new Notification.Builder(this)
                .setSmallIcon(R.drawable.muilab_icon_noti)
                .setContentText(text);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(hintNotificationID, noti.build());

        // debug
        List<AnswerDataRecord> allanswers = answerDataRecordDAO.getAll();
        for (AnswerDataRecord answerDataRecord: allanswers) {
            Log.i("SurveyActivity", answerDataRecord.getJsonString());
        }
        finish();

        // TODO: upload survey data to firestore
    }

    @Override
    public void onResume(){
        super.onResume();
        //BoredomApp.setActivityVisibility(TAG, true);
    }
    @Override
    public void onPause(){
        super.onPause();
        //BoredomApp.setActivityVisibility(TAG, false);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //BoredomApp.setActivityVisibility(TAG, false);
    }
}
