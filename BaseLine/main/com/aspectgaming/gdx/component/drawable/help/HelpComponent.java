package com.aspectgaming.gdx.component.drawable.help;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.HelpConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.bonus.InBonusEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.machine.ChangeBetEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.progressive.ProgressiveIntroEvent;
import com.aspectgaming.common.event.screen.DisableAllButtonEvent;
import com.aspectgaming.common.event.screen.HelpHideEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.event.screen.LogoShowEvent;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.math.SlotGameMode;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * help page and it's buttons.
 *
 * @author kumo.wang
 */
public class HelpComponent extends DrawableComponent {

    private static final float FADING_TIME = 0.3f;
    private static final float AUTO_HIDE_TIME = 180;

    private final List<Image> pages = new ArrayList<>();
    private Button leftButton;
    private Button rightButton;
    private Button returnToGameButton;

    private int currentPage = 0;
    private Sound sndRuleOut;
    private Sound sndPageTurn;

    private TextureLabel gambleMaxRound;
    private TextureLabel gambleLimit1;
    private TextureLabel gambleLimit2;

    private TextureLabel betMinimum;
    private TextureLabel betMaximum;

    private SlotGameMode gameMode;

    public HelpComponent() {
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

        setAlpha(0);

        sndRuleOut = SoundLoader.getInstance().get("help/GameRulesOut");
        sndPageTurn = SoundLoader.getInstance().get("help/PageTurn");

        loadPages();

        leftButton = new Button("Button/Help/Back_");
        addActor(leftButton, "HelpButtonLeft");
        leftButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                sndPageTurn.play();
                currentPage--;
                updatePage();
                clearActions();
                addAction(delay(AUTO_HIDE_TIME, Actions.run(hideHelp)));
            }
        });

        rightButton = new Button("Button/Help/Next_");
        addActor(rightButton, "HelpButtonRight");
        rightButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                sndPageTurn.play();
                currentPage++;
                updatePage();
                clearActions();
                addAction(delay(AUTO_HIDE_TIME, Actions.run(hideHelp)));
            }
        });

        returnToGameButton = new Button("Button/Help/returnToGame_");
        addActor(returnToGameButton, "HelpButtonReturnToGame");
        returnToGameButton.setOnClicked(new Runnable() {
            @Override
            public void run() {
                sndRuleOut.play();
                clearActions();
                addAction(delay(FADING_TIME, Actions.run(hideHelp)));
                leftButton.setDisabled(true);
                rightButton.setDisabled(true);
            }
        });

        gambleMaxRound = new TextureLabel("HelpFont", Align.center, Align.center, "gambleMaxRound.en-US");
        gambleMaxRound.setVisible(false);
//        addActor(gambleMaxRound);
        gambleLimit1 = new TextureLabel("HelpFont", Align.center, Align.center, "gambleLimit1.en-US");
        gambleLimit1.setVisible(false);
//        addActor(gambleLimit1);
        gambleLimit2 = new TextureLabel("HelpFont", Align.center, Align.center, "gambleLimit2.en-US");
        gambleLimit2.setVisible(false);
//        addActor(gambleLimit2);

        betMinimum = new TextureLabel("HelpFont", Align.center, Align.center, "betMinimum.en-US");
        betMinimum.setVisible(false);
        addActor(betMinimum);
        betMaximum = new TextureLabel("HelpFont", Align.center, Align.center, "betMaximum.en-US");
        betMaximum.setVisible(false);
        addActor(betMaximum);

        registerEvent(new HelpShowEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.enabled);

                leftButton.setDisabled(false);
                rightButton.setDisabled(false);

                currentPage = 0;
                updatePage();
                clearActions();
                EventMachine.getInstance().offerEvent(DisableAllButtonEvent.class);
                // FADE_TIME is for alpha 0->1, if fade from alpha 0.5->1, only use half time
                float duration = FADING_TIME * (1 - getAlpha());
                addAction(sequence(fadeIn(duration), delay(AUTO_HIDE_TIME, run(hideHelp))));
            }
        });

        registerEvent(new LogoShowEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.enabled);
                EventMachine.getInstance().offerEvent(DisableAllButtonEvent.class);
                leftButton.setDisabled(false);
                rightButton.setDisabled(false);

                currentPage = pages.size() - 1;
                updatePage();
                clearActions();
                // FADE_TIME is for alpha 0->1, if fade from alpha 0.5->1, only use half time
                float duration = FADING_TIME * (1 - getAlpha());
                addAction(sequence(fadeIn(duration), delay(AUTO_HIDE_TIME, run(hideHelp))));
            }
        });

        registerEvent(new HelpHideEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.disabled);
                clearActions();
                // FADE_TIME is for alpha 1->0, if fade from alpha 0.5->0, only use half time
                float duration = FADING_TIME * getAlpha();
                addAction(fadeOut(duration));
