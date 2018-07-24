//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.flysand.mylibrary.base;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.flysand.mylibrary.adapter.ViewHolder;
import com.flysand.mylibrary.listener.ObserverListener;
import com.flysand.mylibrary.listener.RecyclerViewItemClickListener;
import com.flysand.mylibrary.listener.RecyclerViewItemLongClickListener;
import com.flysand.mylibrary.util.Utils;

import java.util.List;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.RVHolder> {
    private List<?> list;
    protected Context context;
    protected float screenDestiny;
    protected BaseFragment fragment;

    public BaseAdapter(Context var1, List<?> var2) {
        this.list = var2;
        this.context = var1;
        this.screenDestiny = var1.getResources().getDisplayMetrics().density;
    }

    public BaseAdapter(BaseFragment var2, List<?> var3) {
        this.list = var3;
        this.context = var2.getActivity();
        this.fragment = var2;
        this.screenDestiny = context.getResources().getDisplayMetrics().density;
    }

    public void sendUpdateMessage(int type, Intent intent) {
        if (context instanceof ObserverListener)
            ((ObserverListener) context).sendUpdateMessage(type, intent);
    }

    public void updateData(List<?> var1) {
        this.list = var1;
        this.notifyDataSetChanged();
    }

    public Object getObjcet(int var1) {
        return var1 >= 0 && var1 <= this.list.size() - 1 ? this.list.get(var1) : null;
    }

    public RVHolder onCreateViewHolder(ViewGroup var1, int viewType) {
        View var3 = LayoutInflater.from(this.context).inflate(this.onCreateViewLayoutID(viewType), var1, false);
        return new RVHolder(var3, viewType);
    }

    public abstract int onCreateViewLayoutID(int var1);

    protected void onItemClick(View v, int position) {
        Utils.print("onItemClick   " + position);
    }

    public void onBindViewHolder(final BaseAdapter.RVHolder holder, final int position) {
        try {
            //holder.getAdapterPosition()
            onBindViewHolder(holder.getViewType(), holder.getViewHolder().getConvertView(), position);


            if (this.fragment != null) {
                if (this.fragment instanceof RecyclerViewItemClickListener) {
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        public void onClick(View var1x) {
                            onItemClick(var1x, position);
                            ((RecyclerViewItemClickListener) BaseAdapter.this.fragment).onRecyclerViewItemClick(BaseAdapter.this, var1x, position);
                        }
                    });
                }

                if (this.fragment instanceof RecyclerViewItemLongClickListener) {
                    holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View var1x) {
                            ((RecyclerViewItemLongClickListener) BaseAdapter.this.fragment).onRecyclerViewLongItemClick(BaseAdapter.this, var1x, position);
                            return true;
                        }
                    });

                }
            } else {
                if (this.context instanceof RecyclerViewItemClickListener) {
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        public void onClick(View var1x) {
                            onItemClick(var1x, position);
                            ((RecyclerViewItemClickListener) BaseAdapter.this.context).onRecyclerViewItemClick(BaseAdapter.this, var1x, position);
                        }
                    });
                }

                if (this.context instanceof RecyclerViewItemLongClickListener) {
                    holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View var1x) {
                            ((RecyclerViewItemLongClickListener) BaseAdapter.this.context).onRecyclerViewLongItemClick(BaseAdapter.this, var1x, position);
                            return true;
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onBindViewHolder(int viewType, View view, int position) throws Exception;

    public int getItemCount() {
        return this.list.size();
    }


    public class RVHolder extends RecyclerView.ViewHolder {

        private ViewHolder viewHolder;
        private int viewType;

        public RVHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            viewHolder = ViewHolder.getViewHolder(itemView);
        }

        public ViewHolder getViewHolder() {
            return viewHolder;
        }

        public int getViewType() {
            return viewType;
        }
    }
}
