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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import labelingStudy.nctu.boredom_detection.Data.appDatabase;
import labelingStudy.nctu.boredom_detection.adapters.LabelButtonPagerAdapter;
import labelingStudy.nctu.boredom_detection.adapters.RecyclerViewAdapter;
import labelingStudy.nctu.boredom_detection.dao.ImageDataRecordDAO;
import labelingStudy.nctu.boredom_detection.dao.PreferenceDataRecordDAO;
import labelingStudy.nctu.boredom_detection.fragment.LabelButtonFragment;
import labelingStudy.nctu.boredom_detection.labelImage.LabelImage;
import labelingStudy.nctu.boredom_detection.model.DataRecord.ImageDataRecord;
import labelingStudy.nctu.boredom_detection.model.DataRecord.PreferenceDataRecord;
import labelingStudy.nctu.boredom_detection.view.helper.DragSelectTouchListener;
import labelingStudy.nctu.boredom_detection.view.helper.SelectItemAnimator;
import labelingStudy.nctu.minuku.Utilities.ScheduleAndSampleManager;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;


public class LabelActivity extends AppCompatActivity implements LabelButtonFragment.OnButtonClickListener{

    private static final String TAG = "LabelActivity";
    public static ProgressBar progressBar;
    public static int viewPosition = 0;
    public static int size = 200;
    public static int spancount = 5;
    public static GridLayoutManager layoutManager;
    private final ArrayList<LabelImage> mImageData = new ArrayList<>();
    Context mContext;
    List<String> List_ID = new ArrayList<String>();
    long lastRefreshTime = -1;
    File imgFile;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    Toolbar toolbar;
    Button btn_mustdo, btn_emotions, btn_productivity, btn_time, btn_curiosity, btn_habits;
    Button[] buttons = {btn_mustdo, btn_emotions, btn_productivity, btn_time, btn_curiosity, btn_habits};
    int[] btnIds = {R.id.btn_mustdo, R.id.btn_emotions, R.id.btn_productivity, R.id.btn_time, R.id.btn_curiosity, R.id.btn_habits};
    int btnsCount;
    int alreadylabeled = 0;
    long start_time, end_time;
    TextView labeledCount, unlabeledCount, unlabelTile;
    ImageButton btlocationSearching;
    DateFormat simple_time = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat simple_day = new SimpleDateFormat("MM月dd日");
    String day = "";
    Gson gson = new Gson();
    long current = System.currentTimeMillis();
    ArrayList<Integer> checkedList_int = new ArrayList<>();
    int lastLabel = -1;
    String labelTimeString = ScheduleAndSampleManager.getTimeString(current);
    private ImageDataRecordDAO ImageDataRecordDAO;
    private DragSelectTouchListener touchListener;
    private PreferenceDataRecordDAO preferenceDataRecordDAO;
    private SharedPreferences sharedPrefs;
    private ScaleGestureDetector mScaleGestureDetector;
    private static final int MIN_SCALE_TIME_DELTA = 50;
    private static final int MIN_SPAN_FOR_SCALE = 200;
    private static final int MAX_SPAN_COUNT = 5;
    private static final int MIN_SPAN_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating LabelActivity");
        BoredomApp.setActivityVisibility(TAG, true);

        setContentView(R.layout.activity_label);
        mContext = this;

        start_time = getIntent().getExtras().getLong("start_time");
        end_time = getIntent().getExtras().getLong("end_time");
        day = simple_day.format(start_time);
        Log.i(TAG, "start: " + start_time + " , end: " + end_time);

        progressBar = findViewById(R.id.progressBar);
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Log.i(TAG, "android.os.Build.VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.setStatusBarColor(ContextCompat.getColor(LabelActivity.this, R.color.white));// set status background white
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);



        labeledCount = findViewById(R.id.labeledcount);
        unlabeledCount = findViewById(R.id.unlabeledcount);
        unlabelTile = findViewById(R.id.unlabeledtitle);
        btlocationSearching = findViewById(R.id.location_search);


        size = Resources.getSystem().getDisplayMetrics().widthPixels / 6;

        recyclerView = findViewById(R.id.recyclerView);


        layoutManager = new GridLayoutManager(this, spancount);

        ImageDataRecordDAO = appDatabase.getDatabase(this).imageDataRecordDAO();
        preferenceDataRecordDAO = appDatabase.getDatabase(this).preferenceDataRecordDAO();

