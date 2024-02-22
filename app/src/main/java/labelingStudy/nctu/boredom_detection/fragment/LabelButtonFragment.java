package labelingStudy.nctu.boredom_detection.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import labelingStudy.nctu.boredom_detection.R;

public class LabelButtonFragment extends Fragment {
    private static final String TAG = "LabelButtonFragment";
    private String tabTitle;

    public interface OnButtonClickListener {
        void onButtonClick(int tabIndex);
    }

    private OnButtonClickListener buttonClickListener;

    private static final String ARG_TAB_INDEX = "tabIndex";

    private int tabIndex;

    public LabelButtonFragment() {
        // Required empty public constructor
    }

    public static LabelButtonFragment newInstance(int tabIndex) {
        LabelButtonFragment fragment = new LabelButtonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_INDEX, tabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabIndex = getArguments().getInt(ARG_TAB_INDEX, 0);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Fragment View Created");
        // Inflate your fragment layout here
        View view = inflater.inflate(R.layout.label_button_layout, container, false);

        // Find your button in the fragment layout
        Button button = view.findViewById(R.id.your_button_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonClickListener != null) {
                    buttonClickListener.onButtonClick(tabIndex);
                }
            }
        });

        // Set the button text
        button.setText(tabTitle);

        // You can add any other fragment-specific logic here

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            buttonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }
}
