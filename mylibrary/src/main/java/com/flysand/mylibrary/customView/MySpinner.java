package com.flysand.mylibrary.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Administrator on 2016/9/22.
 *
 myAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carName = MainChargeActivity.this.carInfo.get(position).getCode();
                spinnerCar.setSelection(position);
                spinnerCar.onDetachedFromWindow();
        }
});
 *
 */
@SuppressLint("AppCompatCustomView")
public class MySpinner extends Spinner {

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, int mode) {
        super(context, mode);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @SuppressLint("NewApi")
    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    @SuppressLint("NewApi")
    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }
}
