package com.aspectgaming.common.actor;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.gdx.component.drawable.winshow.SymbolAnimationAssets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.Animation;

public class ShapeAnimation extends Actor {
    private static PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch();
    private SkeletonRenderer renderer;
    private SkeletonRendererDebug debugRenderer;

    private TextureAtlas atlas;
    private SkeletonData skeletonData;
    private Skeleton skeleton;
    private Animation anim;
    private float timeElapsed = 0;
    private float pauseTime = 0;
    private Array<Event> events = new Array();

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isLoop = false;
    private String   coordinate;
    private Runnable endListener = null;
    private boolean updatedTransform = false;
    private boolean activePaused = false;

    public ShapeAnimation(String path, String assetName, String animaitonName, String ord) {
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(false);
        debugRenderer = new SkeletonRendererDebug();

        String fullPath = AspectGamingUtil.WORKING_DIR + "/assets/SpineAnimation/";
        atlas = new TextureAtlas(Gdx.files.internal( fullPath +  path + "/" + assetName + ".atlas"));

        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(1.0f);
        skeletonData = json.readSkeletonData(Gdx.files.internal(fullPath +  path + "/" + assetName + ".json"));

        if (animaitonName.length() > 0) {
            anim = skeletonData.findAnimation(animaitonName);
        }

        skeleton = new Skeleton(skeletonData);
        coordinate = ord;
        if (coordinate.length() > 0) {
            Vector2 point = CoordinateLoader.getInstance().getPos(coordinate);
            skeleton.setPosition(point.x, point.y);
        } else {
            skeleton.setPosition(960, 0);
        }

        skeleton.updateWorldTransform();
    }

    public ShapeAnimation(TextureAtlas atlas_,  SkeletonData skeletonData_, String animaitonName, String ord) {
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(false);
        debugRenderer = new SkeletonRendererDebug();

        atlas = atlas_;
        skeletonData = skeletonData_;

        if (animaitonName.length() > 0) {
            anim = skeletonData.findAnimation(animaitonName);
        }

        skeleton = new Skeleton(skeletonData);
        coordinate = ord;
        if (coordinate.length() > 0) {
            Vector2 point = CoordinateLoader.getInstance().getPos(coordinate);
            skeleton.setPosition(point.x, point.y);
        } else {
            skeleton.setPosition(960, 0);
        }

        skeleton.updateWorldTransform();
    }

    public ShapeAnimation(SymbolAnimationAssets symbolAssets, int symbolId, String animaitonName, String ord) {
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(false);
        debugRenderer = new SkeletonRendererDebug();

        atlas = symbolAssets.getTextureAtlas(symbolId);
        skeletonData = symbolAssets.getSkeletonData(symbolId);

        if (animaitonName.length() > 0) {
            anim = skeletonData.findAnimation(animaitonName);
        }

        skeleton = new Skeleton(skeletonData);
        coordinate = ord;
        if (coordinate.length() > 0) {
            Vector2 point = CoordinateLoader.getInstance().getPos(coordinate);
            skeleton.setPosition(point.x, point.y);
        } else {
            skeleton.setPosition(960, 0);
        }

        skeleton.updateWorldTransform();
    }

    public void resetAnim(String name) {
        anim = skeletonData.findAnimation(name);
        timeElapsed =  0.0f;
    }

    public void switchAnim(String name) {
        anim = skeletonData.findAnimation(name);
        timeElapsed =  0.0f;
        isPlaying = true;

        skeleton = new Skeleton(skeletonData);
        if (coordinate.length() > 0) {
            Vector2 point = CoordinateLoader.getInstance().getPos(coordinate);
            skeleton.setPosition(point.x, point.y);
        } else {
            skeleton.setPosition(960, 0);
        }
    }

    public void switchAnimTo(String name, float time) {
        anim = skeletonData.findAnimation(name);
        pauseTime = time;
        timeElapsed =  0.0f;
        isPlaying = true;
        isLoop = false;
    }

    public void onLanguageChanged(String name) {
        anim = skeletonData.findAnimation(name);

        skeleton = new Skeleton(skeletonData);
        if (coordinate.length() > 0) {
            Vector2 point = CoordinateLoader.getInstance().getPos(coordinate);
            skeleton.setPosition(point.x, point.y);
        } else {
            skeleton.setPosition(960, 0);
        }

        //skeleton.updateWorldTransform();
    }

    public void play(boolean loop) {
        timeElapsed = 0;
        isPlaying = true;
        isLoop = loop;
    }

    public void stopLoop() {
        isLoop = false;
        timeElapsed %= anim.getDuration();
    }

    public void playTo(float time) {
        pauseTime = time;
        timeElapsed = 0;
        isPlaying = true;
        isLoop = true;
    }

    public void stop() {
        timeElapsed =  0.0f;
        if (isPlaying && endListener != null) {
            endListener.run();
        }
        isPlaying = false;
    }

    public void dispose() {
        //atlas.dispose();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        skeleton.setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        skeleton.setY(y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        skeleton.setPosition(x, y);
    }

    @Override
    public void pause() {
        if (!isPaused()) {
            super.pause();
        }

        isPaused = true;
    }

    public void pauseActive() {
        activePaused = true;
        pause();
    }

    @Override
    public void resume() {
        if (activePaused) return;

        if (isPaused()) {
            super.resume();
        }

        isPaused = false;
    }

    public void resumeActive() {
        activePaused = false;
        resume();
    }

    @Override
    public void act (float delta) {
        if (!isPlaying) return;
        updatedTransform = true;
        timeElapsed += delta;
        if(timeElapsed>10000){
            if(anim.getDuration()==0.0)
            {
                timeElapsed=0;
            }
            else
            {
                timeElapsed%=anim.getDuration();
            }
        }

        anim.apply(skeleton, timeElapsed, timeElapsed, isLoop, events, 1, Animation.MixPose.current, Animation.MixDirection.in);

        skeleton.updateWorldTransform();
        skeleton.update(Gdx.graphics.getDeltaTime());

        if (timeElapsed >= pauseTime && pauseTime!= 0) {
            pauseActive();
            pauseTime = 0;
            if (endListener != null) {
                endListener.run();
            }
            return;
        }

        if (timeElapsed > anim.getDuration() && !isLoop) {
            timeElapsed = 0;
            isPlaying = false;

            if (endListener != null) {
                endListener.run();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isPlaying ) return;
        if (!updatedTransform) {
            act(0.0001f);
        }
        batch.end();
        polygonBatch.begin();
        renderer.draw(polygonBatch, skeleton);
        polygonBatch.end();
        batch.begin();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setEndListener(Runnable val) {
        endListener = val;
    }
}
