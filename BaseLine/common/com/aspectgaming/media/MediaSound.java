package com.aspectgaming.media;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

/**
 * @author ligang.yao
 */
public class MediaSound extends Actor {

    private float fade = 1; // range: 0-1, 1:highest volume, 0:mute
    private long id; // only cache the final instance;
    private final OpenALSound sound;

    private final FloatAction action = new SoundFadeAction();

    public MediaSound(String path) {
        sound = (OpenALSound) Gdx.audio.newSound(new FileHandle(new File(path)));
    }

    public float duration() {
        return sound.duration();
    }

    public void play() {
        id = sound.play(getVolume());
    }

    public void loop() {
        id = sound.play(getVolume());
        sound.setLooping(id, true);
    }

    public void updateVolume() {
        sound.setVolume(id, getVolume());
    }

    public void stop() {
        sound.stop();
        clearActions();
    }

    public void dispose() {
        sound.dispose();
    }

    public void setFade(float val) {
        fade = val;
        updateVolume();
    }

    public void fade(float start, float end, float duration) {
        clearActions();
        action.setStart(start);
        action.setEnd(end);
        action.setDuration(duration);
        addAction(action);

        fade = start;
        updateVolume();
    }

    private float getVolume() {
        return MediaData.Volume * fade / 10;
    }

    /**
     * Move sound from left to right across multiple machines
     * 
     * @param pos
     *            : current position of the EGM, first is 0
     * @param timeStep
     *            : time of sound move to another machine, control the speed of movement
     * @param timeKeep
     *            : time to keep at highest volume after fadein and before fadeout
     */
    public void playMovedSound(int pos, float timeStep, float timeKeep) {
        clearActions();
        addAction(getMoveAction(pos, timeStep, timeKeep));
        play();
    }

    private Action getFadeAction(float start, float end, float duration) {
        SoundFadeAction action = new SoundFadeAction();
        action.setStart(start);
        action.setEnd(end);
        action.setDuration(duration);
        return action;
    }

    private Action getMoveAction(int pos, float timeStep, float timeKeep) {
        int range = 3; // number of EGMs for fadein or fadeout.
        int posStart = pos - range;
        if (posStart < 0) posStart = 0;

        SequenceAction seq = sequence();

        if (pos != 0) {
            if (posStart > 0) {
                seq.addAction(delay(timeStep * posStart));
            }

            float time = timeStep * (pos - posStart);
            float start = 1 - ((float) (pos - posStart)) / range;
            seq.addAction(getFadeAction(start, 1, time));
            setFade(start);
        } else {
            setFade(1);
        }

        seq.addAction(delay(timeKeep));
        seq.addAction(getFadeAction(1, 0, timeStep * range));
        return seq;
    }

    private class SoundFadeAction extends FloatAction {
        @Override
        protected void update(float percent) {
            super.update(percent);
            fade = getValue();
            updateVolume();
        }
    };
}
