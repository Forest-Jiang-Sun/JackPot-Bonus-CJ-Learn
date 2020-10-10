package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.game.ReelStoppedEvent;
import com.aspectgaming.common.event.game.WinMeterStartRollingEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.progressive.ProgressiveStartIntroEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class ProgressiveBgCoinFillComponent extends DrawableComponent {
//    private Animation viCoinfall_01;
//    private Video viBGcoinfill;
//    private Video viCoingusher;
//
//    private Image imgBGcoinfill;
//    private Image imgBGcoinfill_04;
//    private Image imgReelBg;
//
//    private Sound sndCoins1;
//    private Sound sndCoins2;
//    private Sound sndCoins3;
//
//    private int proAntiNum = 0;
//    private int curProAnit = 0;
//    private boolean isReachBGcoinfill3;
//
//    private ProgressiveReelAnimComponent proReelAnim;
//    private ProgressiveReelComponent progressiveReelCom;

    public ProgressiveBgCoinFillComponent() {
//        viCoinfall_01 = new Animation("Progressive/BgCoinFill/coinfall_01/");//VideoLoader.Instance.load("BgCoinFill/coinfall_01", false);
//        viCoinfall_01.setPosition(0, 0);
//        viCoinfall_01.setAutoVisible(true);
//        addActor(viCoinfall_01);
//
//        imgBGcoinfill = ImageLoader.getInstance().load("Progressive/BgCoinFill/BGcoinfill_0/0");
//        imgBGcoinfill.setPosition(0, 0);
//        imgBGcoinfill.setVisible(true);
//        addActor(imgBGcoinfill);
//
//        imgBGcoinfill_04 = ImageLoader.getInstance().load("Progressive/BgCoinFill/BGcoinfill_4/0");
//        imgBGcoinfill_04.setPosition(0, 0);
//        imgBGcoinfill_04.setAlpha(0.0f);
//        addActor(imgBGcoinfill_04);
//
//        /*
//        for (int i = 0; i < 5; i ++) {
//            viBGcoinfill[i] = VideoLoader.Instance.load("BgCoinFill/BGcoinfill_" + i, false);
//            viBGcoinfill[i].setPosition(0, 0);
//            viBGcoinfill[i].setAutoVisible(true);
//            addActor(viBGcoinfill[i]);
//        }
//
//        viCoingusher = VideoLoader.Instance.load("BgCoinFill/coingusher", false);
//        viCoingusher.setPosition(0, 0);
//        viCoingusher.setAutoVisible(true);
//        addActor(viCoingusher);
//        */
//
//        sndCoins1 = SoundLoader.getInstance().get("progressive/Coins1");
//        sndCoins2 = SoundLoader.getInstance().get("progressive/Coins2");
//        sndCoins3 = SoundLoader.getInstance().get("progressive/Coins3");
//
//        imgReelBg = ImageLoader.getInstance().load("Background/reelsbg2");
//        Vector2 pos = CoordinateLoader.getInstance().getCoordinate(imgReelBg, "reelsbg");
//        imgReelBg.setPosition(pos.x, pos.y);
//        imgReelBg.setVisible(true);
//        addActor(imgReelBg);
//
//
//        registerEvent(new GameResetEvent() {
//            @Override
//            public void execute(Object... obj) {
//                clearActions();
//                proAntiNum = 0;
//                curProAnit = 0;
//                isReachBGcoinfill3 = false;
//                ImageLoader.getInstance().reload(imgBGcoinfill,"Progressive/BgCoinFill/BGcoinfill_0/0");
//
//                proReelAnim = ((ProgressiveReelAnimComponent) Content.getInstance().getComponent(Content.PROGRESSIVEREELANIMCOMPONENT));
//                progressiveReelCom = (ProgressiveReelComponent) Content.getInstance().getComponent(Content.PROGRESSIVEREELCOMPONENT);
//            }
//        });
//
//
//        registerEvent(new ReelStoppedEvent() {
//            @Override
//            public void execute(Object... obj) {
//                if (proReelAnim.isTriProAnti() && progressiveReelCom.getNeedSpinCount() <= 0) {
//                    playProAntiCoinfill();
//                }
//            }
//        });
//
//        registerEvent(new ProgressiveStartIntroEvent() {
//            @Override
//            public void execute(Object... obj) {
//                playProCoinfill();
//            }
//        });
//
//        registerEvent(new ReelStartSpinEvent() {
//            @Override
//            public void execute(Object... obj) {
//                clearActions();
//                if (proAntiNum == 0) {
//                    imgBGcoinfill_04.addAction(fadeOut(0.5f));
//                    ImageLoader.getInstance().reload(imgBGcoinfill, "Progressive/BgCoinFill/BGcoinfill_0/0");
//                    imgBGcoinfill.addAction(fadeIn(0.5f));
//                }
//
//                if (viBGcoinfill != null) {
//                    removeActor(viBGcoinfill);
//                    viBGcoinfill = null;
//                }
//
//                if (viCoingusher != null) {
//                    removeActor(viCoingusher);
//                    viCoingusher = null;
//                }
//            }
//        });
//
//        registerEvent(new WinMeterStartRollingEvent() {
//            @Override
//            public void execute(Object... obj) {
//                if (proAntiNum == 0) {
//                    imgBGcoinfill_04.addAction(fadeOut(0.5f));
//                    ImageLoader.getInstance().reload(imgBGcoinfill, "Progressive/BgCoinFill/BGcoinfill_0/0");
//                    imgBGcoinfill.addAction(fadeIn(0.5f));
//                }
//            }
//        });
//
//        registerEvent(new InTiltEvent() {
//            @Override
//            public void execute(Object... obj) {
//                pause();
//            }
//        });
//
//        registerEvent(new OutTiltEvent() {
//            @Override
//            public void execute(Object... obj) {
//                resume();
//            }
//        });
//    }
//
//    private void playProAntiCoinfill() {
//        proAntiNum ++;
//
//        if (proAntiNum % 4 == 0) {
//            if (isReachBGcoinfill3) {
//                sndCoins1.play();
//                viCoinfall_01.play();
//            } else {
//                sndCoins2.play();
//                viCoinfall_01.play();
//
//                if (viBGcoinfill != null) {
//                    removeActor(viBGcoinfill);
//                    viBGcoinfill = null;
//                }
//                viBGcoinfill = VideoLoader.Instance.load("BgCoinFill/BGcoinfill_" + curProAnit);
//                viBGcoinfill.setPosition(0, 0);
//                viBGcoinfill.setAutoVisible(true);
//                addActorAfter(imgBGcoinfill_04, viBGcoinfill);
//                viBGcoinfill.play();
//                addAction(delay(viBGcoinfill.getDuration(), run(() -> {
//                    ImageLoader.getInstance().reload(imgBGcoinfill, "Progressive/BgCoinFill/BGcoinfill_" + (curProAnit + 1) + "/0");
//                })));
//
//                if (curProAnit < 4) {
//                    curProAnit++;
//                }
//
//                if (curProAnit == 3) {
//                    isReachBGcoinfill3 = true;
//                }
//            }
//        } else {
//            sndCoins1.play();
//            viCoinfall_01.play();
//        }
//    }
//
//    private void playProCoinfill() {
//        proAntiNum = 0;
//        curProAnit = 0;
//        isReachBGcoinfill3 = false;
//
//        sndCoins3.play();
//
//        if (viCoingusher != null) {
//            removeActor(viCoingusher);
//            viCoingusher = null;
//        }
//        viCoingusher = VideoLoader.Instance.load("BgCoinFill/coingusher", false);
//        viCoingusher.setPosition(0, 0);
//        viCoingusher.setAutoVisible(true);
//        addActorAfter(imgBGcoinfill_04, viCoingusher);
//        viCoingusher.play();
//
//        addAction(delay(0.5f, run(() -> {
//            //ImageLoader.getInstance().reload(imgBGcoinfill, "Progressive/BgCoinFill/BGcoinfill_4/0");
//            imgBGcoinfill.addAction(fadeOut(0.5f));
//            imgBGcoinfill_04.addAction(fadeIn(0.5f));
//        })));
    }
}
