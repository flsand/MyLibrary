package com.flysand.timeselect.view;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class CustomPickerView extends NumberPickerView {

    public CustomPickerView(Context context) {
        super(context);
    }

    public CustomPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDisplayedValues(List<String> newDisplayedValues) {
        setDisplayedValues(newDisplayedValues.toArray(new String[0]), true);
        setMaxValue(newDisplayedValues.size() - 1);
        setMinValue(0);
    }
}
