package com.aspectgaming.gdx.component;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.event.GameEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * @author johnny.shi
 */
public abstract class Component extends Group {

    protected Map<Class<?>, GameEvent> events = new HashMap<>();

    protected final Logger log;

    public Component() {
        log = LoggerFactory.getLogger(getClass());

        // Most components have no scale and rotate. Set transform to false to avoid flush SpriteBatch for every group.
        setTransform(false);
        setTouchable(Touchable.disabled);
    }

    protected void registerEvent(GameEvent event) {
        GameEvent evt = events.put(event.getClass().getSuperclass(), event);
        if (evt != null) {
            log.error("Event has already been registered! {}", event);
        }
    }

    protected void removeEvent(Class<?> eventClass) {
        events.remove(eventClass);
    }

    public GameEvent getEvent(Class<?> eventClass) {
        return events.get(eventClass);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        try {
            this.update(delta);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    protected void update(float delta) throws Exception {}

    public void dispose() {}

    @Override
    public void pause() {
        if (!isPaused()) {
            super.pause();

            for (Actor actor : getChildren()) {
                actor.pause();
            }
        }
    }

    @Override
    public void resume() {
        if (isPaused()) {
            super.resume();

            for (Actor actor : getChildren()) {
                actor.resume();
            }
        }
    }

}
