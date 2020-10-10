package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.badlogic.gdx.utils.Align;

/**
 * @author ligang.yao
 */
public class AlignAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(String val) {
        if (val != null) {
            switch (val.toLowerCase()) {
            case "center":
                return Align.center;
            case "top":
                return Align.top;
            case "bottom":
                return Align.bottom;
            case "left":
                return Align.left;
            case "right":
                return Align.right;
            }
        }
        return Align.left;
    }

    @Override
    public String marshal(Integer val) {
        return null;
    }
}
