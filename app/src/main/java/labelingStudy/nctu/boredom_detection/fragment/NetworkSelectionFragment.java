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

public class NetworkSelectionFragment extends BottomSheetDialogFragment {
    private View view;
    RadioGroup rg;
    RadioButton rb1, rb2;

    private Button button_confirm;
    private SharedPreferences sharedPreferences;
    private Set<String> newsSet;

    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    String result = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceDataRecordDAO  =  appDatabase.getDatabase(getActivity().getApplicationContext()).preferenceDataRecordDAO();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_network, container, false);
        button_confirm = (Button) view.findViewById(R.id.nw_confirm);
        rg = (RadioGroup) view.findViewById(R.id.nw_rg);

        rb1 = (RadioButton) view.findViewById(R.id.nw_rb1);
        rb2 = (RadioButton) view.findViewById(R.id.nw_rb2);
        sharedPreferences = this.getActivity().getSharedPreferences(Constants.sharedPrefString, Context.MODE_PRIVATE);

        String n = sharedPreferences.getString("NetworkBuffet","");

        if(n.isEmpty())
        {
            rg.check(rb1.getId());
            result = "y";
        }else{
            if(n.equalsIgnoreCase("y")) {
                rg.check(rb1.getId());
                result = "y";
            }else{
                rg.check(rb2.getId());
                result = "n";
            }
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

              @Override
              public void onCheckedChanged(RadioGroup group, int checkedId) {
                  // find which radio button is selected
                  if (checkedId == R.id.nw_rb1) {
                      result = "y";
                  } else {
                      result = "n";
                  }
              }
          });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences.edit()
                        .putString("NetworkBuffet", result)
                        .apply();
                HashMap<String,String> hashMap=new HashMap<>();
                if(result.equalsIgnoreCase("y"))
                {
                    hashMap.put("Network","Buffet");
                }else{
                    hashMap.put("Network","WIFI");
                }
                hashMap.put("IsDefault","0");
                Gson gson=new Gson();
                preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap), 6, System.currentTimeMillis()));

                invalidateOptionsMenu(getActivity());
                dismiss();
            }
        });
        return view;
    }
}
