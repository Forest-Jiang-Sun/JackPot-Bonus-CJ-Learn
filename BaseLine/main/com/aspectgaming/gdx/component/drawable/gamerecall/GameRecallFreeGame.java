package com.aspectgaming.gdx.component.drawable.gamerecall;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.BackgroundConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.common.SpriteConfiguration;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.math.SlotGameMode;
import com.aspectgaming.net.game.data.ContextData;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.SettingData;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * @author ligang.yao
 */
public class GameRecallFreeGame extends DrawableComponent {

    private Image imgBaseGame;
    private Image imgbasegame_city;
    private Image imgBasegame_frame;
    private Image imgFreeGame;
    private Image imgFreeGameBG;
    private Image imgProgressiveBG;
    private Image imgFreeGameMessage;
    private Image imgProgressiveMessage;
    private Image imgProgressive;
    private Image imgLogo;
    private Image imgNoProgressive;
    private TextureLabel paylineLabel;
    private TextureLabel denomLabel;
    private Image[] background;

    private Image imgBetWinCredits;
    private Image imgDenom;
    private Image imgLanguage;
    private Button helpButton;
    private Button volumeButton;
    //    private Button languageButton;
    //private Button playButton;
    private Button serviceButton;
    private Button cashOutButton;
    //    private Button gambleButton;
//    private Button takeWinButton;
    private Button logoButton;

    private TextureLabel label;
    private Rectangle bounds = new Rectangle();
    private SlotGameMode gameMode;

    public GameRecallFreeGame() {
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
    }

    public void loadBackground(){
        setVisible(true);
        imgbasegame_city=ImageLoader.getInstance().load("Background/basegame_city", "Background");
        addActor(imgbasegame_city);
        imgBaseGame = ImageLoader.getInstance().load("Background/basegame", "Background");
        addActor(imgBaseGame);
    }

    public void loadForeground() {
        setVisible(true);
        ContextData game = GameData.getInstance().GameRecall;

        bounds = CoordinateLoader.getInstance().getBound("RecallDenomination");
        label=new TextureLabel("DenomFont", Align.center, Align.center);
        label.setPosition(bounds.x,bounds.y);
        label.setText(GameData.Currency.denomFormat(GameData.getInstance().Context.Denomination));


        imgBasegame_frame = ImageLoader.getInstance().load("Background/basegame_frame", "Background");
        imgLogo = ImageLoader.getInstance().load("Background/logo", "Logo");
        imgNoProgressive= ImageLoader.getInstance().load("Background/no_progressive", "NoProgressive");
        imgFreeGame = ImageLoader.getInstance().load("Background/freegame", "FreeGame");
        imgProgressive = ImageLoader.getInstance().load("Background/progressive", "Progressive");

        addActor(imgBasegame_frame);
        addActor(imgLogo);
        addActor(imgNoProgressive);
        addActor(imgFreeGame);

        if (gameMode!=SlotGameMode.None) {
            imgNoProgressive.setSprite(imgProgressive.getSprite());
        }
        
        BackgroundConfiguration cfg = GameConfiguration.getInstance().currentResolution().background;
        if (cfg != null && cfg.sprites != null) {
            for (SpriteConfiguration sc : cfg.sprites) {
                addSprite(sc);
            }
        }

        Rectangle rect = CoordinateLoader.getInstance().getBound("PayLinesAndBet");
        if (rect != null) {
            paylineLabel = new TextureLabel("PayLineFont", Align.center, Align.center);
            paylineLabel.setBounds(rect);
            addActor(paylineLabel);
        }

        int numPaylines = GameData.getInstance().Setting.MaxSelections;

        imgBetWinCredits = ImageLoader.getInstance().load("Meter/BetWinCredits","BetWinCredits");
        addActor(imgBetWinCredits);
        addActor(label);

        helpButton = new Button("Button/Help/help_");
        helpButton.setDisabled(true);
        volumeButton = new Button("Button/Volume/volume_" + GameData.getVolumeLevel() + "_");
//        languageButton = new Button("Button/Language/Static/language_");
        //playButton = new Button("Button/Play/play_");
        serviceButton = new Button("Button/Service/Static/service_");
        cashOutButton = new Button("Button/CashOut/Static/cashout_");
        cashOutButton.setDisabled(true);
        logoButton = new Button("Button/Logo/logo_");
        logoButton.setDisabled(true);
//        gambleButton = new Button("Button/Gamble/Static/gamble_");
//        takeWinButton =  new Button("Button/TakeWin/Static/TakeWinButton_");
        addActor(helpButton, "HelpBt");
        addActor(volumeButton, "Volume");
//        addActor(languageButton, "LanguageBt");
        //addActor(playButton, "Play");
        addActor(serviceButton, "ServiceBt");
        addActor(cashOutButton, "CashOutBt");
//        addActor(gambleButton, "GambleBt");
//        addActor(takeWinButton, "TakeWinBtnBt");
        addActor(logoButton, "LogoBt");

        imgLanguage = ImageLoader.getInstance().load("Button/Language/Static/ImgLanguage_up", "ImagLanguage");
        addActor(imgLanguage);

        setWidth(1920);
        setHeight(1080);
        setOrigin(0, 0);
        setTransform(true);
    }

