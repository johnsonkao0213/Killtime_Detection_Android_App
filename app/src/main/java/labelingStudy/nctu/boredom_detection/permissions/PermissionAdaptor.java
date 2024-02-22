package labelingStudy.nctu.boredom_detection.permissions;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import labelingStudy.nctu.boredom_detection.databinding.PermissionItemBinding;

public class PermissionAdaptor extends RecyclerView.Adapter<PermissionAdaptor.ViewHolder> {
    private final List<PermissionItem> localDataSet;
    private final PermissionSelectListener listener;


    public PermissionAdaptor(List<PermissionItem> items, PermissionSelectListener listener) {
        this.localDataSet = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final PermissionItemBinding binding;

        public ViewHolder(PermissionItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        PermissionItemBinding view = PermissionItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.binding.textView2.setText(localDataSet.get(position).description);
        viewHolder.binding.checkBox5.setChecked(localDataSet.get(position).permission);
        viewHolder.binding.checkBox5.setEnabled(false);
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClicked(localDataSet.get(position)));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
