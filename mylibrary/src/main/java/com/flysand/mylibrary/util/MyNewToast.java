package com.flysand.mylibrary.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by FlySand on 2017/12/8.
 */

public class MyNewToast {


    private Toast mToast;
    private Context context;

    public MyNewToast(Context context) {
        this.context = context;
    }

    public void setText(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void cancel() {
        if (mToast != null) {
            try {
                mToast.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
