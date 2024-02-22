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

public class LocationOrderDelegate extends AbsOrderDelegate {

    private TaskAdapter.OnOrderClickListener clickListener;

    public LocationOrderDelegate(TaskAdapter.OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull LabelTask item, int position) {
        return item instanceof LocationLabelTask;
    }

    @NonNull
    @Override
    protected PowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ChildViewHolder(parent, R.layout.task_location);
    }

    @Override
    protected void onBindViewHolder(@NonNull LabelTask item, final int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {
        super.onBindViewHolder(item, position, holder, payloads);
        final ChildViewHolder vh = (ChildViewHolder) holder;
        vh.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onLocationClick(position);
            }
        });
    }

    static class ChildViewHolder extends AbsChildViewHolder {

        @BindView(R.id.location)
        Button location;

        ChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            super(parent, layoutResId);
        }
    }

}
