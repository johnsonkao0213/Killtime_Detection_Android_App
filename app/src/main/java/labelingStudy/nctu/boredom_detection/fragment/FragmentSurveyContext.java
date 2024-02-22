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

public class FragmentSurveyContext extends Fragment {
    private AnswerDataRecordDAO answerDataRecordDAO;

    private ImageView icon;
    private TextView when;
    private TextView appname;
    private TextView time;
    private TextView title;
    private TextView content;
    private Button button_continue;

    private RadioGroup radioGroupQ6;
    private RadioGroup radioGroupQ7;
    private FragmentActivity mContext;
    private long id;
    private int count;

    private String firebasePk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_survey_context, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();

        when = rootView.findViewById(R.id.when);
        icon = rootView.findViewById(R.id.icon);
        appname = rootView.findViewById(R.id.appname);
        time = rootView.findViewById(R.id.time);
        title = rootView.findViewById(R.id.title);
        content = rootView.findViewById(R.id.content);
        button_continue = rootView.findViewById(R.id.button_continue);

        radioGroupQ6 = rootView.findViewById(R.id.q6);
        radioGroupQ7 = rootView.findViewById(R.id.q7);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers answer = Answers.getInstance();

                int i6 = radioGroupQ6.getCheckedRadioButtonId();
                int i7 = radioGroupQ7.getCheckedRadioButtonId();
                String a6 = "-1";
                String a7 = "-1";
                switch (i6) {
                    case R.id.q6_0:
                        a6 = "5";
                        break;
                    case R.id.q6_1:
                        a6 = "4";
                        break;
                    case R.id.q6_2:
                        a6 = "3";
                        break;
                    case R.id.q6_3:
                        a6 = "2";
                        break;
                    case R.id.q6_4:
                        a6 = "1";
                        break;
                    case R.id.q6_5:
                        a6 = "-1";
                        break;
                }
                switch (i7) {
                    case R.id.q7_0:
                        a7 = "5";
                        break;
                    case R.id.q7_1:
                        a7 = "4";
                        break;
                    case R.id.q7_2:
                        a7 = "3";
                        break;
                    case R.id.q7_3:
                        a7 = "2";
                        break;
                    case R.id.q7_4:
                        a7 = "1";
                        break;
                    case R.id.q7_5:
                        a7 = "-1";
                        break;
                }
                if (i6==-1 || i7==-1) {
                    Toast.makeText(getActivity(), "請完成所有題目", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton4 = rootView.findViewById(i6);
                    RadioButton selectedRadioButton5 = rootView.findViewById(i7);
                    answer.put_answer("q6", a6);
                    answer.put_answer("q7", a7);

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
