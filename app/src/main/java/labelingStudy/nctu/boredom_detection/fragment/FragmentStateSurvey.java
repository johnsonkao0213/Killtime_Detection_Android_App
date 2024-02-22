package labelingStudy.nctu.boredom_detection.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.minuku.logger.Log;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;

public class FragmentStateSurvey extends Fragment {
    private static final String TAG = "FragmentStateSurvey";

    private AnswerDataRecordDAO answerDataRecordDAO;

    private ImageView icon;
    private TextView when;
    private TextView appname;
    private TextView time;
    private TextView title;
    private TextView content;
    private Button button_continue;

    private FragmentActivity mContext;
    private long id;
    private String firebasePk;

    private int questionCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_state_survey, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();
        button_continue = rootView.findViewById(R.id.button_continue);
        button_continue.setText(getString(R.string.survey_next));


        // Store question layouts in an array
        int[] questionLayoutIds = {R.id.q1, R.id.q2, R.id.q3, R.id.q4, R.id.q5, R.id.q6, R.id.q7, R.id.q8};

        // Store question string resources in an array
        int[] questionStringResources = {
                R.string.q1_boredom_state,
                R.string.q2_boredom_state,
                R.string.q3_boredom_state,
                R.string.q4_boredom_state,
                R.string.q5_boredom_state,
                R.string.q6_boredom_state,
                R.string.q7_boredom_state,
                R.string.q8_boredom_state
        };

        questionCount = questionStringResources.length;
        // Initialize radio groups and set up text views for each question layout
        final LinearLayout[] layouts = new LinearLayout[questionCount];
        final RadioGroup[] radioGroups = new RadioGroup[questionCount];
        TextView[] questionTextViews = new TextView[questionCount];

        for (int i = 0; i < questionCount; i++) {
            layouts[i] = rootView.findViewById(questionLayoutIds[i]);
            radioGroups[i] = layouts[i].findViewById(R.id.options);
            questionTextViews[i] = layouts[i].findViewById(R.id.question_textview);
            // Set the text of the TextView to the desired string resource
            questionTextViews[i].setText( (i+1) + ". "+ getString(questionStringResources[i]));
        }



        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> responseList = new ArrayList<>();

                for (int i = 0; i < questionCount; i++) {
                    String response = IntentionSurveyActivity.handleRadioGroupResponses(radioGroups[i], layouts[i]);
                    responseList.add(response);
                }

                String unAnsweredQuestions = IntentionSurveyActivity.returnUnansweredQuestions(responseList);

                if (unAnsweredQuestions != ""){
                    Toast.makeText(getActivity(), getString(R.string.unanswered)+ unAnsweredQuestions, Toast.LENGTH_SHORT).show();
                }else{

                    Answers answer = Answers.getStateInstance();
                    for (int i = 0; i < questionCount; i++) {
                        answer.put_answer("boredom_state_q" + (i+1), responseList.get(i));
                    }

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
        id = getArguments().getLong("id");
        firebasePk = getArguments().getString("firebasePK");
    }
}
