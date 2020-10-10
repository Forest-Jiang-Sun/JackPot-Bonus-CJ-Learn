package com.aspectgaming.common.loader;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.ResolutionConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.common.video.Video;

/**
 * @author ligang.yao
 */
public final class VideoLoader {

    private final Logger log = LoggerFactory.getLogger(VideoLoader.class);
    private final String Path1 = AspectGamingUtil.WORKING_DIR + "/assets/Videos/";
    private final String International = "international/";

    private final boolean usePBO = GameConfiguration.getInstance().openGL.pixelBufferObject;
    private final boolean caching = GameConfiguration.getInstance().display.cachingVideo;

    private String folder = null;

    public static final VideoLoader Instance = new VideoLoader();

    private VideoLoader() {
        getVideoFolderString();
        log.info(usePBO ? "OpenGL PBO Enabled" : "OpenGL PBO Disabled");
    }

    public Video load(String name, boolean isCaching) {
        name = LoaderUtil.filterPath(name);

        String dir = Path1;

        if (folder != null) {
            dir += GameConfiguration.getInstance().type + "/" + folder + "/";
        }

        Video video = loadVideo(dir + GameData.getInstance().Context.Language + "/" + name + ".mp4", isCaching);
        if (video == null) {
            video = loadVideo(dir + International + name + ".mp4", isCaching);
        }
        if (video == null) {
            video = loadVideo(dir + name + ".mp4", isCaching);
        }
        if (video == null) {
            log.error("Failed to load video: {}", name);
            throw new RuntimeException("Missing video: " + name);
        }
        return video;
    }

    public Video load(String name) {
        return load(name, caching);
    }

    public Video loadVideo(String path, boolean isCaching) {
        path = LoaderUtil.filterPath(path);

        File file = new File(path);
        return file.exists() ? new Video(file.getAbsolutePath(), isCaching, true, usePBO) : null;
    }

    public void getVideoFolderString() {
        DisplayConfiguration display = GameConfiguration.getInstance().display;

        for (ResolutionConfiguration rc : GameConfiguration.getInstance().resolutions) {
            if (display.width == rc.width && display.height == rc.height) {
                folder = rc.video;
            }
        }
    }
}
