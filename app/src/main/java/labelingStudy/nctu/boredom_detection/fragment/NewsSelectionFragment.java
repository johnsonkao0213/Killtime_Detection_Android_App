package labelingStudy.nctu.boredom_detection.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.minuku.config.Constants;

import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_1;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_10;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_17;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_2;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_20;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_21;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_7;
import static labelingStudy.nctu.boredom_detection.config.Constants.NEWS_9;

public class NewsSelectionFragment extends BottomSheetDialogFragment {
    private View view;
    private CheckBox news1;
    private CheckBox news2;
    private CheckBox news3;
    private CheckBox news4;
    private CheckBox news5;
    private CheckBox news6;
    private CheckBox news7;
    private CheckBox news8;
    private Button button_confirm;
    private SharedPreferences sharedPreferences;
    private Set<String> newsSet;

    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceDataRecordDAO  =  appDatabase.getDatabase(getActivity().getApplicationContext()).preferenceDataRecordDAO();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_news, container, false);
        button_confirm = (Button) view.findViewById(R.id.confirm);
        news1 = (CheckBox) view.findViewById(R.id.news1);
        news2 = (CheckBox) view.findViewById(R.id.news2);
        news3 = (CheckBox) view.findViewById(R.id.news3);
        news4 = (CheckBox) view.findViewById(R.id.news4);
        news5 = (CheckBox) view.findViewById(R.id.news5);
        news6 = (CheckBox) view.findViewById(R.id.news6);
        news7 = (CheckBox) view.findViewById(R.id.news7);
        news8 = (CheckBox) view.findViewById(R.id.news8);
        sharedPreferences = this.getActivity().getSharedPreferences(Constants.sharedPrefString, Context.MODE_PRIVATE);
        newsSet =  (HashSet<String>) (sharedPreferences.getStringSet("news_preference", new HashSet<String>()));
        //Log.e("news_preference", "read: " + newsSet.toString() );
        if (newsSet.contains("1")) {
            news1.setChecked(true);
        }else{
            news1.setChecked(false);
        }
        if (newsSet.contains("2")) {
            news2.setChecked(true);
        }else{
            news2.setChecked(false);
        }
        if (newsSet.contains("9")) {
            news3.setChecked(true);
        }else{
            news3.setChecked(false);
        }
        if (newsSet.contains("10")) {
            news4.setChecked(true);
        }else{
            news4.setChecked(false);
        }
        if (newsSet.contains("20")) {
            news5.setChecked(true);
        }else{
            news5.setChecked(false);
        }
        if (newsSet.contains("17")) {
            news6.setChecked(true);
        }else{
            news6.setChecked(false);
        }
        if (newsSet.contains("7")) {
            news7.setChecked(true);
        }else{
            news7.setChecked(false);
        }
        if (newsSet.contains("21")) {
            news8.setChecked(true);
        }else{
            news8.setChecked(false);
        }

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> type = new ArrayList<String>();
                if(news1.isChecked()){
                    newsSet.add("1");
                    type.add(NEWS_1);

                } else {
                    newsSet.remove("1");
                }
                if(news2.isChecked()){
                    type.add(NEWS_2);
                    newsSet.add("2");
                } else {
                    newsSet.remove("2");
                }
                if(news3.isChecked()){
                    type.add(NEWS_9);
                    newsSet.add("9");
                } else {
                    newsSet.remove("9");
                }
                if(news4.isChecked()){
                    type.add(NEWS_10);
                    newsSet.add("10");
                } else {
                    newsSet.remove("10");
                }
                if(news5.isChecked()){
                    type.add(NEWS_20);
                    newsSet.add("20");
                } else {
                    newsSet.remove("20");
                }
                if(news6.isChecked()){
                    type.add(NEWS_17);
                    newsSet.add("17");
                } else {
                    newsSet.remove("17");
                }
                if(news7.isChecked()){
                    type.add(NEWS_7);
                    newsSet.add("7");
                } else {
                    newsSet.remove("7");
                }
                if(news8.isChecked()){
                    type.add(NEWS_21);
                    newsSet.add("21");
                } else {
                    newsSet.remove("21");
                }

                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("News",String.join(",", type));
                hashMap.put("IsDefault","0");
                Gson gson=new Gson();
                preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap), 1, System.currentTimeMillis()));
                //Log.e("news_preference", "write: " + newsSet.toString() );
                sharedPreferences.edit().remove("news_preference").commit();
                sharedPreferences.edit().putStringSet("news_preference", newsSet).commit();
                dismiss();
            }
        });
        return view;
    }
}
