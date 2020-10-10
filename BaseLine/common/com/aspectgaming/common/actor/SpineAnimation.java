package com.aspectgaming.common.actor;

import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.Animation;

public class SpineAnimation extends Actor {
    private SkeletonRenderer renderer;
    private SkeletonRendererDebug debugRenderer;

    private TextureAtlas atlas;
    private SkeletonData skeletonData;
    private Skeleton skeleton;
    private Animation anim;
    private float timeElapsed = 0;
    private Array<Event> events = new Array();

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isLoop = false;

    private Runnable endListener = null;

    public SpineAnimation(String path, String assetName, String animaitonName, String coodinate) {
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
        Vector2 point = CoordinateLoader.getInstance().getPos(coodinate);
        skeleton.setPosition(point.x, point.y);
        skeleton.updateWorldTransform();
    }

    public void resetAnim(String name) {
        anim = skeletonData.findAnimation(name);
    }

    public void play(boolean loop) {
        timeElapsed = 0;
        isPlaying = true;
        isLoop = loop;
    }

    public void stop() {
        timeElapsed =  0.0f;
        isPlaying = false;
    }

    public void dispose() {
        atlas.dispose();
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

    @Override
    public void resume() {
        if (isPaused()) {
            super.resume();
        }

        isPaused = false;
    }

    @Override
    public void act (float delta) {
        if (!isPlaying || isPaused) return;
        timeElapsed += delta;
        anim.apply(skeleton, timeElapsed, timeElapsed, isLoop, events, 1, Animation.MixPose.current, Animation.MixDirection.in);

        skeleton.updateWorldTransform();
        skeleton.update(Gdx.graphics.getDeltaTime());

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
        if (!isPlaying || isPaused) return;
        renderer.draw(batch, skeleton);
    }

    public void setEndListener(Runnable val) {
        endListener = val;
    }
}
