package com.flysand.mylibrary.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Et
 * 保留两位小小数
 * <p>
 * <p>
 * shellPriceEt.addTextChangedListener(new MyTextWatcher());
 */
public class MyDoubleNumTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable edt) {
        String temp = edt.toString();
        if (temp.startsWith("0") && temp.length() > 1) {
            if (!temp.contains(".")) {
                edt.delete(1, 2);
            }
        }
        int posDot = temp.indexOf(".");
        if (posDot <= 0)
            return;
        if (temp.length() - posDot - 1 > 2) {
            edt.delete(posDot + 3, posDot + 4);
        }
    }

}