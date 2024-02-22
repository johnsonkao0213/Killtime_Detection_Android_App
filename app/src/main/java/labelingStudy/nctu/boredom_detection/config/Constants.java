package labelingStudy.nctu.boredom_detection.config;

import labelingStudy.nctu.boredom_detection.R;

public class Constants {

    public static final int[] labelColors = {
            R.color.color_mustdo,
            R.color.color_emotions,
            R.color.color_productivity,
            R.color.color_time,
            R.color.color_curiosity,
            R.color.color_habits,
            R.color.color_private
    };
    public static final int[] labelResourceStings = {
            R.string.intention_mustdo_short,
            R.string.intention_emotions_short,
            R.string.intention_productivity_short,
            R.string.intention_time_short,
            R.string.intention_curiosity_short,
            R.string.intention_habits_short
    };

    public static final int LABEL_KILL_TIME = 1;
    public static final int LABEL_NOT_KILL_TIME = 0;
    public static final int LABEL_KILL_TIME_BUSY = 3;
    public static final int LABEL_NOT_KILL_TIME_BUSY = 2;
    public static final int LABEL_PRIVATE = -2;
    public static final int LABEL_UNLABEL = -1;

    public static final String STR_KILL_TIME = "殺時間且有空看通知";
    public static final String STR_NOT_KILL_TIME = "非殺時間但可以看通知";
    public static final String STR_KILL_TIME_BUSY = "殺時間但沒空看通知";
    public static final String STR_NOT_KILL_TIME_BUSY = "非殺時間也無法看通知";
    public static final String STR_PRIVATE= "隱私或無法分類";

    public static final String STR_GRANT_UPLOAD= "可以上傳";
    public static final String STR_UNGRANT_UPLOAD= "不能上傳";
    public static final int LABEL_GRANT_UPLOAD = 1;
    public static final int LABEL_NOT_GRANT_UPLOAD = 0;


    public static final String NEWS_1 = "政治";
    public static final String NEWS_2 = "國際";
    public static final String NEWS_9 = "娛樂";
    public static final String NEWS_10 = "體育";
    public static final String NEWS_20= "科技";
    public static final String NEWS_17= "財經";
    public static final String NEWS_7= "地方";
    public static final String NEWS_21= "健康";


    public static final boolean pushToRealtimeDataBase_Minuku = true;
    public static final boolean pushToRealtimeDataBase_Accessibility = true;
    public static final boolean pushToRealtimeDataBase_NotiRemove = true;
    public static final boolean pushToRealtimeDataBase_ServiceStatus = true;
    public static final boolean pushToRealtimeDataBase_Image = true;
    public static final boolean pushToRealtimeDataBase_sys = true;
}
