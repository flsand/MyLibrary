package com.flysand.mylibrary.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysand.mylibrary.R;
import com.flysand.mylibrary.adapter.BaseDropDownAdapter;
import com.flysand.mylibrary.util.SharedPreferenceUtil;
import com.flysand.mylibrary.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dongjunkun on 2015/6/17.
 */
public class DropDownMenu extends LinearLayout {

    private static final String SHARE_FILENAME = "dropdownmenusharefilename";
    private static final String SHARE_KEY = "tabwrapcontent";

    // 顶部菜单布局
    private LinearLayout tabMenuView;
    // 底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    // 弹出菜单父布局
    private FrameLayout popupMenuViews;
    // 遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    // tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    // 分割线颜色
    private int dividerColor = 0xffcccccc;
    // tab选中颜色
    private int textSelectedColor = 0xff890c85;
    // tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    // 遮罩颜色
    private int maskColor = 0x88888888;
    // tab字体大小
    private int menuTextSize;

    // tab选中图标
    private int menuSelectedIcon;
    // tab未选中图标
    private int menuUnselectedIcon;
    // tab是否自适应宽度
    private boolean menuTabWrapContent = false;
    // tab的最小宽度（menuTabWrapContent true是有效）
    private float menuTabMinWidth = 0f;
    // tab中文字和图片是否同时居中
    private boolean menuTabDrawableCenter = false;
    // tab中文字和图片是否同时居中时，文字和图片的间隔
    private float menuTabDrawableCenterPadding;
    //
    private AutoFillEmptyHorrzontalScrollView scrollView;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        // 为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor(R.styleable.DropDownMenu_underlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropDownMenu_dividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_textSelectedColor, textSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_textUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_menuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownMenu_maskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_menuTextSize, Utils.dpToPx(getContext(), 14));
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_menuSelectedIcon, R.drawable.drop_down_selected_icon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_menuUnselectedIcon, R.drawable.drop_down_unselected_icon);
        menuTabWrapContent = a.getBoolean(R.styleable.DropDownMenu_menuTabWrapContent, false);
        menuTabDrawableCenter = a.getBoolean(R.styleable.DropDownMenu_menuTabDrawableCenter, true);
        menuTabDrawableCenterPadding = a.getDimension(R.styleable.DropDownMenu_menuTabDrawableCenterPadding, 10);
        menuTabMinWidth = a.getDimension(R.styleable.DropDownMenu_menuTabMinWidth, Utils.dpToPx(getContext(), 70));
        a.recycle();

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);

        if (menuTabWrapContent) {
            scrollView = new AutoFillEmptyHorrzontalScrollView(getContext());
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            scrollView.addView(tabMenuView);
            addView(scrollView, 0);
        } else {
            addView(tabMenuView, 0);
        }

        //为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(getContext(), 0.5f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);
    }

    public boolean isMenuTabWrapContent() {
        return menuTabWrapContent;
    }

    public int getSelectPosition() {
        return current_tab_position;
    }

    public void setMenuTabWrapContent(boolean isWrap) {

        if (isWrap != menuTabWrapContent) {
            menuTabWrapContent = isWrap;

            if (menuTabWrapContent) {
                removeViewAt(0);
                scrollView = new AutoFillEmptyHorrzontalScrollView(getContext());
                scrollView.setHorizontalScrollBarEnabled(false);
                scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                scrollView.addView(tabMenuView);
                addView(scrollView, 0);
                for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                    if (tabMenuView.getChildAt(i) instanceof AlwaysMarqueeTextView) {
                        AlwaysMarqueeTextView child = (AlwaysMarqueeTextView) tabMenuView.getChildAt(i);
                        child.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        child.setMinWidth((int) menuTabMinWidth);
                    }
                }
            } else {
                scrollView.removeAllViews();
                removeViewAt(0);
                addView(tabMenuView, 0);
                for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                    if (tabMenuView.getChildAt(i) instanceof AlwaysMarqueeTextView) {
                        AlwaysMarqueeTextView child = (AlwaysMarqueeTextView) tabMenuView.getChildAt(i);
                        child.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        child.setMarqueeRepeatLimit(-1);
                        child.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                    }
                }
            }

            SharedPreferenceUtil.getInstance(getContext()).saveParam(SHARE_FILENAME, SHARE_KEY, menuTabWrapContent ? true : false);
        }
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @NonNull View contentView) {

        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        int index = 0;
        if (contentView != null) {
            containerView.addView(contentView, index++);
        }

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        containerView.addView(maskView, index++);
        maskView.setVisibility(GONE);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, index++);

        for (int i = 0; i < popupViews.size(); i++) {
            if (popupViews.get(i) instanceof RecyclerView) {
                RecyclerView.Adapter adapter = ((RecyclerView) popupViews.get(i)).getAdapter();
                if (adapter instanceof BaseDropDownAdapter) {
                    ((BaseDropDownAdapter) adapter).setTabText(tabTexts.get(i));
                    ((BaseDropDownAdapter) adapter).setViewIndex(i);
                    ((BaseDropDownAdapter) adapter).setDropDownView(this);
                }
            }
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }

        setMenuTabWrapContent(SharedPreferenceUtil.getInstance(getContext()).readBooleanParam(SHARE_FILENAME, SHARE_KEY, false));
    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        final AlwaysMarqueeTextView tab = new AlwaysMarqueeTextView(getContext());
        tab.setSingleLine();
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        if (menuTabWrapContent) {
            tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tab.setMinWidth((int) menuTabMinWidth);
        } else {
            tab.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tab.setMarqueeRepeatLimit(-1);
            tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        }
        tab.setTextColor(textUnselectedColor);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
        if (menuTabDrawableCenter) {
            tab.setCompoundDrawablePadding((int) menuTabDrawableCenterPadding);
            tab.setCenter(true);
        } else {
            tab.setGravity(Gravity.CENTER);
        }
        tab.setText(tabTexts.get(i));
        tab.setPadding(Utils.dpToPx(getContext(), 5), Utils.dpToPx(getContext(), 12), Utils.dpToPx(getContext(), 5), Utils.dpToPx(getContext(), 12));
        //添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
        //添加分割线
        if (i < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(Utils.dpToPx(getContext(), 0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
        }
    }

    public void setTabText(String text, int position) {
        if (position <= tabMenuView.getChildCount() / 2) {
            ((TextView) tabMenuView.getChildAt(position * 2)).setText(text);
        }
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(menuUnselectedIcon), null);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_position = -1;
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    private void switchMenu(View target) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    private class AutoFillEmptyHorrzontalScrollView extends HorizontalScrollView {

        private float screenWidth;

        public AutoFillEmptyHorrzontalScrollView(Context context) {
            super(context);
            init();
        }

        public AutoFillEmptyHorrzontalScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public AutoFillEmptyHorrzontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            screenWidth = getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            View view = getChildAt(0);

            int count = ((LinearLayout) view).getChildCount();
            float childTotalWidth = 0f;
            List<View> childList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                View child = ((LinearLayout) view).getChildAt(i);
                int measureWidth = child.getMeasuredWidth();
                childTotalWidth += measureWidth;
                if (measureWidth > Utils.dpToPx(getContext(), 5)) {
                    childList.add(child);
                }
            }

            float residueWidth = screenWidth - childTotalWidth;
            if (residueWidth > 0) {

                int viewHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, view.getPaddingTop()
                        + view.getPaddingBottom(), view.getLayoutParams().height);
                int viewWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) screenWidth, MeasureSpec.EXACTLY);
                view.measure(viewWidthMeasureSpec, viewHeightMeasureSpec);

                int length = childList.size();
                float addWidth = residueWidth / length;
                for (int j = 0; j < length; j++) {
                    View child = childList.get(j);
                    final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();

                    int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, child.getPaddingTop()
                            + child.getPaddingBottom(), lp.height);
                    int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (child.getMeasuredWidth() + addWidth), MeasureSpec.EXACTLY);

                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }
        }
    }

    /*    @Override
        protected void onRestoreInstanceState(Parcelable state) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            int index = 1;
            int openMenuIndex = (int) ss.childrenStates.get(index++);
            if (openMenuIndex != -1) {
                // 当前有打开的菜单，需要恢复
                tabMenuView.getChildAt(openMenuIndex).performClick();
            }
            menuTabWrapContent = (boolean) ss.childrenStates.get(index++);
    //        int length = (tabMenuView.getChildCount() + 1) / 2;
    //        for (int i = 0; i < length; i++) {
    //            ((TextView) tabMenuView.getChildAt(i * 2)).setText((String) ss.childrenStates.get(index++));
    //        }

            for (int i = 0; i < popupMenuViews.getChildCount(); i++) {
                if (popupMenuViews.getChildAt(i) instanceof RecyclerView) {
                    RecyclerView.Adapter adapter = ((RecyclerView) popupMenuViews.getChildAt(i)).getAdapter();
                    if (adapter instanceof BaseDropDownAdapter) {
                        ((BaseDropDownAdapter) adapter).setCheckItemPosition((int) ss.childrenStates.get(index++), false);
                    }
                }
            }

        }
    */
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray<Integer>();

        int index = 1;
        ss.childrenStates.put(index++, current_tab_position);
        ss.childrenStates.put(index++, menuTabWrapContent);

//        int length = (tabMenuView.getChildCount() + 1) / 2;
//        for (int i = 0; i < length; i++) {
//            ss.childrenStates.put(index++, ((TextView) tabMenuView.getChildAt(i * 2)).getText());
//        }
        for (int i = 0; i < popupMenuViews.getChildCount(); i++) {
            if (popupMenuViews.getChildAt(i) instanceof RecyclerView) {
                RecyclerView.Adapter adapter = ((RecyclerView) popupMenuViews.getChildAt(i)).getAdapter();
                if (adapter instanceof BaseDropDownAdapter) {
                    ss.childrenStates.put(index++, ((BaseDropDownAdapter) adapter).getCheckItemPosition());
                }
            }
        }

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
