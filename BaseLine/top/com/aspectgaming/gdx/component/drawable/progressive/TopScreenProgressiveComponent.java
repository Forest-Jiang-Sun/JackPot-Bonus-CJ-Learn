package com.aspectgaming.gdx.component.drawable.progressive;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.math.SlotGameMode;
import com.aspectgaming.net.game.data.ContextData;
import com.aspectgaming.net.game.data.MathParam;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * show top screen progressive meter component
 *
 * @author kumo.wang
 */
public class TopScreenProgressiveComponent extends DrawableComponent implements State {
    private static final int LEVELS = 2;
    private RollingMeter[] jackpotMeters = new RollingMeter[LEVELS];

    private long[] values = new long[LEVELS];
    private long[] newValues = new long[LEVELS];
    private long progressiveWin = 0;
    private Image bgCity;
    private Image bg;
    private Image maskP1;
    private Image maskP2;
    String animationName;
    float animationTime;
    float animOverlap;
    private ShapeAnimation ProgressiveCelebrationStart;
    private ShapeAnimation ProgressiveCelebrationLoop;
    private ShapeAnimation ProgressiveCelebrationEnd;
    private ShapeAnimation ProgressiveFireworksStart;
    private ShapeAnimation ProgressiveFireworksLoop;
    private ShapeAnimation ProgressiveFireworksEnd;
    private ShapeAnimation StandBy_Jackpots;
    private ShapeAnimation Win_Jackpots;
    private ShapeAnimation StandBy_Multi;
    private ShapeAnimation Win_Mult;
    private ShapeAnimation standBy_4000;
    private ShapeAnimation Win_4000;
    private ShapeAnimation Picture_Jackpots;
    private ShapeAnimation Picture_Multi;
    private ShapeAnimation Picture_4000;
    private ShapeAnimation JackpotCityLogo;
    private ShapeAnimation JackpotCityLogoWin;
    private ShapeAnimation P_777;
    private ShapeAnimation Animation_777;
    private ShapeAnimation In_777;
    private boolean isJACKPOTWIN = false;
    private boolean isP2Win = false;
    private Animation Lighting;

    private final Logger log = LoggerFactory.getLogger(TopScreenProgressiveComponent.class);
    private boolean isTestMode = false;
    private SlotGameMode gameMode;


