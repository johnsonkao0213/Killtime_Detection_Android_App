/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package labelingStudy.nctu.boredom_detection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.CalendarView;
import com.ldf.calendar.view.MonthPager;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.dao.ActionDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.AnswerDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.MinukuDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationEventRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NotificationRemoveRecordDao;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.databinding.ActivityMainBinding;
import labelingStudy.nctu.boredom_detection.fragment.NetworkSelectionFragment;
import labelingStudy.nctu.boredom_detection.fragment.SettingSelectionFragment;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ActionDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.MinukuDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationEventRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NotificationRemoveRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.boredom_detection.permissions.PermissionActivity;
import labelingStudy.nctu.boredom_detection.permissions.PermissionRequest;
import labelingStudy.nctu.boredom_detection.service.BackgroundService;
import labelingStudy.nctu.boredom_detection.service.UploadDataService;
import labelingStudy.nctu.boredom_detection.service.UploadService;
import labelingStudy.nctu.boredom_detection.timeline.LabelTask;
import labelingStudy.nctu.boredom_detection.timeline.LocationLabelTask;
import labelingStudy.nctu.boredom_detection.timeline.TaskAdapter;
import labelingStudy.nctu.boredom_detection.timeline.TextLabelTask;
import labelingStudy.nctu.boredom_detection.timeline.UploadLabelTask;
import labelingStudy.nctu.boredom_detection.view.customview.CustomDayView;
import labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.model.DataRecord.MobileAccessibilityDataRecord;
import labelingStudy.nctu.minuku.service.NotificationListenService;

import static labelingStudy.nctu.boredom_detection.config.Constants.LABEL_PRIVATE;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_ACC_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_ACTION_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_LAST_TIME;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_MINUKU_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_NOTIREMOVE_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadDataService.EXTRA_PREF_LIST;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.BEGIN;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.CHECKED;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.COMPLETE;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.CUSTOM;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.END;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.NORMAL;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.UNCHECKED;
import static labelingStudy.nctu.boredom_detection.view.helper.TimeLineDecoration.UPLOAD;


public class MainActivity extends AppCompatActivity implements TaskAdapter.OnOrderClickListener {

    private static final String TAG = "MainActivity";
    private BroadcastReceiver mBroadcastReceiver;
    private ImageDataRecordDAO imageDataRecordDAO;
    private NoteDataRecordDAO noteDataRecordDAO;
    private NotificationEventRecordDAO notificationEventRecordDAO;
    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    private MinukuDataRecordDAO minukuDataRecordDAO;
    private ActionDataRecordDAO actionDataRecordDAO;
    private NotificationDataRecordDAO notificationDataRecordDAO;
    private AnswerDataRecordDAO answerDataRecordDAO;
    private NotificationRemoveRecordDao notificationRemoveRecordDAO;
    private labelingStudy.nctu.minuku.dao.MobileAccessibilityDataRecordDAO mobileAccessibilityDataRecordDAO;

    private SharedPreferences sharedPrefs;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    File imgFile;

    TaskAdapter adapter;
    DateFormat simple_day  = new SimpleDateFormat("yyyy-M-d");
    DateFormat simple  = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    final List<LabelTask> analogs = new ArrayList<>();

    private ArrayList<CalendarView> currentCalendarViews = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private CalendarDate currentDate;
    private boolean initiated = false;

    ArrayList<String> delete_notelist = new ArrayList<>();

    Gson gson = new Gson();
    long firstInitTime ;

    int unupload_minuku = 0;

    ActivityMainBinding binding;
    PermissionRequest permissionRequest;
    ActivityResultLauncher<Intent> startMediaProjection;
    private static Intent resultIntentFromMediaProjection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating Main activity");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BoredomApp.setActivityVisibility(TAG, true);

        binding.linearLayoutOnlyNote.setVisibility(View.GONE);
        permissionRequest = new PermissionRequest(this);

