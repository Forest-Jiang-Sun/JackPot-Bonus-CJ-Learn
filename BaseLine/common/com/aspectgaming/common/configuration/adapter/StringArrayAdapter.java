package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author ligang.yao
 */
public class StringArrayAdapter extends XmlAdapter<String, String[]> {

    @Override
    public String[] unmarshal(String val) {
        return val != null ? val.split(",") : null;
    }

    @Override
    public String marshal(String[] val) {
        return null;
    }
}
