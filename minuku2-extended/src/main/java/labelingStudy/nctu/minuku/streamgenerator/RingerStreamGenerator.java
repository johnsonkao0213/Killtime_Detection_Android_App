package labelingStudy.nctu.minuku.streamgenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import labelingStudy.nctu.minuku.Data.appDatabase;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.dao.RingerDataRecordDAO;
import labelingStudy.nctu.minuku.manager.MinukuStreamManager;
import labelingStudy.nctu.minuku.model.DataRecord.RingerDataRecord;
import labelingStudy.nctu.minuku.stream.RingerStream;
import labelingStudy.nctu.minukucore.exception.StreamAlreadyExistsException;
import labelingStudy.nctu.minukucore.exception.StreamNotFoundException;
import labelingStudy.nctu.minukucore.stream.Stream;

/**
 * Created by Lawrence on 2017/8/22.
 */

public class RingerStreamGenerator extends AndroidStreamGenerator<RingerDataRecord> {

    private String TAG = "RingerStreamGenerator";
    private final boolean debugMode = false; // 20200531 add by Lesley for print all data in detail
    private RingerStream mStream;
    private RingerDataRecordDAO ringerDataRecordDAO;
    //audio and ringer
    public static final String RINGER_MODE_NORMAL = "Normal";
    public static final String RINGER_MODE_VIBRATE = "Vibrate";
    public static final String RINGER_MODE_SILENT = "Silent";

    public static final String MODE_CURRENT = "Current";
    public static final String MODE_INVALID = "Invalid";
    public static final String MODE_IN_CALL = "InCall";
    public static final String MODE_IN_COMMUNICATION = "InCommunicaiton";
    public static final String MODE_NORMAL = "Normal";
    public static final String MODE_RINGTONE = "Ringtone";

    //after api 23
    public static AudioDeviceInfo[] mAllAudioDevices;

    private String mRingerMode = "NA";
    private String mAudioMode = "NA";
    private int mStreamVolumeMusic = -9999;
    private int mStreamVolumeNotification = -9999;
    private int mStreamVolumeRing = -9999;
    private int mStreamVolumeVoicecall = -9999;
    private int mStreamVolumeSystem = -9999;

    private static String sRingerMode = "NA";
    private static String sAudioMode = "NA";
    private static int sStreamVolumeMusic = -9999;
    private static int sStreamVolumeNotification = -9999;
    private static int sStreamVolumeRing = -9999;
    private static int sStreamVolumeVoicecall = -9999;
    private static int sStreamVolumeSystem = -9999;
//    private static int mStreamVolumeDTMF = -9999;
    private static long id;

    public static long getId() { return id; }
    private static AudioManager mAudioManager;

    private SharedPreferences sharedPrefs;

    public static int mainThreadUpdateFrequencyInSeconds = 10;
    public static long mainThreadUpdateFrequencyInMilliseconds = mainThreadUpdateFrequencyInSeconds *Constants.MILLISECONDS_PER_SECOND;

    private Context mContext;

    private static Handler mMainThread;

    public RingerStreamGenerator (Context applicationContext) {
        super(applicationContext);

        this.mContext = applicationContext;
        this.mStream = new RingerStream(Constants.DEFAULT_QUEUE_SIZE);
        ringerDataRecordDAO = appDatabase.getDatabase(applicationContext).ringerDataRecordDao();

        sharedPrefs = mContext.getSharedPreferences(Constants.sharedPrefString,Context.MODE_PRIVATE);

        mAudioManager = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);

