package labelingStudy.nctu.boredom_detection.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Map;

import labelingStudy.nctu.boredom_detection.GrantUploadActivity;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.config.Constants;

import static labelingStudy.nctu.boredom_detection.GrantUploadActivity.size;
import static labelingStudy.nctu.boredom_detection.GrantUploadActivity.spancount;


public class RecyclerViewAdapter_Upload extends RecyclerView.Adapter<RecyclerViewAdapter_Upload.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter_Upload";


    public ArrayList<Map<String, String>> mImageData = new ArrayList<>();

    public ArrayList<String> checkedList = new ArrayList<>();

    public static final String BOREDOMLABELED = "boredomlabeled";
    public static final String LABELED = "labeled";
    public static final String IMAGE = "image";
    public static final String CHECKED = "checked";
    public static final String NAME = "name";
    public static final String ID = "id";



    private Context mContext;
    public int checkCount = 0;

    boolean isMoving= false;

    public int viewposition = 0;

    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;


    public RecyclerViewAdapter_Upload(Context Context, ArrayList<Map<String, String>> mImageData) {
        this.mImageData = mImageData;
        mContext = Context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_upload, parent, false);

        view.setOnLongClickListener(longClickListener);
        view.setOnClickListener(clickListener);
        ViewHolder holder = new ViewHolder(view);
        holder.image = (ImageView)view.findViewById(R.id.image_upload);
        holder.name = (TextView)view.findViewById(R.id.name_upload);
        holder.check = (ImageView)view.findViewById(R.id.check_upload);
        holder.label = (FrameLayout)view.findViewById(R.id.label_upload);

        return holder;
    }

    public void setLongClickListener(View.OnLongClickListener clickListener) {
        this.longClickListener = clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void selectRangeChange(int start, int end, boolean isSelected) {
        if (start < 0 || end >= mImageData.size()) {
            return;
        }
        if (isSelected) {
            dataSelect(start, end);
        } else {
            dataUnselect(start, end);
        }
    }

    private void dataSelect(int start, int end) {
        for (int i = start; i <= end; i++) {
            if (!mImageData.get(i).get(CHECKED).equalsIgnoreCase("1")) {
                mImageData.get(i).put(CHECKED,"1");
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
            if (mImageData.get(i).get(CHECKED).equalsIgnoreCase("1")) {
                mImageData.get(i).put(CHECKED,"0");
                if(checkedList.contains(String.valueOf(i))) {
                    checkedList.remove(String.valueOf(i));
                    checkCount--;
                }
                notifyItemChanged(i);
            }
        }
    }

    public void setSelected(int position, boolean selected) {
        if(selected == true)
        {
            if (!mImageData.get(position).get(CHECKED).equalsIgnoreCase("1")) {
                mImageData.get(position).put(CHECKED,"1");
                if(!checkedList.contains(String.valueOf(position))) {
                    checkedList.add(String.valueOf(position));
                    checkCount++;
                }
                notifyItemChanged(position);
            }
        }else{
            if (mImageData.get(position).get(CHECKED).equalsIgnoreCase("1")) {
                mImageData.get(position).put(CHECKED,"0");
                if(checkedList.contains(String.valueOf(position))) {
                    checkedList.remove(String.valueOf(position));
                    checkCount--;
                }
                notifyItemChanged(position);
            }

        }

    }

    public void setSelected(int position) {
        if(!mImageData.get(position).get(CHECKED).equalsIgnoreCase("1"))
        {
            mImageData.get(position).put(CHECKED,"1");
            if(!checkedList.contains(String.valueOf(position))) {
                checkedList.add(String.valueOf(position));
                checkCount++;
            }
        }else{
            mImageData.get(position).put(CHECKED,"0");
            if(checkedList.contains(String.valueOf(position))) {
                checkedList.remove(String.valueOf(position));
                checkCount--;
            }
        }
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //Log.i(TAG, "onBindViewHolder called " + position);


        if(spancount > 2) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(mImageData.get(position).get(IMAGE))
                    .apply(new RequestOptions()
                            .override(size, size)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(holder.image);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(mImageData.get(position).get(IMAGE))
                    .apply(new RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .skipMemoryCache(true)
                    .into(holder.image);
        }

        holder.name.setVisibility(View.VISIBLE);
        holder.name.setText(mImageData.get(position).get(NAME).substring(8,10) + ":" + mImageData.get(position).get(NAME).substring(10,12));


        if(mImageData.get(position).get(CHECKED).equalsIgnoreCase("1")) {
            holder.label.setForeground(null);
            holder.image.setAlpha(1.0f);
            holder.check.setVisibility(View.VISIBLE);
            holder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_checked_tran));
            holder.name.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
        else {
            if(mImageData.get(position).get(LABELED).equals(String.valueOf(Constants.LABEL_UNLABEL))){ // NA
                holder.label.setForeground(null);
                holder.image.setAlpha(1.0f);
                holder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                holder.name.setTextColor(ContextCompat.getColor(mContext, R.color.primary_text));

            }else if(mImageData.get(position).get(LABELED).equals(String.valueOf(Constants.LABEL_GRANT_UPLOAD))){ // kill time
                holder.label.setForeground(null);
                holder.image.setAlpha(1.0f);
                holder.check.setVisibility(View.VISIBLE);
                holder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_cn_upload));
                holder.name.setTextColor(ContextCompat.getColor(mContext, R.color.white));


            }else if(mImageData.get(position).get(LABELED).equals(String.valueOf(Constants.LABEL_NOT_GRANT_UPLOAD))){ // does not kill time
                holder.label.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_checked_tran)));
                //holder.image.setAlpha(0.5f);
                holder.name.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_cnt_upload));
                holder.name.setTextColor(ContextCompat.getColor(mContext, R.color.white));

            }
            holder.image.setAlpha(1.0f);
            holder.check.setVisibility(View.INVISIBLE);
        }

        //Log.i(TAG, "holder.image : " +holder.getLayoutPosition() + ", " + holder.image.getHeight() + ", " + holder.image.getWidth());


        if(GrantUploadActivity.progressBar.getVisibility()==View.VISIBLE)
        {
            GrantUploadActivity.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mImageData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        ImageView check;
        View label;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}


