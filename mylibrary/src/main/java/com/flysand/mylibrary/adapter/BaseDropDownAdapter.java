package com.flysand.mylibrary.adapter;

import android.content.Context;
import android.view.View;

import com.flysand.mylibrary.base.BaseAdapter;
import com.flysand.mylibrary.customView.DropDownMenu;
import com.flysand.mylibrary.listener.DropDownItemClickListener;
import com.flysand.mylibrary.listener.DropDownSelectChangedListener;

import java.util.List;


public abstract class BaseDropDownAdapter extends BaseAdapter {


    private int viewIndex;
    private DropDownSelectChangedListener listener;
    private DropDownMenu menu;
    private String tabText = "";
    private int checkItemPosition = 0;

    public void setCheckItemPosition(int position, boolean isCallBack) {
        if (checkItemPosition != position) {
            int oldPosition = checkItemPosition;
            checkItemPosition = position;

            if (menu != null) {
                menu.setTabText(getTableText(checkItemPosition), viewIndex);
            }
            notifyDataSetChanged();
            if (isCallBack) {
                if (listener != null) {
                    listener.onSelectChanged(this, checkItemPosition, oldPosition);
                } else if (context instanceof DropDownSelectChangedListener) {
                    ((DropDownSelectChangedListener) context).onSelectChanged(this, position, oldPosition);
                }
            }
        }
    }

    public void setCheckItemPosition(int position) {
        setCheckItemPosition(position, true);
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public void setDropDownSelectChangedListener(DropDownSelectChangedListener listener) {
        this.listener = listener;
    }

    public int getCheckItemPosition() {
        return checkItemPosition;
    }

    public void setDropDownView(DropDownMenu menu) {
        this.menu = menu;
    }

    public void updateDropDownData(List<String> list) {
        checkItemPosition = -1;
        updateData(list);
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }

    public BaseDropDownAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public String getTableText(int position) {
        return position == 0 ? tabText : (String) getObjcet(position);
    }

    protected boolean isSelectedItem(int position) {
        if (checkItemPosition == position) {
            return true;
        }
        return false;
    }

    @Override
    public void onBindViewHolder(final RVHolder holder, int var2) {
        try {
            this.onBindViewHolder(holder.getViewType(), holder.getViewHolder().getConvertView(), var2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (menu.getSelectPosition() != 1 && menu.getSelectPosition() / 2 == viewIndex) {
                    int position = holder.getAdapterPosition();
                    if (menu != null) {
                        setCheckItemPosition(position);
                        // 始终保持顶部tab与选中的一致
                        menu.setTabText(getTableText(checkItemPosition));
                        menu.closeMenu();
                    }
                    if (context instanceof DropDownItemClickListener) {
                        ((DropDownItemClickListener) context).onDropDownItemClick(BaseDropDownAdapter.this, v, position);
                    }
                }
            }
        });
    }
}
