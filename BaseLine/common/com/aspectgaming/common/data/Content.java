package com.aspectgaming.common.data;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.aspectgaming.gdx.component.Component;

public class Content {

    private HashMap<Integer, Component> map = new HashMap<Integer, Component>();

//    public static final int BACKGROUNDCOMPONENT = 0;
//    public static final int FPSCOMPONENT = 1;
//    public static final int BUTTONSCOMPONENT = 2;
//    public static final int INFOCOMPONENT = 3;
//    public static final int TOPSCREENAGENTCOMPONENT = 5;

//    public static final int TOPSCREENBACKGROUNDCOMPONENT = 7;
    public static final int STATEMACHINECOMPONENT = 8;
//    public static final int TILTCOMPONENT = 9;
    public static final int REELCOMPONENT = 10;
    public static final int METERSCOMPONENT = 11;
    public static final int WINSHOWCOMPONENT = 12;
    public static final int FREEGAMEINTROCOMPONENT = 13;
    public static final int RANDOMWILDCOMPONENT = 14;
//    public static final int WINTRACKCOMPONENT = 15;
    public static final int MESSAGEBARCOMPONENT = 16;
//    public static final int GAMBLECOMPONENT = 17;
    public static final int RETRIGGERCOMPONENT = 18;
//    public static final int WINBOXCOMPONENT = 19;
//    public static final int HELPCOMPONENT = 20;
//    public static final int DENOMINATIONCOMPONENT = 21;
//    public static final int PAYTABLECOMPONENT = 22;
//    public static final int SCATTERLANDINGCOMPONENT = 23;
//    public static final int LOGOANIMATIONCOMPONENT = 25;
//    public static final int DIAGNOSTICCOMPONENT = 26;
//    public static final int GAMERECALLCOMPONENT = 27;
//    public static final int SYSTEMMESSAGECOMPONENT = 28;
    public static final int PAYLINESCOMPONENT = 29;
    public static final int BETPIPCOMPONENT = 30;
    public static final int TOPSCREENPROGRESSIVECOMPONENT = 31;
    public static final int SUBSYMBOLCOMPONENT = 32;
    public static final int PROGRESSIVEREELCOMPONENT = 33;
    public static final int PROGRESSIVEREELANIMCOMPONENT = 34;
    public static final int PROGRESSIVEREELROLLCOMPONENT = 35;
    public static final int FREEGAMEANTICIPATIONSPINCOMPONENT = 36;

    private static final Content instance = new Content();

    public static Content getInstance() {
        return instance;
    }

    public void addComponent(int key, Component component) {
        map.put(key, component);
    }

    public Component getComponent(int key) {
        return map.get(key);
    }

    public void addComponent(Component comp) {
        if (comp != null) {
            try {
                Field field = getClass().getField(comp.getClass().getSimpleName().toUpperCase());
                Content.getInstance().addComponent(field.getInt(this), comp);
            } catch (Exception e) {
                // not a problem if component is not in the list
            }
        }
    }
}
