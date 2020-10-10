package com.aspectgaming.common.actor;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;

public class SpriteFont {

    private final Map<Character, AtlasRegion> map = new HashMap<>();

    public SpriteFont(String fontName, TextureAtlas atlas) {
        for (AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith(fontName)) {
                String key = region.name.substring(fontName.length() + 1);
                if (key.indexOf("/") != -1) continue;
                char character = (char) Integer.parseInt(key, 16);
                map.put(character, region);
            }
        }
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Sprite newSprite(char character) {
        AtlasRegion region = map.get(character);

        if (region == null) return null;

        if (region.packedWidth == region.originalWidth && region.packedHeight == region.originalHeight) {
            return new Sprite(region);
        }
        return new AtlasSprite(region);
    }
}
