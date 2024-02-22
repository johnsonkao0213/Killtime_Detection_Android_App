package labelingStudy.nctu.boredom_detection.view.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import labelingStudy.nctu.boredom_detection.R;

public class TimeLineDecoration extends RecyclerView.ItemDecoration {

    public static final int NORMAL = 0;
    public static final int BEGIN = 1;
    public static final int END = 2;
    public static final int END_FULL = 3;
    public static final int LINE = 4;
    public static final int LINE_FULL = 5;
    public static final int CUSTOM = 6;

    public static final int CHECKED = 7;
    public static final int UNCHECKED = 8;
    public static final int UPLOAD = 9;
    public static final int COMPLETE = 10;


    @IntDef({NORMAL, BEGIN, END, END_FULL, LINE, LINE_FULL, CUSTOM, CHECKED, UNCHECKED, UPLOAD, COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeLineType {
    }

    public static abstract class TimeLineAdapter implements TimeLineCallback {

        @Override
        public boolean isShowDivider(int position) {
            return false;
        }

        @Nullable
        @Override
        public Rect getRect(int position) {
            return null;
        }
    }

    public interface TimeLineCallback {

        boolean isShowDivider(int position);

        @Nullable
        Rect getRect(int position);

        @TimeLineType
        int getTimeLineType(int position);

    }

    private float dividerHeight;
    private float dividerPaddingLeft;
    private float dividerPaddingRight;
    @NonNull
    private Paint dividerPaint;

    private int lineWidth;
    @NonNull
    private Paint linePaint;
    private Paint GreenlinePaint;
    private Paint YellowlinePaint;
    private Paint BluelinePaint;
    private Paint BrightLinePaint;

    private int leftDistance;
    private int topDistance;

    @NonNull
    private Paint markerPaint;
    private int markerRadius;

    @Nullable
    private Drawable beginMarker;
    private int beginMarkerRadius;
    @Nullable
    private Drawable endMarker;
    private int endMarkerRadius;
    @Nullable
    private Drawable normalMarker;
    private int normalMarkerRadius;
    @Nullable
    private Drawable customMarker;
    private int customMarkerRadius;
    @Nullable
    private Drawable checkedMarker;
    private int checkedMarkerRadius;
    @Nullable
    private Drawable uncheckedMarker;
    private int uncheckedMarkerRadius;
    @Nullable
    private Drawable uploadMarker;
    private int uploadMarkerRadius;
    @Nullable
    private Drawable completeMarker;
    private int completeMarkerRadius;

    private final Context context;
    @Nullable
    private TimeLineCallback callback;

    public TimeLineDecoration(@NonNull Context context) {
        this.context = context;

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(getColor(android.R.color.black));

        GreenlinePaint = new Paint();
        GreenlinePaint.setAntiAlias(true);
        GreenlinePaint.setStyle(Paint.Style.FILL);
        GreenlinePaint.setColor(getColor(R.color.color_close));


        YellowlinePaint = new Paint();
        YellowlinePaint.setAntiAlias(true);
        YellowlinePaint.setStyle(Paint.Style.FILL);
        YellowlinePaint.setColor(getColor(R.color.color_open));

        BluelinePaint = new Paint();
        BluelinePaint.setAntiAlias(true);
        BluelinePaint.setStyle(Paint.Style.FILL);
        BluelinePaint.setColor(getColor(R.color.color_upload));

        BrightLinePaint = new Paint();
        BrightLinePaint.setAntiAlias(true);
        BrightLinePaint.setStyle(Paint.Style.FILL);
        BrightLinePaint.setColor(getColor(R.color.color_complete));

        markerPaint = new Paint();
        markerPaint.setAntiAlias(true);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setColor(context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName()));
    }

    public TimeLineDecoration setDividerHeight(@FloatRange(from = 0.0f) float height) {
        this.dividerHeight = dp2px(height);
        return this;
    }

    public TimeLineDecoration setDividerColor(@ColorRes int color) {
        dividerPaint.setColor(getColor(color));
        return this;
    }

    public TimeLineDecoration setDividerPaddingLeft(@FloatRange(from = 0.0f) float padding) {
        this.dividerPaddingLeft = dp2px(padding);
        return this;
    }

    public TimeLineDecoration setDividerPaddingRight(@FloatRange(from = 0.0f) float padding) {
        this.dividerPaddingRight = dp2px(padding);
        return this;
    }

    public TimeLineDecoration setLineColor(@ColorRes int color) {
        linePaint.setColor(getColor(color));
        return this;
    }

    public TimeLineDecoration setLineWidth(@FloatRange(from = 0.0f) float width) {
        this.lineWidth = dp2px(width);
        return this;
    }

    public TimeLineDecoration setLeftDistance(@IntRange(from = 0) int distance) {
        this.leftDistance = dp2px(distance);
        return this;
    }

    public TimeLineDecoration setTopDistance(@IntRange(from = 0) int distance) {
        this.topDistance = dp2px(distance);
        return this;
    }

    public TimeLineDecoration setMarkerColor(@ColorRes int color) {
        markerPaint.setColor(getColor(color));
        return this;
    }

    public TimeLineDecoration setMarkerRadius(@FloatRange(from = 0.0f) float radius) {
        this.markerRadius = dp2px(radius);
        return this;
    }

    public TimeLineDecoration setBeginMarkerRadius(@FloatRange(from = 0.0f) float radius) {
        beginMarkerRadius = dp2px(radius);
        return this;
    }

    public TimeLineDecoration setBeginMarker(@DrawableRes int resId) {
        return setBeginMarker(getDrawable(resId));
    }

    public TimeLineDecoration setBeginMarker(@NonNull Drawable drawable) {
        beginMarker = drawable;
        beginMarkerRadius = beginMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setEndMarkerRadius(@FloatRange(from = 0.0f) float radius) {
        endMarkerRadius = dp2px(radius);
        return this;
    }

    public TimeLineDecoration setEndMarker(@DrawableRes int resId) {
        return setEndMarker(getDrawable(resId));
    }

    public TimeLineDecoration setEndMarker(@NonNull Drawable drawable) {
        endMarker = drawable;
        endMarkerRadius = endMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setNormalMarkerRadius(@FloatRange(from = 0.0f) float radius) {
        normalMarkerRadius = dp2px(radius);
        return this;
    }

    public TimeLineDecoration setNormalMarker(@DrawableRes int resId) {
        return setNormalMarker(getDrawable(resId));
    }

    public TimeLineDecoration setNormalMarker(@NonNull Drawable drawable) {
        normalMarker = drawable;
        normalMarkerRadius = normalMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setCustomMarker(@DrawableRes int resId) {
        return setCustomMarker(getDrawable(resId));
    }

    public TimeLineDecoration setCustomMarker(@NonNull Drawable drawable) {
        customMarker = drawable;
        customMarkerRadius = customMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setCheckedMarker(@DrawableRes int resId) {
        return setCheckedMarker(getDrawable(resId));
    }

    public TimeLineDecoration setCheckedMarker(@NonNull Drawable drawable) {
        checkedMarker = drawable;
        checkedMarkerRadius = checkedMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setUncheckedMarker(@DrawableRes int resId) {
        return setUncheckedMarker(getDrawable(resId));
    }

    public TimeLineDecoration setUncheckedMarker(@NonNull Drawable drawable) {
        uncheckedMarker = drawable;
        uncheckedMarkerRadius = uncheckedMarker.getIntrinsicWidth() / 2;
        return this;
    }
    public TimeLineDecoration setUploadMarker(@DrawableRes int resId) {
        return setUploadMarker(getDrawable(resId));
    }

    public TimeLineDecoration setUploadMarker(@NonNull Drawable drawable) {
        uploadMarker = drawable;
        uploadMarkerRadius = uploadMarker.getIntrinsicWidth() / 2;
        return this;
    }
    public TimeLineDecoration setCompleteMarker(@DrawableRes int resId) {
        return setCompleteMarker(getDrawable(resId));
    }

    public TimeLineDecoration setCompleteMarker(@NonNull Drawable drawable) {
        completeMarker = drawable;
        completeMarkerRadius = completeMarker.getIntrinsicWidth() / 2;
        return this;
    }

    public TimeLineDecoration setCallback(@Nullable TimeLineCallback callback) {
        this.callback = callback;
        return this;
    }

    private int dp2px(@FloatRange(from = 0.0f) float dpValue) {
        return dp2px(context, dpValue);
    }

    public static int dp2px(Context context, @FloatRange(from = 0.0f) float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    @ColorInt
    private int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (callback == null) return;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(view);
            if (callback.isShowDivider(position))
                drawDivider(canvas, view, parent.getRight());
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (callback == null) {
            outRect.set(0, 0, 0, 0);
        } else {
            final Rect rect = callback.getRect(parent.getChildAdapterPosition(view));
            outRect.set(rect == null ? new Rect() : rect);
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (callback == null) return;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(view);

            final Rect rect = callback.getRect(position);
            final int bottomMargin = ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).bottomMargin + (rect == null ? 0 : rect.bottom);
            final int topMargin = ((ViewGroup.MarginLayoutParams)view.getLayoutParams()).topMargin + (rect == null ? 0 : rect.top);

            switch (callback.getTimeLineType(position)) {
                case NORMAL:
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, normalMarker, normalMarkerRadius);
                    break;

                case BEGIN:
                    drawLine(canvas, view.getTop() + topDistance + beginMarkerRadius, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, beginMarker, beginMarkerRadius);
                    break;

                case END:
                    drawLine(canvas, view.getTop() - topMargin, view.getTop() + bottomMargin + topDistance + (endMarker == null ? markerRadius : endMarkerRadius));
                    drawMarkerOrCircle(canvas, view, endMarker, endMarkerRadius);
                    break;

                case END_FULL:
                    drawLine(canvas, view.getTop() - topMargin, parent.getBottom());
                    drawMarkerOrCircle(canvas, view, endMarker, endMarkerRadius);
                    break;

                case LINE:
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    break;

                case LINE_FULL:
                    drawLine(canvas, view.getTop() - topMargin, parent.getBottom());
                    break;

                case CUSTOM:
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, customMarker, customMarkerRadius);
                    break;

                case CHECKED:
                    drawCloseRect(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, checkedMarker, checkedMarkerRadius);
                    break;
                case UNCHECKED:
                    drawOpenRect(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, uncheckedMarker, uncheckedMarkerRadius);
                    break;
                case UPLOAD:
                    drawUploadRect(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, uploadMarker, uploadMarkerRadius);
                    break;
                case COMPLETE:
                    drawCompleteRect(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawLine(canvas, view.getTop() - topMargin, view.getBottom() + bottomMargin);
                    drawMarkerOrCircle(canvas, view, completeMarker, completeMarkerRadius);
                    break;
            }
        }
    }

    private void drawDivider(Canvas canvas, View view, int right) {
        canvas.drawRect(dividerPaddingLeft, view.getBottom() - dividerHeight,
                right - dividerPaddingRight, view.getBottom(), dividerPaint);
    }

    private void drawLine(Canvas canvas, float top, float bottom) {
        canvas.drawRect(leftDistance, top, leftDistance + lineWidth, bottom, linePaint);
    }

    private void drawOpenRect(Canvas canvas, float top, float bottom) {
        canvas.drawRect(leftDistance-10, top, leftDistance + lineWidth + 10, bottom, YellowlinePaint);
    }
    private void drawCloseRect(Canvas canvas, float top, float bottom) {
        canvas.drawRect(leftDistance-10, top, leftDistance + lineWidth + 10, bottom, GreenlinePaint);
    }
    private void drawUploadRect(Canvas canvas, float top, float bottom) {
        canvas.drawRect(leftDistance-10, top, leftDistance + lineWidth + 10, bottom, BluelinePaint);
    }
    private void drawCompleteRect(Canvas canvas, float top, float bottom) {
        canvas.drawRect(leftDistance-10, top, leftDistance + lineWidth + 10, bottom, BrightLinePaint);
    }
    private void drawMarkerOrCircle(Canvas canvas, View view, Drawable marker, int radius) {
        if (marker != null)
            drawMarker(canvas, view, marker, radius);
        else
            drawCircle(canvas, view);
    }

    private void drawCircle(Canvas canvas, View view) {
        canvas.drawCircle(leftDistance + lineWidth / 2, view.getTop() + topDistance + markerRadius * 2,
                markerRadius, markerPaint);
    }

    private void drawMarker(Canvas canvas, View view, Drawable marker, int radius) {
        marker.setBounds(leftDistance + lineWidth / 2 - radius, view.getTop() + topDistance,
                leftDistance + lineWidth / 2 + radius, view.getTop() + topDistance + radius * 2);
        marker.draw(canvas);
    }

}
