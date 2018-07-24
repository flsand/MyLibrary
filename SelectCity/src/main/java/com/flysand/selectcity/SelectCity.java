package com.flysand.selectcity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.flysand.selectcity.bean.CityBean;
import com.flysand.selectcity.bean.DistrictBean;
import com.flysand.selectcity.bean.ProvinceBean;
import com.flysand.selectcity.util.XmlParserHandler;
import com.flysand.selectcity.view.CustomPickerView;
import com.flysand.selectcity.view.NumberPickerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import bsharestrong.aioute.com.d.R;

/**
 * Created by FlySand on 2017/9/13.
 */

public class SelectCity {


    private Activity activity;
    //底部PopupWindow View
    private View mPopupWindow;
    private OnCitySelctedListener listener;

    public interface OnCitySelctedListener {
        void onCitySelected(String city);
    }


    private SelectCity(Activity activity, OnCitySelctedListener listener) {
        this.activity = activity;
        this.listener = listener;
        init();
    }

//    protected static SelectCity selectCity;

    public static SelectCity  getInstance(Activity activity, OnCitySelctedListener listener) {
//        if(selectCity == null) {
//            synchronized(SelectCity.class) {
//                if(selectCity == null) {
//                    selectCity = new SelectCity(activity,listener);
//                }
//            }
//        }
//        return selectCity;
        return new SelectCity(activity, listener);
    }



    /**
     * 所有省市区
     */
    protected ArrayList<String> provinceNameList;
    protected ArrayList<String> cityNameList;
    protected ArrayList<String> districtNameList;
    /**
     * key - 省 value - 市
     */
    protected Map<String, ArrayList<String>> citisDataMap;
    /**
     * key - 市 values - 区
     */
    protected Map<String, ArrayList<String>> districtDataMap;

    private ArrayList<ProvinceBean> provinceList;

    private static String provinceName = "山东省";
    private static String cityName = "烟台市";
    private static String districtName = "莱山区";

    private static int provinceIndex = 0;
    private  static int cityIndex = 0;
    private   static int districtIndex = 0;

    private void init() {

        Log.e("---","init");
        // 省市区数据读取
        provinceList = new ArrayList<>();
        cityNameList = new ArrayList<>();
        districtNameList = new ArrayList<>();

        provinceNameList = new ArrayList<>();
        citisDataMap = new HashMap<>();
        districtDataMap = new HashMap<>();
        initProvinceData();
    }

    /**
     * 解析省市区的XML数据
     */
    protected void initProvinceData() {

        Log.e("-----","initProvinceData");

        AssetManager asset = activity.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            int length = provinceList.size();

            provinceNameList.clear();
            citisDataMap.clear();
            districtDataMap.clear();

            for (int i = 0; i < length; i++) {
                ProvinceBean provinceBean = provinceList.get(i);
                provinceNameList.add(provinceBean.getName());

                ArrayList<CityBean> cityList = provinceBean.getCityList();
                ArrayList<String> cityNameList = new ArrayList<>();

                int cityLength = cityList.size();
                for (int j = 0; j < cityLength; j++) {
                    CityBean cityBean = cityList.get(j);
                    cityNameList.add(cityBean.getName());

                    ArrayList<DistrictBean> districtList = cityBean.getDistrictList();
                    ArrayList<String> districtNameList = new ArrayList<>();
                    int districtLength = districtList.size();
                    for (int k = 0; k < districtLength; k++) {
                        districtNameList.add(districtList.get(k).getName());
                    }
                    districtDataMap.put(cityList.get(j).getName(), districtNameList);
                }

                citisDataMap.put(provinceBean.getName(), cityNameList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtainCityList(String provinceName) {
        Log.e("--","citisDataMap = "+citisDataMap.size()+"       provinceName ="+provinceName+"   citisDataMap.get(provinceName) ="+citisDataMap.get(provinceName));
        cityNameList.clear();
        cityNameList.addAll(citisDataMap.get(provinceName));
    }

    private void obtainDistrictList(String cityName) {
        districtNameList.clear();
        districtNameList.addAll(districtDataMap.get(cityName));
    }


    public void show() {
        Log.e("----show------", "provinceIndex = " + provinceIndex + "   cityIndex = " + cityIndex + "   districtIndex = " + districtIndex);
        Log.e("----show------", "provinceNameList = " + provinceNameList.size() + "   cityNameList = " + cityNameList.size() + "   districtNameList = " + districtNameList.size());

        mPopupWindow = LayoutInflater.from(activity).inflate(R.layout.popupwindow_city_select, null);

        final CustomPickerView yearPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.release_popup_year_picker);
        final CustomPickerView monthPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.release_popup_month_picker);
        final CustomPickerView dayPicker = (CustomPickerView) mPopupWindow.findViewById(R.id.release_popup_day_picker);
        Button selectCityConfirmBtn = (Button) mPopupWindow.findViewById(R.id.selectCityConfirmBtn);

        yearPicker.setDisplayedValues(provinceNameList);
        yearPicker.setValue(provinceIndex);

        obtainCityList(provinceNameList.get(provinceIndex));
        monthPicker.setDisplayedValues(cityNameList);
        monthPicker.setValue(cityIndex);

        obtainDistrictList(cityNameList.get(cityIndex));
        dayPicker.setDisplayedValues(districtNameList);
        dayPicker.setValue(districtIndex);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                yearPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        obtainCityList(provinceNameList.get(yearPicker.getPickedIndexRelativeToRaw()));
                        monthPicker.setDisplayedValues(cityNameList);
                        obtainDistrictList(cityNameList.get(monthPicker.getPickedIndexRelativeToRaw()));
                        dayPicker.setDisplayedValues(districtNameList);
                    }
                });
                monthPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        obtainDistrictList(cityNameList.get(monthPicker.getPickedIndexRelativeToRaw()));
                        dayPicker.setDisplayedValues(districtNameList);
                    }
                });
            }
        }, 1000);


        final PopupWindow popupWindow = new PopupWindow(null,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        selectCityConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                provinceIndex = yearPicker.getPickedIndexRelativeToRaw();
                cityIndex = monthPicker.getPickedIndexRelativeToRaw();
                districtIndex = dayPicker.getPickedIndexRelativeToRaw();

                Log.e("----------", "provinceIndex = " + provinceIndex + "   cityIndex = " + cityIndex + "   districtIndex = " + districtIndex);

                provinceName = provinceNameList.get(yearPicker.getPickedIndexRelativeToRaw());
                cityName = cityNameList.get(monthPicker.getPickedIndexRelativeToRaw());
                districtName = districtNameList.get(dayPicker.getPickedIndexRelativeToRaw());

                if (listener != null) {
                    listener.onCitySelected(provinceName + " " + cityName + " " + districtName);
                }

            }
        });


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
        //设置屏幕透明度
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);
        // 设置SelectPicPopupWindow的View
        popupWindow.setContentView(mPopupWindow);
        // 设置SelectPicPopupWindow弹出窗体的宽
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x44000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);

        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mPopupWindow.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = activity.findViewById(android.R.id.content).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });
    }

}
