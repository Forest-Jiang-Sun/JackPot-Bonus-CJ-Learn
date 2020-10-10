package com.aspectgaming.common.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * @author ligang.yao
 */

//负责图片的调用加载
public class ImageLoader {

    private static final String I18N = "international";
    private static final String EN = "en-US";
    private static final String ZH = "zh-CHT";
    private static final String ATLAS_EXT = "*.atlas";
    private static final String TEXTURE_DIR = AspectGamingUtil.WORKING_DIR + "/assets/Textures/";
    private final String MEDIA_DIR = "D:/Media/";


    //AtlasRegion是TextureAtlas的静态内部类  TextureAtlas是用于            的类
    private final Map<String, Map<String, AtlasRegion>> maps = new HashMap<>();
    //日志模块
    private final Logger log = LoggerFactory.getLogger(ImageLoader.class);


    //标准的饿汉式单例模式
    private static final ImageLoader instance = new ImageLoader();

    public static ImageLoader getInstance() {
        return instance;
    }

    //无参构造方法
    private ImageLoader() {
        loadTextures();
    }

    ////加载纹理
    private void loadTextures() {
        String dirRoot = TEXTURE_DIR + GameConfiguration.getInstance().type + "/";
        Map<String, AtlasRegion> en_map = new HashMap<>();
        Map<String, AtlasRegion> zh_map = new HashMap<>();

        loadAtlas(dirRoot + I18N, en_map);

        zh_map.putAll(en_map);

        loadAtlas(dirRoot + EN, en_map);
        loadAtlas(dirRoot + ZH, zh_map);

        if (en_map.isEmpty() && zh_map.isEmpty()) {
            log.error("Failed to load textures!");
        }

        maps.put(EN, en_map);
        maps.put(ZH, zh_map);
    }

    private void loadAtlas(String dirPath, Map<String, AtlasRegion> map) {
        Path dir = FileSystems.getDefault().getPath(dirPath);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, ATLAS_EXT)) {
            for (Path file : ds) {
                TextureAtlas atlas = new TextureAtlas(new FileHandle(file.toFile()));
                for (AtlasRegion ar : atlas.getRegions()) {
                    map.put(ar.name, ar);
                }

                // process windows message to avoid game window long time no response during loading.
                Display.processMessages();
            }
        } catch (IOException e) {}
    }

    public Image load(String name) {
        name = LoaderUtil.filterPath(name);
        AtlasRegion ar = loadRegion(name);
        if (ar == null) return null;

        Image img = new Image();
        img.setPath(name);
        img.setSprite(newSprite(ar));
        return img;
    }

    public Image load(String name, String coordName) {
        Image img = load(name);
        if (img != null) {
            Vector2 point = CoordinateLoader.getInstance().getCoordinate(img, coordName);
            img.setPosition(point.x, point.y);
        }
        return img;
    }

    public Drawable loadDrawable(String name) {
        AtlasRegion ar = loadRegion(name);
        return ar != null ? new SpriteDrawable(newSprite(ar)) : null;
    }

    public void reload(Image image, String name) {
        if (image == null || name == null) return;

        name = LoaderUtil.filterPath(name);

        image.setPath(name);

        AtlasRegion ar = loadRegion(name);

        if (ar != null) {
            image.setSprite(newSprite(ar));
        }
    }

    public void reload(Image image) {
        if (image != null) {
            reload(image, image.getPath());
        }
    }

    public Sprite newSprite(String name) {
        AtlasRegion ar = loadRegion(name);
        return ar != null ? newSprite(ar) : null;
    }

    private AtlasRegion loadRegion(String name) {
        return maps.get(GameData.getInstance().Context.Language).get(name);
    }

    private Sprite newSprite(AtlasRegion region) {
        if (region.packedWidth == region.originalWidth && region.packedHeight == region.originalHeight) {
            return new Sprite(region);
        }
        return new AtlasSprite(region);
    }

    public Image loadMedia(String path) {
        path = LoaderUtil.filterPath(path);
        File file = new File(MEDIA_DIR + path);
        Texture tex = new Texture(new FileHandle(file));

        Image img = new Image();
        img.setSprite(new Sprite(tex));
        return img;
    }

    public int getAnimFrames(String namePrefix) {
        int count = 0;
        Map<String, AtlasRegion> map = maps.get(GameData.getInstance().Context.Language);
        boolean foundEnd = false;

        while (true) {
            if (map.containsKey(namePrefix + count)) {
                if (foundEnd) return count + 1;
                count += 5;
            } else {
                if (count == 0) return 0;
                foundEnd = true;
                count--;
            }
        }
    }

    public int countFrames(String pre, String post) {
        int count = 0;

        // first frame could start from 0 or 1
        if (loadRegion(getFrameName(pre, post, 0)) != null) {
            count++;
        }
        for (int i = 1;; i++) {
            if (loadRegion(getFrameName(pre, post, i)) != null) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private String getFrameName(String pre, String post, int frame) {
        if (pre != null) {
            if (post != null) {
                return pre + frame + post;
            } else {
                return pre + frame;
            }
        } else {
            if (post != null) {
                return frame + post;
            } else {
                return Integer.toString(frame);
            }
        }
    }
}
