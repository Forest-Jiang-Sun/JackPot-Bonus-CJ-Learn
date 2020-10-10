package com.aspectgaming.gdx.component.drawable.gamerecall;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.data.GambleData;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

/**
 * @author ligang.yao
 */
public class GameRecallGamble extends DrawableComponent {

    private static final String[] CARDS = { "cards_spade", "cards_clubs", "cards_diamond", "cards_heart" };

    private Image bg;
    private Button riskAllButton;
    private Button riskHalfButton;

    private Button blackButton;
    private Button redButton;
    private Button spadeButton;
    private Button heartButton;
    private Button clubsButton;
    private Button diamondButton;
    private Button takeWinButton;
    private Button gambleButton;
    private Button buttonLevel2[] = new Button[2];
    private Button buttonLevel4[] = new Button[4];
    private Image message;
    private Image card;
    private Image mask;

    private TextureLabel riskAllLabel;
    private TextureLabel riskHalfLabel;
    private TextureLabel takeWinLabel;

    public GameRecallGamble() {
        setVisible(true);
        setTouchable(Touchable.disabled);

        mask = ImageLoader.getInstance().load("Gamble/mask", "GambleMask");
        addActor(mask);
        if (mask != null) mask.setTouchable(Touchable.enabled); // disable buttons under mask

        bg = ImageLoader.getInstance().load("Gamble/gamble_background", "GambleBg");
        addActor(bg);
        if (bg != null) bg.setTouchable(Touchable.enabled); // disable buttons under background

        card = ImageLoader.getInstance().load("Gamble/Card/cards_back", "Card");
        addActor(card);

        message = ImageLoader.getInstance().load("Gamble/gamble_lose", "GambleMessage");
        addActor(message);

        riskAllButton = new Button("Gamble/Risk/riskall_");

        addActor(riskAllButton, "RiskAll");

        riskHalfButton = new Button("Gamble/Risk/riskhalf_");

        addActor(riskHalfButton, "RiskHalf");

        blackButton = new Button("Gamble/Color/black_");

        addActor(blackButton, "Black");

        redButton = new Button("Gamble/Color/red_");

        addActor(redButton, "Red");
        
        buttonLevel2[0] = blackButton;
        buttonLevel2[1] = redButton;

        spadeButton = new Button("Gamble/Suits/spade_");

        addActor(spadeButton, "Spade");

        heartButton = new Button("Gamble/Suits/heart_");

        addActor(heartButton, "Heart");

        clubsButton = new Button("Gamble/Suits/clubs_");

        addActor(clubsButton, "Clubs");

        diamondButton = new Button("Gamble/Suits/diamond_");

        addActor(diamondButton, "Diamond");
        
        buttonLevel4[0] = spadeButton;
        buttonLevel4[1] = clubsButton;
        buttonLevel4[2] = diamondButton;
        buttonLevel4[3] = heartButton;

        takeWinButton = new Button("Gamble/Takewin/takewin_");
        addActor(takeWinButton, "TakeWin");

        gambleButton = new Button("Gamble/Gamble/gamble_");
        gambleButton.setDisabled(true);
        addActor(gambleButton, "GambleInGamle");

        riskAllLabel = new TextureLabel("GambleFont", Align.center, Align.center, "RiskAll");
        addActor(riskAllLabel);

        riskHalfLabel = new TextureLabel("GambleFont", Align.center, Align.center, "RiskHalf");
        addActor(riskHalfLabel);

        takeWinLabel = new TextureLabel("GambleFont", Align.center, Align.center, "TakeWin");
        addActor(takeWinLabel);

        setWidth(1920);
        setHeight(1080);
        setOrigin(0, 0);
        setTransform(true);
    }
    
    private void reset(){
        redButton.setChecked(false);
        redButton.setDisabled(false);

        blackButton.setChecked(false);
        blackButton.setDisabled(false);

        spadeButton.setChecked(false);
        spadeButton.setDisabled(false);

        heartButton.setChecked(false);
        heartButton.setDisabled(false);

        clubsButton.setChecked(false);
        clubsButton.setDisabled(false);

        diamondButton.setChecked(false);
        diamondButton.setDisabled(false);

        takeWinButton.setDisabled(false);
        riskHalfButton.setChecked(false);
        riskHalfButton.setDisabled(false);
        
        riskAllButton.setChecked(false);
        riskAllButton.setChecked(false);
        
        
    }
    

    public void setValue(GambleData gamble) {
        reset();
        riskAllLabel.setValue(gamble.AffordWager);
        riskHalfLabel.setValue(gamble.AffordWager / 2);
        takeWinLabel.setValue(gamble.TotalWin);        

        ImageLoader.getInstance().reload(card, "Gamble/Card/" + CARDS[gamble.Result]);

        switch (gamble.Level) {
        case 2:
            buttonLevel2[gamble.PlayerPick].setChecked(true);
            buttonLevel2[gamble.PlayerPick].setDisabled(true);
            //addActor(ImageLoader.getInstance().load("Recall/gamble_select_b", "Recall" + GAMBLE_LEVEL_2[gamble.PlayerPick]));
            break;
        case 4:
            buttonLevel4[gamble.PlayerPick].setChecked(true);
            buttonLevel4[gamble.PlayerPick].setDisabled(true);
            //addActor(ImageLoader.getInstance().load("Recall/gamble_select_s", "Recall" + GAMBLE_LEVEL_4[gamble.PlayerPick]));
            break;
        }
        
        if(gamble.RiskHalf){
            riskHalfButton.setChecked(true);
            riskHalfButton.setDisabled(true);
        }else{
            riskAllButton.setChecked(true);
            riskAllButton.setDisabled(true);            
        }
        
        if(gamble.Win > 0){
            ImageLoader.getInstance().reload(message, "Gamble/gamble_win");
        }else{
            ImageLoader.getInstance().reload(message, "Gamble/gamble_lose");
        }

    }
}
