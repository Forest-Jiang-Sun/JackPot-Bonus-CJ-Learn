package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author ligang.yao
 */
public class ButtonsConfiguration {

    @XmlElement(name = "button")
    public ButtonConfiguration[] buttons;

    public ButtonConfiguration getButton(String name) {
        for (ButtonConfiguration btn : buttons) {
            if (btn.name.equals(name)) {
                return btn;
            }
        }
        return null;
    }

    public boolean hasButton(String name) {
        for (ButtonConfiguration btn : buttons) {
            if (btn.name.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
