package labelingStudy.nctu.boredom_detection.timeline;


import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

import butterknife.BindView;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.poweradapter.PowerViewHolder;

/**
 *
 * Created by lin18 on 2017/8/23.
 */

public class UploadOrderDelegate extends AbsOrderDelegate {

    private TaskAdapter.OnOrderClickListener clickListener;

    public UploadOrderDelegate(TaskAdapter.OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull LabelTask item, int position) {
        return item instanceof UploadLabelTask;
    }

    @NonNull
    @Override
    protected PowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ChildViewHolder(parent, R.layout.task_upload);
    }

    @Override
    protected void onBindViewHolder(@NonNull LabelTask item, final int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {
        super.onBindViewHolder(item, position, holder, payloads);
        final ChildViewHolder vh = (ChildViewHolder) holder;
        vh.tl_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onUploadClick(position);
            }
        });
        vh.review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onReviewClick(position);
            }
        });
    }

    static class ChildViewHolder extends AbsChildViewHolder {

        @BindView(R.id.tl_upload)
        Button tl_upload;

        @BindView(R.id.review)
        Button review;

        ChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            super(parent, layoutResId);
        }
    }

}
