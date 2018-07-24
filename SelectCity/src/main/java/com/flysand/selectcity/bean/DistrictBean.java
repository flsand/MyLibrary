package com.flysand.selectcity.bean;

public class DistrictBean {
    private String name;
    private String zipcode;

    public DistrictBean() {
        super();
    }

    public DistrictBean(String name, String zipcode) {
        super();
        this.name = name;
        this.zipcode = zipcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

}
