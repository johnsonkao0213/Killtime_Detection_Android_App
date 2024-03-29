package labelingStudy.nctu.boredom_detection.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.view.WelcomeActivity;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

//import edu.ohio.boredom_detection.R;

/**
 * Created by Lawrence on 2017/4/19.
 */

public class DeviceIdPage extends AppCompatActivity {

    final private String TAG = "DeviceIdPage";

//    Button startdate,enddate,buildreport;
    private TextView showingDeviceId;
    private int mYear, mMonth, mDay;

    private SharedPreferences sharedPrefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        showingDeviceId = (TextView) findViewById(R.id.showingDeviceID);

        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        getDeviceid();

        String device_id = sharedPrefs.getString("DEVICE_ID", Constants.DEVICE_ID);

        showingDeviceId.setText("Device ID = "+ device_id);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            DeviceIdPage.this.finish();

            if(isTaskRoot()){
                startActivity(new Intent(this, WelcomeActivity.class));
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sharedPrefs.edit().putString("lastActivity", getClass().getName()).commit();
    }

    public void getDeviceid(){

        TelephonyManager mngr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        int permissionStatus= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if(permissionStatus== PackageManager.PERMISSION_GRANTED){

            Constants.DEVICE_ID = mngr.getDeviceId();

            sharedPrefs.edit().putString("DEVICE_ID",  Constants.DEVICE_ID).commit();

            Log.e(TAG,"DEVICE_ID"+Constants.DEVICE_ID+" : "+mngr.getDeviceId());

        }
    }

}