//                EventMachine.getInstance().offerEvent(GameStateChangedEvent.class);
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (!pages.isEmpty()) {
                    for (Image page : pages) {
                        ImageLoader.getInstance().reload(page);
                    }
                    updatePage();
                    setGambleParamVisible();

                    returnToGameButton.updateLanguage();
                    leftButton.updateLanguage();
                    rightButton.updateLanguage();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });

        registerEvent(new InBonusEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });

        registerEvent(new ProgressiveIntroEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });

//        registerEvent(new ProgressiveConfiguredEvent() {
//            @Override
//            public void execute(Object... obj) {
//                loadPages();
//                updatePage();
//            }
//        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });

        registerEvent(new ChangeBetEvent() {
            @Override
            public void execute(Object... obj) {
                hideHelp();
            }
        });
    }

    private final Runnable hideHelp = new Runnable() {
        @Override
        public void run() {
            EventMachine.getInstance().offerEvent(HelpHideEvent.class);
        }
    };

    private void hideHelp() {
        if (getAlpha() != 0) {
            EventMachine.getInstance().offerEvent(HelpHideEvent.class);
        }
    }

    private void loadPages() {
        clear();
        pages.clear();

        Map<Integer, Image> map = new HashMap<>();
        int count = 0;
        for (int idx = 1; ; idx++) {
            Image page = ImageLoader.getInstance().load("Help/" + idx, "HelpPage");
            if (gameMode==SlotGameMode.StandAlone) {
                page = ImageLoader.getInstance().load("Help2/" + idx, "HelpPage");
            }
            if (gameMode==SlotGameMode.Linked) {
                page = ImageLoader.getInstance().load("Help3/" + idx, "HelpPage");
            }
            if (page != null) {
                count = 0;
                map.put(idx, page);
            } else {
                // continue to read next page if 1 page is missing
                if (++count > 1) {
                    break;
                }
            }
        }

//        HelpConfiguration cfg = GameConfiguration.getInstance().help;
//        if (!GameData.getInstance().Setting.GambleEnabled) {
//            if (cfg.gamble != null) {
//                map.put(cfg.gamble, null);
//            }
//        }
//
//        if (!GameData.getInstance().Setting.ProgressiveEnabled) {
//            if (cfg.progressive != null) {
//                map.put(cfg.progressive, null);
//            }
//        }

        for (Entry<Integer, Image> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                Image page = entry.getValue();
                page.setVisible(false);
                pages.add(page);
                addActor(page);
            }
        }
    }

    private void updatePage() {
        if (currentPage >= pages.size()) {
            currentPage = 0;
        }
        if (currentPage < 0) {
            currentPage = pages.size() - 1;
        }

        for (int i = 0; i < pages.size(); i++) {
            Image page = pages.get(i);
            page.setVisible(i == currentPage);
        }

        setGambleParamVisible();
        setBetLimitValue();
    }

    private void setGambleParamVisible() {
        boolean visible = false;
        Image page = pages.get(currentPage);
        String path = page.getPath();
        String idx = path.substring(path.lastIndexOf("/") + 1, path.length());

        HelpConfiguration cfg = GameConfiguration.getInstance().help;
        if (cfg.gamble == Integer.parseInt(idx)) {
            visible = true;
        } else {
            visible = false;
        }

        gambleMaxRound.setVisible(visible);
        gambleLimit1.setVisible(visible);
        gambleLimit2.setVisible(visible);

        if (visible) {
            gambleMaxRound.setForamtVal(GameData.getInstance().Setting.GambleMaxRound);
            gambleLimit1.setText(GameData.Currency.format(GameData.getInstance().Setting.GambleLimit));
            gambleLimit2.setText(GameData.Currency.format(GameData.getInstance().Setting.GambleLimit));

            String language = GameData.getInstance().Context.Language;
            gambleMaxRound.setBounds("gambleMaxRound." + language);
            gambleLimit1.setBounds("gambleLimit1." + language);
            gambleLimit2.setBounds("gambleLimit2." + language);
        }
    }

    private void setBetLimitValue() {
        boolean visible = false;
        Image page = pages.get(currentPage);
        String path = page.getPath();
        String idx = path.substring(path.lastIndexOf("/") + 1, path.length());

        if (1 == Integer.parseInt(idx)) {
            visible = true;
        } else {
            visible = false;
        }

        betMinimum.setVisible(visible);
        betMaximum.setVisible(visible);

        if (visible) {
            betMinimum.setText(GameData.Currency.format(1 * GameData.getInstance().Context.Denomination));
            betMaximum.setText(GameData.Currency.format(5 * GameData.getInstance().Context.Denomination));

            String language = GameData.getInstance().Context.Language;
            betMinimum.setBounds("betMinimum." + language);
            betMaximum.setBounds("betMaximum." + language);
        }
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
