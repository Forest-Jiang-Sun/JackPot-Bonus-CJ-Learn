package com.aspectgaming.common.configuration.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.badlogic.gdx.math.Vector2;

/**
 * @author ligang.yao
 */
public class VectorAdapter extends XmlAdapter<String, Vector2> {

    @Override
    public Vector2 unmarshal(String val) {
        if (val != null) {
            String[] strArr = val.split(",");
            return new Vector2(Float.parseFloat(strArr[0].trim()), Float.parseFloat(strArr[1].trim()));
        }
        return null;
    }

    @Override
    public String marshal(Vector2 val) {
        if (val != null) {
            String str = new String(val.x + "," + val.y);
            return str;
        }
        return null;
    }
}
