package com.aspectgaming.gdx.component.drawable.reel;

import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.*;
import com.aspectgaming.common.event.screen.ShowDiagnosticUIEvent;
import com.aspectgaming.common.event.screen.CloseDiagnosticUIEvent;
import com.aspectgaming.common.event.wild.RandomWildIntroEvent;
import com.aspectgaming.common.event.wild.RandomWildOutroEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.freegames.FreeGameAnticipationSpinComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import static com.aspectgaming.gdx.component.drawable.reel.Symbol.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;

/**
 * @author johnny.shi & ligang.yao
 */
public class ReelComponent extends DrawableComponent {

    private final List<SingleReel> reels = new ArrayList<>();
    private int symbolCount;
    public int startBreakReelId;
    public boolean isSpinning = false;
    public boolean isFastSpin = false;
    public boolean isInRandomWild = false;
    public boolean isInPreshow = false;
    public float preFeatureDuration = 0.0f;
    private Sound sndAnticipation;
    private Sound sndAnticipationOther;
    private Sound sndAnticipation2X3X2X;
    private boolean[] have7XSound;
    private Sound[] sndLine17X;
    private Sound[] sndOther7X;
    private Sound[] snd2X3X2X;
    private int numRows = 3;
    private TextureLabel touchReelStop1;
    private TextureLabel touchReelStop2;
    private TextureLabel touchReelStop3;

    FreeGameAnticipationSpinComponent anticipationSpincmp;

