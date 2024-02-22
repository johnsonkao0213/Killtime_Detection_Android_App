package labelingStudy.nctu.boredom_detection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;

public class FragmentSurveyContent extends Fragment {
    private AnswerDataRecordDAO answerDataRecordDAO;

    private ImageView icon;
    private TextView when;
    private TextView appname;
    private TextView time;
    private TextView title;
    private TextView content;
    private Button button_continue;

    private RadioGroup radioGroupQ4;
    private RadioGroup radioGroupQ5;
    private FragmentActivity mContext;
    private long id;
    private int count;

    private String firebasePk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_content, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();

        when = rootView.findViewById(R.id.when);
        icon = rootView.findViewById(R.id.icon);
        appname = rootView.findViewById(R.id.appname);
        time = rootView.findViewById(R.id.time);
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        button_continue = rootView.findViewById(R.id.button_continue);

        radioGroupQ4 = rootView.findViewById(R.id.q4);
        radioGroupQ5 = rootView.findViewById(R.id.q5);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers answer = Answers.getInstance();

                int i4 = radioGroupQ4.getCheckedRadioButtonId();
                int i5 = radioGroupQ5.getCheckedRadioButtonId();
                String a4 = "-1";
                String a5 = "-1";
                switch (i4) {
                    case R.id.q4_0:
                        a4 = "5";
                        break;
                    case R.id.q4_1:
                        a4 = "4";
                        break;
                    case R.id.q4_2:
                        a4 = "3";
                        break;
                    case R.id.q4_3:
                        a4 = "2";
                        break;
                    case R.id.q4_4:
                        a4 = "1";
                        break;
                }
                switch (i5) {
                    case R.id.q5_0:
                        a5 = "5";
                        break;
                    case R.id.q5_1:
                        a5 = "4";
                        break;
                    case R.id.q5_2:
                        a5 = "3";
                        break;
                    case R.id.q5_3:
                        a5 = "2";
                        break;
                    case R.id.q5_4:
                        a5 = "1";
                        break;
                }
                if (i4==-1 || i5==-1) {
                    Toast.makeText(getActivity(), "請完成所有題目", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton4 = rootView.findViewById(i4);
                    RadioButton selectedRadioButton5 = rootView.findViewById(i5);
                    answer.put_answer("q4", a4);
                    answer.put_answer("q5", a5);

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
    }
}
