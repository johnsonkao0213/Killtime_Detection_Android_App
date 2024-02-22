package labelingStudy.nctu.boredom_detection.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import labelingStudy.nctu.boredom_detection.IntentionSurveyActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.Utils;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.Answers;

public class FragmentActivityContext extends Fragment {
    private static final String TAG = "FragmentActivityContext";
    private static final int PRECEDING_ACTIVITY = 1;
    private static final int SUCCEEDING_ACTIVITY = 2;
    private static final int WITHIN_ACTIVITY = 3;
    private static final int NONE = R.id.none;
    private static final int PRECEDING = R.id.preceding;
    private static final int SUCCEEDING = R.id.succeeding;
    private static final int WITHIN = R.id.within;
    private static final int BETWEEN = R.id.between;


    private Button button_continue;

    private FragmentActivity mContext;
    private long id;
    private String firebasePk;
    private RadioGroup rgActivityContext;
    private LinearLayout questionContainer;
    private String activityContextPreceding;
    private String activityContextSucceeding;
    private String activityContextWithin;
    private String activityContext;
    private int selectedBranchId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_context, container, false);

        rgActivityContext = view.findViewById(R.id.activity_context);
        questionContainer = view.findViewById(R.id.questionContainer);
        button_continue = view.findViewById(R.id.button_continue);
        button_continue.setText(getString(R.string.survey_next));

        rgActivityContext.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton selectedRadioButton = view.findViewById(checkedId);

                if (selectedRadioButton != null) {
                    selectedBranchId = selectedRadioButton.getId();
                    handleQuestionBranchingById(selectedBranchId);
                    activityContext = getCheckedRadioButtonText(view, R.id.activity_context);
                }
            }
        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityContextPreceding = handleQuestionResponseById(PRECEDING_ACTIVITY);
                activityContextSucceeding = handleQuestionResponseById(SUCCEEDING_ACTIVITY);
                activityContextWithin = handleQuestionResponseById(WITHIN_ACTIVITY);

                if (checkResponse(selectedBranchId)) {
                    Log.i(TAG, "activityContext: " + activityContext);
                    Log.i(TAG, "activityContextPreceding: " + activityContextPreceding);
                    Log.i(TAG, "activityContextSucceeding: " + activityContextSucceeding);
                    Log.i(TAG, "activityContextWithin: " + activityContextWithin);


                    Answers answer = Answers.getStateInstance();
                    answer.put_answer("activityContext", activityContext);
                    answer.put_answer("activityContextPreceding", activityContextPreceding);
                    answer.put_answer("activityContextSucceeding", activityContextSucceeding);
                    answer.put_answer("activityContextWithin", activityContextWithin);
                    Utils.updateAnswerData(getContext(), answer, id, firebasePk);

                    ((IntentionSurveyActivity) mContext).go_to_next();

                } else {
                    Toast.makeText(getActivity(), getString(R.string.unanswered), Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }


    private boolean checkResponse(int branchId) {
        if (branchId == -1) {
            return false;
        }

        switch (branchId) {
            case NONE:
                break;

            case PRECEDING:
                if (activityContextPreceding.isEmpty()) {
                    return false;
                }
                break;

            case WITHIN:
                if (activityContextWithin.isEmpty()) {
                    return false;
                }
                break;

            case SUCCEEDING:
                if (activityContextSucceeding.isEmpty()) {
                    return false;
                }
                break;
            case BETWEEN:
                if (activityContextPreceding.isEmpty() || activityContextSucceeding.isEmpty()) {
                    return false;
                }
                break;
        }
        return true;
    }


    private String handleQuestionResponseById(int viewId) {
        View newQuestionsView = questionContainer.findViewById(viewId);

        if (newQuestionsView == null) {
            return "";
        }

        String col1 = getCheckedRadioButtonText(newQuestionsView, R.id.radioGroupColumn1);
        String col2 = getCheckedRadioButtonText(newQuestionsView, R.id.radioGroupColumn2);

        return !col1.isEmpty() ? col1 : !col2.isEmpty() ? col2 : "";
    }

    private String getCheckedRadioButtonText(View view, int radioGroupId) {
        RadioGroup radioGroup = view.findViewById(radioGroupId);
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId != -1) {
            RadioButton checkedRadioButton = view.findViewById(checkedRadioButtonId);
            return checkedRadioButton.getText().toString();
        }

        return "";
    }

    private void handleQuestionBranchingById(int selectedOptionId) {
        questionContainer.removeAllViews();
        switch (selectedOptionId) {
            case NONE:
                break;

            case PRECEDING:
                loadQuestions(getString(R.string.title_preceding), PRECEDING_ACTIVITY);
                break;

            case WITHIN:
                loadQuestions(getString(R.string.title_within), WITHIN_ACTIVITY);
                break;


            case SUCCEEDING:
                loadQuestions(getString(R.string.title_succeeding), SUCCEEDING_ACTIVITY);
                break;


            case BETWEEN:
                loadQuestions(getString(R.string.title_preceding), PRECEDING_ACTIVITY);
                loadQuestions(getString(R.string.title_succeeding), SUCCEEDING_ACTIVITY);
                break;
        }
    }

    private void loadQuestions(String title, int viewId) {
        View newQuestionsView = LayoutInflater.from(getContext()).inflate(R.layout.activity_types, null);
        newQuestionsView.setId(viewId);
        TextView titleTextView = newQuestionsView.findViewById(R.id.title);
        if (titleTextView != null) {
            titleTextView.setText(title);
        }

        RadioGroup radioGroupColumn1 = newQuestionsView.findViewById(R.id.radioGroupColumn1);
        RadioGroup radioGroupColumn2 = newQuestionsView.findViewById(R.id.radioGroupColumn2);

        RadioGroup.OnCheckedChangeListener radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    return;
                }

                // Uncheck all radio buttons in the other group
                switch (group.getId()) {
                    case R.id.radioGroupColumn1:
                        radioGroupColumn2.setOnCheckedChangeListener(null);
                        radioGroupColumn2.clearCheck();
                        radioGroupColumn2.setOnCheckedChangeListener(this);
                        break;
                    case R.id.radioGroupColumn2:
                        radioGroupColumn1.setOnCheckedChangeListener(null);
                        radioGroupColumn1.clearCheck();
                        radioGroupColumn1.setOnCheckedChangeListener(this);
                        break;
                }
            }
        };

        radioGroupColumn1.setOnCheckedChangeListener(radioButtonListener);
        radioGroupColumn2.setOnCheckedChangeListener(radioButtonListener);

        questionContainer.addView(newQuestionsView);

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
