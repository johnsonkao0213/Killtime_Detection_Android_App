package labelingStudy.nctu.boredom_detection.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import labelingStudy.nctu.boredom_detection.LabelActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.config.Constants;
import labelingStudy.nctu.boredom_detection.labelImage.LabelImage;

import static labelingStudy.nctu.boredom_detection.LabelActivity.size;
import static labelingStudy.nctu.boredom_detection.LabelActivity.spancount;


public class RecyclerViewAdapter extends ListAdapter<LabelImage, RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    public ArrayList<String> checkedList = new ArrayList<>();
    private Context mContext;
    private int checkCount = 0;
    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;
    int labelCount = Constants.labelColors.length;

    public RecyclerViewAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    private static final DiffUtil.ItemCallback<LabelImage> DIFF_CALLBACK = new DiffUtil.ItemCallback<LabelImage>() {
        @Override
        public boolean areItemsTheSame(@NonNull LabelImage oldItem, @NonNull LabelImage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LabelImage oldItem, @NonNull LabelImage newItem) {
            return oldItem.equals(newItem);
        }
    };
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.i(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);

        view.setOnLongClickListener(longClickListener);
        view.setOnClickListener(clickListener);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public void setLongClickListener(View.OnLongClickListener clickListener) {
        this.longClickListener = clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void selectRangeChange(int start, int end, boolean isSelected) {
//        if (start < 0 || end >= mImageData.size()) {
//            return;
//        }
        if (isSelected) {
            dataSelect(start, end);
        } else {
            dataUnselect(start, end);
        }
    }

    private void dataSelect(int start, int end) {
        for (int i = start; i <= end; i++) {
            if (!getItem(i).isCheck()) {
                getItem(i).setCheck(true);
                if(!checkedList.contains(String.valueOf(i))) {
                    checkedList.add(String.valueOf(i));
                    checkCount++;
                }
                notifyItemChanged(i);
            }
        }
    }

    private void dataUnselect(int start, int end) {
        for (int i = start; i <= end; i++) {
            if (getItem(i).isCheck()) {
                getItem(i).setCheck(false);
                if(checkedList.contains(String.valueOf(i))) {
                    checkedList.remove(String.valueOf(i));
                    checkCount--;
                }
                notifyItemChanged(i);
            }
        }
    }


    public void setSelected(int position) {
        if(!getItem(position).isCheck())
        {
            getItem(position).setCheck(true);
            if(!checkedList.contains(String.valueOf(position))) {
                checkedList.add(String.valueOf(position));
                checkCount++;
            }
        }else{
            getItem(position).setCheck(false);
            if(checkedList.contains(String.valueOf(position))) {
                checkedList.remove(String.valueOf(position));
                checkCount--;
            }
        }
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Log.i(TAG, "onBindViewHolder called " + position);
        LabelImage item = getItem(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        if(spancount > 2) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(item.getDir())
                    .apply(new RequestOptions()
                            .override(size, size)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(viewHolder.image);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(item.getDir())
                    .apply(new RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .skipMemoryCache(true)
                    .into(viewHolder.image);
        }

        viewHolder.name.setVisibility(View.VISIBLE);
        viewHolder.name.setText(item.getName().substring(8,10) + ":" + item.getName().substring(10,12));

        if(item.isCheck()) {
            viewHolder.label.setForeground(null);
            viewHolder.image.setAlpha(1.0f);
            viewHolder.check.setVisibility(View.VISIBLE);
            viewHolder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_checked_tran));
            viewHolder.name.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        else {
            if (item.getLabel() == LabelImage.LABEL_UNLABEL){ // NA
                viewHolder.label.setForeground(null);
                viewHolder.image.setAlpha(1.0f);
                viewHolder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.name.setTextColor(ContextCompat.getColor(mContext, R.color.primary_text));

            }else if(item.getLabel() == LabelImage.LABEL_PRIVATE){ // private
                viewHolder.label.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_checked_tran)));
                //holder.image.setAlpha(0.5f);
                viewHolder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_private));
                viewHolder.name.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            for (int i = 0; i < labelCount; i++) {
                if (item.getLabel() == i) {
                    int labelColor = ContextCompat.getColor(mContext, Constants.labelColors[i]);
                    int textColor = ContextCompat.getColor(mContext, R.color.white);
                    int foregroundColor = ContextCompat.getColor(mContext, R.color.color_checked_tran);

                    viewHolder.label.setForeground(new ColorDrawable(foregroundColor));
                    viewHolder.name.setBackgroundColor(labelColor);
                    viewHolder.name.setTextColor(textColor);
                }
                viewHolder.image.setAlpha(1.0f);
                viewHolder.check.setVisibility(View.INVISIBLE);
            }
        }

        //Log.i(TAG, "holder.image : " +holder.getLayoutPosition() + ", " + holder.image.getHeight() + ", " + holder.image.getWidth());


        if(LabelActivity.progressBar.getVisibility()==View.VISIBLE)
        {
            LabelActivity.progressBar.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        ImageView check;
        View label;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            check = itemView.findViewById(R.id.check);
            label = itemView.findViewById(R.id.label);
        }
    }
    public void submitList(ArrayList<LabelImage> newData) {
        Log.i(TAG, "submitList()");
        super.submitList(newData);

    }

}