        firstInitTime = System.currentTimeMillis();
        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        try {
            Log.i(TAG, "android.os.Build.VERSION.SDK_INT:" + android.os.Build.VERSION.SDK_INT);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));// set status background white
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(binding.toolbar);

        imageDataRecordDAO = appDatabase.getDatabase(this).imageDataRecordDAO();
        noteDataRecordDAO = appDatabase.getDatabase(this).noteDataRecordDAO();
        notificationEventRecordDAO = appDatabase.getDatabase(this).notificationEventRecordDAO();
        preferenceDataRecordDAO  =  appDatabase.getDatabase(this).preferenceDataRecordDAO();

        minukuDataRecordDAO = appDatabase.getDatabase(this).minukuDataRecordDAO();
        notificationDataRecordDAO = appDatabase.getDatabase(this).notificationDataRecordDAO();
        answerDataRecordDAO = appDatabase.getDatabase(this).answerDataRecordDAO();
        notificationRemoveRecordDAO = appDatabase.getDatabase(this).notificationRemoveRecordDao();
        actionDataRecordDAO = appDatabase.getDatabase(this).actionDataRecordDAO();
        mobileAccessibilityDataRecordDAO = labelingStudy.nctu.minuku.Data.appDatabase.getDatabase(this).mobileAccessibilityDataRecordDAO();

        initView();
        initCurrentDate();
        initCalendarView();


        imgFile = new File(Environment.getExternalStoragePublicDirectory(
                Constants.PACKAGE_DIRECTORY_PATH), "Demo");

        getNote(currentDate);
        sharedPrefs.edit().putString("currentWork", Constants.currentWork).commit();


        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive:" + intent);

                switch (intent.getAction()) {
                    case UploadService.UPLOAD_COMPLETED:
                        Log.i(TAG, "mBroadcastReceiver:" + intent.getStringExtra(UploadService.UPLOADED_ID) +" " + intent.getStringExtra(UploadService.UPLOADED_STATUS));
                        refreshNote(intent.getStringExtra(UploadService.UPLOADED_ID), intent.getStringExtra(UploadService.UPLOADED_STATUS));

                        break;
                }
            }

        };

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, UploadService.getIntentFilter());

        startMediaProjection = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent serviceIntent = new Intent(this, BackgroundService.class);
                        serviceIntent.putExtra("mediaProjectionData", result.getData());
                        startForegroundService(serviceIntent);
                        BackgroundService.setResultIntent(result.getData());
                        resultIntentFromMediaProjection = result.getData();
                    }
                }
        );
    }

    @Override
    public void onResume(){
        super.onResume();
        BoredomApp.setActivityVisibility(TAG, true);

        if (!permissionRequest.areAllTrue()) {
            Intent intent = new Intent(this, PermissionActivity.class);
            startActivity(intent);
        }else {
            startService(new Intent(getBaseContext(), NotificationListenService.class));
            if(!Utils.isMyServiceRunning(this, BackgroundService.class)) {
                requestCapturePermission();
            }

            askNetwork();

            getNote(currentDate);
            refreshUnUploadedData();
        }
    }

    public void requestCapturePermission(){
        final MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);
        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent());
    }

    @Override
    public void onPause(){
        super.onPause();
        BoredomApp.setActivityVisibility(TAG, false);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        BoredomApp.setActivityVisibility(TAG, false);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !initiated) {
            String lastlabeldate = sharedPrefs.getString("labeldate", "");
            if(lastlabeldate.equalsIgnoreCase(""))
                currentDate = new CalendarDate();
            else
                currentDate = strDate2CalendarDate(lastlabeldate);
            Log.i(TAG,"onResume " + currentDate.toString());

            refreshMonthPager(currentDate);
            refreshClickDate(currentDate);
            initiated = true;
        }
    }
    /**
     * 初始化currentDate
     *
     * @return void
     */
    private void initCurrentDate() {
        currentDate = new CalendarDate();
        binding.showYearView.setText(currentDate.getYear() + "年");
        binding.showMonthView.setText(currentDate.getMonth() + "");
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        initListener();
        CustomDayView customDayView = new CustomDayView(this, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                this,
                onSelectDateListener,
                CalendarAttr.WeekArrayType.Monday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(type -> {
        });
        initMarkData();
        initMonthPager();
    }
    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {
                refreshClickDate(date);
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                binding.calendarView.selectOtherMonth(offset);
            }
        };
    }


    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        binding.showYearView.setText(date.getYear() + "年");
        binding.showMonthView.setText(date.getMonth() + "");
        sharedPrefs.edit().putString("labeldate",currentDate.toString() ).commit();
        getNote(currentDate);
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private void initMonthPager() {
        binding.calendarView.setAdapter(calendarAdapter);
        binding.calendarView.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        binding.calendarView.setPageTransformer(false, (page, position) -> {
            position = (float) Math.sqrt(1 - Math.abs(position));
            page.setAlpha(position);
        });
        binding.calendarView.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendarViews = calendarAdapter.getPagers();
                if (currentCalendarViews.get(position % currentCalendarViews.size()) != null) {
                    CalendarDate date = currentCalendarViews.get(position % currentCalendarViews.size()).getSeedDate();
                    currentDate = date;
                    currentCalendarViews.get(position % currentCalendarViews.size()).setActiveDay();
                    Log.i(TAG,  "change date to " + currentDate.toString());
                    refreshClickDate(currentDate);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Log.i(TAG, "getCurrentPosition" + binding.calendarView.getCurrentPosition());
    }


    private CalendarDate strDate2CalendarDate(String date)
    {

        CalendarDate out = new CalendarDate();
        String[] tmp = date.split("-");
        if(tmp.length == 3)
        {
            out.setYear(Integer.valueOf(tmp[0]));
            out.setMonth(Integer.valueOf(tmp[1]));
            out.setDay(Integer.valueOf(tmp[2]));
        }
        return out;
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用kData之后调用 calendarAdapter.notifyDataChanged();
     */
    private void initMarkData() {

        HashMap<String, String> markData = new HashMap<>();


        List<NoteDataRecord> list = noteDataRecordDAO.getAllIsUpdated();
        for (NoteDataRecord r : list)
        {
            String day = simple_day.format(r.getCreatedTime());
            Log.i(TAG,"initMarkData 0 - " + day);
            markData.put(day, "1");
        }
        list = noteDataRecordDAO.getAllIsNotUpdated();
        for (NoteDataRecord r : list)
        {
            String day = simple_day.format(r.getCreatedTime());
            Log.i(TAG,"initMarkData 1 - " + day);
            markData.put(day, "0");
        }

        calendarAdapter.setMarkData(markData);
    }

    private void refreshMonthPager(CalendarDate date) {
        calendarAdapter.notifyDataChanged(date);
        binding.showYearView.setText(date.getYear() + "年");
        binding.showMonthView.setText(date.getMonth() + "");
    }

    public void getDeviceid(){
        Constants.DEVICE_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //MenuItem menu_item_upload = menu.findItem(R.id.menu_item_upload);

        //if(Utils.isNetworkBuffet(sharedPrefs))
        //menu_item_upload.setVisible(false);


        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }


        return true;
    }

    long current = System.currentTimeMillis();
    String confirmTimeString = ScheduleAndSampleManager.getTimeString(current);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_refresh){
            //askUploadImages();
            currentDate = new CalendarDate();
            refreshMonthPager(currentDate);
            refreshClickDate(currentDate);
            refreshUnUploadedData();
        }
        /*else if(itemId == R.id.menu_item_upload){
            UploadData();
        }
        */
        else if(itemId == R.id.menu_item_devoice_id){
            //Toast.makeText(this, Constants.DEVICE_ID, Toast.LENGTH_LONG).show();
            getPIDfromFirebase();
        }
        /*else if (itemId == R.id.menu_item_preference_news) {
            // 設定新聞類型 by jeff
            NewsSelectionFragment newsSelectionFragment = new NewsSelectionFragment();
            newsSelectionFragment.show(getSupportFragmentManager(),"");
        }*/else if (itemId == R.id.menu_item_preference_network) {
            // 設定網路類型
            NetworkSelectionFragment networkSelectionFragment = new NetworkSelectionFragment();
            networkSelectionFragment.show(getSupportFragmentManager(),"");
        } else if (itemId == R.id.menu_item_preference) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);

        } else if(itemId == android.R.id.home) {
            //goback();

        } /*else if(itemId == R.id.menu_item_send_mail)
        {
            Intent intent = new Intent (Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"passingtime.mui@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "關於執行紀錄或實驗ＡＰＰ");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi, 我的device id 是" + Constants.DEVICE_ID +", 我有一些問題，");
            intent.setPackage("com.google.android.gm");
            if (intent.resolveActivity(getPackageManager())!=null)
                startActivity(intent);
            else
                Toast.makeText(this,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
        }*/
        /*
        else if (itemId == R.id.menu_item_reminder_setting) {
            periodAlarm();
        }
         */
        else if (itemId == R.id.menu_item_test_settings) {
            SettingSelectionFragment settingSelectionFragment = new SettingSelectionFragment();
            settingSelectionFragment.show(getSupportFragmentManager(),"");

        }


        return super.onOptionsItemSelected(item);
    }


    public void UploadData()
    {
        String PID = sharedPrefs.getString("ParticipantID", "");
        if(PID.isEmpty()){
            Toast.makeText(this, "未知ＰＩＤ, 請晚點再重試。如果一直無法上傳，請至右上角設定->寄信詢問，寄信與實驗聯絡人聯絡，謝謝。", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        List<MinukuDataRecord> minukuDataRecordList = minukuDataRecordDAO.getByIsUpload(0, 0);
                        Log.d(TAG, "minukuDataRecordList: " + minukuDataRecordList.size());

                        List<NotificationRemoveRecord> notificationRemoveList = notificationRemoveRecordDAO.getByIsUpload(0);
                        Log.d(TAG, "notificationRemoveList: " + notificationRemoveList.size());

                        List<MobileAccessibilityDataRecord> accessibilityList = mobileAccessibilityDataRecordDAO.getByIsUpload(0);
                        Log.d(TAG, "mobileAccessibilityDataRecord: " + accessibilityList.size());

                        List<PreferenceDataRecord> pList = preferenceDataRecordDAO.getByIsUpload(0);
                        Log.d(TAG, "preferenceDataRecord: " + pList.size());

                        List<ActionDataRecord> actionList = actionDataRecordDAO.getAll();
                        Log.d(TAG, "actionList: " + actionList.size());

                        current = System.currentTimeMillis();

                        saveUploadDataToSharePref(current, minukuDataRecordList, notificationRemoveList, accessibilityList, pList, actionList);

                        if(!Utils.isMyServiceRunning(MainActivity.this, UploadDataService.class)){
                            Log.i(TAG, "UploadData , UploadDataService is not running. ");
                            startService(new Intent(getBaseContext(), UploadDataService.class)
                                    .setAction(UploadDataService.ACTION_UPLOAD));
                        }
                    }
                }).start();
    }

    private void initView() {
        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMain.setHasFixedSize(true);
        binding.recyclerViewMain.setNestedScrollingEnabled(false);
        binding.recyclerViewMain.setItemAnimator(new DefaultItemAnimator());
        final TimeLineDecoration decoration = new TimeLineDecoration(this)
                .setLineColor(android.R.color.black)
                .setLineWidth(1)
                .setLeftDistance(85)
                .setTopDistance(10)
                .setBeginMarker(R.drawable.down)
                .setEndMarker(R.drawable.up)
                .setCheckedMarker(R.drawable.ic_checklist)
                .setUncheckedMarker(R.drawable.ic_assign)
                .setUploadMarker(R.drawable.ic_upload)
                .setCompleteMarker(R.drawable.ic_quality)
                .setMarkerRadius(4)
                .setMarkerColor(R.color.colorAccent)
                .setCallback(new TimeLineDecoration.TimeLineAdapter() {

                    @Override
                    public int getTimeLineType(int position) {
                        //if (position == 0) return BEGIN;
                        //else if (position == adapter.getItemCount() - 1) return END;
                        if (adapter.getItem(position).isStart) return BEGIN;
                        else if (adapter.getItem(position).isEnd) return END;
                        else if (adapter.getItem(position).isCustom) return CUSTOM;
                        else if (adapter.getItem(position).isChecked) return CHECKED;
                        else if (adapter.getItem(position).isUnchecked) return UNCHECKED;
                        else if (adapter.getItem(position).isUploaded) return UPLOAD;
                        else if (adapter.getItem(position).isComplete) return COMPLETE;
                        else return NORMAL;
                    }
                });
        binding.recyclerViewMain.addItemDecoration(decoration);

        adapter = new TaskAdapter(this, this);
        binding.recyclerViewMain.setAdapter(adapter);
    }

    private long getStartTime1(CalendarDate date )
    {
        return getStartTime(date.getYear(), date.getMonth(), date.getDay());
    }
    private long getEndTime1(CalendarDate date )
    {

        return getEndTime(date.getYear(), date.getMonth(), date.getDay());
    }

    private long getStartTime(int y, int m, int d )
    {
        long time = -1;
        String Date = y+"/" +m +"/" + d;
        try {
            String tmp = Date + " 00:00:00.000";

            Date dt = f.parse(Date + " 00:00:00.000");
            time = dt.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getStartTime:" + time );
        return time;
    }
    private long getEndTime(int y, int m, int d )
    {
        long time = -1;
        String Date = y+"/" +m +"/" + d;
        try {
            String tmp = Date + " 23:59:59.999";

            Date dt = f.parse(tmp);
            time = dt.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getEndTime:" + time );
        return time;
    }

    private void getNote(CalendarDate date ) {
        analogs.clear();

        List<NoteDataRecord> list = noteDataRecordDAO.getByCreatedTime( getStartTime1(date),  getEndTime1(date));

        int count = 0;
        if(!list.isEmpty()){


            int count_complete = 0;
            for(NoteDataRecord note : list) {
                if (note.getMode() == 0) {
                    if(note.isGroup) {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.createdTime);
                        t.isStart = true;
                        analogs.add(t);
                    } else {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.createdTime);
                        t.isCustom = true;
                        analogs.add(t);
                    }
                }
                else if (note.getMode() == 1) {
                    if(note.isGroup) {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.createdTime);
                        t.isEnd = true;
                        analogs.add(t);
                    } else {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.createdTime);
                        t.isCustom = true;
                        analogs.add(t);
                    }
                }else if (note.getMode() == 2) {


                    if (note.getIsUpload() > 0) {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.getEndtime()) + "\n|       \n" + simple.format(note.getStarttime());
                        t.starttime = note.getStarttime();
                        t.endtime = note.getEndtime();
                        t.isComplete = true;
                        analogs.add(t);
                        count_complete++;
                    }

                    else if (note.getIsUpload() == 0 ) {
                        TextLabelTask t = new TextLabelTask();
                        t.id = note.get_id();
                        t.title = note.getText();
                        t.timing = note.getCreatedTime();
                        t.time = simple.format(note.getEndtime()) + "\n|       \n" + simple.format(note.getStarttime());
                        t.starttime = note.getStarttime();
                        t.endtime = note.getEndtime();
                        t.isUploaded = true;
                        analogs.add(t);
                        count_complete++;
                    }
                    else {
                        count++;
                        int count_unlabeled = imageDataRecordDAO.getUnLabeledLabelByTime(note.getStarttime(),note.getEndtime());
                        int count_labeled = imageDataRecordDAO.getLabeledLabelByTime(note.getStarttime(),note.getEndtime());
//                        String str1 = " 未標記 ： " + count_unlabeled + " , 已標記 ： " + count_labeled;
                        String str1 = getString(R.string.main_unlabeled) + count_unlabeled + "; " + getString(R.string.main_labeled) + count_labeled;

                        Log.i(TAG, note.getStarttime() + " -> " + note.getEndtime() + str1);
                        boolean isdone = false;
                        if(count_unlabeled == 0) {
                            note.setDone(true);
                            note.setText(str1);
                            noteDataRecordDAO.updateOne(note);
                            UploadLabelTask t = new UploadLabelTask();
                            t.id = note.get_id();
                            t.title = note.getText();
                            t.timing = note.getCreatedTime();
                            t.time = simple.format(note.getEndtime()) + "\n|       \n" + simple.format(note.getStarttime());
                            t.starttime = note.getStarttime();
                            t.endtime = note.getEndtime();
                            t.isChecked = true;
                            analogs.add(t);

                        }else {
                            LocationLabelTask t = new LocationLabelTask();
                            t.id = note.get_id();
                            t.title = getString(R.string.main_screenshot_reminder);
                            t.subTitle = str1;
                            t.time = simple.format(note.getEndtime()) + "\n|       \n" + simple.format(note.getStarttime());
                            t.starttime = note.getStarttime();
                            t.endtime = note.getEndtime();
                            t.timing = note.getCreatedTime();
                            t.isUnchecked = true;
                            analogs.add(t);
                        }

                    }
                }
            }

        }

        HashMap<String, String> map = calendarAdapter.getMarkData();
        Log.i(TAG, "getNote : "+date.toString());
        if(date.toString().equalsIgnoreCase(simple_day.format(System.currentTimeMillis())))
        {
            Log.i(TAG, "getNote -today" );
            if(Utils.isActiveTiming(sharedPrefs))
            {
                long lastCapturetime = sharedPrefs.getLong("lastCapturetime", -1);
                if(lastCapturetime > 0)
                {
                    long current = ScheduleAndSampleManager.getCurrentTimeInMillis();
                    int count_unlabeled = imageDataRecordDAO.getUnLabeledLabelByTime(lastCapturetime, current);
                    int count_labeled = imageDataRecordDAO.getLabeledLabelByTime(lastCapturetime, current);
                    Log.i(TAG, "getNote : " + lastCapturetime + " -> " + current +" : " + (count_unlabeled + count_labeled));

                    count++;
                    String str1 = getString(R.string.main_unlabeled) + count_unlabeled + "; " +getString(R.string.main_labeled) + count_labeled;
                    Log.i(TAG, lastCapturetime + " -> " + current + str1);
                    boolean isdone = false;

                    LocationLabelTask t = new LocationLabelTask();
                    t.id = -1;
                    t.title = getString(R.string.main_screenshot_reminder);
                    t.subTitle = str1;
                    t.time = "current \n|       \n" + simple.format(lastCapturetime);
                    t.starttime = lastCapturetime;
                    t.endtime = current;
                    t.timing = current;
                    t.isUnchecked = true;
                    analogs.add(t);
                }

            }

            if(count > 0)
            {
                map.put(date.toString(),"0");
            }else{
                map.put(date.toString(),"1");
            }

        } else {
            if(count > 0)
            {
                map.put(date.toString(),"0");
            }else{
                map.put(date.toString(),"1");
            }
        }
        //calendarAdapter.setMarkData(map);

        Collections.sort(analogs, (m1, m2) -> {
            if ((m1.timing - m2.timing)> 0) {
                return -1;
            } else if ((m1.timing - m2.timing) < 0) {
                return 1;
            } else {
                return 0;
            }
        });

        adapter.setItems(analogs);
    }

    private void refreshNote(String json_id, String json_success)
    {
        ArrayList<String> idlist = Utils.getArrayListFromJson(gson, json_id);
        ArrayList<String> successlist = Utils.getArrayListFromJson(gson, json_success);

        for(int k=0; k<idlist.size(); k++)
        {
            long tmpid= Long.parseLong(idlist.get(k));

            int i;
            for (i=0 ; i < analogs.size() ; i++)
            {
                LabelTask task = analogs.get(i);
                if(task.id == tmpid) {
                    String str = task.title;
                    str = str.replace(getString(R.string.main_uploading),"");
                    if(successlist.size()==0)
                    {
                        task.title = getString(R.string.main_uploading_success) + str + "\n" + getString(R.string.uploaded_images, "0");
                    }else{
                        task.title = getString(R.string.main_uploading_success)+ str + "\n" + getString(R.string.uploaded_images, successlist.get(k));
                    }
                    task.isUploaded = false;
                    task.isComplete = true;
                    break;
                }
            }
            adapter.notifyItemChanged(i);

            List<NoteDataRecord> list = noteDataRecordDAO.getByPKId(tmpid);
            if(!list.isEmpty())
            {
                DateFormat simple_y  = new SimpleDateFormat("yyyy");
                DateFormat simple_m  = new SimpleDateFormat("M");
                DateFormat simple_d  = new SimpleDateFormat("d");

                String y = simple_y.format(list.get(0).getCreatedTime());
                String m = simple_m.format(list.get(0).getCreatedTime());
                String d = simple_d.format(list.get(0).getCreatedTime());

                List<NoteDataRecord> l = noteDataRecordDAO.getAllIsNotUpdatedByCreatedTime(getStartTime(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d)),  getEndTime(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d)));
                if(l.isEmpty())
                {
                    HashMap<String, String> map = calendarAdapter.getMarkData();
                    map.put(simple_day.format(list.get(0).getCreatedTime()),"1");

                    //calendarAdapter.setMarkData(map);

                }
            }

        }
        delete_notelist = idlist;
        deleteImages();
    }

    private void refreshUnUploadedData()
    {
        String n = sharedPrefs.getString("NetworkBuffet","");
        if(!n.equalsIgnoreCase("y")) {
            new Thread(
                    () -> {
                        unupload_minuku = minukuDataRecordDAO.getCountByIsUpload(0, 0);

                        runOnUiThread(() -> {

                            String str = getString(R.string.main_unuploadcounts, unupload_minuku);
//                                    "共有 " + unupload_minuku + " 筆背景資料";
                            if(!Utils.isWifiAvailable(MainActivity.this)){
                                str = str + getString(R.string.main_uploadwithWifi);
                            }else{
                                str = str + getString(R.string.main_waitingforupload) + getString(R.string.main_wificonnection);
                            }
                            binding.showUnuploadedView.setText(str);
                        });

                    }).start();
        }else{
            new Thread(
                    () -> {
                        unupload_minuku = minukuDataRecordDAO.getCountByIsUpload(0, 0);
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String str = getString(R.string.main_unuploadcounts, unupload_minuku);
                                if(!Utils.isNetworkConneted(MainActivity.this)){
                                    str = str + getString(R.string.main_uploadwithWifi);
                                }else{
                                    str = str + getString(R.string.main_waitingforupload);
                                }
                                binding.showUnuploadedView.setText(str);
                            }
                        });
                    }).start();
        }

        String PID = sharedPrefs.getString("ParticipantID", "");
        if(!PID.isEmpty()) {
            if (PID.startsWith("U")) {
                String tx = preferenceDataRecordDAO.getCountByIsUpload(0) + " new Preference data need to upload. \n"
                        + actionDataRecordDAO.getAllCount() + " new Action data need to upload. \n"
                        + mobileAccessibilityDataRecordDAO.getCountByIsUpload(0) + " new Accessibility data need to upload. \n"
                        + notificationRemoveRecordDAO.getCountByIsUpload(0) + " new notification remove data need to upload. ";

                Toast.makeText(this, tx, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void deleteImages()
    {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        List<ImageDataRecord> deletelist = new ArrayList<>();

                        Log.i(TAG, " deleteImages is called");
                        for(String idStr :delete_notelist) {
                            long tmpid= Long.parseLong(idStr);
                            List<NoteDataRecord> waittodeletelist = noteDataRecordDAO.getByPKId(tmpid);
                            if(!waittodeletelist.isEmpty())
                            {
                                NoteDataRecord n = waittodeletelist.get(0);
                                Log.i(TAG, " deleteImages : " + n.getStarttime() + " ~ " + n.getEndtime());
                                List<ImageDataRecord> tmpdeletelist = imageDataRecordDAO.getAllByTime(n.getStarttime(), n.getEndtime());

                                if(!tmpdeletelist.isEmpty()){
                                    for(ImageDataRecord x : tmpdeletelist)
                                        deletelist.add(x);
                                }
                            }
                        }

                        Log.i(TAG, " delete " + deletelist.size() + " Images ");
                        for(ImageDataRecord a: deletelist) {
                            if (a.getIsUpload() != 1 && a.getLabel() != LABEL_PRIVATE) {
                                File fdelete = new File(imgFile + "/" + a.getFileName());
                                if (fdelete.exists()) {
                                    insertImageRecordToPreference(a, 3);
                                } else{
                                    imageDataRecordDAO.deleteOne(a);
                                }

                            }else {
                                if(a.getLabel() == LABEL_PRIVATE)
                                {
                                    insertImageRecordToPreference(a, 4);
                                }
                                File fdelete = new File(imgFile + "/" + a.getFileName());
                                if (fdelete.exists()) {
                                    fdelete.delete();
                                }
                                imageDataRecordDAO.deleteOne(a);
                            }
                        }

                        Log.i(TAG, " deleteImages end");
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    private void insertImageRecordToPreference(ImageDataRecord a, int type) {
        Log.e(TAG, "insertImageRecordToPreference " + a.getFileName() +" " + a.getLabel());
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(a), type, System.currentTimeMillis()));
    }

    private void saveUploadDataToSharePref(long time , List<MinukuDataRecord> minu, List<NotificationRemoveRecord> notiR, List<MobileAccessibilityDataRecord> acc, List<PreferenceDataRecord> pList, List<ActionDataRecord> action) {
        ArrayList<String> minukulist = Utils.getArrayList(sharedPrefs, gson, EXTRA_MINUKU_LIST);
        ArrayList<String> notiRlist = Utils.getArrayList(sharedPrefs, gson, EXTRA_NOTIREMOVE_LIST);
        ArrayList<String> acclist = Utils.getArrayList(sharedPrefs, gson, EXTRA_ACC_LIST);
        ArrayList<String> preflist = Utils.getArrayList(sharedPrefs, gson, EXTRA_PREF_LIST);
        ArrayList<String> actionlist = Utils.getArrayList(sharedPrefs, gson, EXTRA_ACTION_LIST);
        if(!minu.isEmpty())
        {
            for(MinukuDataRecord a: minu)
            {
                String strId = Long.toString(a.get_id());
                if(!minukulist.contains(strId))
                {
                    minukulist.add(strId);
                }
            }

        }

        if(!notiR.isEmpty())
        {
            for(NotificationRemoveRecord a: notiR)
            {
                String strId = Integer.toString(a.id);
                if(!notiRlist.contains(strId))
                {
                    notiRlist.add(strId);
                }
            }

        }

        if(!acc.isEmpty())
        {
            for(MobileAccessibilityDataRecord a: acc)
            {
                String strId = Integer.toString(a.getId());
                if(!acclist.contains(strId))
                {
                    acclist.add(strId);
                }
            }

        }

        if(!pList.isEmpty())
        {
            for(PreferenceDataRecord a: pList)
            {
                String strId = Long.toString(a.getId());
                if(a.getType()==4 || a.getType()==3 || a.getType()==5) {
                    if (!preflist.contains(strId)) {
                        preflist.add(strId);
                    }
                }
            }

        }

        if(!action.isEmpty())
        {
            for(ActionDataRecord a: action)
            {
                String strId = Long.toString(a.get_id());
                if(!actionlist.contains(strId))
                {
                    actionlist.add(strId);
                }
            }
        }

        String json_m = gson.toJson(minukulist);
        String json_n = gson.toJson(notiRlist);
        String json_a = gson.toJson(acclist);
        String json_p = gson.toJson(preflist);
        String json_act = gson.toJson(actionlist);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_MINUKU_LIST, json_m);
        Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_NOTIREMOVE_LIST, json_n);
        Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_ACC_LIST, json_a);
        Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_PREF_LIST, json_p);
        Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_ACTION_LIST, json_act);

        editor.putLong(EXTRA_LAST_TIME, time)
                .commit();

        Log.i(TAG,"saveUploadDataToSharePref time: " +time);
        Log.i(TAG,"saveUploadDataToSharePref minuku: " +json_m);
        Log.i(TAG,"saveUploadDataToSharePref notiR: " +json_n);
        Log.i(TAG,"saveUploadDataToSharePref acc: " +json_a);
        Log.i(TAG,"saveUploadDataToSharePref pref: " +json_p);
        Log.i(TAG,"saveUploadDataToSharePref action: " +json_act);
    }


    public void askNetwork() {
        String n = sharedPrefs.getString("NetworkBuffet","");
        if(n.isEmpty()) {
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(R.string.main_data_plan)
                    .setPositiveButton(R.string.yes, (dialog, which) -> sharedPrefs.edit()
                            .putString("NetworkBuffet", "y")
                            .apply())
                    .setNegativeButton(R.string.no , (dialog, which) -> sharedPrefs.edit()
                            .putString("NetworkBuffet", "n")
                            .apply()).show();
        }
    }

    private void getPIDfromFirebase()
    {
        /// get Participant_ID
        if(sharedPrefs.getString("ParticipantID", "").isEmpty())
        {
            if(Constants.DEVICE_ID.equalsIgnoreCase("NA"))
                getDeviceid();
            if(Constants.DEVICE_ID.equalsIgnoreCase("NA"))
            {
                Toast.makeText(this, "無法取得Device ID。", Toast.LENGTH_LONG).show();
            }else{
                if(Utils.isNetworkConneted(MainActivity.this)) {
                    final DocumentReference UserRef = db.collection("Users").document("Overview");

                    UserRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = document.getData();
                            } else {
                                Map<String, Object> newData = new HashMap<>();
                                newData.put("Number", 0); // Add your data
                                UserRef.set(newData, SetOptions.merge())
                                        .addOnSuccessListener(aVoid -> android.util.Log.d(TAG, "DocumentSnapshot added successfully"))
                                        .addOnFailureListener(e -> android.util.Log.e(TAG, "Error adding document", e));
                            }
                        } else {
                            android.util.Log.e(TAG, "Error getting document: ", task.getException());
                        }
                    });

                    final DocumentReference DeviceRef = db.collection("Users").document(Constants.DEVICE_ID);
                    db.runTransaction(transaction -> {
                        DocumentSnapshot snapshot = transaction.get(DeviceRef);
                        if (snapshot.exists()) {
                            String id = snapshot.getString("id");
                            Double retry = snapshot.getDouble("retry");
                            retry = retry + 1;
                            transaction.update(DeviceRef, "retry", retry);
                            transaction.update(DeviceRef, "timestamp", FieldValue.serverTimestamp());
                            return id;

                        } else {

                            DocumentSnapshot snapshot1 = transaction.get(UserRef);
                            Double num = snapshot1.getDouble("Number");
                            String id = "";
                            num = num + 1;
                            int value = num.intValue();
                            if (value < 10) {
                                id = "P0" + value;
                            } else {
                                id = "P" + value;
                            }
                            transaction.update(UserRef, "Number", num);
                            final Map<String, Object> record = new HashMap<>();
                            record.put("id", id);
                            record.put("retry", 0);
                            record.put("timestamp", FieldValue.serverTimestamp());
                            transaction.set(DeviceRef, record);
                            return id;
                        }
                    }).addOnSuccessListener(result -> {
                        android.util.Log.i(TAG, "Transaction success: " + result);
                        sharedPrefs.edit()
                                .putString("ParticipantID", result)
                                .apply();
                            Toast.makeText(MainActivity.this, "Device ID: " + Constants.DEVICE_ID, Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Transaction failure." + e.getMessage());
                            Toast.makeText(MainActivity.this, "Transaction failure. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
                else {
                    Toast.makeText(this, "沒網路，無法取得ＰＩＤ，請開啟網路並重試。", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(this, "Device ID: " + Constants.DEVICE_ID, Toast.LENGTH_LONG).show();
        }
        new Thread(
                () -> {
                    if (Utils.isNetworkConneted(MainActivity.this)) {
                        List<NotificationEventRecord> notificationEventRecordList = notificationEventRecordDAO.getOne();
                        if (notificationEventRecordList.isEmpty()) {
                            Log.i(TAG, "syncNotificationQuestionsRunnable ---  No notification data existed  ");
                            db.collection("Notification")
                                    .whereEqualTo("enable", true)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    //Log.i(TAG, document.getId() + " => " + document.getData());

                                                    NotificationEventRecord notificationEventRecord = new NotificationEventRecord(document.getData().get("id").toString(),
                                                            document.getData().get("title").toString(), document.getData().get("options").toString(),
                                                            document.getData().get("url").toString(), document.getData().get("type").toString());
                                                    notificationEventRecordDAO.insertOne(notificationEventRecord);
                                                }
                                                sharedPrefs.edit()
                                                        .putLong("lastUpdateNotificationListTime", ScheduleAndSampleManager.getCurrentTimeInMillis())
                                                        .apply();
                                                Log.i(TAG, "syncNotificationQuestionsRunnable ---  download notification data success " + notificationEventRecordDAO.getAll().size());

                                            } else {
                                                Log.i(TAG, "syncNotificationQuestionsRunnable Error getting documents: " + task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                }).start();
    }


    @Override
    public void onLookClick(int position) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCallClick(int position) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationClick(int position) {
        Intent mIntent = new Intent(this, LabelActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putLong("start_time", analogs.get(position).starttime);
        mBundle.putLong("end_time", analogs.get(position).endtime);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        //Toast.makeText(this,  + "~" + analogs.get(position).endtime, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReviewClick(int position) {
        Intent mIntent = new Intent(this, LabelActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putLong("start_time", analogs.get(position).starttime);
        mBundle.putLong("end_time", analogs.get(position).endtime);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        //Toast.makeText(this,  + "~" + analogs.get(position).endtime, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUploadClick(int position) {
        String PID = sharedPrefs.getString("ParticipantID", "");
        if(!PID.isEmpty()) {
            //askUploadAGroupofImages(position);
            Intent mIntent = new Intent(this, GrantUploadActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putLong("note_id",  analogs.get(position).id);
            mBundle.putLong("start_time", analogs.get(position).starttime);
            mBundle.putLong("end_time", analogs.get(position).endtime);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
        } else {
            Toast.makeText(this, "未知ＰＩＤ, 請晚點再重試。如果一直無法上傳，請至右上角設定->寄信詢問，寄信與實驗聯絡人聯絡，謝謝。", Toast.LENGTH_LONG).show();
        }
    }

}
