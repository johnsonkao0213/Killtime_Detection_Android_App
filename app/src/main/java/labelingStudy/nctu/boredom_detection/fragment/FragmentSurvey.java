package labelingStudy.nctu.boredom_detection.fragment;

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

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.SurveyActivity;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;

public class FragmentSurvey extends Fragment {
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
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();

        when = rootView.findViewById(R.id.when);
        icon = rootView.findViewById(R.id.icon);
        appname = rootView.findViewById(R.id.appname);
        time = rootView.findViewById(R.id.time);
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        button_continue = rootView.findViewById(R.id.button_continue);

        radioGroupQ1 = rootView.findViewById(R.id.q1);
        radioGroupQ2 = rootView.findViewById(R.id.q2);
        editTextQ2 = rootView.findViewById(R.id.q2_other);
        titleQ3 = rootView.findViewById(R.id.q3_title);
        radioGroupQ3 = rootView.findViewById(R.id.q3);
        linearLayoutQ2 = rootView.findViewById(R.id.q2_all);
        linearLayoutQ3 = rootView.findViewById(R.id.q3_all);

        linearLayoutQ2.setVisibility(LinearLayout.GONE);
        linearLayoutQ3.setVisibility(LinearLayout.GONE);

        radioGroupQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.q1_1) { // Yes
                    linearLayoutQ2.setVisibility(LinearLayout.VISIBLE);
                    onlyOne = false;
                } else if (checkedId == R.id.q1_2) { // No
                    linearLayoutQ2.setVisibility(LinearLayout.GONE);
                    onlyOne = true;
                }

            }

        });

        radioGroupQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.q2_1 || checkedId == R.id.q2_2 || checkedId == R.id.q2_6  ) { // Yes
                    linearLayoutQ3.setVisibility(LinearLayout.VISIBLE);
                    onlyTwo = false;
                    onlyOne = false;
                } else if (checkedId == R.id.q2_4 || checkedId == R.id.q2_5) { // No
                    onlyTwo = true;
                    onlyOne = false;
                    linearLayoutQ3.setVisibility(LinearLayout.GONE);
                }

            }

        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers answer = Answers.getInstance();

                int i1 = radioGroupQ1.getCheckedRadioButtonId();
                int i2 = radioGroupQ2.getCheckedRadioButtonId();
                int i3 = radioGroupQ3.getCheckedRadioButtonId();
                String a2 = "";
                String a3 = "-1";
                switch (i3) {
                    case R.id.q3_0:
                        a3 = "5";
                        break;
                    case R.id.q3_1:
                        a3 = "4";
                        break;
                    case R.id.q3_2:
                        a3 = "3";
                        break;
                    case R.id.q3_3:
                        a3 = "2";
                        break;
                    case R.id.q3_4:
                        a3 = "1";
                        break;
                }
                if (i1==-1 || (i2==-1 && !onlyOne) || (i3==-1 && !onlyOne && !onlyTwo)) {
                    Toast.makeText(getActivity(), "請完成所有題目", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton1 = rootView.findViewById(i1);
                    RadioButton selectedRadioButton2 = rootView.findViewById(i2);
                    RadioButton selectedRadioButton3 = rootView.findViewById(i3);
                    String selectedRadioButtonText1 = selectedRadioButton1.getText().toString();
                    if (!onlyOne)
                        a2 = selectedRadioButton2.getText().toString();
                    //String selectedRadioButtonText3 = selectedRadioButton3.getText().toString();
                    if (a2.equals("其他")) {
                        a2 = a2 + ":" + editTextQ2.getText().toString();
                    }
                    answer.put_answer("q1", selectedRadioButtonText1);
                    answer.put_answer("q2", a2 );
                    answer.put_answer("q3", a3);

                    AnswerDataRecord answerDataRecord = new AnswerDataRecord();
                    answerDataRecord.setIsUpload(0);
                    answerDataRecord.setId(id);
                    answerDataRecord.setFirebasePK(firebasePk);
                    answerDataRecord.setJsonString(answer.get_json_object());

                    // store to db
                    long id = answerDataRecordDAO.insert(answerDataRecord);
                    if (id == -1) {
                        answerDataRecordDAO.update(answerDataRecord);
                    }


                    Log.i("FragmentSurvey", answer.get_json_object());

                    ((SurveyActivity) mContext).go_to_next();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        icon.setImageResource(R.drawable.muilab_icon_noti);
        when.setText("您在 "+ getArguments().getString("time") + " 時收到以下通知：");
        appname.setText("殺時間標記");
        title.setText(getArguments().getString("title"));
        content.setText(getArguments().getString("content"));
        time.setText(getArguments().getString("time"));
        id = getArguments().getLong("id");
        count = getArguments().getInt("count");
        firebasePk = getArguments().getString("firebasePK");

        NotificationDataRecordDAO notificationDataRecordDAO = appDatabase.getDatabase(mContext).notificationDataRecordDAO();
        NotificationDataRecord notificationDataRecord = notificationDataRecordDAO.getByNotifyNotificationID( id).get(0);
        Log.i("RespondCode", notificationDataRecord.getType() + notificationDataRecord.getResponseCode());
        if (notificationDataRecord.getType().equals("Questionnaire")
                || notificationDataRecord.getType().equals("Crowdsourcing")
                || notificationDataRecord.getResponseCode()!=1) {
            linearLayoutQ3.setVisibility(LinearLayout.GONE);
            onlyTwo = true;
        } else if (notificationDataRecord.getType().equals("News")) {
            titleQ3.setText("3. 點擊通知後，我閱讀此新聞的仔細程度為？");
            onlyTwo = false;
        } else if (notificationDataRecord.getType().equals("Advertisement")) {
            titleQ3.setText("3. 點擊通知後，我閱讀此廣告的仔細程度為？");
            onlyTwo = false;
        }
    }
}
