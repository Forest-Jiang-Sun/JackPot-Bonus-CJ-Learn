package com.aspectgaming.gdx.component.drawable.background;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.game.SingleReelStoppedEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.math.SlotGameMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static com.aspectgaming.gdx.component.drawable.reel.Symbol.D7;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * show background about basegame and freegame with fade in or fade out.
 *
 * @author ligang.yao & johnny.shi
 */
public class ForegroundComponent extends DrawableComponent {

    private final Image imgBaseGame;
    private final Image imgFreeGame;
    private final Image imgFreeGameBG;
    private final Image imgProgressiveBG;
    private final Image imgFreeGameMessage;
    private final Image imgProgressive;
    private Image imgNoProgressive;
    private ShapeAnimation AirPlane;
    private Animation Lighting2;
    private SlotGameMode gameMode;

    private final String Path1 = AspectGamingUtil.WORKING_DIR + "/assets/Videos/";


    public ForegroundComponent() throws IOException, SAXException, ParserConfigurationException {
        switch (GameData.getInstance().Setting.ProgressiveType)
        {
            case 0:
                gameMode= SlotGameMode.None;
                break;
            case 1:
                gameMode=SlotGameMode.Linked;
                break;
            case 2:
                gameMode= SlotGameMode.StandAlone;
                break;
            default:
                gameMode= SlotGameMode.None;
                break;
        }

        imgBaseGame = ImageLoader.getInstance().load("Background/basegame_frame", "Background");
        imgFreeGame = ImageLoader.getInstance().load("Background/freegame", "FreeGame");
        imgProgressive = ImageLoader.getInstance().load("Background/progressive", "Progressive");
        imgFreeGameBG = ImageLoader.getInstance().load("Background/freegameBG", "Background");
        imgProgressiveBG = ImageLoader.getInstance().load("Background/progressiveBG", "Background");
        imgFreeGameMessage = ImageLoader.getInstance().load("Background/freeGameMessage", "freeGameMessage");
        imgNoProgressive = ImageLoader.getInstance().load("Background/no_progressive", "NoProgressive");
        AirPlane = new ShapeAnimation("AirPlane", "zsfj", "animation", "Zero");

        Lighting2 = new Animation("Lighting2/");
        Lighting2.loop();
        addActor(imgBaseGame);
        addActor(Lighting2);

        addActor(imgFreeGameMessage);
        addActor(imgProgressiveBG);
        addActor(imgFreeGameBG);
        addActor(AirPlane);
        addActor(imgFreeGame);
        addActor(imgNoProgressive);

        FileHandle f1 = new FileHandle(AspectGamingUtil.WORKING_DIR+"/MainScreen.xml");

        if (gameMode!=SlotGameMode.None) {
            imgNoProgressive.setSprite(imgProgressive.getSprite());
            Vector2 point = CoordinateLoader.getInstance().getCoordinate(imgNoProgressive, "Progressive");
            imgNoProgressive.setPosition(point.x,point.y);
        }


        playLoopAnimation();

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                // if has free game background, then fade out base game background
                boolean isFreeSpin = GameData.currentGameMode == GameMode.FreeGame;
                int[] stops = GameData.getInstance().Context.Result.Stops;
//                if (imgFreeGameBG != null) {
                if (isFreeSpin) {
                    imgFreeGameBG.setAlpha(1);
                    imgFreeGameMessage.setAlpha(1);
                } else {
                    imgFreeGameBG.setAlpha(0);
                    imgFreeGameMessage.setAlpha(0);
                }
                if (stops[6] == D7 && stops[7] == D7 && stops[8] == D7) {
                    if (!isFreeSpin) {
                        imgProgressiveBG.setAlpha(1);
                    }
                } else {
                    imgProgressiveBG.setAlpha(0);
                }

            }

//            }
        });

        registerEvent(new GameModeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                // if has free game background, then fade out base game background
                boolean isFreeSpin = GameData.currentGameMode == GameMode.FreeGame;
//                    if (imgFreeGameBG != null) {
                if (isFreeSpin) {
                    imgFreeGameBG.setAlpha(1);
                    imgFreeGameMessage.setAlpha(1);
                } else {
                    imgFreeGameBG.setAlpha(0);
                    imgFreeGameMessage.setAlpha(0);
                }

            }
//                }
        });

        registerEvent(new SingleReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                int reelId = (int) obj[0];
                boolean isFreeSpin = GameData.currentGameMode == GameMode.FreeGame;
                int[] stops = GameData.getInstance().Context.Result.Stops;
                if (reelId == 1) {
                    if (stops[6] == D7 && stops[7] == D7 && stops[8] == D7) {
                        if (!isFreeSpin) {
                            imgProgressiveBG.setAlpha(1);
                        }
                    }
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                imgProgressiveBG.setAlpha(0);
            }
        });

    }

    private void playLoopAnimation() {
        AirPlane.play(false);
        addAction(delay(30, run(this::playLoopAnimation)));
    }

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
}
