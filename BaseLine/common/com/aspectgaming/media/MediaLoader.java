package com.aspectgaming.media;

import java.util.HashMap;
import java.util.Map;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;

/**
 * @author ligang.yao
 */
public class MediaLoader {

    private final String MEDIA_DIR = "D:/Media/";
    private final Map<String, MediaSound> sounds = new HashMap<>();

    private static final MediaLoader instance = new MediaLoader();

    private MediaLoader() {}

    public static MediaLoader getInstance() {
        return instance;
    }

    public Video loadVideo(String name) {
        if (name.indexOf('/') < 0) { // if not absolute path
            name = MEDIA_DIR + name + ".mp4";
        }
        return VideoLoader.Instance.loadVideo(name, GameConfiguration.getInstance().display.cachingVideo);
    }

    public MediaSound loadSound(String name) {
        if (sounds.containsKey(name)) {
            return sounds.get(name);
        } else {
            if (name.indexOf('/') < 0) { // if not absolute path
                name = MEDIA_DIR + name + ".wav";
            }
            MediaSound snd = new MediaSound(name);
            sounds.put(name, snd);
            return snd;
        }
    }

    public void updateVolume(int volume) {
        if (MediaData.Volume != volume) {
            MediaData.Volume = volume;

            for (MediaSound snd : sounds.values()) {
                if (snd != null) {
                    snd.updateVolume();
                }
            }
        }
    }

    public void disposeAll() {
        for (MediaSound snd : sounds.values()) {
            if (snd != null) {
                snd.dispose();
            }
        }
    }
}
