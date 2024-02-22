package labelingStudy.nctu.boredom_detection.timeline;

import androidx.annotation.Nullable;

import labelingStudy.nctu.boredom_detection.poweradapter.DefaultAdapterDelegate;
import labelingStudy.nctu.boredom_detection.poweradapter.MultiAdapter;
import labelingStudy.nctu.boredom_detection.poweradapter.PowerViewHolder;


public class TaskAdapter extends MultiAdapter<LabelTask, PowerViewHolder> {

    public TaskAdapter(@Nullable Object listener, @Nullable OnOrderClickListener clickListener) {
        super(listener);
        delegatesManager.addDelegate(new TextOrderDelegate());
        delegatesManager.addDelegate(new InfoOrderDelegate(clickListener));
        delegatesManager.addDelegate(new LocationOrderDelegate(clickListener));
        delegatesManager.addDelegate(new UploadOrderDelegate(clickListener));
        delegatesManager.setFallbackDelegate(new DefaultAdapterDelegate<LabelTask>());
    }

    public interface OnOrderClickListener {
        void onLookClick(int position);

        void onCallClick(int position);

        void onLocationClick(int position);

        void onUploadClick(int position);

        void onReviewClick(int position);
    }
}


