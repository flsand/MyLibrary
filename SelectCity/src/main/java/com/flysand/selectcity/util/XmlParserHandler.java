package com.flysand.selectcity.util;


import com.flysand.selectcity.bean.CityBean;
import com.flysand.selectcity.bean.DistrictBean;
import com.flysand.selectcity.bean.ProvinceBean;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;


public class XmlParserHandler extends DefaultHandler {

    private ArrayList<ProvinceBean> provinceList = new ArrayList<ProvinceBean>();

    public XmlParserHandler() {

    }

    public ArrayList<ProvinceBean> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {

    }

    ProvinceBean provinceBean = new ProvinceBean();
    CityBean cityBean = new CityBean();
    DistrictBean districtBean = new DistrictBean();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equals("province")) {
            provinceBean = new ProvinceBean();
            provinceBean.setName(attributes.getValue(0));
            provinceBean.setCityList(new ArrayList<CityBean>());
        } else if (qName.equals("city")) {
            cityBean = new CityBean();
            cityBean.setName(attributes.getValue(0));
            cityBean.setDistrictList(new ArrayList<DistrictBean>());
        } else if (qName.equals("district")) {
            districtBean = new DistrictBean();
            districtBean.setName(attributes.getValue(0));
            districtBean.setZipcode(attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equals("district")) {
            cityBean.getDistrictList().add(districtBean);
        } else if (qName.equals("city")) {
            provinceBean.getCityList().add(cityBean);
        } else if (qName.equals("province")) {
            provinceList.add(provinceBean);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }

}