        imgFile = new File(Environment.getExternalStoragePublicDirectory(Constants.PACKAGE_DIRECTORY_PATH), "Demo");


        sharedPrefs = getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        initRecyclerView();
        initLabelButtons();
        getImages();
        initTabLayout();
        //set scale gesture detector
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > MIN_SPAN_FOR_SCALE && detector.getTimeDelta() > MIN_SCALE_TIME_DELTA) {
                    if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                        Log.i(TAG, "onScale < -1, Scale down");
                        if (spancount < MAX_SPAN_COUNT) {
                            spancount++;
                            updateLayoutManagerBySpancount();
                            adapter.notifyItemRangeChanged(getCurrentItem(), calculateRange());
                            return true;
                        }
                    } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                        Log.i(TAG, "onScale > 1, Scale up");
                        if (spancount > MIN_SPAN_COUNT) {
                            spancount--;
                            updateLayoutManagerBySpancount();
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
                    viewPosition = getCurrentItem();
                    Log.e(TAG, "viewposition " + getCurrentItem());
                }
            }
        });

        btlocationSearching.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                scrollToFirstUnlabeledItem();
            }
        });
        unlabelTile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                scrollToFirstUnlabeledItem();
            }
        });


    }
    @Override
    public void onButtonClick(int tabIndex) {
        // Handle the button click in the activity
        // Reflect the change in the specified tab (tabIndex)
        // For example, update the tab text
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            tab.setText("Reflect");  // Update text or apply any other effect
        }
    }
    private void initTabLayout() {
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        LabelButtonPagerAdapter adapter = new LabelButtonPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Customize the tab text based on position
            switch (position) {
                case 0:
                    tab.setText("Button 1");
                    break;
                case 1:
                    tab.setText("Button 2");
                    break;
                case 2:
                    tab.setText("Button 3");
                    break;
                // Add more cases for additional tabs
                default:
                    break;
            }
        }).attach();
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
                        imageList = ImageDataRecordDAO.getAllIsReadyByTime(0, start_time, end_time);
                    }

                    if (imageList != null) {
                        ArrayList<LabelImage> tmp = new ArrayList<LabelImage>();
                        for (ImageDataRecord img : imageList) {
                            if (!List_ID.contains(String.valueOf(img.get_id()))) {
                                File f = new File(imgFile.toString() + "/" + img.getFileName());
                                if (f.exists()) {
                                    LabelImage newLabelImage = new LabelImage(
                                            String.valueOf(img.get_id()),
                                            (imgFile.toString() + "/" + img.getFileName()),
                                            img.getFileName()
                                            );

                                    newLabelImage.setLabel(img.getLabel());
                                    newLabelImage.setCheck(false);


                                    if (img.isLabeled()) {
                                        alreadylabeled++;
                                    }
                                    tmp.add(newLabelImage);
                                } else {
                                    ImageDataRecordDAO.deleteOne(img);
                                    long t = System.currentTimeMillis();
                                    img.setLabel(labelingStudy.nctu.boredom_detection.config.Constants.LABEL_PRIVATE);
                                    img.setLabeltime(t);
                                    img.setLabelTimeString(ScheduleAndSampleManager.getTimeString(t));
                                    img.setConfirmtime(t);
                                    img.setConfirmTimeString(ScheduleAndSampleManager.getTimeString(t));
                                    img.setDoing("NA");
                                    insertImageRecordToPreference(img, 4);
                                }
                            }
                        }

                        setLabelCount();

                        Collections.sort(tmp, new Comparator<LabelImage>() {
                            public int compare(LabelImage image1, LabelImage image2) {
                                return image1.getName().compareTo(image2.getName());
                            }
                        });

                        if (tmp.size() > 0) {
                            for (LabelImage img : tmp) {
                                mImageData.add(img);
                                List_ID.add(img.getId());
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
                                updateLabelImage();
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
                int count_unlabeled = ImageDataRecordDAO.getUnLabeledLabelByTime(start_time, end_time);
                int count_labeled = ImageDataRecordDAO.getLabeledLabelByTime(start_time, end_time);
                labeledCount.setText("" + count_labeled);
                unlabeledCount.setText("" + count_unlabeled);
            }
        });
    }

    private void initRecyclerView() {
        Log.i(TAG, "initRecyclerView: init recyclerview");

        Configuration orientation = new Configuration();
        updateLayoutManagerBySpancount();
        adapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);


        adapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int position = recyclerView.getChildAdapterPosition(v);
                adapter.setSelected(position);
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
        getMenuInflater().inflate(R.menu.menu_label, menu);
        MenuItem menu_item_cannot_classify = menu.findItem(R.id.menu_item_cannot_classify);


        SpannableString s = new SpannableString(getResources().getString(R.string.cannot_classify));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_private)), 0, s.length(), 0);
        menu_item_cannot_classify.setTitle(s);


        menu_item_cannot_classify.setVisible(true);

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

        if (itemId == R.id.menu_item_cannot_classify) {

            lastLabel = labelingStudy.nctu.boredom_detection.config.Constants.LABEL_PRIVATE;
            doublecheckUserLabelAction(getResources().getString(R.string.privacy_unclassifiable));

        }
        return super.onOptionsItemSelected(item);
    }

    private void doublecheckUserLabelAction(String strQuestion) {
        current = System.currentTimeMillis();
        labelTimeString = ScheduleAndSampleManager.getTimeString(current);
        new AlertDialog.Builder(LabelActivity.this).setMessage(getResources().getString(R.string.double_check, strQuestion)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                checkedList_int.clear();
                for (int k = 0; k < adapter.checkedList.size(); k++) {
                    String idxstr = adapter.checkedList.get(k);
                    int idx = Integer.parseInt(idxstr);
                    checkedList_int.add(idx);
                }
                adapter.checkedList.clear();

                for (int idx : checkedList_int) {
                    if (mImageData.get(idx).isLabeled()){
                        alreadylabeled++;
                    }

                    mImageData.get(idx).setLabel(lastLabel);
                    mImageData.get(idx).setCheck(false);

                    ImageDataRecordDAO.updateLabelById(Long.parseLong(mImageData.get(idx).getId()), lastLabel, current, labelTimeString);

                }

                setLabelCount();
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        updateLabelImage();
                    }
                });

            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    private void insertImageRecordToPreference(ImageDataRecord a, int type) {
        Log.e(TAG, "insertImageRecordToPreference " + a.getFileName() + " " + a.getLabel());
        preferenceDataRecordDAO.insert(new PreferenceDataRecord(gson.toJson(a), type, System.currentTimeMillis()));
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
    }

    private void updateLayoutManagerBySpancount() {
        Log.i(TAG, "updateLayoutManager()");
        if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(spancount);
            size = Resources.getSystem().getDisplayMetrics().widthPixels / spancount;
        } else if (recyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount(spancount * 2);

            size = Resources.getSystem().getDisplayMetrics().widthPixels / (spancount * 2);
        }

        recyclerView.setLayoutManager(layoutManager);
    }

    private void scrollToFirstUnlabeledItem() {
        Log.i(TAG, "scrollToFirstUnlabeledItem()");
        boolean finish = true;
        int count = 0;
        for (LabelImage img : mImageData) {
            if (!img.isLabeled()) {
                finish = false;
                break;
            }
            count++;
        }
        if (!finish) {
            viewPosition = count;
            Log.d(TAG, "count : " + count);

            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Define the Index we wish to scroll to.
                    recyclerView.setLayoutManager(layoutManager);
                    layoutManager.smoothScrollToPosition(recyclerView, new RecyclerView.State(), viewPosition);
                }
            }, 300);
        }
    }

    private void initLabelButtons(){
        android.util.Log.i(TAG, "initLabelButtons()");
        btnsCount = buttons.length;
        for (int i = 0; i < btnsCount; i++) {
            buttons[i] = findViewById(btnIds[i]);
            buttons[i].setBackgroundColor(ContextCompat.getColor(this, labelingStudy.nctu.boredom_detection.config.Constants.labelColors[i]));
        }
        for (int i = 0; i < btnsCount; i++) {
            final int labelID = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Do something in response to button click
                    lastLabel = labelID;
                    doublecheckUserLabelAction(buttons[labelID].getText().toString());
                }
            });
        }
    }

    private void updateLabelImage(){
        android.util.Log.i(TAG, "updateLabelImage: ");
        ArrayList<LabelImage> newList = new ArrayList<>();

        for (LabelImage labelImage : mImageData) {
            // Create a new LabelImage object using the copy constructor to deep copy
            LabelImage newLabelImage = new LabelImage(labelImage);
            newList.add(newLabelImage);
        }

        adapter.submitList(newList);
    }


}