    public ReelComponent() {
        setTouchable(Touchable.childrenOnly);
        symbolCount = GameData.getInstance().Context.Result.Stops.length;
        sndAnticipation = SoundLoader.getInstance().get("reel/Anticipation");
        sndAnticipationOther = SoundLoader.getInstance().get("reel/AnticipationOther");
        sndAnticipation2X3X2X = SoundLoader.getInstance().get("reel/Anticipation2X3X2X");
        have7XSound = new boolean[numRows];
        sndLine17X = new Sound[numRows];
        sndOther7X = new Sound[numRows];
        snd2X3X2X =new Sound[numRows];

        for (int i = 0; i < numRows; i++) {
            sndLine17X[i] = SoundLoader.getInstance().get("reel/line1Sound7X" + (i + 1));

        }

        for (int i = 0; i < numRows; i++) {
            sndOther7X[i] = SoundLoader.getInstance().get("reel/otherSound7X" + (i + 1));
        }

        for (int i = 0; i < numRows; i++) {
            snd2X3X2X[i] = SoundLoader.getInstance().get("reel/snd2X3X2X" + (i + 1));
        }

        for (int i = 0; i < GameConfiguration.getInstance().reel.reels.length; i++) {
            SingleReel singleReel = new SingleReel(this, GameConfiguration.getInstance().reel.getSingleReel(i));
            addActor(singleReel);
            reels.add(singleReel);
        }

        ClickListener onClicked1 = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SingleReel singleReel = reels.get(0);
                singleReel.reelStop();
                log.info("****************reelStop1*************");
            }
        };

        ClickListener onClicked2 = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SingleReel singleReel = reels.get(1);
                singleReel.reelStop();
                log.info("****************reelStop2*************");
            }
        };

        ClickListener onClicked3 = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SingleReel singleReel = reels.get(2);
                singleReel.reelStop();
                log.info("****************reelStop3*************");
            }
        };

        touchReelStop1=new TextureLabel("touchReelStop1", Align.center, Align.center);
        Rectangle bounds = CoordinateLoader.getInstance().getBound("touchReelStop1");
        touchReelStop1.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        touchReelStop1.addListener(onClicked1);
        touchReelStop1.setTouchable(Touchable.disabled);
        addActor(touchReelStop1);

        touchReelStop2=new TextureLabel("touchReelStop2", Align.center, Align.center);
        bounds = CoordinateLoader.getInstance().getBound("touchReelStop2");
        touchReelStop2.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        touchReelStop2.addListener(onClicked2);
        touchReelStop2.setTouchable(Touchable.disabled);
        addActor(touchReelStop2);

        touchReelStop3=new TextureLabel("touchReelStop3", Align.center, Align.center);
        bounds = CoordinateLoader.getInstance().getBound("touchReelStop3");
        touchReelStop3.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        touchReelStop3.addListener(onClicked3);
        touchReelStop3.setTouchable(Touchable.disabled);
        addActor(touchReelStop3);


        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                updateMask();

                for (int i = 0; i < symbolCount; i++) {
                    Symbol symbol = getSymbol(i);
                    if (symbol != null) {
                        symbol.setColor(Color.WHITE);
                    }
                }

                for (SingleReel singleReel : reels) {
                    singleReel.onGameReset();
                }

                if (GameData.getInstance().Context.TestMode) {
                    GameClient.getInstance().setTestStops(getStops());
                }

                anticipationSpincmp = (FreeGameAnticipationSpinComponent) Content.getInstance().getComponent(Content.FREEGAMEANTICIPATIONSPINCOMPONENT);
            }
        });

        registerEvent(new ShowDiagnosticUIEvent() {
            @Override
            public void execute(Object... obj) {
                updateMask();
                int[] allStops = {1, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                GameData.getInstance().Context.Result.Stops = allStops;

                for (SingleReel singleReel : reels) {
                    singleReel.setStopSymbols();
                    singleReel.resetPositions();
                    singleReel.onGameReset();
                    singleReel.setSymbolsVisible();
                }

                if (GameData.getInstance().Context.TestMode) {
                    GameClient.getInstance().setTestStops(getStops());
                }
            }
        });

        registerEvent(new CloseDiagnosticUIEvent() {
            @Override
            public void execute(Object... obj) {
                for (SingleReel singleReel : reels) {
                    singleReel.setTouchable(Touchable.childrenOnly);
                    singleReel.setReelPosVisible(false);
                }
            }
        });

        /*
        registerEvent(new GameModeChangeEvent() {

            @Override
            public void execute(Object... obj) {
                for (SingleReel reel : reels) {
                    reel.onGameModeChanged();
                }
            }
        });
        */

        registerEvent(new SingleReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                int reelId = (int) obj[0];
                sndAnticipation.stop();
                sndAnticipationOther.stop();
                sndAnticipation2X3X2X.stop();
                if (reelId==0){
                    touchReelStop1.setTouchable(Touchable.disabled);
                    touchReelStop2.setTouchable(Touchable.enabled);
                    touchReelStop3.setTouchable(Touchable.disabled);
                }
                if (reelId==1){
                    touchReelStop1.setTouchable(Touchable.disabled);
                    touchReelStop2.setTouchable(Touchable.disabled);
                    touchReelStop3.setTouchable(Touchable.enabled);
                }
                if (reelId==2){
                    touchReelStop1.setTouchable(Touchable.disabled);
                    touchReelStop2.setTouchable(Touchable.disabled);
                    touchReelStop3.setTouchable(Touchable.disabled);
                }


                int[] stops = GameData.getInstance().Context.Result.Stops;

                if (reelId == 1) {
                    if (stops[6] == D7 && stops[7] == D7) {
                        sndAnticipation.loop();
                    }
                    if ((stops[3]==D7 || stops[9]==D7) && (stops[4]==D7 || stops[7]==D7 || stops[10]==D7 )) {
                        sndAnticipationOther.loop();
                    }
                    if (stops[3]==D2 && stops[4]==D2){
                        sndAnticipation2X3X2X.loop();
                    }
                }

                if (have7XSound[0] && reelId == 0) {
                    if (stops[6] == D7) {
                        sndLine17X[0].play();
                    } else {
                        sndOther7X[0].play();
                    }
                }

                if (have7XSound[1] && reelId == 1) {
                    if (stops[6] == D7 && stops[7] == D7) {
                        sndLine17X[1].play();
                    } else {
                        sndOther7X[1].play();
                    }
                }

                if (have7XSound[2] && reelId == 2) {
                    if (stops[6] == D7 && stops[7] == D7 && stops[8] == D7) {
                        sndLine17X[2].play();
                    } else {
                        sndOther7X[2].play();
                    }
                }

                if (stops[6]==D3 && reelId==0){
                    snd2X3X2X[reelId].play();
                }

                if (stops[6]==D3 && stops[7]==D3 && reelId==1){
                    snd2X3X2X[reelId].play();
                }

                if (stops[6]==D3 && stops[7]==D3 && stops[8]==D3 && reelId==2){
                    snd2X3X2X[reelId].play();
                }
            }
        });

        registerEvent(new OutFreeGameIntroEvent() {

            @Override
            public void execute(Object... obj) {
                for (SingleReel reel : reels) {
                    reel.onGameModeChanged();
                }
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                for (SingleReel singleReel : reels) {
                    singleReel.onLanguageChanged();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                preFeatureDuration = 0;

                for (SingleReel singleReel : reels) {
                    singleReel.clearActions();
                    singleReel.startSpin();
                }

                isSpinning = true;
                isFastSpin = false;
                GameData.isReelStopped = false;
                startBreakReelId = -1;

                int[] stops = GameData.getInstance().Context.Result.Stops;

                if (stops[3] == D7 || stops[6] == D7 || stops[9] == D7) {
                    have7XSound[0] = true;
                } else {
                    have7XSound[0] = false;
                }
                if (stops[4] == D7 || stops[7] == D7 || stops[10] == D7) {
                    have7XSound[1] = true;
                } else {
                    have7XSound[1] = false;
                }
                if (stops[5] == D7 || stops[8] == D7 || stops[11] == D7) {
                    have7XSound[2] = true;
                } else {
                    have7XSound[2] = false;
                }

                touchReelStop1.setTouchable(Touchable.enabled);
            }
        });


        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                isSpinning = false;
                isFastSpin = false;
                isInPreshow = false;
                touchReelStop1.setTouchable(Touchable.disabled);
                touchReelStop2.setTouchable(Touchable.disabled);
                touchReelStop3.setTouchable(Touchable.disabled);
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

        registerEvent(new OutFreeGameOutroEvent() {
            @Override
            public void execute(Object... obj) {
                for (SingleReel singleReel : reels) {
                    singleReel.updateSymbol();
                }
            }
        });

        registerEvent(new PaytableChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateStrips();

                if (GameData.getInstance().Context.TestMode) {
                    GameClient.getInstance().setTestStops(getStops());
                }
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                updateMask();
                updateStrips();

                if (GameData.getInstance().Context.TestMode) {
                    GameClient.getInstance().setTestStops(getStops());
                }
            }
        });

        registerEvent(new ChangeTestReelStopEvent() {
            @Override
            public void execute(Object... obj) {
                for (SingleReel singleReel : reels) {
                    singleReel.changeTestReelStopDown();
                }
            }
        });


        registerEvent(new RandomWildIntroEvent() {
            @Override
            public void execute(Object... obj) {
                isInRandomWild = true;
            }
        });

        registerEvent(new RandomWildOutroEvent() {
            @Override
            public void execute(Object... obj) {
                isInRandomWild = false;
            }
        });

    }

    public void updateStrips() {
        for (SingleReel singleReel : reels) {
            singleReel.updateStrip();
        }
    }

    public void changeTestReelStopDowns() {
        for (SingleReel singleReel : reels) {
            singleReel.changeTestReelStopDown();
        }
    }

    public void changeTestReelStopUps() {
        for (SingleReel singleReel : reels) {
            singleReel.changeTestReelStopUp();
        }
    }

    public void testReelIDs() {
        for (SingleReel singleReel : reels) {
            singleReel.testReelID();
        }
    }

    public void reelStop() {
        if (isFastSpin) return;

        for (SingleReel singleReel : reels) {
            if (singleReel.isSpinning()) {
                if (startBreakReelId == -1) {
                    startBreakReelId = singleReel.getReelID();
                }
                singleReel.reelStop();
            }
        }
        GameData.isReelStopped = true;
    }

    @Override
    protected void update(float delta) throws Exception {
        super.update(delta);

        if (isSpinning) {
            isSpinning = false;
            for (SingleReel singleReel : reels) {
                if (singleReel.isSpinning()) {
                    isSpinning = true;
                }
            }

            if (!isSpinning) {
                int[] wildPos = null;
                for (MathParam param : GameData.getInstance().Context.MathParams) {
                    if (param.Key.equals("RANDOMWILD")) {
                        wildPos = CommonUtil.stringToArray(param.Value);
                        break;
                    }
                }

                EventMachine.getInstance().offerEvent(ReelStoppedEvent.class);
            }
        }
    }

    public void revertSymbol() {
        for (SingleReel singleReel : reels) {
            singleReel.updateSymbol();
        }
    }

    public Symbol getSymbol(int stopIndex) {
        for (SingleReel singleReel : reels) {
            Symbol symbol = singleReel.getSymbol(stopIndex);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public SingleReel getReel(int reelId) {
        return this.reels.get(reelId);
    }

    public String getStops() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbolCount; i++) {
            Symbol symbol = getSymbol(i);
            sb.append(symbol.symbolIndex);
            if (i != symbolCount - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private void updateMask() {
        // only works for ways game
        if (GameData.getInstance().isWaysGame()) {
            int idx = 1;
            for (SingleReel singleReel : reels) {
                singleReel.showMask(idx > GameData.getInstance().Context.Selections);
                idx++;
            }
        }
    }

    public void stopWinShow() {
        for (SingleReel singleReel : reels) {
            singleReel.stopWinShow();
        }
    }

    public boolean isTriProAnticipation() {
        return false;
    }

    public void setSymbolAlpha(int stopIndex, float alpha) {
        for (SingleReel singleReel : reels) {
            Symbol symbol = singleReel.getSymbol(stopIndex);
            if (symbol != null) {
                symbol.setAlpha(alpha);
            }
        }
    }

    public boolean have7XSound(int reelID) {
        return have7XSound[reelID];
    }
}
