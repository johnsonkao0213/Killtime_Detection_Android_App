package labelingStudy.nctu.boredom_detection.poweradapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.legacy.widget.Space;

import java.util.List;

/**
 *
 * Created by lin18 on 2017/4/18.
 */

public class DefaultAdapterDelegate<T> extends AdapterDelegate<T, PowerViewHolder> {

    @Override
    protected boolean isForViewType(@NonNull T item, int position) {
        return true;
    }

    @NonNull
    @Override
    protected PowerViewHolder onCreateViewHolder(ViewGroup parent) {
        return new PowerViewHolder(new Space(parent.getContext()));
    }

    @Override
    protected void onBindViewHolder(@NonNull T item, int position, @NonNull PowerViewHolder holder, @NonNull List<Object> payloads) {

    }

}
