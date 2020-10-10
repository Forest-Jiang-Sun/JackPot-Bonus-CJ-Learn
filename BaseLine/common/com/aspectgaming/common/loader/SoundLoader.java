package com.aspectgaming.common.loader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;

/**
 * @author ligang.yao
 */
public final class SoundLoader {

    private final String I18N = "international";
    private final String EN = "en-US";
    private final String ZH = "zh-CHT";
    private final String SOUND_DIR = AspectGamingUtil.WORKING_DIR + "/assets/Sound/";

    private final Map<String, Map<String, Sound>> maps;
    private final List<Sound> sounds;
    private boolean isPaused;
    private ExecutorService exec;

    private final Logger log = LoggerFactory.getLogger(SoundLoader.class);
    private static final SoundLoader instance = new SoundLoader();

    public static SoundLoader getInstance() {
        return instance;
    }

    private SoundLoader() {
        long time = System.currentTimeMillis();

        log.info("Start loading sounds");

        String dirRoot = SOUND_DIR + GameConfiguration.getInstance().type + "/";
        Map<String, Sound> i18n_map = new ConcurrentHashMap<>();
        Map<String, Sound> en_map = new ConcurrentHashMap<>();
        Map<String, Sound> zh_map = new ConcurrentHashMap<>();
        List<Sound> list = new CopyOnWriteArrayList<>();
        exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        loadSounds(dirRoot + I18N, i18n_map, list);
        loadSounds(dirRoot + EN, en_map, list);
        loadSounds(dirRoot + ZH, zh_map, list);

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exec = null;
        }

        if (list.isEmpty()) {
            log.error("Failed to load sounds!");
        }

        // change to normal map and list for better performance
        sounds = new ArrayList<>(list);
        maps = new HashMap<>();

        Map<String, Sound> en = new HashMap<>(i18n_map);
        Map<String, Sound> zh = new HashMap<>(i18n_map);
        en.putAll(en_map);
        zh.putAll(zh_map);

        maps.put(EN, en);
        maps.put(ZH, zh);

        log.info("All game sounds are loaded in {}ms", (System.currentTimeMillis() - time));
    }
    private void loadSounds(String dir, Map<String, Sound> map, List<Sound> list) {
        Path path = FileSystems.getDefault().getPath(dir);
        loadSounds(path, path, map, list);
    }

    private void loadSounds(Path root, Path dir, final Map<String, Sound> map, final List<Sound> list) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (final Path path : ds) {
                if (Files.isDirectory(path)) {
                    loadSounds(root, path, map, list);
                } else {
                    String name = root.relativize(path).toString();
                    if (name.length() < 5) continue;

                    final String key = name.substring(0, name.length() - 4).replace("\\", "/");
                    String ext = name.substring(name.length() - 4).toLowerCase();

                    if (ext.equals(".mp3")) {
                        // TODO: check why using lambda here caused booting failure.
                        exec.execute(new Runnable() {
                            public void run() {
                                Sound sound = new Sound(path.toString());
                                map.put(key, sound);
                                list.add(sound);
                            }
                        });
                    } else if (ext.equals(".wav")) {
                        Sound sound = new Sound(path.toString());
                        map.put(key, sound);
                        list.add(sound);
                    }
                }
            }
        } catch (IOException e) {}
    }

    public void stopAllSounds() {
        for (Sound snd : sounds) {
            snd.stop();
        }
    }

    public void pauseAllSounds() {
        if (!isPaused) {
            isPaused = true;
            for (Sound snd : sounds) {
                snd.pause();
            }
        }
    }

    public void resumeAllSounds() {
        if (isPaused) {
            isPaused = false;
            for (Sound snd : sounds) {
                snd.resume();
            }
        }
    }

    public void updateVolume() {
        for (Sound snd : sounds) {
            snd.updateVolume();
        }
    }

    public void disposeAllSounds() {
        for (Sound snd : sounds) {
            snd.dispose();
        }
    }

    public Sound get(String name) {
        String language = GameData.getInstance().Context.Language;
        return name != null ? maps.get(language).get(name) : null;
    }

    public void play(String name) {
        if (name == null || name.isEmpty()) return;
        Sound snd = get(name);
        if (snd != null) snd.play();
    }

    public void loop(String name) {
        Sound snd = get(name);
        if (snd != null) snd.loop();
    }

    public void stop(String name) {
        Sound snd = get(name);
        if (snd != null) snd.stop();
    }
}
