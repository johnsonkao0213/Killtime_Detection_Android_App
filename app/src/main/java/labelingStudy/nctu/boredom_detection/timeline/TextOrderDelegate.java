package labelingStudy.nctu.boredom_detection.timeline;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.poweradapter.PowerViewHolder;


public class TextOrderDelegate extends AbsOrderDelegate {

    @Override
    protected boolean isForViewType(@NonNull LabelTask item, int position) {
        return item instanceof TextLabelTask;
    }

    @NonNull
    @Override
    protected PowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ChildViewHolder(parent, R.layout.task_text);
    }

    @Override
    protected void onBindViewHolder(@NonNull LabelTask item, int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {
        super.onBindViewHolder(item, position, holder, payloads);
    }

    static class ChildViewHolder extends AbsChildViewHolder {

        ChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            super(parent, layoutResId);
        }
    }

}
