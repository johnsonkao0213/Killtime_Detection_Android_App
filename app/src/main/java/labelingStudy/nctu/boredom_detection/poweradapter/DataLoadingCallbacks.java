package labelingStudy.nctu.boredom_detection.poweradapter;

/**
 *
 * Created by lin18 on 2017/4/19.
 */

public interface DataLoadingCallbacks {

    boolean isDataLoading();
    void dataStartedLoading();
    void dataFinishedLoading();

}
