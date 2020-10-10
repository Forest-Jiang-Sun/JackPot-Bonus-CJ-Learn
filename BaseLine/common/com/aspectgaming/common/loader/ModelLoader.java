package com.aspectgaming.common.loader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

public class ModelLoader {

    private static final String I18N = "international";
    private static final String EN = "en-US";
    private static final String ZH = "zh-CHT";
    private static final String OBJ_EXT = ".obj";
    private static final String OBJ_DIR = AspectGamingUtil.WORKING_DIR + "/assets/Model/";
    private ObjLoader objLoader = new ObjLoader();

    private final Map<String, Map<String, Model>> maps = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(ImageLoader.class);
    private static final ModelLoader instance = new ModelLoader();

    public static ModelLoader getInstance() {
        return instance;
    }

    private ModelLoader() {
        loadModels();
    }

    private void loadModels() {
        String dirRoot = OBJ_DIR + GameConfiguration.getInstance().type + "/";
        Map<String, Model> en_map = new HashMap<>();
        Map<String, Model> zh_map = new HashMap<>();

        loadObjs((dirRoot + I18N).replace("\\", "/"), dirRoot + I18N, en_map);

        zh_map.putAll(en_map);

        loadObjs((dirRoot + EN).replace("\\", "/"), dirRoot + EN, en_map);
        loadObjs((dirRoot + ZH).replace("\\", "/"), dirRoot + ZH, zh_map);

        if (en_map.isEmpty() && zh_map.isEmpty()) {
            log.error("Failed to load textures!");
        }

        maps.put(EN, en_map);
        maps.put(ZH, zh_map);
    }

    private void loadObjs(String rootPath, String path, Map<String, Model> map) {
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                loadObjs(rootPath, file.getAbsolutePath(), map);
            } else {
                String name = file.getAbsolutePath();
                if (name.endsWith(OBJ_EXT)) {
                    name = name.replace("\\", "/").replace(rootPath + "/", "").replace(OBJ_EXT, "");
                    map.put(name, objLoader.loadModel(new FileHandle(file)));
                }
            }
        }
    }
    public Model load(String name) {
        Map<String, Model> map = maps.get(GameData.getInstance().Context.Language);
        Model ar = map.get(name);
        if (ar == null) return null;
        return ar;

    }

    public Model load(String name, String coordName) {
//        Mesh img = load(name);
//        if (img != null) {
//            Vector2 point = CoordinateLoader.getInstance().getCoordinate(img, coordName);
//            img.setPosition(point.x, point.y);
//        }
//        return img;
        return null;
    }

}
