package com.flysand.timeselect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.flysand.timeselect.view.CustomPickerView;
import com.flysand.timeselect.view.NumberPickerView;

import java.util.ArrayList;
import java.util.Calendar;

import bsharestrong.aioute.com.timeselect.R;

/**
 * Created by FlySand on 2017/9/19.
 */

public class TimeSelect {

    public enum TimeType {
        YearMothDay, YerMonthDayHourMinute
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(String time);
    }

    private OnTimeSelectListener listener;
    private Activity activity;
    private TimeType type;
    //底部PopupWindow View
    private View mPopupWindow;
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<String> years = new ArrayList<>();
    private ArrayList<String> pickerValues = new ArrayList<>();

    public TimeSelect(Activity activity, TimeType type, OnTimeSelectListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.type = type;
        init();
    }

    private CustomPickerView yearPicker;
    private CustomPickerView monthPicker;
    private CustomPickerView dayPicker;
    private CustomPickerView hourPicker;
    private CustomPickerView minutePicker;
    private Button confirmBtn;

    private  int yearIndex=8;
    private  int monthIndex = calendar.get(Calendar.MONTH);
    private  int dayIndex= calendar.get(Calendar.DAY_OF_MONTH)-1;
    private  int hourIndex;
    private  int minuteIndex;

    private void init() {
        // year
        int currentYear = calendar.get(Calendar.YEAR);
        currentYear-=8;
        for (int i = 0; i < 12; i++) {
            years.add(currentYear + "");
            currentYear++;
        }

        for (int i = 1; i <= 60; i++) {
            if (i < 10) {
                pickerValues.add("0" + i);
            } else {
                pickerValues.add("" + i);
            }
        }
    }

    public void show() {
        mPopupWindow = LayoutInflater.from(activity).inflate(R.layout.date_select, null);

        yearPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.year_picker);
        monthPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.month_picker);
        dayPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.day_picker);
        hourPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.hour_picker);
        minutePicker = (CustomPickerView) mPopupWindow.findViewById(R.id.minute_picker);
        confirmBtn = (Button) mPopupWindow.findViewById(R.id.timeselect_btn);
        initTimePicker();
        if (type == TimeType.YearMothDay) {
            hourPicker.setVisibility(View.GONE);
            minutePicker.setVisibility(View.GONE);
        }
    }

    private void initTimePicker() {
        yearPicker.setDisplayedValues(years.subList(0, years.size()-1));
        monthPicker.setDisplayedValues(pickerValues.subList(0, 12));
        dayPicker.setDisplayedValues(pickerValues.subList(0, calendar.getActualMaximum(Calendar.DATE)));
        /*
        int  currentDay = calendar.get(Calendar.DATE);
        int currentMonth = calendar.get(Calendar.MONTH);
        monthPicker.setDisplayedValues(pickerValues.subList(currentDay,12));
        dayPicker.setDisplayedValues(pickerValues.subList(currentMonth,calendar.getActualMaximum(Calendar.DATE)));
        */
        hourPicker.setDisplayedValues(pickerValues.subList(0, 24));
        minutePicker.setDisplayedValues(pickerValues.subList(0, 60));


        yearPicker.setValue(yearIndex);
        monthPicker.setValue(monthIndex);
        dayPicker.setValue(dayIndex);
        hourPicker.setValue(hourIndex);
        minutePicker.setValue(minuteIndex);


        yearPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                calendar.set(Calendar.YEAR, Integer.parseInt(years.get(newVal)));
                dayPicker.setDisplayedValues(pickerValues.subList(0, calendar.getActualMaximum(Calendar.DATE)));
            }
        });
        monthPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                calendar.set(Calendar.MONTH, newVal);
                dayPicker.setDisplayedValues(pickerValues.subList(0, calendar.getActualMaximum(Calendar.DATE)));
            }
        });
        final PopupWindow popupWindow = new PopupWindow(null,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                yearIndex = yearPicker.getPickedIndexRelativeToRaw();
                monthIndex = monthPicker.getPickedIndexRelativeToRaw();
                dayIndex = dayPicker.getPickedIndexRelativeToRaw();
                hourIndex = hourPicker.getPickedIndexRelativeToRaw();
                minuteIndex = minutePicker.getPickedIndexRelativeToRaw();

                Log.e("----------", "yearIndex = " + yearIndex + "   monthIndex = " + monthIndex + "   dayIndex = " + dayIndex + "  hourIndex =" + hourIndex + "  minuteIndex = " + minuteIndex);

                String year = years.get(yearIndex);
                String month = pickerValues.get(monthIndex);
                String day = pickerValues.get(dayIndex);
                String hour = pickerValues.get(hourIndex);
                String minute = pickerValues.get(minuteIndex);
                String time;
                if (type == TimeType.YearMothDay) {
                    time = year + "-" + month + "-" + day + "";
                } else {
                    time = year + "-" + month + "-" + day + " " + hour + ":" + minute + "";
                }

                if (listener != null) {
                    listener.onTimeSelect(time);
                }

            }
        });


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        //设置屏幕透明度
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);
        // 设置SelectPicPopupWindow的View
        popupWindow.setContentView(mPopupWindow);
        // 设置SelectPicPopupWindow弹出窗体的宽
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x44000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);

        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mPopupWindow.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = activity.findViewById(android.R.id.content).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });
    }
}
