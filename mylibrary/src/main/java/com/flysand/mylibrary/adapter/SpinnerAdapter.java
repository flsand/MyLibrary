package com.flysand.mylibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flysand.mylibrary.R;

import java.util.List;

/**
 *
 *
 * .
 * myAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
carName = MainChargeActivity.this.carInfo.get(position).getCode();
spinnerCar.setSelection(position);
spinnerCar.onDetachedFromWindow();
}
});
 */
public class SpinnerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> nameList;
    private String TAG = "ChargeCarNameAdapter";

    public SpinnerAdapter(Context context, List<String> nameList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.nameList = nameList;
    }

    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int i) {
        return nameList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_name, parent, false);
            holder.nameTv = (TextView) convertView.findViewById(R.id.spinner_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = nameList.get(position);
        holder.nameTv.setText(name);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_name_item, parent, false);
            holder.nameTv = (TextView) convertView.findViewById(R.id.spinnerNameTv);
            holder.position = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = nameList.get(position);
        holder.nameTv.setText(name);
        if (onItemClickListener != null) {
            holder.nameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, holder.position, 000000L);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
        View view;
        int position;
    }


}

