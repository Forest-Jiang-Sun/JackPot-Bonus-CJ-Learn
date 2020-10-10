package com.aspectgaming.gdx.component.drawable.diagnostic;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.GameStateChangedEvent;
import com.aspectgaming.common.event.screen.CloseDiagnosticUIEvent;
import com.aspectgaming.common.event.screen.ShowDiagnosticUIEvent;
import com.aspectgaming.common.event.machine.ChangeTestReelEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * This is a Test Mode.
 *
 * @author kumo.wang
 */
public class DiagnosticComponent extends DrawableComponent {

    private static final float REEL_SWITCH_TIME = 0.0f;

    private boolean inDiagnostic;
    private Image waterMask;
    private Image message;
    private Animation boxReel;
    private Vector2 offset;
    private Vector2 upBtnOffset;

    private int numReels;

    private Sound sndButton;
    private ReelComponent reels;
    private Button downBtn;
    private Button upBtn;

    public DiagnosticComponent() {
        setTouchable(Touchable.enabled);
        setAlpha(0);

        numReels = GameConfiguration.getInstance().reel.reels.length;

        offset = CoordinateLoader.getInstance().getOffset("DiagnosticBox");
        upBtnOffset = CoordinateLoader.getInstance().getOffset("upBtnOffset");

        waterMask = ImageLoader.getInstance().load("Test/watermask", "Background");
        addActor(waterMask);

        message = ImageLoader.getInstance().load("Test/message", "DiagnosticMessage");
        addActor(message);

        boxReel = new Animation("Test/Frame/", 1f);
        boxReel.play(-1);
        addActor(boxReel);

        sndButton = SoundLoader.getInstance().get("button/button");
        reels = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
        downBtn = new Button("Test/Button/scrollDown_");
        downBtn.setOnClicked(new Runnable() {
            @Override
            public void run() {
                sndButton.play();
                reels.changeTestReelStopDowns();
            }
        });

        upBtn = new Button("Test/Button/scrollUp_");
        upBtn.setOnClicked(new Runnable() {
            @Override
            public void run() {
                sndButton.play();
                reels.changeTestReelStopUps();
            }
        });

        ClickListener onClicked = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reels.testReelIDs();
            }
        };
        reels.addListener(onClicked);

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.TestMode != inDiagnostic) {
                    inDiagnostic = GameData.getInstance().Context.TestMode;
                    if (inDiagnostic) {
                        GameData.dignosticReelIndex = 0;
                        Vector2 point = getFramePos(GameData.dignosticReelIndex);
                        boxReel.setPosition(point.x, point.y);
                        boxReel.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);

                        downBtn.setPosition(point.x, point.y-100);
                        downBtn.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);
                        addActor(downBtn);

                        upBtn.setPosition(point.x + upBtnOffset.x, point.y + upBtnOffset.y);
                        upBtn.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);
                        addActor(upBtn);

                        addAction(fadeIn(0.5f));
                    } else {
                        addAction(fadeOut(0.5f));
                    }
                }
            }
        });

        registerEvent(new ChangeTestReelEvent() {
            @Override
            public void execute(Object... obj) {
                GameData.dignosticReelIndex = (int) obj[0];
                Vector2 point = getFramePos(GameData.dignosticReelIndex);
                boxReel.addAction(moveTo(point.x, point.y, REEL_SWITCH_TIME));
                downBtn.setPosition(point.x + 12, point.y-100);
                upBtn.setPosition(point.x + upBtnOffset.x + 12, point.y + upBtnOffset.y);
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                boxReel.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);
                downBtn.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);
                upBtn.setVisible(GameData.getInstance().Context.GameState == State.GameIdle);
            }
        });

        registerEvent(new ShowDiagnosticUIEvent() {
            @Override
            public void execute(Object... obj) {
                showDiagnosticUI();
            }
        });

        registerEvent(new CloseDiagnosticUIEvent() {
            @Override
            public void execute(Object... obj) {
                closeDiagnosticUI();
            }
        });
    }

    private void showDiagnosticUI() {
        GameData.dignosticReelIndex = 0;
        Vector2 point = getFramePos(GameData.dignosticReelIndex);
        boxReel.setPosition(point.x, point.y);
        boxReel.setVisible(true);

        downBtn.setPosition(point.x, point.y-100);
        downBtn.setVisible(true);
        addActor(downBtn);

        upBtn.setPosition(point.x + upBtnOffset.x, point.y + upBtnOffset.y);
        upBtn.setVisible(true);
        addActor(upBtn);

        addAction(fadeIn(0.5f));
    }

    private void closeDiagnosticUI() {
        addAction(fadeOut(0.5f));
    }

    private Vector2 getFramePos(int reel) {
        Vector2 point = CoordinateLoader.getInstance().getCoordinate(boxReel, "SingleReel" + reel);
        return new Vector2(point.x + offset.x, point.y + offset.y);
    }
}
