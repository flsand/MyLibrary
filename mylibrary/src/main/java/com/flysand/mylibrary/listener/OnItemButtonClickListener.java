package com.flysand.mylibrary.listener;

import android.view.View;

import com.flysand.mylibrary.base.BaseAdapter;

/**
 * Created by FlySand on 2017/9/11.
 */

public interface OnItemButtonClickListener {
    void  onButtonClick(BaseAdapter adapter, View view, int clickType,int position);
}
