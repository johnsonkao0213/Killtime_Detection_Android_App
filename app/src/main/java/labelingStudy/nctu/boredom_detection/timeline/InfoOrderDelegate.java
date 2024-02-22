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

public class InfoOrderDelegate extends AbsOrderDelegate {

    private TaskAdapter.OnOrderClickListener clickListener;

    public InfoOrderDelegate(TaskAdapter.OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected boolean isForViewType(@NonNull LabelTask item, int position) {
        return item instanceof InfoLabelTask;
    }

    @NonNull
    @Override
    protected PowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return new ChildViewHolder(parent, R.layout.task_info);
    }

    @Override
    protected void onBindViewHolder(@NonNull LabelTask item, final int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {
        super.onBindViewHolder(item, position, holder, payloads);
        final ChildViewHolder vh = (ChildViewHolder) holder;
        vh.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onLookClick(position);
            }
        });
        vh.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCallClick(position);
            }
        });
    }

    static class ChildViewHolder extends AbsChildViewHolder {

        @BindView(R.id.look)
        Button look;
        @BindView(R.id.call)
        Button call;

        ChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            super(parent, layoutResId);
        }
    }

}
