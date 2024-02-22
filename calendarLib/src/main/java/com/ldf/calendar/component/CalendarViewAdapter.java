package com.ldf.calendar.component;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.ldf.calendar.interf.OnAdapterSelectListener;
import com.ldf.calendar.interf.IDayRenderer;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.Utils;
import com.ldf.calendar.view.MonthPager;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarViewAdapter extends PagerAdapter {

    private static CalendarDate date = new CalendarDate();
    private ArrayList<CalendarView> calendarViews = new ArrayList<>();
    private int currentPosition = MonthPager.CURRENT_DAY_INDEX;
    private CalendarAttr.CalendarType calendarType = CalendarAttr.CalendarType.WEEK;
    private int rowCount = 0;
    private CalendarDate seedDate;
    private OnCalendarTypeChanged onCalendarTypeChangedListener;
    //周排列方式 1：代表周日显示为本周的第一天
    //0:代表周一显示为本周的第一天
    private CalendarAttr.WeekArrayType weekArrayType = CalendarAttr.WeekArrayType.Monday;

    public CalendarViewAdapter(Context context,
                               OnSelectDateListener onSelectDateListener,
                               CalendarAttr.WeekArrayType weekArrayType,
                               IDayRenderer dayView) {
        super();
        this.weekArrayType = weekArrayType;
        init(context, onSelectDateListener);
        setCustomDayRenderer(dayView);
    }

    public static void saveSelectedDate(CalendarDate calendarDate) {
        date = calendarDate;
    }

    public static CalendarDate loadSelectedDate() {
        return date;
    }

    private void init(Context context, OnSelectDateListener onSelectDateListener) {
        saveSelectedDate(new CalendarDate());
        //初始化的种子日期为今天
        seedDate = new CalendarDate();
        for (int i = 0; i < 3; i++) {
            CalendarAttr calendarAttr = new CalendarAttr();
            calendarAttr.setCalendarType(CalendarAttr.CalendarType.WEEK);
            calendarAttr.setWeekArrayType(weekArrayType);
            CalendarView calendarView = new CalendarView(context, onSelectDateListener, calendarAttr);
            calendarView.setOnAdapterSelectListener(new OnAdapterSelectListener() {
                @Override
                public void cancelSelectState() {
                    cancelOtherSelectState();
                }

                @Override
                public void updateSelectState() {
                    invalidateCurrentCalendar();
                }
            });
            calendarViews.add(calendarView);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Log.d("ldf", "setPrimaryItem "+ position);
        this.currentPosition = position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("ldf", "instantiateItem "+ position);
        if (position < 2) {
            return null;
        }
        CalendarView calendarView = calendarViews.get(position % calendarViews.size());
        if (calendarType == CalendarAttr.CalendarType.MONTH) {
            CalendarDate current = seedDate.modifyMonth(position - MonthPager.CURRENT_DAY_INDEX);
            current.setDay(1);//每月的种子日期都是1号
            calendarView.showDate(current);
        } else {
            CalendarDate current = seedDate.modifyWeek(position - MonthPager.CURRENT_DAY_INDEX);
            if (weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                calendarView.showDate(Utils.getSaturday(current));
            } else {
                calendarView.showDate(Utils.getSunday(current));
            }//每周的种子日期为这一周的最后一天
            calendarView.updateWeek(rowCount);
        }
        if (container.getChildCount() == calendarViews.size()) {
            container.removeView(calendarViews.get(position % 3));
        }
        if (container.getChildCount() < calendarViews.size()) {
            container.addView(calendarView, 0);
        } else {
            container.addView(calendarView, position % 3);
        }
        return calendarView;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container);
    }

    public ArrayList<CalendarView> getPagers() {
        return calendarViews;
    }

    public CalendarDate getFirstVisibleDate() {
        return calendarViews.get(currentPosition % 3).getFirstDate();
    }

    public CalendarDate getLastVisibleDate() {
        return calendarViews.get(currentPosition % 3).getLastDate();
    }

    public void cancelOtherSelectState() {
        for (int i = 0; i < calendarViews.size(); i++) {
            CalendarView calendarView = calendarViews.get(i);
            calendarView.cancelSelectState();
        }
    }

    public void invalidateCurrentCalendar() {
        for (int i = 0; i < calendarViews.size(); i++) {
            CalendarView calendarView = calendarViews.get(i);
            calendarView.update();
            if (calendarView.getCalendarType() == CalendarAttr.CalendarType.WEEK) {
                calendarView.updateWeek(rowCount);
            }
        }
    }

    public void setMarkData(HashMap<String, String> markData) {
        Utils.setMarkData(markData);
        notifyDataChanged();
    }

    public HashMap<String, String> getMarkData() {
        return Utils.loadMarkData();
    }

    public void switchToMonth() {
        if (calendarViews != null && calendarViews.size() > 0 && calendarType != CalendarAttr.CalendarType.MONTH) {
            if (onCalendarTypeChangedListener != null) {
                onCalendarTypeChangedListener.onCalendarTypeChanged(CalendarAttr.CalendarType.MONTH);
            }
            calendarType = CalendarAttr.CalendarType.MONTH;
            MonthPager.CURRENT_DAY_INDEX = currentPosition;
            CalendarView v = calendarViews.get(currentPosition % 3);//0
            seedDate = v.getSeedDate();

            CalendarView v1 = calendarViews.get(currentPosition % 3);//0
            v1.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            v1.showDate(seedDate);

            CalendarView v2 = calendarViews.get((currentPosition - 1) % 3);//2
            v2.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            CalendarDate last = seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);

            CalendarView v3 = calendarViews.get((currentPosition + 1) % 3);//1
            v3.switchCalendarType(CalendarAttr.CalendarType.MONTH);
            CalendarDate next = seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }
    }

    public void switchToWeek(int rowIndex) {
        rowCount = rowIndex;
        if (calendarViews != null && calendarViews.size() > 0 && calendarType != CalendarAttr.CalendarType.WEEK) {
            if (onCalendarTypeChangedListener != null) {
                onCalendarTypeChangedListener.onCalendarTypeChanged(CalendarAttr.CalendarType.WEEK);
            }
            calendarType = CalendarAttr.CalendarType.WEEK;
            MonthPager.CURRENT_DAY_INDEX = currentPosition;
            CalendarView v = calendarViews.get(currentPosition % 3);
            seedDate = v.getSeedDate();

            rowCount = v.getSelectedRowIndex();

            CalendarView v1 = calendarViews.get(currentPosition % 3);
            v1.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            v1.showDate(seedDate);
            v1.updateWeek(rowIndex);

            CalendarView v2 = calendarViews.get((currentPosition - 1) % 3);
            v2.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            CalendarDate last = seedDate.modifyWeek(-1);
            if (weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }//每周的种子日期为这一周的最后一天
            v2.updateWeek(rowIndex);

            CalendarView v3 = calendarViews.get((currentPosition + 1) % 3);
            v3.switchCalendarType(CalendarAttr.CalendarType.WEEK);
            CalendarDate next = seedDate.modifyWeek(1);
            if (weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }//每周的种子日期为这一周的最后一天
            v3.updateWeek(rowIndex);
        }
    }

    public void notifyMonthDataChanged(CalendarDate date) {
        seedDate = date;
        refreshCalendar();
    }

    public void notifyDataChanged(CalendarDate date) {
        seedDate = date;
        saveSelectedDate(date);
        refreshCalendar();
    }

    public void notifyDataChanged() {
        refreshCalendar();
    }

    private void refreshCalendar() {
        if (calendarType == CalendarAttr.CalendarType.WEEK) {
            MonthPager.CURRENT_DAY_INDEX = currentPosition;
            CalendarView v1 = calendarViews.get(currentPosition % 3);
            v1.showDate(seedDate);
            v1.updateWeek(rowCount);

            CalendarView v2 = calendarViews.get((currentPosition - 1) % 3);
            CalendarDate last = seedDate.modifyWeek(-1);
            if (weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v2.showDate(Utils.getSaturday(last));
            } else {
                v2.showDate(Utils.getSunday(last));
            }
            v2.updateWeek(rowCount);

            CalendarView v3 = calendarViews.get((currentPosition + 1) % 3);
            CalendarDate next = seedDate.modifyWeek(1);
            if (weekArrayType == CalendarAttr.WeekArrayType.Sunday) {
                v3.showDate(Utils.getSaturday(next));
            } else {
                v3.showDate(Utils.getSunday(next));
            }//每周的种子日期为这一周的最后一天
            v3.updateWeek(rowCount);
        } else {
            MonthPager.CURRENT_DAY_INDEX = currentPosition;

            CalendarView v1 = calendarViews.get(currentPosition % 3);//0
            v1.showDate(seedDate);

            CalendarView v2 = calendarViews.get((currentPosition - 1) % 3);//2
            CalendarDate last = seedDate.modifyMonth(-1);
            last.setDay(1);
            v2.showDate(last);

            CalendarView v3 = calendarViews.get((currentPosition + 1) % 3);//1
            CalendarDate next = seedDate.modifyMonth(1);
            next.setDay(1);
            v3.showDate(next);
        }
    }

    public CalendarAttr.CalendarType getCalendarType() {
        return calendarType;
    }

    /**
     * 为每一个Calendar实例设置renderer对象
     *
     * @return void
     */
    public void setCustomDayRenderer(IDayRenderer dayRenderer) {
        CalendarView c0 = calendarViews.get(0);
        c0.setDayRenderer(dayRenderer);

        CalendarView c1 = calendarViews.get(1);
        c1.setDayRenderer(dayRenderer.copy());

        CalendarView c2 = calendarViews.get(2);
        c2.setDayRenderer(dayRenderer.copy());
    }

    public void setOnCalendarTypeChangedListener(OnCalendarTypeChanged onCalendarTypeChangedListener) {
        this.onCalendarTypeChangedListener = onCalendarTypeChangedListener;
    }

    public CalendarAttr.WeekArrayType getWeekArrayType() {
        return weekArrayType;
    }

    public interface OnCalendarTypeChanged {
        void onCalendarTypeChanged(CalendarAttr.CalendarType type);
    }
}