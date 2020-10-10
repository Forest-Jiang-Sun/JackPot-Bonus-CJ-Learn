package com.aspectgaming.gdx.component.drawable.lineButton;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LineButton extends Component {

   public static final int SELECTABLE = 0;
   public static final int SELECTED = 1;
   public static final int UNAVAIABLE = 2;
   private static final int NUM_SKINS = 3;

   private int btnState;
   public final int btnIndex;
   private String skinPath;
   private Image[] bgSprites = new Image[NUM_SKINS];
   private Image[] creditSprites = new Image[NUM_SKINS];
   private ClickListener clickListener;
   private int line;

   private Runnable onClicked = null;

   public LineButton(String skin, int index, int lin) {
      btnState = 0;
      skinPath = skin;
      btnIndex = index;
      line = lin;
      setTouchable(Touchable.enabled);
      init();
   }
   private void init() {
      for (int i=0; i<NUM_SKINS; ++i) {
         bgSprites[i] = ImageLoader.getInstance().load(skinPath + "bg" + i);
         Vector2 point = CoordinateLoader.getInstance().getCoordinate(bgSprites[i], "LineButton" + (btnIndex+1));
         bgSprites[i].setPosition(point.x, point.y);
         addActor(bgSprites[i]);
      }

      for (int i=0; i<NUM_SKINS; ++i) {
         int index = i==1 ? 1:0;
         creditSprites[i] = ImageLoader.getInstance().load(skinPath + btnIndex + "/credit" + index);
         Vector2 point = CoordinateLoader.getInstance().getCoordinate(creditSprites[i], "LineButton" + (btnIndex+1));
         creditSprites[i].setPosition(point.x, point.y);
         addActor(creditSprites[i]);
      }

      setBounds(bgSprites[SELECTABLE].getX(), bgSprites[SELECTABLE].getY(), bgSprites[SELECTABLE].getWidth(), bgSprites[SELECTABLE].getHeight());
      addListener(clickListener = new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if (onClicked != null) {
               if (btnState != UNAVAIABLE ) {
                  onClicked.run();
               }
            }
         }
      });
   }

   public void updateLanguage() {
      for (int i=0; i<NUM_SKINS; ++i) {
         ImageLoader.getInstance().reload(creditSprites[i]);
      }
   }
   public void setOnClicked(Runnable val) {
      onClicked = val;
   }

   public void setState(int state) {
      btnState = state;
      for (int i=0; i<NUM_SKINS; ++i) {
         bgSprites[i].setVisible(state == i);
         creditSprites[i].setVisible(state == i);
      }
   }

   public int getLine() {
      return line;
   }
}
