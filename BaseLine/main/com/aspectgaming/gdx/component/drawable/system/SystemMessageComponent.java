package com.aspectgaming.gdx.component.drawable.system;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.List;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.MessageConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.SystemMessageChangedEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.FontLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

/**
 * Show platform message.
 * 
 * @author ligang.yao
 *
 */
public class SystemMessageComponent extends DrawableComponent {

    private Label txtMessage;
    private int index;
    private boolean showMeter;
    private final String txtMeter = " Spins Since Last Feature";

    private final Runnable updateMessage = new Runnable() {
        @Override
        public void run() {
            List<String> messages = GameData.getInstance().Context.Messages;
            long meter = GameData.getInstance().Context.GamesSinceLastFeature;

            if (showMeter) {
                if (messages.size() > 0) {
                    if (index < messages.size()) {
                        txtMessage.setText(messages.get(index));
                        index++;
                    } else if (index == messages.size() && meter > 0) {
                        txtMessage.setText(meter + txtMeter);
                        index = 0;
                    } else {
                        txtMessage.setText(messages.get(0));
                        index = 1;
                    }
                } else {
                    if (meter > 0) {
                        txtMessage.setText(meter + txtMeter);
                    } else {
                        txtMessage.setText("");
                    }
                    index = 0;
                }
                txtMessage.setVisible(true);
            } else {
                if (messages.size() > 0) {
                    if (index < messages.size()) {
                        txtMessage.setText(messages.get(index));
                        index++;
                    } else {
                        txtMessage.setText(messages.get(0));
                        index = 1;
                    }
                    txtMessage.setVisible(true);
                } else {
                    txtMessage.setVisible(false);
                    index = 0;
                }
            }
        }
    };

    public SystemMessageComponent() {
        MessageConfiguration cfg = GameConfiguration.getInstance().message;
        showMeter = (cfg != null && cfg.spinsSinceLastFeature);

        LabelStyle style = new LabelStyle(FontLoader.getInstance().load("SystemMessageFont"), Color.WHITE);
        Rectangle rect = CoordinateLoader.getInstance().getBound("SystemMessage");
        txtMessage = new Label("", style);
        txtMessage.setAlignment(Align.right);
        txtMessage.setBounds(rect.x, rect.y, rect.width, rect.height);
        addActor(txtMessage);

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessages();
            }
        });

        registerEvent(new SystemMessageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessages();
            }
        });
    }
    private void updateMessages() {
        clearActions();
        index = 0;

        updateMessage.run();

        if (txtMessage.isVisible()) {
            addAction(forever(delay(2, run(updateMessage))));
        }
    }
}
