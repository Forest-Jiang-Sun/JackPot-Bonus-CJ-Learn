package com.aspectgaming.gdx.component.drawable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.configuration.common.SpriteConfiguration;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author johnny.shi & ligang.yao
 */
public abstract class DrawableComponent extends Component {

    protected final Map<String, Sprite> spritesMap = new HashMap<>();
    protected final List<Video> videos = new LinkedList<>();
    protected final List<ShapeAnimation> shapeAnims = new LinkedList<>();
    @Override
    public void dispose() {
        super.dispose();
        disposeVideos();
        disposeShapeAnims();
    }

    public void disposeVideos() {
        for (Video video : videos) {
            video.dispose();
        }
        videos.clear();
    }

    public void disposeShapeAnims() {
        for (ShapeAnimation anim : shapeAnims) {
            anim.dispose();
        }
        shapeAnims.clear();
    }

    @Override
    public void clearChildren() {
        for (Actor actor : getChildren()) {
            actor.clearActions();
        }
        super.clearChildren();
        disposeVideos();
        disposeShapeAnims();
    }

    @Override
    public void clearActions() {
        super.clearActions();
        for (Actor actor : getChildren()) {
            actor.clearActions();
        }
    }

    @Override
    public boolean removeActor(Actor actor) {
        if (actor instanceof Video) {
            ((Video) actor).dispose();
            videos.remove(actor);
        }

        if (actor instanceof ShapeAnimation) {
            ((ShapeAnimation) actor).dispose();
            shapeAnims.remove(actor);
        }
        actor.clearActions();
        return super.removeActor(actor);
    }

    @Override
    public void addActor(Actor actor) {
        if (actor == null) return;

        if (actor instanceof Video) {
            videos.add((Video) actor);
        }

        if (actor instanceof ShapeAnimation) {
            shapeAnims.add((ShapeAnimation)actor);
        }
        super.addActor(actor);
    }

    @Override
    public void addActorAt(int index, Actor actor) {
        if (actor == null) return;

        if (actor instanceof Video) {
            videos.add((Video) actor);
        }

        if (actor instanceof ShapeAnimation) {
            shapeAnims.add((ShapeAnimation)actor);
        }
        super.addActorAt(index, actor);
    }

    @Override
    public void addActorBefore(Actor actorBefore, Actor actor) {
        if (actor == null) return;

        if (actor instanceof Video) {
            videos.add((Video) actor);
        }

        if (actor instanceof ShapeAnimation) {
            shapeAnims.add((ShapeAnimation)actor);
        }
        super.addActorBefore(actorBefore, actor);
    }

    @Override
    public void addActorAfter(Actor actorAfter, Actor actor) {
        if (actor == null) return;

        if (actor instanceof Video) {
            videos.add((Video) actor);
        }

        if (actor instanceof ShapeAnimation) {
            shapeAnims.add((ShapeAnimation)actor);
        }
        super.addActorAfter(actorAfter, actor);
    }

    public void addActor(Actor actor, String coordName) {
        if (actor == null) return;

        Vector2 point = CoordinateLoader.getInstance().getCoordinate(actor, coordName);
        actor.setPosition(point.x, point.y);
        addActor(actor);
    }

    @Override
    public void pause() {
        if (!isPaused()) {
            super.pause();
            for (Video video : videos) {
                video.pause();
            }
        }
    }

    @Override
    public void resume() {
        if (isPaused()) {
            super.resume();
            for (Video video : videos) {
                video.resume();
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getAlpha() < 0.01) return null;
        return super.hit(x, y, touchable);
    }

    protected Actor getSprite(String name) {
        Sprite sp = spritesMap.get(name);
        if (sp != null) return sp.actor;
        return null;
    }

    protected void loadSprites(SpriteConfiguration[] sprites) {
        if (sprites == null) return;

        for (SpriteConfiguration sc : sprites) {
            String name = sc.name;

            Actor actor;
            if ("video".equals(sc.type)) {
                actor = VideoLoader.Instance.load(sc.path);
            } else if ("animation".equals(sc.type)) {
                actor = new Animation(sc.path, sc.fps, sc.delay);
            } else {
                actor = ImageLoader.getInstance().load(sc.path);
            }

            Vector2 pos = CoordinateLoader.getInstance().getCoordinate(actor, name);
            actor.setPosition(pos.x, pos.y);
            addActor(actor);
            spritesMap.put(name, new Sprite(actor, sc));
        }
    }

    protected void updateSpritesLanguage() {
        for (Sprite sprite : spritesMap.values()) {
            SpriteConfiguration sc = sprite.cfg;

            if (!sc.isMultilingual) continue;

            Actor actor;
            if (sprite.actor instanceof Video) {
                actor = VideoLoader.Instance.load(sc.path);
            } else if (sprite.actor instanceof Animation) {
                actor = new Animation(sc.path, sc.fps, sc.delay);
            } else {
                ImageLoader.getInstance().reload((Image) sprite.actor);
                actor = sprite.actor;
            }

            Vector2 position = CoordinateLoader.getInstance().getCoordinate(actor, sc.name);
            actor.setPosition(position.x, position.y);
            actor.setVisible(sprite.actor.isVisible());

            addActorAfter(sprite.actor, actor);
            removeActor(sprite.actor);

            sprite.actor = actor;
        }
    }

    protected void showSprite(Actor sprite, boolean visible, boolean isLoop) {
        if (sprite == null) return;

        if (sprite instanceof Video) {
            Video video = (Video) sprite;

            if (visible) {
                if (isLoop) {
                    video.loop();
                } else {
                    video.play();
                }
            } else {
                video.stop();
            }

        } else if (sprite instanceof Animation) {
            Animation anim = (Animation) sprite;

            if (visible) {
                if (isLoop) {
                    anim.play(-1);
                } else {
                    anim.play(1);
                }
            } else {
                anim.stop();
                anim.stopAtFrame(0);
            }
        }

        sprite.setVisible(visible);
    }

    protected void showSprite(Actor sprite, boolean visible) {
        showSprite(sprite, visible, false);
    }

    protected void showSprite(Actor sprite) {
        showSprite(sprite, true, false);
    }

    protected void showSprite(String name) {
        showSprite(getSprite(name));
    }

    protected void hideSprite(Actor sprite) {
        showSprite(sprite, false, false);
    }

    protected void hideSprite(String name) {
        hideSprite(getSprite(name));
    }

    protected void showAllSprites() {
        for (Sprite sprite : spritesMap.values()) {
            showSprite(sprite.actor);
        }
    }

    protected void hideAllSprites() {
        for (Sprite sprite : spritesMap.values()) {
            hideSprite(sprite.actor);
        }
    }

    private static class Sprite {
        Actor actor;
        SpriteConfiguration cfg;

        Sprite(Actor actor, SpriteConfiguration cfg) {
            this.actor = actor;
            this.cfg = cfg;
        }
    }
}
