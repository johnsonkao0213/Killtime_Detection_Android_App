package labelingStudy.nctu.boredom_detection;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import labelingStudy.nctu.boredom_detection.fragment.FragmentActivityContext;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.adapters.AdapterFragmentQ;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.fragment.FragmentEnd;
import labelingStudy.nctu.boredom_detection.fragment.FragmentStateSurvey;
import labelingStudy.nctu.boredom_detection.fragment.FragmentAffectSurvey;
import labelingStudy.nctu.boredom_detection.fragment.FragmentOthersSurvey;

import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.boredom_detection.model.SurveyPojo;

public class IntentionSurveyActivity extends AppCompatActivity {
    private static final String TAG = "IntentionSurveyActivity";

    public static final String LINKING_BUNDLE = "LINKING_BUNDLE";
    public static final String LINKING_INTENTION = "LINKING_INTENTION";
    public static final String LINKING_TIME = "LINKING_TIME";



    private SurveyPojo mSurveyPojo;
    private ViewPager mPager;
    private String style_string = null;
    private NotificationDataRecordDAO notificationDataRecordDAO;
    private AnswerDataRecordDAO answerDataRecordDAO;
    private List<NotificationDataRecord> notificationDataRecords;
    private NotificationDataRecord notificationDataRecord;
    private int hintNotificationID = 33;
    private String completeTime;
    private String firebasePK;
    private long id;
    private TextView pageTitleTextView;
    Bundle linkingBundle;
    private String linkingIntention;
    private String linkingTime;
    private Integer linkingTimeHour;
    private Integer linkingTimeMinute;
    private long notificationReceivedTime = 0;
    private static final long TIME_LIMIT = 15*60*1000;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_survey);



        Intent intent = getIntent();
        notificationReceivedTime = intent.getLongExtra("notificationReceivedTime", 0);
        startTimer();

        id = intent.getLongExtra("_id",-1);
        firebasePK = intent.getStringExtra("_firebasePK");

        setTitleLinkingTimeString(intent);


        final ArrayList<Fragment> arraylist_fragments = new ArrayList<>();

        Bundle xBundle = new Bundle();
        xBundle.putLong("id", id);
        xBundle.putString("firebasePK", firebasePK);

        FragmentActivityContext frag = new FragmentActivityContext();
        frag.setArguments(xBundle);
        arraylist_fragments.add(frag);

        FragmentAffectSurvey frag_affect = new FragmentAffectSurvey();
        frag_affect.setArguments(xBundle);
        arraylist_fragments.add(frag_affect);

        FragmentStateSurvey frag_state = new FragmentStateSurvey();
        frag_state.setArguments(xBundle);
        arraylist_fragments.add(frag_state);

//        FragmentOthersSurvey frag_Others = new FragmentOthersSurvey();
//        frag_Others.setArguments(xBundle);
//        arraylist_fragments.add(frag_Others);

        FragmentEnd frag_end = new FragmentEnd();
        frag_end.setArguments(xBundle);
        arraylist_fragments.add(frag_end);

        mPager = (ViewPager) findViewById(R.id.pager);
        AdapterFragmentQ mPagerAdapter = new AdapterFragmentQ(getSupportFragmentManager(), arraylist_fragments);
        mPager.setAdapter(mPagerAdapter);


    }

    private void setTitleLinkingTimeString(Intent intent) {
        linkingBundle = intent.getBundleExtra(LINKING_BUNDLE);
        linkingIntention = linkingBundle.getString(LINKING_INTENTION);
        linkingTime = ScheduleAndSampleManager.extractCurrentTimeString(linkingBundle.getString(LINKING_TIME));
        pageTitleTextView = findViewById(R.id.pageTitle);
        pageTitleTextView.setText(getResources().getString(R.string.linkingTitle, linkingTime, linkingIntention));
    }

    private void startTimer() {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run () {
                    // 当计时结束时调用
                    cancelSurveyIfNotCompleted();
                }
            };
        handler.postDelayed(runnable,TIME_LIMIT);
    }

    private void cancelSurveyIfNotCompleted() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - notificationReceivedTime) >= TIME_LIMIT) {
            showTimeDialog();
        }
    }

    private void showTimeDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.timeout))
                .setMessage(getString(R.string.timeout_message, TIME_LIMIT/(60*1000)))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void go_to_next() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            showExitConfirmationDialog();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit_confirmation))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // press cancel
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // press yes
                        finish();
                    }
                })
                .show();
    }
    public static String handleRadioGroupResponses(RadioGroup radioGroup, LinearLayout layout){
        int checkedId = radioGroup.getCheckedRadioButtonId();
        String checkedText = "";
        if (checkedId != -1) {
            RadioButton checkedRadioButton = layout.findViewById(checkedId);
            checkedText = checkedRadioButton.getText().toString();
        }
        else{
            checkedText ="None";
        }

        return checkedText;

    }
    public static String returnUnansweredQuestions(ArrayList<String> responseList){
        int questionNumber = 1;
        String unanswered = "";
        for (String response : responseList) {
            if (response == "None") {
                unanswered +=  questionNumber + ", ";
            }
            questionNumber += 1;
        }
        return unanswered;

    }
    public void event_survey_completed(Answers instance) {
        answerDataRecordDAO =  appDatabase.getDatabase(this).answerDataRecordDAO();
//        // save to db
        completeTime = Utils.getCurrentTimeString();

        Log.i(TAG, "event_survey_completed: ");
        Log.i(TAG, "completed time: " + completeTime);
//
        Answers answer = Answers.getStateInstance();
        answer.put_answer("completedTimeString", completeTime);

        Utils.updateAnswerData(this, answer, id, firebasePK);

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
        long countsESMAllStateSurvey = sharedPrefs.getLong("countsESMAllStateSurvey", 0);
        sharedPrefs.edit()
                .putLong("countsESMAllStateSurvey", countsESMAllStateSurvey+1)
                .commit();

//
//        AnswerDataRecord answerDataRecord = new AnswerDataRecord();
//        answerDataRecord.setIsUpload(0);
//        answerDataRecord.setId(id);
//        answerDataRecord.setFirebasePK(firebasePK);
//        answerDataRecord.setJsonString(answer.get_json_object());
////
//        // store to db
//        long id2 = answerDataRecordDAO.insert(answerDataRecord);
//        if (id2 == -1) {
//            answerDataRecordDAO.update(answerDataRecord);
//        }
//
//         debug

        List<AnswerDataRecord> notuploadanswers = answerDataRecordDAO.getByIsUpload(0);
        for (AnswerDataRecord a: notuploadanswers) {
            Log.i(TAG, a.getJsonString());
        }
        finish();
//
//        // TODO: upload survey data to firestore
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
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


}
