package labelingStudy.nctu.boredom_detection.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.SurveyActivity;
import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.model.Answers;
import labelingStudy.nctu.boredom_detection.model.DataRecord.AnswerDataRecord;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

public class FragmentEnd extends Fragment {
    private static final String TAG = "FragmentEnd";
    private FragmentActivity mContext;
    private TextView textView_end;
    private TextView textView_countsESMAllIntentionSurvey;
    private TextView textView_countsESMAllStateSurvey;
    private EditText editText_commments;
    SharedPreferences sharedPrefs;
    private long id;
    private String firebasePk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_end, container, false);

        sharedPrefs = getActivity().getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
        long countsESMAllIntentionSurvey = sharedPrefs.getLong("countsESMAllIntentionSurvey", 0);
        long countsESMAllStateSurvey = sharedPrefs.getLong("countsESMAllStateSurvey", 0);



        Button button_finish = (Button) rootView.findViewById(R.id.button_finish);
        textView_end = (TextView) rootView.findViewById(R.id.textView_end);
        textView_countsESMAllIntentionSurvey = (TextView) rootView.findViewById(R.id.textView_countsESMAllIntentionSurvey);
        textView_countsESMAllStateSurvey = (TextView) rootView.findViewById(R.id.textView_countsESMAllStateSurvey);
        editText_commments = rootView.findViewById(R.id.editText_comments);

        String formattedText_countsESMAllIntentionSurvey = getString(R.string.countsESMAllIntentionSurvey, countsESMAllIntentionSurvey);
        String formattedText_countsESMAllStateSurvey = getString(R.string.countsESMAllStateSurvey, countsESMAllStateSurvey+1);

        textView_countsESMAllIntentionSurvey.setText(formattedText_countsESMAllIntentionSurvey);
        textView_countsESMAllStateSurvey.setText(formattedText_countsESMAllStateSurvey);



        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((SurveyActivity) mContext).event_survey_completed(Answers.getInstance());

                String commentsString =editText_commments.getText().toString();
                Answers answer = Answers.getStateInstance();
                answer.put_answer("Comments", commentsString);
                Utils.updateAnswerData(getContext(), answer, id, firebasePk);

                ((IntentionSurveyActivity) mContext).event_survey_completed(Answers.getStateInstance());

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        id = getArguments().getLong("id");
        firebasePk = getArguments().getString("firebasePK");


        //SurveyProperties survery_properties = (SurveyProperties) getArguments().getSerializable("survery_properties");

        //assert survery_properties != null;
        //textView_end.setText(Html.fromHtml(survery_properties.getEndMessage()));

    }
}