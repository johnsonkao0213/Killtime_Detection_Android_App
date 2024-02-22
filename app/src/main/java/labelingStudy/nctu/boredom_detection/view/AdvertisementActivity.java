package labelingStudy.nctu.boredom_detection.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


import labelingStudy.nctu.boredom_detection.BoredomApp;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationDataRecord;



public class AdvertisementActivity extends AppCompatActivity {


    private static final String TAG = "AdvertisementActivity";

    private NotificationDataRecordDAO NotificationDataRecordDAO;
    NotificationDataRecord notice ;

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);
        //BoredomApp.setActivityVisibility(TAG, true);


        webview = (WebView) findViewById(R.id.webview);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setSupportZoom(true);
//        webview.getSettings().setBuiltInZoomControls(true);
//        webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        Log.i(TAG, "AdvertisementActivity onCreate");

        NotificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();

        Intent intent = getIntent();
        long _Id = intent.getLongExtra("_id",-1);
        Log.i(TAG, "_id : " + _Id);
        List<NotificationDataRecord> List = NotificationDataRecordDAO.getByNotifyNotificationID(_Id);

        if(List.size()>0)
        {
            notice = List.get(0);

            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.setWebViewClient(new WebViewClient());

            webview.loadUrl(notice.getUrl());
            //notice.setIsUpdated(1);
            notice.setResponseCode(1);

            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setResponsedTime(current);
            notice.setResponsedTimeString(currentTime);
            notice.setClickedTimeString(currentTime);

            if(ScheduleAndSampleManager.getCurrentTimeInMillis() - notice.getCreatedTime() > 900000)
                notice.setIsExpired(1);
            else
                notice.setIsExpired(0);

            NotificationDataRecordDAO.updateOne(notice);

        }else{
            Log.e(TAG, "_Id is not found");
        }


    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");
        //BoredomApp.setActivityVisibility(TAG, true);
        if (notice!=null) {
            String resumedTimeString = notice.getResumedTimeString();
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setResumedTimeString(resumedTimeString + "," + currentTime);
            if (resumedTimeString.equals("")) notice.setResumedTimeString(currentTime);
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause");
        //BoredomApp.setActivityVisibility(TAG, false);
        if (notice!=null) {
            String pausedTimeString = notice.getPausedTimeString();
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setPausedTimeString(pausedTimeString + "," + currentTime);
            if (pausedTimeString.equals("")) notice.setPausedTimeString(currentTime);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        //BoredomApp.setActivityVisibility(TAG, false);
        if (notice!=null) {
            notice.setIsUpdated(1);
            long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
            String currentTime = ScheduleAndSampleManager.getTimeString(current);
            notice.setDestroyTimeString(currentTime);
            NotificationDataRecordDAO.updateOne(notice);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, 0, "重新整理");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.setWebViewClient(new WebViewClient());

            webview.loadUrl(notice.getUrl());
        }
        return super.onOptionsItemSelected(item);
    }
}
