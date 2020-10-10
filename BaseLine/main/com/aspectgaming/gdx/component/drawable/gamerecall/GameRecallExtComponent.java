package com.aspectgaming.gdx.component.drawable.gamerecall;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.GameRecallConfiguration;
import com.aspectgaming.common.configuration.MessageBarConfiguration;
import com.aspectgaming.common.configuration.MeterConfiguration;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.recall.GameRecallChangedEvent;
import com.aspectgaming.common.event.recall.GameRecallStartedEvent;
import com.aspectgaming.common.event.recall.GameRecallStoppedEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.MessageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.MathParamsParser;
import com.aspectgaming.net.game.data.*;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;

import static com.aspectgaming.common.data.State.AttendantApp;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * @author ligang.yao
 */
public class GameRecallExtComponent extends DrawableComponent {

    private static final String FONT = null;

    private TextureLabel txtLinesPlayed;
    private TextureLabel txtbetPerLine;
    private TextureLabel txtBet;
    private TextureLabel txtCredit;
    private TextureLabel txtWin;

//    private final TextureLabel creditslLabel;
//    private final TextureLabel betLabel;
//    private final TextureLabel winLabel;
    private final Image recallBg;

   // private GameRecallBonus bonus;
    private GameRecallGamble gamble;
    private GameRecallBaseGame base;
    private GameRecallFreeGame free;

    private String mode = "BASE_GAME";
    private String value;

