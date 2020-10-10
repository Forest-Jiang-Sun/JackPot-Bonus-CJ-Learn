package com.aspectgaming.gdx.component.drawable.tilt;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.State;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.game.GameStateChangedEvent;
import com.aspectgaming.common.event.machine.StateChangedEvent;
import com.aspectgaming.common.event.machine.SystemMessageChangedEvent;
import com.aspectgaming.common.loader.FontLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.MessageLoader;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

public class TiltComponent extends DrawableComponent {

    private static final String CREDIT_HANDPAY_TXT = "cancelled credits hand pay";
    private static final String POWER_RESET_MESSAGE= "power reset";
    private static final String DOOR_OPEN_MESSAGE = "opened";
    private static final String STACKER_INSERTED_MESSAGE = "inserted";
    private static final String STACKER_REMOVED_MESSAGE = "removed";
    private static final String JAMMED_MESSAGE = "Jammed";
    private static final String DISCONNECTED_MESSAGE = "Disconnected";
    private static final String OUT_OF_TICKETS_MESSAGE = "Printer Out of Tickets";
    private static final String TICKET_LOW_MESSAGE = "Printer Ticket Low";
    private static final String ERROR_MESSAGE = "Error";
    private static final String VALIDATION_ID_NOT_CONFIGURED_MESSAGE = "Validation ID Not Configured";
    private static final String NO_PROGRESSIVE_INFORMATION = "No Progressive Information";
    private static final String PROGRESSIVE_NOT_SETUP = "Progressives Not Setup";
    private static final String BILL_FAIL = "Bill Acceptor Malfunctioning";

    private final Image errorTiltBg;
    private final Image subTiltBg;
    private final TextureLabel errorMassage;
    private final TextureLabel subMessage;
    private final Image attendantTilt;
    private final Map<Integer, String> stateMessages = new HashMap<>();

    public TiltComponent() {
        setVisible(false);

        errorTiltBg = ImageLoader.getInstance().load("Tilt/error_tilt_bg", "ErrorTiltBG");
        addActor(errorTiltBg);
        errorMassage = new TextureLabel("MessageFont", Align.center, Align.center, "ErrorTiltMessage");
        addActor(errorMassage);
        errorMassage.setWrap(true);

        subTiltBg = ImageLoader.getInstance().load("Tilt/sub_tilt_bg", "SubTiltBG");
        addActor(subTiltBg);
        subMessage = new TextureLabel("TiltFont", Align.center, Align.center, "SubTiltMessage");
        addActor(subMessage);

        attendantTilt = ImageLoader.getInstance().load("Tilt/call_attendant_tilt_message", "AttendantTilt");
        addActor(attendantTilt);
        attendantTilt.setVisible(false);

        try {
            Field[] fields = State.class.getDeclaredFields();
            for (Field f : fields) {
                int val = f.getInt(State.class);
                stateMessages.put(val, f.getName());
            }
        } catch (Exception e) {
            log.error("Failed to get State messages {}", e);
        }

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessage();
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessage();
            }
        });

        registerEvent(new StateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessage();
            }
        });

        registerEvent(new SystemMessageChangedEvent() {
            @Override
            public void execute(Object... obj) {
                updateMessage();
            }
        });
    }

    private void updateMessage() {
        if (GameData.getInstance().isTilt()) {
            /// get more information for Game Disable state
            List<String> strs = GameData.getInstance().Context.Messages;

            int state = GameData.getInstance().Context.State;
            String strState = stateMessages.get(state);
            String msg = MessageLoader.getInstance().getMessage(strState);

            if(GameData.getInstance().Context.StateStringData != null)
            {
                msg = GameData.getInstance().Context.StateStringData;
            }

//            if (state == State.Handpay) {
//                if (isCancelledHandpay(strs)) {
//                    msg += ": " + GameData.Currency.format(GameData.getInstance().Context.Credits);
//                } else {
//                    msg += ": " + GameData.Currency.format(GameData.getInstance().Context.TotalWin);
//                }
//            }
            if ((state == State.BillAccepting) || (state == State.CashoutPending) || (state == State.TicketPrinting)) {
                attendantTilt.setVisible(false);
            } else {
                attendantTilt.setVisible(true);
            }

            // avoid message box with no text
            if (msg != null && !msg.trim().isEmpty()) {
                subMessage.setText(msg);
                if (strs != null && !strs.isEmpty()) {
                    String errorMsg = "";
                    int count = 0;
                    for (String str : strs) {
                        if (isErrorMessage(str)) {
                            if (count == 0) {
                                errorMsg += str;
                            } else {
                                errorMsg += " / " + str;
                            }
                            count++;
                        }
                    }
                    if ((errorMsg.isEmpty())) {
                        errorTiltBg.setVisible(false);
                    } else {
                        errorTiltBg.setVisible(true);
                    }
                    errorMassage.setText(errorMsg);
                }
                setVisible(true);
                return;
            }
        }

        setVisible(false);
    }

    private boolean isErrorMessage(String str) {
        if (str.toLowerCase().contains(POWER_RESET_MESSAGE.toLowerCase()) || str.toLowerCase().contains(DOOR_OPEN_MESSAGE.toLowerCase())
                || str.toLowerCase().contains(STACKER_INSERTED_MESSAGE.toLowerCase()) || str.toLowerCase().contains(STACKER_REMOVED_MESSAGE.toLowerCase())
                || str.toLowerCase().contains(JAMMED_MESSAGE.toLowerCase()) || str.toLowerCase().contains(DISCONNECTED_MESSAGE.toLowerCase())
                || str.toLowerCase().contains(OUT_OF_TICKETS_MESSAGE.toLowerCase()) || str.toLowerCase().contains(TICKET_LOW_MESSAGE.toLowerCase())
                || str.toLowerCase().contains(ERROR_MESSAGE.toLowerCase()) || str.toLowerCase().contains(VALIDATION_ID_NOT_CONFIGURED_MESSAGE.toLowerCase())
                || str.toLowerCase().contains(NO_PROGRESSIVE_INFORMATION.toLowerCase())
                || str.toLowerCase().equals(BILL_FAIL.toLowerCase())
                || str.toLowerCase().contains(PROGRESSIVE_NOT_SETUP.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCancelledHandpay(List<String> strings) {
        for (String str : strings) {
            if (str.toLowerCase().equals(CREDIT_HANDPAY_TXT)) {
                return true;
            }
        }
        return false;
    }
}
