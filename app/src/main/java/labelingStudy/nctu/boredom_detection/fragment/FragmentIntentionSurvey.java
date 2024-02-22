package labelingStudy.nctu.boredom_detection.fragment;

import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;

public class FragmentIntentionSurvey extends Fragment {
    private static final String TAG = "FragmentIntentionSurvey";

    private AnswerDataRecordDAO answerDataRecordDAO;

    private ImageView icon;
    private TextView when;
    private TextView appname;
    private TextView time;
    private TextView title;
    private TextView content;
    private Button button_continue;

    private RadioGroup radioGroupQ1;
    private RadioGroup radioGroupQ2;
    private EditText editTextQ2;
    private RadioGroup radioGroupQ3;
    private TextView titleQ3;
    private EditText editTextQ3;
    private FragmentActivity mContext;
    private long id;
    private int count;
    private LinearLayout linearLayoutQ2;
    private LinearLayout linearLayoutQ3;
    private boolean onlyOne = false;
    private boolean onlyTwo = false;

    private String firebasePk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_intention_survey, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();
        button_continue = rootView.findViewById(R.id.button_continue);
        radioGroupQ2 = rootView.findViewById(R.id.q2);
        editTextQ2 = rootView.findViewById(R.id.q2_other);
        linearLayoutQ2 = rootView.findViewById(R.id.q2_all);
        linearLayoutQ2.setVisibility(LinearLayout.VISIBLE);




        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers answer = Answers.getIntentionInstance();
                int i2 = radioGroupQ2.getCheckedRadioButtonId();
                String a2 = "";
                if(i2 == -1){
                    Toast.makeText(getActivity(), "請完成所有題目", Toast.LENGTH_SHORT).show();
                }else {
                    RadioButton selectedRadioButton2 = rootView.findViewById(i2);
                    a2 = selectedRadioButton2.getText().toString();
                    if (a2.equals("其他")) {
                        a2 = "其他";
                    }
                    a2 = a2 + ":" + editTextQ2.getText().toString();
                    answer.put_answer("Intention", a2);
                    Utils.updateAnswerData(getContext(), answer, id, firebasePk);

                    Log.i(TAG, answer.get_json_object());
                    ((IntentionSurveyActivity) mContext).go_to_next();
                }


            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
//        icon.setImageResource(R.drawable.muilab_icon_noti);
//        when.setText("您在 "+ getArguments().getString("time") + " 時收到以下通知：");
//        appname.setText("殺時間標記");
//        title.setText(getArguments().getString("title"));
//        content.setText(getArguments().getString("content"));
//        time.setText(getArguments().getString("time"));
        id = getArguments().getLong("id");
//        count = getArguments().getInt("count");
        firebasePk = getArguments().getString("firebasePK");

//        NotificationDataRecordDAO notificationDataRecordDAO = appDatabase.getDatabase(mContext).notificationDataRecordDAO();
//        NotificationDataRecord notificationDataRecord = notificationDataRecordDAO.getByNotifyNotificationID( id).get(0);
//        Log.i("RespondCode", notificationDataRecord.getType() + notificationDataRecord.getResponseCode());
//        if (notificationDataRecord.getType().equals("Questionnaire")
//                || notificationDataRecord.getType().equals("Crowdsourcing")
//                || notificationDataRecord.getResponseCode()!=1) {
//            linearLayoutQ3.setVisibility(LinearLayout.GONE);
//            onlyTwo = true;
//        } else if (notificationDataRecord.getType().equals("News")) {
//            titleQ3.setText("3. 點擊通知後，我閱讀此新聞的仔細程度為？");
//            onlyTwo = false;
//        } else if (notificationDataRecord.getType().equals("Advertisement")) {
//            titleQ3.setText("3. 點擊通知後，我閱讀此廣告的仔細程度為？");
//            onlyTwo = false;
//        }
    }
}