    private Actor addSprite(SpriteConfiguration sc) {
        Actor actor;
        if (sc.type != null && sc.type.equals("video")) {
            actor = VideoLoader.Instance.load(sc.path);
            ((Video) actor).loop();
        } else {
            actor = ImageLoader.getInstance().load(sc.path);
        }

        Vector2 position = CoordinateLoader.getInstance().getCoordinate(actor, sc.name);
        if (position != null) {
            actor.setPosition(position.x, position.y);
        }
        addActor(actor);
        return actor;
    }

    public void initSymbols() {
        ContextData game = GameData.getInstance().GameRecall;
        int numCols = GameConfiguration.getInstance().reel.reels.length;
        int numRows = game.Result.Stops.length / numCols;

        int[] stops =game.Result.Stops;

        for (int i = 0; i < stops.length; i++) {
            Group symbol = new Group();

            Image imgSymbol;
            imgSymbol = ImageLoader.getInstance().load("Symbol/" + stops[i]);

            addActor(imgSymbol);

            symbol.addActor(imgSymbol);

            int reelId = i % numCols;
            int rowId = i / numCols;

            Vector2 point = CoordinateLoader.getInstance().getPos("SingleReel" + reelId);
            symbol.setPosition(point.x, 1080 - (point.y + (imgSymbol.getHeight()-70) * (rowId + 1)));
            addActor(symbol);
        }
    }

    public void initJackpots(int index) {
        ContextData game = GameData.getInstance().GameRecall;
        int total = 0;
        int[] jackpots = new int[game.Selections];

        int numCols = GameConfiguration.getInstance().reel.reels.length;
        int numRows = game.Result.Stops.length / numCols;

        for (int pos = 0; pos < numRows; pos++) {
            for (MathParam param: game.MathParams) {
                if (param.Key.equals("JACKPOTLINE" + pos)) {
                    int []jackpotLineInfo = CommonUtil.stringToArray(param.Value);
                    if (jackpotLineInfo == null) {
                        break;
                    }

                    for (int i = 0; i < jackpotLineInfo.length; i ++) {
                        if (jackpotLineInfo[i] == -1) {
                            break;
                        } else {
                            jackpots[total] = jackpotLineInfo[i];
                            total++;
                        }
                    }
                }
            }
        }

        if (total == 0 || index >= total) return;

        int level = game.Progressive.Level > 4 ? game.Progressive.Level - 4 : game.Progressive.Level;
        int line = jackpots[total - index - 1];
        int idx = (GameConst.getSelectionPositions(game.Selections, "")[line][numCols - 1]) / numCols;

        if (level < 1 || level > Symbol.BN - Symbol.BL) return;

        int subSymbolId = Symbol.BL + level;
        Image image = ImageLoader.getInstance().load("Symbol/" + subSymbolId);
        Rectangle rect = CoordinateLoader.getInstance().getBound("SubSymbol" + idx);
        image.setPosition(rect.getX(), rect.getY());
        image.setViewArea(rect);
        image.addAction(fadeIn(0.5f));
        addActor(image);

        SettingData cfg = GameData.getInstance().Setting;
        int[][] lines = GameConst.getSelectionPositions(cfg.MaxSelections, cfg.SelectedGame);

        if (line >= 0 && lines != null) {
            int[] positions = lines[line];

            for (int reel = 0; reel < numCols - 1; reel++) {
                int distance = 0;
                int sum = positions[reel];
                distance = Math.abs(positions[reel+1] - positions[reel]);
                sum += positions[reel+1];

                Vector2 pos = CoordinateLoader.getInstance().getPos("IdentifyLine"+sum);

                Image LineFrame0 = ImageLoader.getInstance().load("PayLine/"+distance + "_0");
                LineFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
                LineFrame0.setPosition(pos.x-LineFrame0.getWidth()/2, pos.y-LineFrame0.getHeight()/2);
                addActor(LineFrame0);

            }

            Rectangle bound = CoordinateLoader.getInstance().getBound("LineNumber"+positions[numCols - 1]);
            Image NumFrame0 = ImageLoader.getInstance().load("PayLine/PaylineNumber" + "_0");
            NumFrame0.setPosition(bound.x - 37, bound.y);
            NumFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
            addActor(NumFrame0);


            TextureLabel NumLabel = new TextureLabel("PayLineFont", Align.center, Align.center);
            NumLabel.setBounds(bound.x, bound.y - 23, bound.width,bound.height);
            NumLabel.setValue(line+1);
            addActor(NumLabel);
        }
    }

    public void setButtonDisable(boolean disable, long totolWin) {
        if (disable) {
            cashOutButton.setDisabled(disable);
            helpButton.setDisabled(disable);
            logoButton.setDisabled(disable);
//            gambleButton.setDisabled(disable);
//            takeWinButton.setDisabled(disable);
        } else {
            cashOutButton.setDisabled(disable);
            helpButton.setDisabled(disable);
            logoButton.setDisabled(disable);
            if (totolWin > 0) {
//                gambleButton.setDisabled(disable);
//                takeWinButton.setDisabled(disable);
            } else {
//                gambleButton.setDisabled(!disable);
//                takeWinButton.setDisabled(!disable);
            }
        }
    }

    private boolean hasProgressive(String path) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        File file=new File(path);
        Document document = documentBuilder.parse(file);
        NodeList nodelist = document.getElementsByTagName("JackpotController");
        Element element =(Element) nodelist.item(0);
        String enabled=element.getAttribute("hasProgressive").toUpperCase();
        if(enabled.equals("TRUE"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
