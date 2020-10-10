package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class ProgressiveReelRollComponent extends DrawableComponent {
    private int numCols;
    private int numRows;

    private Image imgProOverlay[];

    private ProgressiveSingReel proSingleReel[];

    public ProgressiveReelRollComponent() {
        numCols = GameConfiguration.getInstance().reel.reels.length;
        numRows = GameData.getInstance().Context.Result.Stops.length / numCols;

        proSingleReel = new ProgressiveSingReel[numRows];
        for (int i = 0; i < numRows; i ++) {
            proSingleReel[i] = new ProgressiveSingReel(this, GameConfiguration.getInstance().progressiveReel.progressiveSingleReel, i);
            proSingleReel[i].setAlpha(0.0f);
            addActor(proSingleReel[i]);
        }

        imgProOverlay = new Image[numRows];
        for (int i = 0; i < numRows; i++) {
            Vector2 point = CoordinateLoader.getInstance().getPos("Symbol" + (numCols - 1));
            imgProOverlay[i] = ImageLoader.getInstance().load("Progressive/SpinRemaining/ProOverlay" + (i + 1));
            imgProOverlay[i].setPosition(point.x, 1080 - ( point.y + imgProOverlay[i].getHeight()));
            imgProOverlay[i].setVisible(false);
            addActor(imgProOverlay[i]);
        }

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                clearActions();

                for (int i = 0; i < numRows; i ++) {
                    proSingleReel[i].setAlpha(0.0f);
                    proSingleReel[i].onGameReset();
                }
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                for (int i = 0; i < numRows; i ++) {
                    proSingleReel[i].onLanguageChanged();
                }
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                pause();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
            }
        });
    }

    public void setProSingleReelVisible(boolean isVisible, int proReelId) {
        if (isVisible) {
            proSingleReel[proReelId].onGameReset();
            //proSingleReel[proReelId].addAction(fadeIn(0.5f));
            proSingleReel[proReelId].setAlpha(1.0f);
            imgProOverlay[proReelId].setVisible(true);
        } else {
            for (int i = 0; i < numRows; i ++) {
                proSingleReel[i].setAlpha(0.0f);
                imgProOverlay[i].setVisible(false);
            }
        }
    }

    public void startSpin(int proReelId, int pos, int level) {
        proSingleReel[proReelId].startSpin(pos, level);
    }

    @Override
    protected void update(float delta) throws Exception {
        super.update(delta);
    }
}
