package com.aspectgaming.gdx.component.drawable.attract;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.event.screen.AttractStartEvent;
import com.aspectgaming.common.event.screen.AttractStopEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * @author ligang.yao
 */
public class AttractMovieComponent extends DrawableComponent {
    private Video reelsAnim;
    private boolean isPlaying;

    public AttractMovieComponent() {
        isPlaying = false;

        registerEvent(new AttractStartEvent() {
            @Override
            public void execute(Object... obj) {
                start();
            }
        });

        registerEvent(new AttractStopEvent() {
            @Override
            public void execute(Object... obj) {
                stop();
            }
        });
    }

    private void start() {
        if (isPlaying) return;

        loadReelsAnim();

        if (reelsAnim != null) {
            reelsAnim.loop();
            isPlaying = true;
        }
    }

    private void stop() {
        if (reelsAnim != null && isPlaying) {
            reelsAnim.stop();
            isPlaying = false;
        }

        if (reelsAnim != null) {
            removeActor(reelsAnim);
            reelsAnim = null;
        }
    }

    private void loadReelsAnim() {
        clear();
        if (reelsAnim != null) {
            removeActor(reelsAnim);
            reelsAnim = null;
        }

        reelsAnim = VideoLoader.Instance.load("Attract/attract", false);
        if (reelsAnim == null) return;

        reelsAnim.setAutoVisible(true);
        addActor(reelsAnim);
        Vector2 point = CoordinateLoader.getInstance().getCoordinate(reelsAnim, "ReelsAnim");
        reelsAnim.setPosition(point.x, point.y);
    }
}
