package labelingStudy.nctu.boredom_detection.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Set;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.minuku.config.Constants;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

public class SettingSelectionFragment extends BottomSheetDialogFragment {
    private View view;
    RadioGroup rg;
    RadioButton rb_not, rb_local, rb_firebase;

    private Button button_confirm;
    private SharedPreferences sharedPreferences;

    String result = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_selection, container, false);
        button_confirm = (Button) view.findViewById(R.id.nw_confirm);
        rg = (RadioGroup) view.findViewById(R.id.nw_rg);

        rb_not = (RadioButton) view.findViewById(R.id.rb_do_not_store);
        rb_local = (RadioButton) view.findViewById(R.id.rb_local);
        rb_firebase = (RadioButton) view.findViewById(R.id.rb_firebase);

        sharedPreferences = this.getActivity().getSharedPreferences(Constants.sharedPrefString, Context.MODE_PRIVATE);

        String n = sharedPreferences.getString("isSavingData","");

        if(n.isEmpty())
        {
            rg.check(rb_not.getId());
            result = "not";
        }else{
            if(n.equalsIgnoreCase("firebase")) {
                rg.check(rb_firebase.getId());
                result = "firebase";
            }else if(n.equalsIgnoreCase("local")) {
                rg.check(rb_local.getId());
                result = "local";
            }else{
                rg.check(rb_not.getId());
                result = "not";

             }

        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.rb_firebase) {
                    result = "firebase";
                } else if (checkedId == R.id.rb_local){
                    result = "local";
                } else{
                    result = "not";
                }
                sharedPreferences.edit()
                        .putString("isSavingData", result)
                        .apply();
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit()
                        .putString("isSavingData", result)
                        .apply();
                dismiss();
            }
        });
        return view;
    }
}