    public TopScreenProgressiveComponent() throws IOException, SAXException, ParserConfigurationException {
        switch (GameData.getInstance().Setting.ProgressiveType) {
            case 0:
                gameMode = SlotGameMode.None;
                break;
            case 1:
                gameMode = SlotGameMode.Linked;
                break;
            case 2:
                gameMode = SlotGameMode.StandAlone;
                break;
            default:
                gameMode = SlotGameMode.None;
                break;
        }


        bgCity = ImageLoader.getInstance().load("Background/1920x1080/BGCity");
        Vector2 point = CoordinateLoader.getInstance().getCoordinate(bgCity, "Background");
        bgCity.setPosition(point.x, point.y);
        addActor(bgCity);

        Lighting = new Animation("Lighting/");
        addActor(Lighting);
        Lighting.loop();
        if (gameMode == SlotGameMode.StandAlone) {
            bg = ImageLoader.getInstance().load("Background/1920x1080/BackgroundProgressive");
        } else if (gameMode == SlotGameMode.Linked) {
            bg = ImageLoader.getInstance().load("Background/1920x1080/BackgroundLink");

        } else if (gameMode == SlotGameMode.None) {
            bg = ImageLoader.getInstance().load("Background/1920x1080/background");
        }
        bg.setPosition(point.x, point.y);
        addActor(bg);


        if (gameMode == SlotGameMode.StandAlone) {
            maskP1 = ImageLoader.getInstance().load("Background/1920x1080/maskP1", "MaskP1");
        } else {
            maskP1 = ImageLoader.getInstance().load("Background/1920x1080/MaskLink", "MaskLink");
        }
        maskP2 = ImageLoader.getInstance().load("Background/1920x1080/maskP2", "MaskP2");




       /* img_copyright=ImageLoader.getInstance().load("Background/1920x1080/copyright");
        point = CoordinateLoader.getInstance().getCoordinate(img_copyright, "Copyright");
        img_copyright.setPosition(point.x, point.y);
        addActor(img_copyright);*/

        for (int i = 0; i < LEVELS; ++i) {
            if (gameMode == SlotGameMode.Linked) {
                jackpotMeters[i] = new RollingMeter("JackpotFont" + Math.min((i + 1), 3), Color.WHITE, "JackpotLink");
            } else {
                jackpotMeters[i] = new RollingMeter("JackpotFont" + Math.min((i + 1), 3), Color.WHITE, "JackpotMeter" + (i + 1));

            }
            addActor(jackpotMeters[i]);
        }

        registerEvent(new PlayProgressiveEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Credits > 0) {
                    animationName = (String) obj[0];
                    animationTime = Float.parseFloat((String) obj[1]);
                    animOverlap = Float.parseFloat((String) obj[2]);
                    if (animationName.equals("CelebrationMajor")) {
                        PlayCelebrationMajor();
                    }
                    if (animationName.equals("CelebrationMinor")) {
                        PlayCelebrationMinor();
                    }
                }
            }
        });


        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (isTestMode == false)
                    isTestMode = GameData.getInstance().Context.TestMode;

                switch (GameData.getInstance().Context.GameState) {
                    case State.ProgressiveResults:
                        ContextData game = GameData.getInstance().Context;
                        progressiveWin = game.ProgressiveTotalWin;
                        break;

                    case State.PrimaryGameStarted:
                        StopAllAnim();
                        break;
                }
            }
        });

        registerEvent(new StopCelebrationEvent() {
            @Override
            public void execute(Object... obj) {
//                StopAllAnim();
                boolean testMode = GameData.getInstance().Context.TestMode;
                int state = GameData.getInstance().Context.GameState;
                if (isTestMode) {
                    if (testMode == false && state == State.GameIdle) {
                        StopAllAnim();
                        isTestMode = false;
                    }
                }
            }
        });

        registerEvent(new StopCelebrationClickSpinEvent() {
            @Override
            public void execute(Object... obj) {
                StopAllAnim();
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                isP1OrP2();
                if (GameData.getInstance().Context.TotalWin >= 1) {
                    JackpotCityLogo.stop();
                    JackpotCityLogoWin.play(true);
                } else if (isJACKPOTWIN || isP2Win) {
                    JackpotCityLogo.stop();
                    JackpotCityLogoWin.play(true);
                }

            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                JackpotCityLogoWin.stop();
                JackpotCityLogo.play(true);
            }
        });

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.TestMode) {
                    StopAllAnim();
                }
            }
        });

        registerEvent(new CreditsChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.Credits==0)
                {
                    StopAllAnim();
                }
            }
        });


        FileHandle f1 = new FileHandle(AspectGamingUtil.WORKING_DIR + "/MainScreen.xml");

        standBy_4000 = new ShapeAnimation("TopScreen", "JACKPOT", "standby_4000", "Background");
        addActor(standBy_4000);

        Win_4000 = new ShapeAnimation("TopScreen", "JACKPOT", "animation_4000", "Background");
        addActor(Win_4000);

        Picture_4000 = new ShapeAnimation("TopScreen", "JACKPOT", "picture_4000", "Background");
        addActor(Picture_4000);
        //+NEW_SAPC
        if (gameMode == SlotGameMode.StandAlone) {
            addActor(maskP1);
            addActor(maskP2);
            StandBy_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "standby_mult", "Background");
            addActor(StandBy_Multi);
            Win_Mult = new ShapeAnimation("TopScreen", "JACKPOT", "animation_mult", "Background");
            addActor(Win_Mult);
            Picture_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "picture_mult", "Background");
            addActor(Picture_Multi);

            StandBy_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "standby_jack", "Background");
            addActor(StandBy_Jackpots);
            Win_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "animation_jack", "Background");
            addActor(Win_Jackpots);
            Picture_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "picture_jack", "Background");
            addActor(Picture_Jackpots);

            JackpotCityLogo = new ShapeAnimation("logo/mini", "logo", "animation", "Logo");
            addActor(JackpotCityLogo);

            JackpotCityLogoWin = new ShapeAnimation("logo/mini", "logo", "animation2", "Logo");
            addActor(JackpotCityLogoWin);

            P_777 = new ShapeAnimation("logo/mini", "777", "777_P", "Logo");
            addActor(P_777);

            Animation_777 = new ShapeAnimation("logo/mini", "777", "777_animation", "Logo");
            addActor(Animation_777);

            In_777 = new ShapeAnimation("logo/mini", "777", "777_in", "Logo");
            addActor(In_777);
        } else if (gameMode == SlotGameMode.Linked) {
            addActor(maskP1);
            StandBy_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "standby_800", "Background");
            addActor(StandBy_Multi);
            Win_Mult = new ShapeAnimation("TopScreen", "JACKPOT", "animation_800", "Background");
            addActor(Win_Mult);
            Picture_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "picture_800", "Background");
            addActor(Picture_Multi);

            StandBy_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "standby_jack", "Background");
            addActor(StandBy_Jackpots);
            Win_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "animation_jack", "Background");
            addActor(Win_Jackpots);
            Picture_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "picture_jack", "Background");
            addActor(Picture_Jackpots);

            JackpotCityLogo = new ShapeAnimation("logo/mini", "logo", "animation", "LinkLogo");
            addActor(JackpotCityLogo);

            JackpotCityLogoWin = new ShapeAnimation("logo/mini", "logo", "animation2", "LinkLogo");
            addActor(JackpotCityLogoWin);

            P_777 = new ShapeAnimation("logo/mini", "777", "777_P", "LinkLogo");
            addActor(P_777);

            Animation_777 = new ShapeAnimation("logo/mini", "777", "777_animation", "LinkLogo");
            addActor(Animation_777);

            In_777 = new ShapeAnimation("logo/mini", "777", "777_in", "LinkLogo");
            addActor(In_777);
        } else if (gameMode == SlotGameMode.None) {
            StandBy_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "standby_800", "Background");
            addActor(StandBy_Multi);
            Win_Mult = new ShapeAnimation("TopScreen", "JACKPOT", "animation_800", "Background");
            addActor(Win_Mult);
            Picture_Multi = new ShapeAnimation("TopScreen", "JACKPOT", "picture_800", "Background");
            addActor(Picture_Multi);

            StandBy_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "standby_20000", "Background");
            addActor(StandBy_Jackpots);
            Win_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "animation_20000", "Background");
            addActor(Win_Jackpots);
            Picture_Jackpots = new ShapeAnimation("TopScreen", "JACKPOT", "picture_20000", "Background");
            addActor(Picture_Jackpots);

            JackpotCityLogo = new ShapeAnimation("logo/big", "logo", "animation", "Logo");
            addActor(JackpotCityLogo);

            JackpotCityLogoWin = new ShapeAnimation("logo/big", "logo", "animation2", "Logo");
            addActor(JackpotCityLogoWin);

            P_777 = new ShapeAnimation("logo/big", "777", "777_P", "Logo");
            addActor(P_777);

            Animation_777 = new ShapeAnimation("logo/big", "777", "777_animation", "Logo");
            addActor(Animation_777);

            In_777 = new ShapeAnimation("logo/big", "777", "777_in", "Logo");
            addActor(In_777);
        }


        ProgressiveFireworksStart = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation_in", "TopScreenCelebration");
        addActor(ProgressiveFireworksStart);

        ProgressiveFireworksLoop = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation", "TopScreenCelebration");
        addActor(ProgressiveFireworksLoop);

        ProgressiveFireworksEnd = new ShapeAnimation("ProgressiveCelebration", "1213B", "animation_out", "TopScreenCelebration");
        addActor(ProgressiveFireworksEnd);

        ProgressiveCelebrationStart = new ShapeAnimation("ProgressiveCelebration", "J", "animation_in", "TopScreenCelebration");
        addActor(ProgressiveCelebrationStart);

        ProgressiveCelebrationLoop = new ShapeAnimation("ProgressiveCelebration", "J", "animation", "TopScreenCelebration");
        addActor(ProgressiveCelebrationLoop);

        ProgressiveCelebrationEnd = new ShapeAnimation("ProgressiveCelebration", "J", "animation_out", "TopScreenCelebration");
        addActor(ProgressiveCelebrationEnd);
        PlayProgressiveStand();
    }

    private void PlayProgressiveStand() {

        Win_Jackpots.setZIndex(0);
        Win_4000.setZIndex(0);
        StandBy_Jackpots.setEndListener(
                () ->
                {
                    Picture_Jackpots.play(true);
                    Picture_Multi.stop();
                    StandBy_Multi.play(false);
                }
        );


        StandBy_Multi.setEndListener(
                () ->
                {
                    standBy_4000.play(false);
                    Picture_4000.stop();
                    Picture_Multi.play(true);
                    addAction(delay(0.01f, run(() -> standBy_4000.setZIndex(500))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationStart.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationLoop.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationEnd.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksStart.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksLoop.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksEnd.setZIndex(1000))));
                }
        );


        standBy_4000.setEndListener(
                () ->
                {
                    StandBy_Jackpots.play(false);
                    Picture_Jackpots.stop();
                    Picture_4000.play(true);
                    addAction(delay(0.01f, run(() -> StandBy_Jackpots.setZIndex(500))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationStart.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationLoop.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveCelebrationEnd.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksStart.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksLoop.setZIndex(1000))));
                    addAction(delay(0.01f, run(() -> ProgressiveFireworksEnd.setZIndex(1000))));
                }
        );

        P_777.setEndListener(() -> Animation_777.play(false));

        Animation_777.setEndListener(() -> In_777.play(false));

        In_777.setEndListener(() -> P_777.play(false));

        StandBy_Jackpots.play(false);
        Picture_Multi.play(true);
        Picture_4000.play(true);
        JackpotCityLogo.play(true);
        P_777.play(false);
    }

    private void StopProgressiveStand() {
        StandBy_Jackpots.setEndListener(null);
        StandBy_Multi.setEndListener(null);
        standBy_4000.setEndListener(null);
        StandBy_Jackpots.stop();
        StandBy_Multi.stop();
        standBy_4000.stop();
        Picture_Jackpots.stop();
        Picture_4000.stop();
        Picture_Multi.stop();
    }

    private void PlayCelebration4000() {
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("Win4000")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_4000.play(true);
                    Picture_Jackpots.play(true);
                    Picture_Multi.play(true);
                    break;
                }
            }
        }
    }

    private void PlayCelebrationMinor() {
        ProgressiveCelebrationStart.setEndListener(
                () -> {
                    ProgressiveCelebrationLoop.play(true);

                    addAction(delay(0.01f, run(() -> {
                        ProgressiveCelebrationLoop.setZIndex(1000);
                    })));
                }
        );

        ProgressiveCelebrationLoop.setEndListener(
                () ->
                {
                    ProgressiveCelebrationEnd.play(false);
                    addAction(delay(0.01f, run(() -> {
                        ProgressiveCelebrationEnd.setZIndex(1000);
                    })));
                }
        );

        addAction(delay(0.01f, run(() -> ProgressiveCelebrationStart.play(false))));
//        addAction(delay(animationTime, run(() -> ProgressiveCelebrationLoop.stop())));

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_Jackpots.play(true);
                    Picture_Multi.play(true);
                    Picture_4000.play(true);
                    break;
                }
            }

            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_Mult.play(true);
                    Picture_Jackpots.play(true);
                    Picture_4000.play(true);
                    break;
                }
            }

            if (param.Key.equals("Win4000")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_4000.play(true);
                    Picture_Jackpots.play(true);
                    Picture_Multi.play(true);
                    break;
                }
            }
        }
    }

    private void PlayCelebrationMajor() {
//        ProgressiveCelebrationStart.setEndListener(
//                () ->
//                {
//                    ProgressiveCelebrationStart.stop();
//                    ProgressiveCelebrationStart.setZIndex(1000);
//                }
//        );

        ProgressiveCelebrationStart.setEndListener(
                () -> {
                    ProgressiveCelebrationLoop.play(true);
                    addAction(delay(0.01f, run(() -> {
                        ProgressiveCelebrationLoop.setZIndex(1000);
                    })));
                }
        );

        ProgressiveCelebrationLoop.setEndListener(
                () ->
                {
                    ProgressiveCelebrationEnd.play(false);
                    addAction(delay(0.01f, run(() -> {
                        ProgressiveCelebrationEnd.setZIndex(1000);
                    })));
                }
        );

        ProgressiveFireworksStart.setEndListener(
                () -> {
                    ProgressiveFireworksLoop.play(true);
                    addAction(delay(0.01f, run(() -> {
                        ProgressiveFireworksLoop.setZIndex(1000);
                    })));
                }

        );

        ProgressiveFireworksLoop.setEndListener(
                () ->
                {
                    ProgressiveFireworksEnd.play(false);
                    addAction(delay(0.01f, run(() -> {
                        ProgressiveFireworksEnd.setZIndex(1000);
                    })));
                }
        );

//        JackpotPrpgressiveStandBy.setEndListener(
//                ()->JackpotPrpgressiveWin.play(true)
//        );
        ProgressiveCelebrationStart.play(false);
        ProgressiveFireworksStart.play(false);
//        JackpotPrpgressiveStandBy.stop();

        addAction(delay(0.1f, run(() -> {
            ProgressiveCelebrationStart.setZIndex(1000);
            ProgressiveFireworksStart.setZIndex(1000);
        })));

//        ProgressiveCelebrationLoop.setZIndex(1000);
//        ProgressiveCelebrationEnd.setZIndex(1000);

//        ProgressiveFireworksLoop.setZIndex(1000);
//        ProgressiveFireworksEnd.setZIndex(1000);

        isJACKPOTWIN = false;
        isP2Win = false;
        isP1OrP2();

        if (isJACKPOTWIN || isP2Win) {

        } else {
//            addAction(delay(animationTime, run(() -> ProgressiveCelebrationLoop.stop())));
//            addAction(delay(animationTime, run(() -> ProgressiveFireworksLoop.stop())));
        }

        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_Jackpots.play(true);
                    Win_Jackpots.setZIndex(999);
                    Picture_Multi.play(true);
                    Picture_4000.play(true);
                    break;
                }
            }

            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_Mult.play(true);
                    Picture_Jackpots.play(true);
                    Picture_4000.play(true);
                    break;
                }
            }

            if (param.Key.equals("Win4000")) {
                if (param.Value.equals("true")) {
                    StopProgressiveStand();
                    Win_4000.play(true);
                    Win_4000.setZIndex(999);
                    Picture_Jackpots.play(true);
                    Picture_Multi.play(true);
                    break;
                }
            }
        }


        addAction(delay(animationTime, run(() -> PlayEndAnim())));
    }


    //+NEW_SAPC
    public void update(long[] meters) {
        System.out.println(gameMode.toString());
        if (meters != null) {
            long[] newMeters = new long[LEVELS];
            newMeters[0] = meters[0];
            this.newValues = newMeters;
            if (gameMode == SlotGameMode.StandAlone&&meters.length>1) {
                newMeters[1] = meters[1] + (800 * GameData.getInstance().Context.Denomination);
            }

            for (int i = 0; i < values.length; i++) {
                if (values[i] != newValues[i]) {
                    values[i] = newValues[i];
                    //already in cents
                    jackpotMeters[i].setValue(values[i], false);
                }
            }
        }
    }

    private long valueToCents(long value) {
        return value / 100000000;
    }


    private Runnable progressiveMeterIntro = new Runnable() {
        @Override
        public void run() {
            ContextData game = GameData.getInstance().Context;
        }
    };

    private Runnable progressiveMeterOuter = new Runnable() {
        @Override
        public void run() {

        }
    };

    private boolean hasProgressive(String path) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        File file = new File(path);
        Document document = documentBuilder.parse(file);
        NodeList nodelist = document.getElementsByTagName("Progressive");
        Element element = (Element) nodelist.item(0);
        String enabled = element.getAttribute("protocol").toUpperCase();
        if (enabled.equals("STANDALONE") || enabled.equals("SAS")) {
            return true;
        } else {
            return false;
        }
    }

    private void StopAllAnim() {
        clearActions();
        ProgressiveCelebrationStart.setEndListener(null);
        ProgressiveCelebrationLoop.setEndListener(null);
        ProgressiveFireworksStart.setEndListener(null);
        ProgressiveFireworksLoop.setEndListener(null);
        ProgressiveCelebrationStart.stop();
        ProgressiveCelebrationLoop.stop();
        ProgressiveCelebrationEnd.stop();
        ProgressiveFireworksStart.stop();
        ProgressiveFireworksLoop.stop();
        ProgressiveFireworksEnd.stop();
        Win_Jackpots.stop();
        Win_Mult.stop();
        Win_4000.stop();
        if (!StandBy_Jackpots.isPlaying() && !StandBy_Multi.isPlaying() && !standBy_4000.isPlaying()) {
            StopProgressiveStand();
            PlayProgressiveStand();
        }


//        JackpotPrpgressiveWin.stop();
//        JackpotPrpgressiveStandBy.play(true);
    }

    private void PlayEndAnim() {
//        ProgressiveCelebrationLoop.stop();
//        ProgressiveFireworksLoop.stop();
//        JackpotPrpgressiveWin.stop();
    }

    private void isP1OrP2() {
        isP2Win = false;
        isJACKPOTWIN = false;
        for (MathParam param : GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("P2Win")) {
                if (param.Value.equals("true")) {
                    isP2Win = true;
                    break;
                }
            }
            if (param.Key.equals("JACKPOTWIN")) {
                if (param.Value.equals("true")) {
                    isJACKPOTWIN = true;
                    break;
                }
            }
        }
    }
}
