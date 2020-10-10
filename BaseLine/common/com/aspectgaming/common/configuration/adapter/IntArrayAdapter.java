package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author ligang.yao
 */
public class IntArrayAdapter extends XmlAdapter<String, int[]> {

    @Override
    public int[] unmarshal(String val) {
        if (val == null) return null;

        String[] vals = val.replaceAll("[^,\\-0-9]", "").split(",");

        int[] ret = new int[vals.length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(vals[i]);
        }
        return ret;
    }

    @Override
    public String marshal(int[] val) {
        return null;
    }
}
