package com.flysand.selectcity.bean;

import java.util.ArrayList;

public class CityBean {
	private String name;
	private ArrayList<DistrictBean> districtList;
	
	public CityBean() {
		super();
	}

	public CityBean(String name, ArrayList<DistrictBean> districtList) {
		super();
		this.name = name;
		this.districtList = districtList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<DistrictBean> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(ArrayList<DistrictBean> districtList) {
		this.districtList = districtList;
	}

}
