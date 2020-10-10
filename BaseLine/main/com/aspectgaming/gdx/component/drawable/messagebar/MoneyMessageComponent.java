package com.aspectgaming.gdx.component.drawable.messagebar;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.machine.ShowMessageEvent;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.MessageLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

/**
 * cash in or cash out.
 * 
 * @author johnny.shi & ligang.yao
 */
public class MoneyMessageComponent extends DrawableComponent {

    private Image background;
    private final Rectangle rect = new Rectangle();

    private TextureLabel text;
    private TextureLabel money;

    private Sound cashIn;
    private Sound cashOut;

    private final String font = "CashMessageFont";

    public MoneyMessageComponent() {
        setVisible(false);

        DisplayConfiguration display = GameConfiguration.getInstance().display;

        background = ImageLoader.getInstance().load("Message/cashBackround");
        addActor(background);

        rect.width = background.getWidth();
        rect.height = background.getHeight();
        rect.x = (display.width - rect.width) / 2;
        rect.y = (display.height - rect.height) / 2;

        background.setPosition(rect.x, rect.y);

        cashIn = SoundLoader.getInstance().get("cash/cashin");
        cashOut = SoundLoader.getInstance().get("cash/cashout");

        text = new TextureLabel(font, Align.center, Align.bottom);
        text.setBounds(rect.x, rect.y + rect.height / 2, rect.width, rect.height / 2);
        text.setColor(getColor());
        addActor(text);

        money = new TextureLabel(font, Align.center, Align.top);
        money.setBounds(rect.x, rect.y, rect.width, rect.height / 2);
        money.setColor(getColor());
        addActor(money);

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                setVisible(false);
            }
        });

        registerEvent(new ShowMessageEvent() {
            @Override
            public void execute(Object... obj) {
                String msg = GameData.getInstance().Context.MoneyRelatedMessages;
                if (msg != null) {
                    if (obj != null && obj.length > 0) {
                        float duration = (Float) obj[0];
                        showMessage(msg, duration);
                    } else {
                        showMessage(msg);
                    }
                }
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                pause();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                resume();
            }
        });
    }

    private void showMessage(String msg) {
        float duration;

        int index = msg.indexOf(':');
        String key = msg.substring(0, index);

        if (key.contains("In")) {
            cashIn.play();
            duration = cashIn.duration();
        } else if (key.contains("Out")) {
            cashOut.play();
            duration = cashOut.duration();
        } else {
            duration = cashIn.duration();
        }

        showMessage(msg, duration);
    }

    private void showMessage(String msg, float duration) {
        clearActions();

        int index = msg.indexOf(':');
        String key = msg.substring(0, index);
        String value = msg.substring(index + 1);

        text.setText(MessageLoader.getInstance().getMessage(key));
        money.setText(GameData.Currency.format(Long.parseLong(value)));

        setVisible(true);
        addAction(delay(duration, hide()));
    }
}
