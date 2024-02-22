package labelingStudy.nctu.boredom_detection.timeline;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;

import butterknife.BindView;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.poweradapter.AdapterDelegate;
import labelingStudy.nctu.boredom_detection.poweradapter.PowerViewHolder;

/**
 *
 * Created by lin18 on 2017/8/23.
 */

public abstract class AbsOrderDelegate extends AdapterDelegate<LabelTask, PowerViewHolder> {

    @CallSuper
    @Override
    protected void onBindViewHolder(@NonNull LabelTask item, int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        final AbsChildViewHolder vh = (AbsChildViewHolder) holder;
        final int color = ContextCompat.getColor(context, item.isHead ? R.color.colorAccent : android.R.color.darker_gray);
        vh.time.setTextColor(color);
        vh.time.setText(item.time);
        vh.title.setTextColor(color);
        vh.title.setText(item.title);
        vh.subtitle.setTextColor(color);
        vh.subtitle.setText(item.subTitle);
        vh.subtitle.setVisibility(TextUtils.isEmpty(item.subTitle) ? View.GONE : View.VISIBLE);
    }

    static class AbsChildViewHolder extends BaseViewHolder {

        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subtitle)
        TextView subtitle;

        AbsChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            super(parent, layoutResId);
        }
    }

}
