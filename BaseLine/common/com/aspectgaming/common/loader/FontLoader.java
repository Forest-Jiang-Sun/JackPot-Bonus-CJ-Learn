package com.aspectgaming.common.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.aspectgaming.common.actor.SpriteFont;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * @author ligang.yao
 */
public class FontLoader {

    private static final String I18N = "international";
    private static final String EN = "en-US";
    private static final String ZH = "zh-CHT";
    private static final String FONT_EXT = "*.fnt";
    private static final String FONT_DIR = AspectGamingUtil.WORKING_DIR + "/assets/Fonts/";

    private static final BitmapFont FONT_DEFAULT = new BitmapFont();

    private TextureAtlas i18n;
    
    private final Map<String, Map<String, BitmapFont>> fonts = new HashMap<>();
    private final Map<String, SpriteFont> spriteFonts = new HashMap<>();
    
    private static final FontLoader instance = new FontLoader();

    public static FontLoader getInstance() {
        return instance;
    }

    private FontLoader() {
        TextureAtlas en = null;
        TextureAtlas zh = null;

        String dirRoot = FONT_DIR + GameConfiguration.getInstance().type + "/";

        File path = new File(dirRoot + I18N + "/" + I18N + ".atlas");
        if (path.exists()) i18n = new TextureAtlas(new FileHandle(path));

        path = new File(dirRoot + EN + "/" + EN + ".atlas");
        if (path.exists()) en = new TextureAtlas(new FileHandle(path));

        path = new File(dirRoot + ZH + "/" + ZH + ".atlas");
        if (path.exists()) zh = new TextureAtlas(new FileHandle(path));

        Map<String, BitmapFont> enFontMap = new HashMap<>();
        Map<String, BitmapFont> zhFontMap = new HashMap<>();

        loadBitmapFonts(dirRoot + I18N, enFontMap, i18n);
        zhFontMap.putAll(enFontMap);

        loadBitmapFonts(dirRoot + EN, enFontMap, en);
        loadBitmapFonts(dirRoot + ZH, zhFontMap, zh);

        fonts.put(EN, enFontMap);
        fonts.put(ZH, zhFontMap);
    }

    private void loadBitmapFonts(String dirPath, Map<String, BitmapFont> fontMap, TextureAtlas atlasMap) {
        Path dir = FileSystems.getDefault().getPath(dirPath);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, FONT_EXT)) {
            for (Path file : ds) {
                String name = dir.relativize(file).toString();
                name = name.substring(0, name.lastIndexOf('.'));

                fontMap.put(name, new BitmapFont(new FileHandle(file.toFile()), atlasMap.findRegion(name), false));
            }
        } catch (IOException e) {}

    }

    public BitmapFont load(String name) {
        if (name == null || name.isEmpty()) {
            return FONT_DEFAULT;
        } else {
            return fonts.get(GameData.getInstance().Context.Language).get(name);
        }
    }

    public SpriteFont loadSpriteFont(String name) {
        SpriteFont fnt = spriteFonts.get(name);
        if (fnt != null) return fnt;
        
        fnt = new SpriteFont(name, i18n);
        spriteFonts.put(name, fnt);
        return fnt;
    }
}
