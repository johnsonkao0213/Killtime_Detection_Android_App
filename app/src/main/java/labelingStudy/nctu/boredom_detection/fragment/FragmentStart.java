package labelingStudy.nctu.boredom_detection.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.SurveyActivity;
import labelingStudy.nctu.boredom_detection.model.SurveyProperties;


public class FragmentStart extends Fragment {

    private FragmentActivity mContext;
    private TextView textView_start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_start, container, false);

        textView_start = (TextView) rootView.findViewById(R.id.textView_start);
        Button button_continue = (Button) rootView.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SurveyActivity) mContext).go_to_next();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        SurveyProperties survery_properties = (SurveyProperties) getArguments().getSerializable("survery_properties");

        assert survery_properties != null;
        textView_start.setText(Html.fromHtml(survery_properties.getIntroMessage()));




    }
}