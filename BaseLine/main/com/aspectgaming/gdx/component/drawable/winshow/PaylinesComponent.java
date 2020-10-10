package com.aspectgaming.gdx.component.drawable.winshow;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.ReelComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.SettingData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PaylinesComponent extends DrawableComponent {

    public PaylinesComponent() {
        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
            }
        });
    }

    public void hidePaylines() {
        this.clearChildren();
        this.setVisible(false);
    }

    public void showPaylines(int line, int winMask) {
        this.clearChildren();

        SettingData cfg = GameData.getInstance().Setting;
        int[][] lines = GameConst.getSelectionPositions(cfg.MaxSelections, cfg.SelectedGame);

        if (line >= 0 && lines != null) {

            int distance = line+1;
            Image LineFrame0 =ImageLoader.getInstance().load("PayLine/"+distance + "_0");
            Vector2 pos = CoordinateLoader.getInstance().getPos("IdentifyLine"+distance);
//            LineFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
            LineFrame0.setPosition(pos.x,pos.y);
            addActor(LineFrame0);

            pos = CoordinateLoader.getInstance().getPos("LineNumFrame"+distance);
            Image NumFrame0 = ImageLoader.getInstance().load("PayLine/PaylineNumber" +distance+ "_0");
            //NumFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
            NumFrame0.setPosition(pos.x,pos.y);
            addActor(NumFrame0);

           /* TextureLabel NumLabel=new TextureLabel("PayLineFont", Align.center, Align.center);
            Rectangle bound1 = CoordinateLoader.getInstance().getBound("LineNumber"+distance);
            NumLabel.setBounds(bound1.x,bound1.y,bound1.width,bound1.height);
            NumLabel.setValue(distance);
            addActor(NumLabel);*/
        }

        this.setVisible(true);
    }
}
