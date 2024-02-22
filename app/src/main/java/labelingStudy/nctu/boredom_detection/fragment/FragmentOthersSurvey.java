package labelingStudy.nctu.boredom_detection.fragment;

import java.util.ArrayList;
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
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import labelingStudy.nctu.boredom_detection.SurveyActivity;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.minuku.config.Constants;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;
import labelingStudy.nctu.minuku.logger.Log;

public class FragmentOthersSurvey extends Fragment {
    private static final String TAG = "FragmentOthersSurvey";

    private AnswerDataRecordDAO answerDataRecordDAO;


    private Button button_continue;

    private RadioGroup radioGroupQ1 ;
    private RadioGroup radioGroupQ2;
    private RadioGroup radioGroupQ3;
    private RadioGroup radioGroupQ4;
    private RadioGroup radioGroupQ5;
    private RadioGroup radioGroupQ6;



    private LinearLayout layoutQ1;
    private LinearLayout layoutQ2;
    private LinearLayout layoutQ3;
    private LinearLayout layoutQ4;
    private LinearLayout layoutQ5;
    private LinearLayout layoutQ6;


    private String[] questionArray = new String[]{
            "others_activity_context",
            "others_locations",
            "others_social_context",
            "others_busyness",
            "others_perceived_privacy",
            "others_social_norms"
    };





    private FragmentActivity mContext;
    private long id;


    private String firebasePk;

    public FragmentOthersSurvey() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_others_survey, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();
        button_continue = rootView.findViewById(R.id.button_continue);
        button_continue.setText(getString(R.string.survey_next));

        layoutQ1 = rootView.findViewById(R.id.activity_context);
        layoutQ2 = rootView.findViewById(R.id.locations);
        layoutQ3 = rootView.findViewById(R.id.social_context);
        layoutQ4 = rootView.findViewById(R.id.busyness);
        layoutQ5 = rootView.findViewById(R.id.perceived_privacy);
        layoutQ6 = rootView.findViewById(R.id.social_norms);


        radioGroupQ1 = layoutQ1.findViewById(R.id.options_activity_context);
        radioGroupQ2 = layoutQ2.findViewById(R.id.options_locations);
        radioGroupQ3 = layoutQ3.findViewById(R.id.options_social_context);
        radioGroupQ4 = layoutQ4.findViewById(R.id.options);
        radioGroupQ5 = layoutQ5.findViewById(R.id.options);
        radioGroupQ6 = layoutQ6.findViewById(R.id.options);


        // Access the TextView within each layout
        TextView textView1 = layoutQ4.findViewById(R.id.question_textview);
        TextView textView2 = layoutQ5.findViewById(R.id.question_textview);
        TextView textView3 = layoutQ6.findViewById(R.id.question_textview);



        // Set the text of the TextView to the desired string resource
        textView1.setText(R.string.title_busyness);
        textView2.setText(R.string.title_perceived_privacy);
        textView3.setText(R.string.title_social_norms);




        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> responseList = new ArrayList<>();
                String a1 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ1, layoutQ1);
                responseList.add(a1);
                String a2 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ2, layoutQ2);
                responseList.add(a2);
                String a3 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ3, layoutQ3);
                responseList.add(a3);
                String a4 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ4, layoutQ4);
                responseList.add(a4);
                String a5 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ5, layoutQ5);
                responseList.add(a5);
                String a6 = IntentionSurveyActivity.handleRadioGroupResponses(radioGroupQ6, layoutQ6);
                responseList.add(a6);

                String unanswered = "";
                unanswered = IntentionSurveyActivity.returnUnansweredQuestions(responseList);
                if (unanswered != ""){
                    Toast.makeText(getActivity(), getString(R.string.unanswered)+ unanswered, Toast.LENGTH_SHORT).show();
                }else{

                    Answers answer = Answers.getStateInstance();

                    for (int i = 0; i < questionArray.length; i++) {
                        answer.put_answer(questionArray[i], responseList.get(i));
                    }

                    Log.i(TAG, answer.get_json_object());
                    Utils.updateAnswerData(getContext(), answer, id, firebasePk);

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
