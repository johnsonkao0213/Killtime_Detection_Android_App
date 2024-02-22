package labelingStudy.nctu.boredom_detection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import labelingStudy.nctu.boredom_detection.Utils;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;

public class FragmentAffectSurvey extends Fragment {
    private static final String TAG = "FragmentAffectSurvey";

    private AnswerDataRecordDAO answerDataRecordDAO;

    private Button button_continue;
    private RadioGroup rgSAM_Valence;
    private RadioGroup rgSAM_Arousal;

    private RadioGroup rgSocialContext;
    private FragmentActivity mContext;
    private long id;
    private String firebasePk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_affect_survey, container, false);
        answerDataRecordDAO = appDatabase.getDatabase(getActivity()).answerDataRecordDAO();
        button_continue = rootView.findViewById(R.id.button_continue);
        button_continue.setText(getString(R.string.survey_next));

        final LinearLayout layoutSocialContext = rootView.findViewById(R.id.social_context);
        final LinearLayout layoutSAM_Valence = rootView.findViewById(R.id.sam_q1);
        final LinearLayout layoutSAM_Arousal = rootView.findViewById(R.id.sam_q2);

        rgSocialContext = layoutSocialContext.findViewById(R.id.options_social_context);
        rgSAM_Valence = layoutSAM_Valence.findViewById(R.id.options);
        rgSAM_Arousal = layoutSAM_Arousal.findViewById(R.id.options);

        ImageView imageViewSAM_Valence = layoutSAM_Valence.findViewById(R.id.sam_imageView);
        ImageView imageViewSAM_Arousal = layoutSAM_Arousal.findViewById(R.id.sam_imageView);

        imageViewSAM_Valence.setImageResource(R.drawable.sam_valence);
        imageViewSAM_Arousal.setImageResource(R.drawable.sam_arousal);

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aSocialContext = IntentionSurveyActivity.handleRadioGroupResponses(rgSocialContext, layoutSocialContext);
                String aSAM_Valence = IntentionSurveyActivity.handleRadioGroupResponses(rgSAM_Valence, layoutSAM_Valence);
                String aSAM_Arousal = IntentionSurveyActivity.handleRadioGroupResponses(rgSAM_Arousal, layoutSAM_Arousal);

                ArrayList<String> responseList = new ArrayList<>();
                responseList.add(aSocialContext);
                responseList.add(aSAM_Valence);
                responseList.add(aSAM_Arousal);

                String unanswered = "";
                unanswered = IntentionSurveyActivity.returnUnansweredQuestions(responseList);
                if (unanswered != ""){
                    Toast.makeText(getActivity(), getString(R.string.unanswered), Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.i(TAG, "Social Context: " + aSocialContext);
                    android.util.Log.i(TAG, "SAM_Valence: " + aSAM_Valence);
                    android.util.Log.i(TAG, "SAM_Arousal: " + aSAM_Arousal);

                    Answers answer = Answers.getStateInstance();
                    answer.put_answer("Social Context", aSocialContext);
                    answer.put_answer("SAM_Valence", aSAM_Valence);
                    answer.put_answer("SAM_Arousal", aSAM_Arousal);

                    Utils.updateAnswerData(getContext(), answer, id, firebasePk);


//                    Log.i(TAG, answer.get_json_object());

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
