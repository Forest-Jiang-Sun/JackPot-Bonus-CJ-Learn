package com.aspectgaming.gdx.component.drawable.denom;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.bonus.InBonusEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.ReelStartSpinEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.progressive.ProgressiveIntroEvent;
import com.aspectgaming.common.event.screen.DenomShowEvent;
import com.aspectgaming.common.event.screen.HelpShowEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

/**
 * Show Denomination as button.
 *
 * @author kumo.wang
 */
public class DenominationComponent extends DrawableComponent {

    private static final int OFFSET = 10;
    private static final float ACT_DURATION = 0.3f;

    private int[] denoms;

    private Sound sndButton;
    private Button[] buttons;
    private TextureLabel[] labels;

    private boolean isPlaying;

    private Vector2 pos = new Vector2();
    private Rectangle bounds = new Rectangle();
    private float height = 0;

    public DenominationComponent() {
        setTransform(true);

        sndButton = SoundLoader.getInstance().get("button/button");

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                setTouchable(Touchable.disabled);
                setAlpha(0);
                clear();

                denoms = GameData.getInstance().Setting.Denominations;

                buttons = new Button[denoms.length];
                labels = new TextureLabel[buttons.length];

                bounds = CoordinateLoader.getInstance().getBound("Denomination");

                for (int i = 0; i < denoms.length; i++) {
                    final int denom = denoms[i];

                    Button button = new Button("Denom/denom_");
                    button.setOnClicked(() -> {
                        GameClient.getInstance().selectDenom(denom);
                        sndButton.play();
                        closeDenom();
                    });

                    TextureLabel label = new TextureLabel("DenomFont", Align.center, Align.center);
                    label.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
                    label.setText(GameData.Currency.denomFormat(denom));

                    addActor(button);
                    addActor(label);

                    buttons[i] = button;
                    labels[i] = label;
                }

                pos = CoordinateLoader.getInstance().getCoordinate(buttons[0], "Denomination");
                height = buttons[0].getHeight();
            }
        });

        registerEvent(new LanguageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                for (Button btn : buttons) {
                    btn.updateLanguage();
                }
            }
        });

        registerEvent(new DenomShowEvent() {
            @Override
            public void execute(Object... obj) {
                if (isPlaying) {
                    closeDenom();
                } else {
                    showDenom();
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                closeDenom();
            }
        });

        registerEvent(new HelpShowEvent() {
            @Override
            public void execute(Object... obj) {
                closeDenom();
            }
        });

        registerEvent(new InBonusEvent() {
            @Override
            public void execute(Object... obj) {
                closeDenom();
            }
        });

        registerEvent(new InFreeGameIntroEvent() {
            @Override
            public void execute(Object... obj) {
                closeDenom();
            }
        });

        registerEvent(new ProgressiveIntroEvent() {
            @Override
            public void execute(Object... obj) {
                closeDenom();
            }
        });
    }

    private void showDenom() {
        if (isPlaying) return;

        isPlaying = true;

        int count = 0;

        for (int i = 0; i < buttons.length; i++) {
            int denom = denoms[i];
            Button button = buttons[i];
            TextureLabel label = labels[i];

            if (denom != GameData.getInstance().Context.Denomination) {
                count++;
                button.setVisible(true);
                label.setVisible(true);

                float offset = height * count - OFFSET;
                button.setPosition(pos.x, pos.y + offset);
                label.setPosition(bounds.x, bounds.y + offset);
            } else {
                button.setVisible(false);
                label.setVisible(false);
            }
        }
        setTouchable(Touchable.enabled);
        clearActions();

        addAction(Actions.moveBy(0, OFFSET, ACT_DURATION));
        addAction(fadeIn(ACT_DURATION));
    }

    private void closeDenom() {
        if (!isPlaying) return;

        isPlaying = false;
        setTouchable(Touchable.disabled);

        addAction(Actions.moveBy(0, -OFFSET, ACT_DURATION));
        addAction(fadeOut(ACT_DURATION));
    }
}
