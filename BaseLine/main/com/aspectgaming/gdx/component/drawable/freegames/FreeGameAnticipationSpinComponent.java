package com.aspectgaming.gdx.component.drawable.freegames;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.progressivereel.ProgressiveReelAnimComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.SingleReel;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.MathParam;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aspectgaming.gdx.component.drawable.reel.Symbol.D7;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class FreeGameAnticipationSpinComponent extends DrawableComponent {
    final int SMART_SND_COUNT = 3;

    private Sound sndAnticipation;
    private Sound[] sndSmart;
    private Sound normalStop;

    private boolean[] haveSmartSound;

    private int scatterCount;
    private int numReels;
    private int numRows;

    private int fgAnticipationReelId;

    private boolean doveIsFly;
    private int scatterNum;

    private Map<Integer, Image> exScatterImages = new HashMap<>();

    public FreeGameAnticipationSpinComponent() {
        sndAnticipation = SoundLoader.getInstance().get("freegame/Anticipation");
        doveIsFly=false;
        scatterNum=0;

        sndSmart = new Sound[SMART_SND_COUNT];
        for (int i = 0; i < SMART_SND_COUNT; i ++) {
            sndSmart[i] = SoundLoader.getInstance().get("freegame/SmartSnd" + (i + 1));
        }

        normalStop=SoundLoader.getInstance().get("reel/reelstop");

        numReels = GameConfiguration.getInstance().reel.reels.length;
        numRows = GameData.getInstance().Context.Result.Stops.length / numReels;

        haveSmartSound = new boolean[numReels];


        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                scatterCount = 0;
                for (int i = 0; i < numReels; i ++) {
                    haveSmartSound[i] = false;
                }

                fgAnticipationReelId = Integer.MAX_VALUE;
                for (MathParam param: GameData.getInstance().Context.MathParams) {
                    if (param.Key.equals("FREEGAMEANTICIPATION")) {
                        fgAnticipationReelId = Integer.parseInt(param.Value);
                        break;
                    }
                }

                long mask = GameData.getInstance().Context.Result.ScatterMask;
                for (int reelId = 0; reelId < numReels; reelId++) {
                    for (int i = 0; i < numRows; i++) {
                        int stopIndex = reelId + i * numReels;
                        if ((mask & (1 << stopIndex)) != 0) {
                            if (reelId < 3 || haveAnticipatSpinEligible()) {
                                haveSmartSound[reelId] =  true;
                                break;
                            }
                        }
                    }
                }
            }
        });

        registerEvent(new SingleReelBounceDownEvent() {
            @Override
            public void execute(Object... obj) {
                int reelId = (int) obj[0];

                playSmartSound(reelId);
            }
        });

        registerEvent(new SingleReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                int reelId = (int) obj[0];
                sndAnticipation.stop();

                ReelComponent reels = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);

                if (reelId >= fgAnticipationReelId && reelId < numReels - 1 && reels.startBreakReelId == -1) {
                    sndAnticipation.loop();
                }

                if (haveSmartSound(reelId)) {
                    scatterNum+=1;
                }

            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
               doveIsFly=false;
                scatterNum=0;
            }
        });

    }

    public boolean haveAnticipatSpinEligible() {
        return fgAnticipationReelId > 0 && fgAnticipationReelId < numReels - 1;
    }

    private void playSmartSound(int reelId) {
        long mask = GameData.getInstance().Context.Result.ScatterMask;
        int[] stops = GameData.getInstance().Context.Result.Stops;
        int BN=8;
        boolean reel1CanPlay=false;
        boolean reel2CanPlay=false;
        boolean reel3CanPlay=false;
        boolean canPlay=false;
        if (stops[3]==BN||stops[6]==BN||stops[9]==BN)
        {
            reel1CanPlay=true;
        }
        if (reel1CanPlay&&(stops[4]==BN||stops[7]==BN||stops[10]==BN))
        {
            reel2CanPlay=true;
        }
        if (reel2CanPlay&&(stops[5]==BN||stops[8]==BN||stops[11]==BN))
        {
            reel3CanPlay=true;
        }
        if (reelId==0)
        {
            canPlay=reel1CanPlay;
        }
        if (reelId==1)
        {
            canPlay=reel2CanPlay;
        }
        if (reelId==2)
        {
            canPlay=reel3CanPlay;
        }

        if (canPlay) {
            for (int i = 0; i < numRows; i++) {
                int stopIndex = reelId + i * numReels;
                if ((mask & (1 << stopIndex)) != 0) {
                    if (reelId < 3 || haveAnticipatSpinEligible()) {
                        scatterCount++;
                        if (scatterCount <= sndSmart.length) {
                            sndSmart[scatterCount - 1].play();
                        } else {
                            sndSmart[sndSmart.length - 1].play();
                        }
                    }
                }
            }
        }
        else
        {
            normalStop.play();
        }
    }

    public boolean haveSmartSound(int reelId) {
        ReelComponent reels = (ReelComponent) Content.getInstance().getComponent(Content.REELCOMPONENT);
        if (reels.startBreakReelId == -1) {
            return haveSmartSound[reelId];
        } else {
            for (int i = reels.startBreakReelId; i < numReels; i ++) {
                if (haveSmartSound[i]) {
                    return true;
                }
            }
        }

        return false;
    }


    private void addExScatterImage(int reelId) {

    }

    private void delExScatterImageSWOnSamePos() {

    }

    private void clearExScatterImage() {
        for (Integer stopIndex : exScatterImages.keySet()) {
            Image exScatterImage = exScatterImages.get(stopIndex);
            exScatterImage.setVisible(false);
            removeActor(exScatterImage);
        }

        exScatterImages.clear();
    }
}