    public GameRecallExtComponent() {
        setVisible(false);

        GameRecallConfiguration grc = GameConfiguration.getInstance().gameRecall;

//        creditslLabel = new TextureLabel(FONT, grc.creditColor, Align.right, Align.center, "RecallCredit");
//        betLabel = new TextureLabel(FONT, grc.betColor, Align.center, Align.center, "RecallBet");
//        winLabel = new TextureLabel(FONT, grc.winColor, Align.right, Align.center, "RecallWin");
        txtLinesPlayed = new TextureLabel(FONT, grc.linesPlayedColor, Align.right, Align.center, "RecallLinesPlayed");
        txtbetPerLine = new TextureLabel(FONT, grc.betPerLineColor, Align.right, Align.center, "RecallBetPerLine");
        txtCredit = new TextureLabel(FONT, grc.creditColor, Align.right, Align.center, "RecallCredit");
        txtBet = new TextureLabel(FONT, grc.betColor, Align.right, Align.center, "RecallBet");
        txtWin = new TextureLabel(FONT, grc.winColor, Align.right, Align.center, "RecallWin");


        recallBg = ImageLoader.getInstance().load("Recall/bg", "Recall");

        gamble = new GameRecallGamble();
        base = new GameRecallBaseGame();
        free = new GameRecallFreeGame();

        registerEvent(new GameRecallStartedEvent() {
            @Override
            public void execute(Object... obj) {
                mode = (String) obj[0];
                value = (String) obj[1];
                    setVisible(true);
                    initScreen();
            }
        });

        registerEvent(new GameRecallStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                setVisible(false);
                clear();
            }
        });

        registerEvent(new GameRecallChangedEvent() {
            @Override
            public void execute(Object... obj) {
                mode = (String) obj[0];
                value = (String) obj[1];
                initScreen();
            }
        });
    }

    private void initScreen() {
        clear();

        addActor(recallBg);

        switch (mode) {
        case "BASE_GAME":
            initBaseGame();
            break;
        case "FREE_GAME":
            initFreeGame();
            break;
        case "GAMBLE":
            initBaseGame(); // show as gamble background
            initGameble();
            break;
        /*case "BONUS_PICK":
            initBonusPick();
            break;*/
        case "RESPIN":
            initRespin();
            break;
        case "PROGRESSIVE":
        default:
            if (GameData.getInstance().GameRecall.FreeGameMode) {
                initFreeGame();
            } else {
                initBaseGame();
            }
            break;
        }
    }

    private void initSymbols(ResultData resultData) {
        int[] stops = resultData.Stops;
        for (int i = 0; i < stops.length; i++) {
            if (((resultData.ScatterMask & (1 << i)) != 0) && stops[i] == Symbol.BN){
                log.info("{}", "0_12");
            } else {
                log.info("{}", stops[i]);
            }
        }
        for (int i = 0; i < stops.length; i++) {
            Group symbol = new Group();

            Image imgSymbol;
            if (((resultData.ScatterMask & (1 << i)) != 0) && stops[i] == Symbol.BN){
                imgSymbol = ImageLoader.getInstance().load("Symbol/0_12");
            } else {
                imgSymbol = ImageLoader.getInstance().load("Symbol/" + stops[i]);
            }

            symbol.addActor(imgSymbol);
            symbol.setOrigin(0, 0);
            symbol.setScale(0.5f);
            Vector2 point = CoordinateLoader.getInstance().getPos("RecallSymbol" + i);
            addActor(symbol);
            symbol.setPosition(point.x, 1080 - 225 / 2 - point.y);
        }
    }

    private void initBaseGame() {
        ContextData game = GameData.getInstance().GameRecall;
        base.clear();
        base.loadBackground();
        addActor(base);
        base.setScale(0.5f);
        Vector2 point = CoordinateLoader.getInstance().getPos("Recall");
        base.setPosition(point.x, 540 - point.y);

        base.initSymbols();
        base.loadForeground();

        if (mode.equals("PROGRESSIVE")) {
//            base.initJackpots(Integer.parseInt(value));
            base.setButtonDisable(true, game.TotalWin);
        } else {
            base.setButtonDisable(false, game.TotalWin);
        }

        txtCredit.setValue(game.Credits);
        txtWin.setValue(game.TotalWin);
        txtBet.setValue(game.TotalBet);
        txtbetPerLine.setValue(game.BetMultiplier);
        txtLinesPlayed.setValue(game.TotalBet);
        addActor(txtCredit);
        addActor(txtWin);
        addActor(txtBet);
        addActor(txtbetPerLine);
        addActor(txtLinesPlayed);
    }

    private void initFreeGame() {
        ContextData game = GameData.getInstance().GameRecall;

        free.clear();
        free.loadBackground();
        addActor(free);
        free.setScale(0.5f);
        Vector2 point = CoordinateLoader.getInstance().getPos("Recall");
        free.setPosition(point.x, 540 - point.y);
        free.initSymbols();
        free.loadForeground();
        if (mode.equals("PROGRESSIVE")) {
//            free.initJackpots(Integer.parseInt(value));
        }
        free.setButtonDisable(true, game.TotalWin);
        txtCredit.setValue(game.Credits);
        txtWin.setValue(game.TotalWin);
        txtBet.setValue(game.TotalBet);
        txtbetPerLine.setValue(game.BetMultiplier);
        txtLinesPlayed.setValue(game.TotalBet);
        addActor(txtCredit);
        addActor(txtWin);
        addActor(txtBet);
        addActor(txtbetPerLine);
        addActor(txtLinesPlayed);

        String message = MessageLoader.getInstance().getMessage("NumFreeSpins");
        message = message.replace("@{numFreeSpinsRemaining}", String.valueOf(game.NumFreeSpinsTotalWon - game.NumFreeSpinsRemaining));
        message = message.replace("@{numFreeSpinsTotalWon}", String.valueOf(game.NumFreeSpinsTotalWon));
//        free.SetTextMessage(message);
    }

    private void initGameble() {
        GambleData gambleValue = GameData.getInstance().GameRecall.Gamble;

        addActor(gamble, "Recall");
        gamble.setScale(0.5f);
        Vector2 point = CoordinateLoader.getInstance().getPos("Recall");
        gamble.setValue(gambleValue);
        gamble.setPosition(point.x, 540 - point.y);
    }

    private void initRespin() {
        ContextData game = GameData.getInstance().GameRecall;
        int index = Integer.parseInt(value);
        ResultData result = game.MultipleResults.get(index);

        txtCredit.setValue(game.Credits);
        txtWin.setValue(game.TotalWin);
        txtBet.setValue(game.TotalBet);
        txtbetPerLine.setValue(game.BetMultiplier);
        txtLinesPlayed.setValue(game.TotalBet);
        addActor(txtCredit);
        addActor(txtWin);
        addActor(txtBet);
        addActor(txtbetPerLine);
        addActor(txtLinesPlayed);

        addActor(base);
        base.setScale(0.5f);
        Vector2 point = CoordinateLoader.getInstance().getPos("Recall");
        base.setPosition(point.x, 540 - point.y);
        initSymbols(result);

    }

    private TextureLabel addLabel(String name) {
        return addLabel(name, name);
    }

    private TextureLabel addLabel(String name, String bounds) {
        MeterConfiguration mc = GameConfiguration.getInstance().meters.getMeter(name);
        if (mc == null) return null;

        TextureLabel txt = new TextureLabel(mc.font, mc.color, mc.align, Align.center, bounds);
        addActor(txt);
        return txt;
    }
}
