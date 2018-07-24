//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.flysand.mylibrary.R;

import java.util.List;


public class ListDropDownAdapter extends BaseDropDownAdapter {
    public ListDropDownAdapter(Context var1, List<String> var2) {
        super(var1, var2);
    }

    public int onCreateViewLayoutID(int var1) {
        return R.layout.item_default_drop_down;
    }

    public void onBindViewHolder(int var1, View var2, int var3) {
        TextView var4 = (TextView)var2.findViewById(R.id.text);
        var4.setText((String)this.getObjcet(var3));
        if(this.isSelectedItem(var3)) {//ff6652
            var4.setTextColor(Color.parseColor("#ff6652"));
//            var4.setBackgroundResource(R.color.check_bg);
//            var4.setBackgroundColor(Color.parseColor("#f2f2f2"));
        } else {
            var4.setTextColor(Color.parseColor("#404040"));
//            var4.setBackgroundResource(R.color.white);
        }

    }
}
