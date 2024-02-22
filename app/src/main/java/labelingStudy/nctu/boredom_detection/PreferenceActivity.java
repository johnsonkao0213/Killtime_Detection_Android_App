package labelingStudy.nctu.boredom_detection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.util.HashMap;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.boredom_detection.preference.PreferenceFragmentCustom;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

public class PreferenceActivity extends AppCompatActivity {

    public static SharedPreferences sharedPrefs;
    private PreferenceDataRecordDAO preferenceDataRecordDAO;

    private static final String TAG = "PreferenceActivity";

    Toolbar toolbar;

    int old_start , old_end;


    static {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BoredomApp.setActivityVisibility(TAG, true);


        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);
        preferenceDataRecordDAO  =  appDatabase.getDatabase(this).preferenceDataRecordDAO();

        old_start = sharedPrefs.getInt("recording_start", 600);
        old_end = sharedPrefs.getInt("recording_end", 1320);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.preference);
        setSupportActionBar(toolbar);

        Fragment preferenceFragment = new PreferenceFragmentCustom();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.pref_container, preferenceFragment);
        ft.commit();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == android.R.id.home) {
            finish();

        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        BoredomApp.setActivityVisibility(TAG, true);
    }
    @Override
    public void onPause(){
        super.onPause();
        BoredomApp.setActivityVisibility(TAG, false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        BoredomApp.setActivityVisibility(TAG, false);
        insertRecordingTimePreference();
    }

    private void insertRecordingTimePreference() {
        int start = sharedPrefs.getInt("recording_start", 600);
        int end = sharedPrefs.getInt("recording_end", 1320);
        if(start != old_start || end != old_end)
        {
            int start_hour = start/60;
            int start_min = start%60;

            int end_hour = end/60;
            int end_min = end%60;

            String start_h = start_hour<10?"0"+start_hour:""+start_hour;
            String start_m = start_min<10?"0"+start_min:""+start_min;
            String end_h = end_hour<10?"0"+end_hour:""+end_hour;
            String end_m = end_min<10?"0"+end_min:""+end_min;

            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("StartTime",start_h +":" + start_m);
            hashMap.put("EndTime", end_h + ":" +end_m);
            hashMap.put("IsDefault","0");
            Gson gson=new Gson();
            preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(hashMap), 2, System.currentTimeMillis()));
        }
    }

}
