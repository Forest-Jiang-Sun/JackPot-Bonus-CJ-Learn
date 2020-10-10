package com.aspectgaming.common.actor;

import java.io.File;

import com.aspectgaming.common.data.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALSound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

/**
 * @author ligang.yao
 */
public class Sound {

    private float fade = 1; // range: 0-1, 1:highest volume, 0:mute
    private final OpenALSound sound;
    private long id; // only cache the final instance;

    public Sound(String path) {
        sound = (OpenALSound) Gdx.audio.newSound(new FileHandle(new File(path)));
    }

    public void dispose() {
        sound.dispose();
    }

    public void play() {
        id = sound.play(getVolume());
    }

    public void loop() {
        id = sound.play(getVolume());
        sound.setLooping(id, true);
    }

    /**
     * @param val
     *            range: 0.0f - 1.0f
     */
    public void setFade(float val) {
        fade = val;
        updateVolume();
    }

    public void pause() {
        sound.pause();
    }

    public void resume() {
        sound.resume();
    }

    public void stop() {
        sound.stop();
    }

    public Runnable stop = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    public float duration() {
        return sound.duration();
    }

    public OpenALSound getImpl() {
        return sound;
    }

    public void updateVolume() {
        sound.setVolume(id, getVolume());
    }

    private float getVolume() {
        return GameData.getVolume() * fade;
    }

    public Action fadeInAction(float duration) {
        return new FadeAction(fade, 1, duration);
    }

    public Action fadeOutAction(float duration) {
        return new FadeAction(fade, 0, duration);
    }

    private class FadeAction extends FloatAction {

        FadeAction(float start, float end, float duration) {
            super();

            setStart(start);
            setEnd(end);
            setDuration(duration);
        }

        @Override
        protected void update(float percent) {
            super.update(percent);
            fade = getValue();
            updateVolume();
        }
    };

}
