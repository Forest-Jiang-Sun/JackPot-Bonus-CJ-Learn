package com.aspectgaming.common.action;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
/**
 * A game action. override the delegate function to decide whether delay or continue.
 * @author johnny.shi
 *
 */
public class ConditionDelayAction extends DelegateAction {

    @Override
    protected boolean delegate(float delta) {
        return condition();
    }
    /**
     * 
     * @return return true means cancel delay
     */
    public boolean condition(){
        return true;
    }

}
