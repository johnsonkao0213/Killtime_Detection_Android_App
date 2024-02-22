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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.adapters.RecyclerViewAdapter_Upload;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.NoteDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.NoteDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.boredom_detection.service.UploadService;
import labelingStudy.nctu.boredom_detection.view.helper.DragSelectTouchListener;
import labelingStudy.nctu.boredom_detection.view.helper.SelectItemAnimator;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;

import static labelingStudy.nctu.boredom_detection.config.Constants.LABEL_GRANT_UPLOAD;
import static labelingStudy.nctu.boredom_detection.config.Constants.LABEL_NOT_GRANT_UPLOAD;
import static labelingStudy.nctu.boredom_detection.config.Constants.LABEL_PRIVATE;
import static labelingStudy.nctu.boredom_detection.service.UploadService.EXTRA_ID_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadService.EXTRA_ID_NOTE_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadService.EXTRA_NON_PRIVATE;
import static labelingStudy.nctu.boredom_detection.service.UploadService.EXTRA_NOTE_ID_LIST;
import static labelingStudy.nctu.boredom_detection.service.UploadService.EXTRA_PRIVATE;


public class GrantUploadActivity extends AppCompatActivity {

    private static final String TAG = "GrantUploadActivity";

    public static ProgressBar progressBar;
    public static int viewPosition = 0;
    public static int size = 200;
    public static GridLayoutManager layoutManager;
    public static int spancount = 5;
    public ArrayList<String> tmpUploadIdList = new ArrayList<String>();
    public ArrayList<String> tmpUnuploadIdList = new ArrayList<String>();
    public Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            return m1.get(RecyclerViewAdapter_Upload.NAME).compareTo(m2.get(RecyclerViewAdapter_Upload.NAME));
        }
    };
    Context mContext;
    List<String> List_ID = new ArrayList<String>();
    File imgFile;
    long lastRefreshTime = -1;
    int alreadylabeled = 0;
    RecyclerView recyclerView;
    RecyclerViewAdapter_Upload adapter;
    Toolbar toolbar;
    Button btn_grant_upload, btn_cannot_upload;
    boolean all_select_or_not = false;
    long start_time, end_time;
    TextView cn_upload_count, cnt_upload_count, grant_count, not_grant_count;
    ImageView all_select;
    DateFormat simple_time = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat simple_day = new SimpleDateFormat("MM月dd日");
    String day = "";
    int unlable_count = 1;
    int UnuploadCount = 0;
    int willUploadCount = 0;
    long uploaded_note_id = -1;
    String confirmTimeString = "";
    Gson gson = new Gson();
    String dialogStr = "";
    String btn_Yes = "";
    String btn_No = "";
    long current = System.currentTimeMillis();
    int lastLabel = -1;
    String labelTimeString = ScheduleAndSampleManager.getTimeString(current);
    private ImageDataRecordDAO imageDataRecordDAO;
    private NoteDataRecordDAO noteDataRecordDAO;
    private DragSelectTouchListener touchListener;
    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    private SharedPreferences sharedPrefs;
    private final ArrayList<Map<String, String>> mImageData = new ArrayList<>();
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating GrantUploadActivity");

        setContentView(R.layout.activity_upload);
        mContext = this;
        BoredomApp.setActivityVisibility(TAG, true);


        uploaded_note_id = getIntent().getExtras().getLong("note_id");
        start_time = getIntent().getExtras().getLong("start_time");
        end_time = getIntent().getExtras().getLong("end_time");
        day = simple_day.format(start_time);
        Log.i(TAG, "start: " + start_time + " , end: " + end_time);

        progressBar = findViewById(R.id.progressBar_upload);
        //progressBar.setVisibility(View.GONE);
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Log.i(TAG, "android.os.Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.setStatusBarColor(ContextCompat.getColor(GrantUploadActivity.this, R.color.white));// set status background white
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar_upload);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


        btn_grant_upload = findViewById(R.id.btn_cn_uploaded);
        btn_cannot_upload = findViewById(R.id.btn_cnt_uploaded);

        grant_count = findViewById(R.id.confirmed_grant_count);
        not_grant_count = findViewById(R.id.confirmed_not_grant_count);

        cn_upload_count = findViewById(R.id.grant_count);
        cnt_upload_count = findViewById(R.id.not_grant_count);

        all_select = findViewById(R.id.checked_all);


        size = Resources.getSystem().getDisplayMetrics().widthPixels / 6;

        recyclerView = findViewById(R.id.recyclerView_upload);


        layoutManager = new GridLayoutManager(this, spancount);

        imageDataRecordDAO = appDatabase.getDatabase(this).imageDataRecordDAO();
        noteDataRecordDAO = appDatabase.getDatabase(this).noteDataRecordDAO();
        preferenceDataRecordDAO = appDatabase.getDatabase(this).preferenceDataRecordDAO();

        imgFile = new File(Environment.getExternalStoragePublicDirectory(Constants.PACKAGE_DIRECTORY_PATH), "Demo");


        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        initRecyclerView();


        getImages();
        //set scale gesture detector
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                    if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                        Log.i(TAG, "onScale < -1");
                        if (spancount < 5) {
                            spancount++;
                            if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                layoutManager.setSpanCount(spancount);
                                size = Resources.getSystem().getDisplayMetrics().widthPixels / spancount;
                            } else if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                layoutManager.setSpanCount(spancount * 2);
                                size = Resources.getSystem().getDisplayMetrics().widthPixels / (spancount * 2);
                            }
                            recyclerView.setLayoutManager(layoutManager);
                            //adapter.notifyDataSetChanged();
                            adapter.notifyItemRangeChanged(getCurrentItem(), calculateRange());
                            return true;
                        }
                    } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                        Log.i(TAG, "onScale > 1");
                        if (spancount > 2) {
                            spancount--;
                            if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                layoutManager.setSpanCount(spancount);
                                size = Resources.getSystem().getDisplayMetrics().widthPixels / spancount;
                            } else if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                layoutManager.setSpanCount(spancount * 2);
                                size = Resources.getSystem().getDisplayMetrics().widthPixels / (spancount * 2);
                            }
                            recyclerView.setLayoutManager(layoutManager);
                            //adapter.notifyDataSetChanged();
                            adapter.notifyItemRangeChanged(getCurrentItem(), calculateRange());
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //set touch listener on recycler view
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = getCurrentItem();
                    viewPosition = position;
                    //Log.e(TAG, "viewposition " +position);
                }
            }
        });


        btn_grant_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                lastLabel = LABEL_GRANT_UPLOAD;
                doublecheckUserLabelAction(getResources().getString(R.string.can_upload));

            }
        });
        btn_cannot_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click

                lastLabel = LABEL_NOT_GRANT_UPLOAD;
                doublecheckUserLabelAction(getResources().getString(R.string.cannot_upload));
            }
        });


    }

    public void checked_all_event(View v) {
        if (!all_select_or_not) {
            all_select_or_not = true;


            for (int i = 0; i < mImageData.size(); i++) {

                mImageData.get(i).put(RecyclerViewAdapter_Upload.CHECKED, "1");
                if (!adapter.checkedList.contains(String.valueOf(i))) {
                    adapter.checkedList.add(String.valueOf(i));
                    adapter.checkCount++;
                }
            }

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    all_select.setImageResource(R.drawable.check);
                }
            });

        } else {
            all_select_or_not = false;


            for (int i = 0; i < mImageData.size(); i++) {

                mImageData.get(i).put(RecyclerViewAdapter_Upload.CHECKED, "0");
            }
            adapter.checkedList.clear();
            adapter.checkCount = 0;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            runOnUiThread(new Runnable() {
                public void run() {
                    all_select.setImageResource(R.drawable.ic_circle_outline);
                }
            });
        }
    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
    }

    public int calculateRange() {
        int start = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int end = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        if (start < 0) start = 0;
        if (end < 0) end = mImageData.size();
        Log.i(TAG, "" + (end - start));
        return end - start;
    }

    public void getImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                if ((current - lastRefreshTime) >= 1500) {
                    lastRefreshTime = current;
                    Log.i(TAG, "loadImages - initImageBitmaps: preparing bitmaps.");
                    Log.i(TAG, "loadImages - mImageData: " + mImageData.size());

                    List<ImageDataRecord> imageList = null;

                    runOnUiThread(new Runnable() {
                        public void run() {

                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });


                    if (mImageData.size() == 0) {

                        setLabelCount();
                        List_ID.clear();
                        mImageData.clear();
                        alreadylabeled = 0;
                        imageList = imageDataRecordDAO.getAllIsReadyByTime(0, start_time, end_time);
                    }

                    if (imageList != null) {
                        ArrayList<Map<String, String>> tmp = new ArrayList<Map<String, String>>();
                        for (ImageDataRecord img : imageList) {
                            if (!List_ID.contains(String.valueOf(img.get_id()))) {
                                Map<String, String> newone = new HashMap<>();
                                newone.put(RecyclerViewAdapter_Upload.BOREDOMLABELED, String.valueOf(img.getLabel()));
                                newone.put(RecyclerViewAdapter_Upload.IMAGE, imgFile.toString() + "/" + img.getFileName());
                                newone.put(RecyclerViewAdapter_Upload.NAME, img.getFileName());
                                newone.put(RecyclerViewAdapter_Upload.CHECKED, "0");
                                if (img.getGrantUpload() != -1) {
                                    alreadylabeled++;
                                }
                                newone.put(RecyclerViewAdapter_Upload.LABELED, String.valueOf(img.getGrantUpload()));
                                newone.put(RecyclerViewAdapter_Upload.ID, String.valueOf(img.get_id()));
                                tmp.add(newone);
                            }
                        }

                        Collections.sort(tmp, new Comparator<Map<String, String>>() {
                            public int compare(Map<String, String> m1, Map<String, String> m2) {
                                return m1.get(RecyclerViewAdapter_Upload.NAME).compareTo(m2.get(RecyclerViewAdapter_Upload.NAME));
                            }
                        });

                        if (tmp.size() > 0) {
                            for (Map<String, String> img : tmp) {
                                mImageData.add(img);
                                List_ID.add(img.get(RecyclerViewAdapter_Upload.ID));
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        }).start();

    }

    void setLabelCount() {
        runOnUiThread(new Runnable() {
            public void run() {

                int count_unlabeled = imageDataRecordDAO.getUnconfirmGrantByTime(start_time, end_time);
                int count_labeled = imageDataRecordDAO.getConfirmGrantByTime(start_time, end_time);
                grant_count.setText("" + count_labeled);
                not_grant_count.setText("" + count_unlabeled);

                int count_cn_upload = imageDataRecordDAO.getGrantCountByTime(LABEL_GRANT_UPLOAD, start_time, end_time);
                int count_cnt_upload = imageDataRecordDAO.getGrantCountByTime(LABEL_NOT_GRANT_UPLOAD, start_time, end_time);
                cn_upload_count.setText("" + count_cn_upload);
                cnt_upload_count.setText("" + count_cnt_upload);

                unlable_count = count_unlabeled;

                if (count_unlabeled == 0) {
                    invalidateOptionsMenu();
                }
            }
        });
    }

    private void initRecyclerView() {
        Log.i(TAG, "initRecyclerView: init recyclerview");

        Configuration orientation = new Configuration();
        if (this.recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(spancount);
            size = Resources.getSystem().getDisplayMetrics().widthPixels / spancount;
        } else if (this.recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(spancount * 2);
            size = Resources.getSystem().getDisplayMetrics().widthPixels / (spancount * 2);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter_Upload(this, mImageData);
        recyclerView.setAdapter(adapter);

        adapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int position = recyclerView.getChildAdapterPosition(v);
                adapter.setSelected(position, true);
                touchListener.setStartSelectPosition(position);
                return false;
            }
        });

        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                adapter.setSelected(position);
            }
        });

        touchListener = new DragSelectTouchListener();

        recyclerView.addOnItemTouchListener(touchListener);

        touchListener.setSelectListener(new DragSelectTouchListener.onSelectListener() {
            @Override
            public void onSelectChange(int start, int end, boolean isSelected) {
                adapter.selectRangeChange(start, end, isSelected);
            }
        });

        RecyclerView.ItemAnimator itemAnimator = new SelectItemAnimator();
        itemAnimator.setChangeDuration(150);

        recyclerView.setItemAnimator(itemAnimator);

    }

    @Override
    public void onResume() {
        super.onResume();
        BoredomApp.setActivityVisibility(TAG, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        BoredomApp.setActivityVisibility(TAG, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BoredomApp.setActivityVisibility(TAG, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        MenuItem menu_upload = menu.findItem(R.id.menu_item_confirm_n_upload);


        menu_upload.setVisible(unlable_count == 0);

        if (getSupportActionBar() != null) {
            long today = System.currentTimeMillis();
            if (day.equalsIgnoreCase(simple_day.format(today))) {
                getSupportActionBar().setTitle(getString(R.string.today));
            } else {
                getSupportActionBar().setTitle(day);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_confirm_n_upload) {
            UnuploadCount = 0;
            willUploadCount = 0;
            tmpUploadIdList.clear();
            tmpUnuploadIdList.clear();

            if (Integer.parseInt(not_grant_count.getText().toString()) > 0) {
                Toast.makeText(GrantUploadActivity.this, getResources().getString(R.string.unmarked_images), Toast.LENGTH_SHORT).show();
                return true;
            }

            for (Map<String, String> map : adapter.mImageData) {
                if (map.get(RecyclerViewAdapter_Upload.LABELED).equalsIgnoreCase(String.valueOf(LABEL_GRANT_UPLOAD))) {
                    if (map.get(RecyclerViewAdapter_Upload.BOREDOMLABELED).equalsIgnoreCase(String.valueOf(LABEL_PRIVATE))) {
                        UnuploadCount++;
                        tmpUnuploadIdList.add(map.get(RecyclerViewAdapter_Upload.ID));
                    } else {
                        willUploadCount++;
                        tmpUploadIdList.add(map.get(RecyclerViewAdapter_Upload.ID));
                    }
                } else {
                    UnuploadCount++;
                    tmpUnuploadIdList.add(map.get(RecyclerViewAdapter_Upload.ID));
                }
            }

            askUploadAGroupofImages(getResources().getString(R.string.ask_upload_images, Integer.valueOf(cn_upload_count.getText().toString()), willUploadCount), getResources().getString(R.string.ok), getResources().getString(R.string.cancel));

            return true;
        } else if (itemId == R.id.home) {

            if (Integer.parseInt(not_grant_count.getText().toString()) == 0) {
                askUploadAGroupofImages("你已全數確認過，是否要現在上傳？" + "\n 您共允許上傳" + Integer.valueOf(cn_upload_count.getText().toString()) + "張截圖，在這些截圖中包含殺時間相關標記" + willUploadCount + "張。" + "\n請注意我們只會上傳殺時間相關標記的截圖，而上傳完成後，會自動清除全部照片。", "現在上傳", "下次再說");
            } else {
                Toast.makeText(GrantUploadActivity.this, "尚有" + not_grant_count.getText() + "張未確認使否同意上傳。", Toast.LENGTH_SHORT).show();
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void askUploadAGroupofImages(String dialog_Str, String btn_yes_str, String btn_no_str) {
        dialogStr = dialog_Str;
        btn_Yes = btn_yes_str;
        btn_No = btn_no_str;

        current = System.currentTimeMillis();
        confirmTimeString = ScheduleAndSampleManager.getTimeString(current);

        new androidx.appcompat.app.AlertDialog.Builder(GrantUploadActivity.this).setMessage(dialogStr).setPositiveButton(btn_Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                imageDataRecordDAO.updateIsReadyByTime(1, current, confirmTimeString, start_time, end_time);

                if (willUploadCount > 0) {
                    Log.i(TAG, "id:" + uploaded_note_id);
                    List<NoteDataRecord> list = noteDataRecordDAO.getByPKId(uploaded_note_id);
                    if (!list.isEmpty()) {
                        String str = getResources().getString(R.string.uploading_bar, willUploadCount, UnuploadCount);
                        NoteDataRecord t = list.get(0);
                        t.setText(str);
                        t.setIsUpload(0);
                        t.setNum_upload(willUploadCount);
                        t.setNum_skip_upload(UnuploadCount);
                        noteDataRecordDAO.updateOne(t);
                    }


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<ImageDataRecord> deletelist = new ArrayList<>();

                            Log.i(TAG, " deleteImages is called");

                            List<NoteDataRecord> waittodeletelist = noteDataRecordDAO.getByPKId(uploaded_note_id);
                            for (String id : tmpUnuploadIdList) {
                                List<ImageDataRecord> list = imageDataRecordDAO.getByPKId(Long.parseLong(id));
                                if (!list.isEmpty()) {
                                    ImageDataRecord a = list.get(0);
                                    deletelist.add(a);
                                }
                            }

                            Log.i(TAG, " delete " + deletelist.size() + " Images ");
                            for (ImageDataRecord a : deletelist) {
                                insertImageRecordToPreference(a, 4);

                                File fdelete = new File(imgFile + "/" + a.getFileName());
                                if (fdelete.exists()) {
                                    fdelete.delete();
                                }
                                imageDataRecordDAO.deleteOne(a);

                            }

                            Log.i(TAG, " deleteImages end");
                        }
                    }
                            // Starts the thread by calling the run() method in its Runnable
                    ).start();

                    saveUploadImageToSharePref(Long.toString(uploaded_note_id), tmpUploadIdList, willUploadCount, UnuploadCount);

                    if (!Utils.isMyServiceRunning(mContext, UploadService.class)) {
                        Log.i(TAG, "askUploadAGroupofImages , UploadService is not running. ");
                        startService(new Intent(getBaseContext(), UploadService.class).setAction(UploadService.ACTION_UPLOAD));
                    } else {
                    }
                } else {
                    List<NoteDataRecord> list = noteDataRecordDAO.getByPKId(uploaded_note_id);
                    if (!list.isEmpty()) {

                        NoteDataRecord t = list.get(0);
                        String str = getResources().getString(R.string.uploading_complete_bar, willUploadCount, UnuploadCount);
                        t.setText(str);
                        t.setNum_upload(willUploadCount);
                        t.setNum_skip_upload(UnuploadCount);
                        t.setIsUpload(1);
                        noteDataRecordDAO.updateOne(t);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<ImageDataRecord> deletelist = new ArrayList<>();

                                Log.i(TAG, " deleteImages is called");

                                List<NoteDataRecord> waittodeletelist = noteDataRecordDAO.getByPKId(uploaded_note_id);
                                if (!waittodeletelist.isEmpty()) {
                                    NoteDataRecord n = waittodeletelist.get(0);
                                    Log.i(TAG, " deleteImages : " + n.getStarttime() + " ~ " + n.getEndtime());
                                    List<ImageDataRecord> tmpdeletelist = imageDataRecordDAO.getAllByTime(n.getStarttime(), n.getEndtime());

                                    if (!tmpdeletelist.isEmpty()) {
                                        for (ImageDataRecord x : tmpdeletelist)
                                            deletelist.add(x);
                                    }
                                }


                                Log.i(TAG, " delete " + deletelist.size() + " Images ");
                                for (ImageDataRecord a : deletelist) {
                                    insertImageRecordToPreference(a, 4);


                                    File fdelete = new File(imgFile + "/" + a.getFileName());
                                    if (fdelete.exists()) {
                                        fdelete.delete();
                                    }
                                    imageDataRecordDAO.deleteOne(a);

                                }

                                Log.i(TAG, " deleteImages end");
                            }
                        }
                                // Starts the thread by calling the run() method in its Runnable
                        ).start();

                    }
                }

                finish();
            }
        }).setNegativeButton(btn_No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
        return;
    }

    private void insertImageRecordToPreference(ImageDataRecord a, int type) {
        Log.e(TAG, "insertImageRecordToPreference " + a.getFileName() + " " + a.getLabel());
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(a), type, System.currentTimeMillis()));
    }

    private void saveUploadImageToSharePref(String note_id, ArrayList<String> arrPackage, int nonPrivateCount, int privateCount) {
        ArrayList<String> notelist = Utils.getArrayList(sharedPrefs, gson, EXTRA_NOTE_ID_LIST);
        boolean exist = false;
        for (String noteId : notelist) {
            if (note_id.equalsIgnoreCase(noteId)) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            ArrayList<String> list = Utils.getArrayList(sharedPrefs, gson, EXTRA_ID_LIST);
            ArrayList<String> list_note = Utils.getArrayList(sharedPrefs, gson, EXTRA_ID_NOTE_LIST);

            notelist.add(note_id);


            int n = sharedPrefs.getInt(EXTRA_NON_PRIVATE, 0);
            int m = sharedPrefs.getInt(EXTRA_PRIVATE, 0);

            n = n + nonPrivateCount;
            m = m + privateCount;


            for (String id : arrPackage) {
                list.add(id);
                list_note.add(note_id);
            }

            String json = gson.toJson(list);
            String json_note1 = gson.toJson(list_note);
            String json_note = gson.toJson(notelist);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_ID_LIST, json);
            Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_ID_NOTE_LIST, json_note1);
            Utils.saveStringIntoSharePref(sharedPrefs, EXTRA_NOTE_ID_LIST, json_note);

            editor.putInt(EXTRA_NON_PRIVATE, n).putInt(EXTRA_PRIVATE, m).commit();

            Log.i(TAG, "saveUploadImageToSharePref : " + json);
        }
    }

    private void doublecheckUserLabelAction(String strQuestion) {
        current = System.currentTimeMillis();
        labelTimeString = ScheduleAndSampleManager.getTimeString(current);
        new AlertDialog.Builder(GrantUploadActivity.this).setMessage(getResources().getString(R.string.double_check, strQuestion)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                long[] idarray = new long[adapter.checkedList.size()];
                for (int k = 0; k < adapter.checkedList.size(); k++) {
                    String idxstr = adapter.checkedList.get(k);
                    int idx = Integer.parseInt(idxstr);
                    idarray[k] = Long.parseLong(mImageData.get(idx).get(RecyclerViewAdapter_Upload.ID));

                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                        //viewPosition = adapter.viewposition;
                        for (String idxstr : adapter.checkedList) {
                            int idx = Integer.parseInt(idxstr);
                            if (mImageData.get(idx).get(RecyclerViewAdapter_Upload.LABELED).equalsIgnoreCase("-1"))
                                alreadylabeled++;
                            mImageData.get(idx).put(RecyclerViewAdapter_Upload.LABELED, String.valueOf(lastLabel));
                            mImageData.get(idx).put(RecyclerViewAdapter_Upload.CHECKED, "0");

                            imageDataRecordDAO.updatePermissionById(Long.parseLong(mImageData.get(idx).get(RecyclerViewAdapter_Upload.ID)), lastLabel, current);
                        }
                        adapter.checkedList.clear();
                        //getSupportActionBar().setTitle("未標記：" + (mImageData.size() - alreadylabeled) + " 已標記：" + alreadylabeled) ;
                        setLabelCount();

                        //getImages();
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        runOnUiThread(new Runnable() {
                            public void run() {

                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();

                if (all_select_or_not) {
                    all_select_or_not = false;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            all_select.setImageResource(R.drawable.ic_circle_outline);
                        }
                    });
                }

            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent it) {
        super.onActivityResult(requestCode, resultCode, it);

        switch (requestCode) {
        }

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(spancount);
            size = Resources.getSystem().getDisplayMetrics().widthPixels / spancount;
        } else if (this.recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(spancount * 2);
            size = Resources.getSystem().getDisplayMetrics().widthPixels / (spancount * 2);
        }
        recyclerView.setLayoutManager(layoutManager);
    }
}
