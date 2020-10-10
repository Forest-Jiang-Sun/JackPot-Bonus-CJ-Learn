package com.aspectgaming.gdx.component.drawable.randomwild;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.SingleReelConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.ModifyFreeBGMVolEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.wild.RandomWildIntroEvent;
import com.aspectgaming.common.event.wild.RandomWildOutroEvent;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.SettingData;
import com.aspectgaming.util.CommonUtil;
import com.aspectgaming.common.actor.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class RandomWildComponent extends DrawableComponent {
    private final int numReels;
    private final int numRows;
    private final List<RandomWildSymbol> wildSymbols = new ArrayList<>();

    private int[] wildPos;
    private int wildIndex;
    private float[] animationInterval = new float[20];
    private Vector< HashSet<Integer> > sndSet = new Vector<HashSet<Integer> >();

    private final Image overlay;
    private final Random random = new Random();
    private final Video wildBg;
    private final Sound sndRandomWildsSfx;

    public RandomWildComponent() {
        numReels = GameConfiguration.getInstance().reel.reels.length;
        numRows = GameData.getInstance().Context.Result.Stops.length / numReels;
        SingleReelConfiguration cfg = GameConfiguration.getInstance().reel.getSingleReel(0);

        wildBg = VideoLoader.Instance.load("RandomWild/RandomWildsBG");
        wildBg.setAutoVisible(true);
        addActor(wildBg);

        overlay = ImageLoader.getInstance().load("Background/Overlay");
        overlay.setAlpha(0);
        addActor(overlay);

        initWildSymbol();

        sndRandomWildsSfx = SoundLoader.getInstance().get("randomwilds/RandomWilds");

        for (int i=0; i<numRows; ++i) {
            sndSet.add(new HashSet<Integer>());
        }

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
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

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                onReelStopped();
            }
        });
    }

    public float initWildAnimation(float delayTime) {
        reset();

        wildPos = null;
        for (MathParam param:GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("RANDOMWILD")) {
                wildPos =  CommonUtil.stringToArray(param.Value);
            }
        }
        if (wildPos == null || wildPos.length == 0) {
            return delayTime;
        }

        MetersComponent metersComp = (MetersComponent) Content.getInstance().getComponent(Content.METERSCOMPONENT);
        float multiple = ((float)metersComp.getCurrentGameWin()) / GameData.getInstance().Context.TotalBet;

        if (wildPos.length >= 5 && multiple >= 10 && random.nextInt(4) == 0) {
            if (delayTime > 0) {
                delayTime += 0.5f;
            }else {
                delayTime += 0.5f;
            }

            float intervalTotalTime = 4.0f;

            addAction(delay(delayTime, run(()->randomWildIntro1())));

            return delayTime + intervalTotalTime;
        } else {
            if (delayTime > 0) {
                delayTime += 0.5f;
            }else {
                delayTime += 2.0f;
            }

            float intervalTotalTime = 0.0f;
            for (int i=0; i<wildPos.length-1; ++i) {
                int time = random.nextInt(11) + 1;
                animationInterval[i] = time * 0.1f;
                intervalTotalTime += animationInterval[i];
            }

            addAction(delay(delayTime, run(()->randomWildIntro())));

            return delayTime + intervalTotalTime + 1.0f;
        }
    }

    private void initWildSymbol(){
        SettingData cfg = GameData.getInstance().Setting;
        ReelComponent reels = ((ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT));
        for (int i=0; i<numRows; ++i) {
            for (int j=0; j<numReels; ++j) {
                int stopIndex = i*numReels + j;
                Symbol symbol0 = reels.getSymbol(stopIndex);
                RandomWildSymbol symbol = new RandomWildSymbol();

                symbol.setPosition(symbol0.getScreenX(), symbol0.getScreenY());
                wildSymbols.add(symbol);
                addActor(symbol);
            }
        }
    }

    private void onReelStopped() {
        reset();
    }

    private void reset() {
        clearActions();
        overlay.setAlpha(0);

        for (int i=0; i<numRows; ++i) {
            sndSet.get(i).clear();
        }
    }

    private void randomWildIntro() {
        overlay.addAction(alpha(1.0f, 0.25f));

        wildIndex = 0;
        EventMachine.getInstance().offerEvent(RandomWildIntroEvent.class);
        addAction(delay(0.75f, run(()->placeLantern())));
    }

    private Runnable randomWildOutro = new Runnable() {
        @Override
        public void run() {
            EventMachine.getInstance().offerEvent(RandomWildOutroEvent.class);
            overlay.addAction(alpha(0.0f, 0.25f));
        }
    };

    private int getLandSnd(int symbolPos) {
        int sndIndex = random.nextInt(11);
        while (sndSet.get(symbolPos/numReels).contains(new Integer(sndIndex))) {
            sndIndex = random.nextInt(11);
        }

        sndSet.get(symbolPos/numReels).add(new Integer(sndIndex));
        return sndIndex;
    }

    private void placeLantern(){

    }

    private void randomWildIntro1() {
        wildIndex = 0;
        EventMachine.getInstance().offerEvent(RandomWildIntroEvent.class);
        addAction(delay(0.1f, run(()->placeLantern1())));

        if (GameData.currentGameMode == GameMode.FreeGame) {
            EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 0.01f);
        }
    }

    private Runnable randomWildOutro1 = new Runnable() {
        @Override
        public void run() {
            EventMachine.getInstance().offerEvent(RandomWildOutroEvent.class);
            //overlay.addAction(alpha(0.0f, 0.25f));
            sndRandomWildsSfx.stop();
            if (GameData.currentGameMode == GameMode.FreeGame) {
                EventMachine.getInstance().offerEvent(ModifyFreeBGMVolEvent.class, 1.0f);
            }
        }
    };

    private void placeLantern1(){
    }

    public void setWildSymbolAlpha(int symbolPos, float alpha) {
        if (wildPos != null && wildPos.length > 0) {
            for (int i = 0; i < wildPos.length; i++) {
                if (wildPos[i] == symbolPos) {
                    RandomWildSymbol symbol = wildSymbols.get(symbolPos);
                    symbol.setAlpha(alpha);
                }
            }
        }
    }
}

