package com.flysand.selectcity.bean;

import java.util.ArrayList;

public class ProvinceBean {
	private String name;
	private ArrayList<CityBean> cityList;
	
	public ProvinceBean() {
		super();
	}

	public ProvinceBean(String name, ArrayList<CityBean> cityList) {
		super();
		this.name = name;
		this.cityList = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CityBean> getCityList() {
		return cityList;
	}

	public void setCityList(ArrayList<CityBean> cityList) {
		this.cityList = cityList;
	}

}