        this.register();
    }
    @Override
    public void register() {
        Log.d(TAG, "Registring with StreamManage");

        try {
            MinukuStreamManager.getInstance().register(mStream, RingerDataRecord.class, this);
        } catch (StreamNotFoundException streamNotFoundException) {
            Log.e(TAG, "One of the streams on which" +
                    "RingerDataRecord/RingerStream depends in not found.");
        } catch (StreamAlreadyExistsException streamAlreadyExsistsException) {
            Log.e(TAG, "Another stream which provides" +
                    " RingerDataRecord/RingerStream is already registered.");
        }
    }

    @Override
    public Stream<RingerDataRecord> generateNewStream() {
        return mStream;
    }

    @Override
    public boolean updateStream() {

        Log.d(TAG, "updateStream called");

//        int session_id = SessionManager.getOngoingSessionId();

        int session_id = sharedPrefs.getInt("ongoingSessionid", Constants.INVALID_INT_VALUE);

        //TODO get service data
        RingerDataRecord ringerDataRecord = new RingerDataRecord(mRingerMode,mAudioMode,mStreamVolumeMusic
                ,mStreamVolumeNotification,mStreamVolumeRing,mStreamVolumeVoicecall,mStreamVolumeSystem);
        mStream.add(ringerDataRecord);
        Log.d(TAG, "Ringer to be sent to event bus" + ringerDataRecord);
        // also post an event.
        EventBus.getDefault().post(ringerDataRecord);
        try {
//            appDatabase db;
//            db = Room.databaseBuilder(mContext,appDatabase.class,"dataCollection")
//                    .allowMainThreadQueries()
//                    .build();
            id = ringerDataRecordDAO.insertAll(ringerDataRecord);
            if(debugMode) {
                List<RingerDataRecord> ringerDataRecords = ringerDataRecordDAO.getAll();
                for (RingerDataRecord r : ringerDataRecords) {
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " RingerMode: " + r.getRingerMode());
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " AudioMode: " + r.getAudioMode());
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " StreamVolumeMusic: " + String.valueOf(r.getStreamVolumeMusic()));
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " StreamVolumeNotification: " + String.valueOf(r.getStreamVolumeNotification()));
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " StreamVolumeRing: " + String.valueOf(r.getStreamVolumeRing()));
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " StreamVolumeVoicecall: " + String.valueOf(r.getStreamVolumeVoicecall()));
                    labelingStudy.nctu.minuku.logger.Log.e(TAG, " StreamVolumeSystem: " + String.valueOf(r.getStreamVolumeSystem()));
                }
            }
        } catch (NullPointerException e){ //Sometimes no data is normal
            e.printStackTrace();
            return false;
        }

        sAudioMode = mAudioMode;
        sRingerMode = mRingerMode;
        sStreamVolumeMusic = mStreamVolumeMusic;
        sStreamVolumeNotification = mStreamVolumeNotification;
        sStreamVolumeRing = mStreamVolumeRing;
        sStreamVolumeSystem = mStreamVolumeSystem;
        sStreamVolumeVoicecall = mStreamVolumeVoicecall;

        return true;
    }

    @Override
    public long getUpdateFrequency() {
        return 1;
    }

    @Override
    public void sendStateChangeEvent() {

    }

    @Override
    public void onStreamRegistration() {
//        new Threading().start();
        Log.e(TAG,"onStreamRegistration");

        runPhoneStatusMainThread();

    }

    public void runPhoneStatusMainThread(){

        Log.d(TAG, "runPhoneStatusMainThread") ;

        mMainThread = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                getAudioRingerUpdate();

                mMainThread.postDelayed(this, mainThreadUpdateFrequencyInMilliseconds);

            }
        };

        mMainThread.post(runnable);
    }

    private void getAudioRingerUpdate() {
        if (mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)
            mRingerMode = RINGER_MODE_NORMAL;
        else if (mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE)
            mRingerMode = RINGER_MODE_VIBRATE;
        else if (mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT)
            mRingerMode = RINGER_MODE_SILENT;

        int mode = mAudioManager.getMode();
//        Log.d(LOG_TAG, "[getAudioRingerUpdate] ringer mode: " + mRingerMode + " mode: " + mode);

        mStreamVolumeMusic= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mStreamVolumeNotification= mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        mStreamVolumeRing= mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mStreamVolumeVoicecall = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        mStreamVolumeSystem= mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

        mAudioMode = getAudioMode(mAudioManager.getMode());

        Log.d(TAG,"mRingerMode : "+ mRingerMode +" mAudioMode : "+ mAudioMode+" mStreamVolumeMusic : "+ mStreamVolumeMusic
                +" mStreamVolumeNotification : "+ mStreamVolumeNotification+" mStreamVolumeRing : "+ mStreamVolumeRing
                +" mStreamVolumeVoicecall : "+ mStreamVolumeVoicecall +" mStreamVolumeSystem : "+ mStreamVolumeSystem);

        //android 6
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            mAllAudioDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        }


        mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);

    }

    public String getAudioMode(int mode) {

        if (mode==AudioManager.MODE_CURRENT)
            return MODE_CURRENT;
        else if (mode==AudioManager.MODE_IN_CALL)
            return MODE_IN_CALL;
        else if (mode==AudioManager.MODE_IN_COMMUNICATION)
            return MODE_IN_COMMUNICATION;
        else if (mode==AudioManager.MODE_INVALID)
            return MODE_INVALID;

        else if (mode==AudioManager.MODE_NORMAL)
            return MODE_NORMAL;

        else if (mode==AudioManager.MODE_RINGTONE)
            return MODE_RINGTONE;
        else
            return "NA";
    }

    @Override
    public void offer(RingerDataRecord ringerdataRecord) {
        mStream.add(ringerdataRecord);
    }

    public static String getmRingerMode() {
        return sRingerMode;
    }

    public static String getmAudioMode() {
        return sAudioMode;
    }

    public static int getmStreamVolumeMusic() {
        return sStreamVolumeMusic;
    }

    public static int getmStreamVolumeNotification() {
        return sStreamVolumeNotification;
    }

    public static int getmStreamVolumeRing() {
        return sStreamVolumeRing;
    }

    public static int getmStreamVolumeSystem() {
        return sStreamVolumeSystem;
    }

    public static int getmStreamVolumeVoicecall() {
        return sStreamVolumeVoicecall;
    }
}
