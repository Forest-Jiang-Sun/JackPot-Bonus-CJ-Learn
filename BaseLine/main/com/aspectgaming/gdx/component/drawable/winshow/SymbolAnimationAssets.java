package com.aspectgaming.gdx.component.drawable.winshow;


import com.aspectgaming.common.util.AspectGamingUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

import java.util.HashMap;
import java.util.Map;

public class SymbolAnimationAssets  {
    private static final String TEXTURE_DIR = AspectGamingUtil.WORKING_DIR + "/assets/SpineAnimation/WinShow/";
    private final Map<Integer, TextureAtlas> atlasMaps = new HashMap<>();
    private final Map<Integer, SkeletonData> skeletonMaps = new HashMap<>();
    private int[] symbols = {1,2,3};

    public SymbolAnimationAssets() {
        loadAssets();
    }

    public void loadAssets() {
        for (int i =0; i<symbols.length; ++i) {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal( TEXTURE_DIR + symbols[i] + "/" + symbols[i]  + ".atlas"));

            SkeletonJson json = new SkeletonJson(atlas);
            json.setScale(1.0f);
            SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(TEXTURE_DIR + symbols[i]  + "/" + symbols[i] + ".json"));
            atlasMaps.put(symbols[i], atlas);
            skeletonMaps.put(symbols[i], skeletonData);
        }
    }

    public TextureAtlas getTextureAtlas(int symbolId) {
        return atlasMaps.get(symbolId);
    }

    public SkeletonData getSkeletonData(int symbolId) {
        return skeletonMaps.get(symbolId);
    }
}

