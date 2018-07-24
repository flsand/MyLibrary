//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.adapter;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
    private SparseArray<View> v;
    private View view;

    public static ViewHolder getViewHolder(View var0) {
        ViewHolder var1 = (ViewHolder)var0.getTag();
        if(var1 == null) {
            var1 = new ViewHolder(var0);
            var0.setTag(var1);
        }

        return var1;
    }

    private ViewHolder(View var1) {
        this.view = var1;
        this.v = new SparseArray();
        var1.setTag(this.v);
    }

    public View a(int var1) {
        View var2 = (View)this.v.get(var1);
        if(var2 == null) {
            var2 = this.view.findViewById(var1);
            this.v.put(var1, var2);
        }

        return var2;
    }

    public View getConvertView() {
        return this.view;
    }
}
