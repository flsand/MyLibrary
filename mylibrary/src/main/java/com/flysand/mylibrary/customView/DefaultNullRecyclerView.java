package com.flysand.mylibrary.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flysand.mylibrary.R;
import com.flysand.mylibrary.util.Utils;


/**
 * Created by Administrator on 2017/11/21.
 */

public class DefaultNullRecyclerView extends FrameLayout {

    private int layoutId = -1;
    private int icon = -1;
    private String text = "";
    private RecyclerView mRecyclerView;
    private int yPosition = 0;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private RecyclerView.OnScrollListener scrollListener;

    public DefaultNullRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public DefaultNullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultNullRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NullRecyclerView);
        layoutId = a.getResourceId(R.styleable.NullRecyclerView_nullLayout, -1);
        icon = a.getResourceId(R.styleable.NullRecyclerView_nullIcon, R.drawable.nullicon);
        text = a.getString(R.styleable.NullRecyclerView_nullText);
        if (text == null || text.length() == 0) {
            text = "空空如也";
        }
        a.recycle();

        init();

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                int count = mRecyclerView.getAdapter().getItemCount();
                if (count == 0) {
                    getChildAt(0).setVisibility(View.VISIBLE);
                    getChildAt(1).setVisibility(View.GONE);
                } else {
                    getChildAt(0).setVisibility(View.GONE);
                    getChildAt(1).setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void init() {
        if (layoutId > 0) {
            View nullView = LayoutInflater.from(getContext()).inflate(layoutId, null);
            addView(nullView);
        } else {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            TextView textView = new TextView(getContext());

            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText(text);
            textView.setPadding(0, Utils.dpToPx(getContext(), 30), 0, 0);
            textView.setTextColor(Color.parseColor("#5F5F5F"));
            Drawable iconDrawable = getResources().getDrawable(icon);
            iconDrawable.setBounds(0, 0, Utils.dpToPx(getContext(), 100), Utils.dpToPx(getContext(), 100));
            textView.setCompoundDrawables(null, iconDrawable, null, null);
            textView.setCompoundDrawablePadding(Utils.dpToPx(getContext(), 15));
            textView.setLayoutParams(params);
            addView(textView);
        }
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mRecyclerView);
    }

    private void setyPosition(int yPosition) {
        mRecyclerView.smoothScrollBy(0, yPosition, null);
    }

    public void setLayoutId(int resouceId) {
        if (resouceId > 0) {
            this.layoutId = resouceId;
            View nullView = LayoutInflater.from(getContext()).inflate(this.layoutId, null);
            if (nullView != null) {
                removeViewAt(0);
                addView(nullView, 0);
            }
        }
    }

    public void setNullIcon(int resouceId) {
        if (layoutId <= 0) {
            this.icon = resouceId;
            Drawable iconDrawable = getResources().getDrawable(this.icon);
            iconDrawable.setBounds(0, 0, Utils.dpToPx(getContext(), 100), Utils.dpToPx(getContext(), 100));
            ((TextView) getChildAt(0)).setCompoundDrawables(null, iconDrawable, null, null);
        }
    }

    public void setNullText(String text) {
        if (layoutId <= 0) {
            this.text = text;
            ((TextView) getChildAt(0)).setText(text);
        }
    }

    private void setRecyclerViewSrcollerListener() {
        if (scrollListener != null) {
            this.mRecyclerView.removeOnScrollListener(scrollListener);
        }
        scrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                yPosition += dy;
            }
        };
        this.mRecyclerView.addOnScrollListener(scrollListener);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        setRecyclerViewSrcollerListener();
        setObserver(adapter);
    }

    public void updateRecyclerView(RecyclerView recyclerView) {
        if (recyclerView != null) {
            removeViewAt(1);
            this.mRecyclerView = recyclerView;
            addView(this.mRecyclerView);
            setRecyclerViewSrcollerListener();
            setObserver(recyclerView.getAdapter());
        }
    }

    private void setObserver(RecyclerView.Adapter adapter) {
        try {
            if (adapter.hasObservers()) {
                adapter.unregisterAdapterDataObserver(adapterDataObserver);
            }
        } catch (IllegalStateException e) {

        } finally {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            if (mRecyclerView.getAdapter().hasObservers()) {
                mRecyclerView.getAdapter().unregisterAdapterDataObserver(adapterDataObserver);
            }
        } catch (IllegalStateException e) {

        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        int index = 1;
        setLayoutId((int) ss.childrenStates.get(index++));
        setNullIcon((int) ss.childrenStates.get(index++));
        setNullText((String) ss.childrenStates.get(index++));

        setyPosition((int) ss.childrenStates.get(index++));
        setRecyclerViewSrcollerListener();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray<Integer>();

        int index = 1;
        ss.childrenStates.put(index++, layoutId);
        ss.childrenStates.put(index++, icon);
        ss.childrenStates.put(index++, text);
        ss.childrenStates.put(index++, yPosition);

        return ss;
    }

    static class SavedState extends BaseSavedState {

        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
