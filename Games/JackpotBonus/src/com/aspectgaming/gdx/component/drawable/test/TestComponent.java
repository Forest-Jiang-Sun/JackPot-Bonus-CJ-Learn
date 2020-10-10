package com.aspectgaming.gdx.component.drawable.test;

import com.aspectgaming.common.actor.Button;
import com.aspectgaming.common.configuration.ButtonsConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.screen.ShowDiagnosticUIEvent;
import com.aspectgaming.common.event.screen.CloseDiagnosticUIEvent;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.aspectgaming.common.data.State;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

public class TestComponent extends DrawableComponent {
    private Button btTestDemo;
    private Button btTestFreeGames;
    private Button btTestRandomWilds;

    private boolean bShowTestButton;
    private final ButtonsConfiguration cfg;
    private TextField demoField;
    private List<TextButton> demoBtns = new ArrayList<>();
    private boolean isClickedStopedBtn = false;

    public TestComponent() {
        setTouchable(Touchable.enabled);

        cfg = GameConfiguration.getInstance().buttons;
        String name = "demo";

        if (cfg.hasButton(name)) {
            btTestDemo = new Button("Test/Button/demo_");
            btTestDemo.setOnClicked(new Runnable() {
                @Override
                public void run() {
                    updateTestButton();
                }
            });

            addActor(btTestDemo, "TestDemo");

            name = "stops";
            if (cfg.hasButton(name)) {
                btTestFreeGames = new Button("Test/Button/stops_");
                btTestFreeGames.setOnClicked(new Runnable() {
                    @Override
                    public void run() {
                        isClickedStopedBtn = true;
                        if (GameData.getInstance().Context.TestMode) {
                            GameData.getInstance().Context.TestMode = false;
                            EventMachine.getInstance().offerEvent(CloseDiagnosticUIEvent.class);
                        } else {
                            GameData.getInstance().Context.TestMode = true;
                            EventMachine.getInstance().offerEvent(ShowDiagnosticUIEvent.class);
                        }
                    }
                });

                addActor(btTestFreeGames, "TestDemoItem1");
            }

            name = "features";
            if (cfg.hasButton(name)) {
                btTestRandomWilds = new Button("Test/Button/features_");
                btTestRandomWilds.setOnClicked(new Runnable() {
                    @Override
                    public void run() {
                        if (demoField.getText().length() != 0) {
                            GameClient.getInstance().selectTestFeature(demoField.getText());
                            GameClient.getInstance().buttonPlay();
                        }
                    }
                });
                addActor(btTestRandomWilds, "TestDemoItem2");

                TextField.TextFieldStyle style = new TextField.TextFieldStyle();

                Pixmap pixmap = new Pixmap(112, 30, Pixmap.Format.RGBA8888);
                pixmap.setColor(0, 0, 1, 1);
                pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
                Texture texture = new Texture(pixmap);
                pixmap.dispose();
                style.background = new TextureRegionDrawable(new TextureRegion(texture));

                pixmap = new Pixmap(1, 30 - 4, Pixmap.Format.RGBA8888);
                pixmap.setColor(1, 0, 0, 1);
                pixmap.fill();
                texture = new Texture(pixmap);
                pixmap.dispose();
                style.cursor = new TextureRegionDrawable(new TextureRegion(texture));

                style.font = new BitmapFont();
                style.fontColor = new Color(1, 1, 1, 1);
                demoField = new TextField("", style);
                demoField.setSize(112, 30);
                addActor(demoField, "TestDemoItem3");
            }
            name="DemoBTNs";
            if(cfg.hasButton(name)){
                TextButton.TextButtonStyle style=new TextButton.TextButtonStyle();
                BitmapFont bf=new BitmapFont();
                bf.getData().setScale(1.5f);
                style.font=bf;
                style.fontColor = new Color(1, 1, 1, 1);
                style.downFontColor = new Color(1, 1, 1, 1);
                TextButton tb;

                tb = new TextButton("P1", style);
                tb.setPosition(30, 600);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("P1");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("P2", style);
                tb.setPosition(30, 560);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("P2");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Jackpot Feature", style);
                tb.setPosition(30, 520);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("TestP1");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("7X7X7X WIN 4000", style);
                tb.setPosition(30, 480);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("JF");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games", style);
                tb.setPosition(30, 440);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("SC3");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games with Retrigger", style);
                tb.setPosition(30, 400);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("sc3;sc3");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games with P1 win", style);
                tb.setPosition(30, 360);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("sc3;P1");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games with P2 win", style);
                tb.setPosition(30, 320);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("sc3;P2");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games with Jackpot Feature win", style);
                tb.setPosition(30, 280);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("sc3;TestP1");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);

                tb = new TextButton("Free Games with 7X7X7X win 4000", style);
                tb.setPosition(30, 240);
                tb.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameClient.getInstance().selectTestFeature("sc3;JF");
                        GameClient.getInstance().buttonPlay();
                    }
                });
                tb.setVisible(false);
                demoBtns.add(tb);
                addActor(tb);
            }
        }
        bShowTestButton = false;

        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                closeTestButton();

                if (GameData.getInstance().Context.GameState == State.GameIdle || GameData.getInstance().Context.GameState == State.GambleChoice) {
                    if (btTestDemo != null ) btTestDemo.setDisabled(false);
                } else {
                    if (btTestDemo != null ) btTestDemo.setDisabled(true);
                }
            }
        });

        registerEvent(new ReelStartSpinEvent() {
            @Override
            public void execute(Object... obj) {
                closeTestButton();
                if (btTestDemo != null ) btTestDemo.setDisabled(true);
            }
        });

        registerEvent(new ReelStoppedEvent() {
            @Override
            public void execute(Object... obj) {
                if (btTestFreeGames != null &&  isClickedStopedBtn){
                    isClickedStopedBtn = false;
                    EventMachine.getInstance().offerEvent(CloseDiagnosticUIEvent.class);
                }
            }
        });

        registerEvent(new GameStateChangedEvent() {
            @Override
            public void execute(Object... obj) {
                if (GameData.getInstance().Context.GameState == State.GameIdle || GameData.getInstance().Context.GameState == State.GambleChoice) {
                    if (btTestDemo != null ) btTestDemo.setDisabled(false);
                } else {
                    if (btTestDemo != null ) btTestDemo.setDisabled(true);
                }
            }
        });
    }

    private void updateTestButton() {
        bShowTestButton = !bShowTestButton;
        if (btTestFreeGames != null) btTestFreeGames.setVisible(bShowTestButton);
        if (btTestRandomWilds != null) btTestRandomWilds.setVisible(bShowTestButton);
        if (demoField != null) {
            demoField.setText("");
            demoField.setVisible(bShowTestButton);
        }
        for (int i=0; i<demoBtns.size(); ++i) {
            demoBtns.get(i).setVisible(bShowTestButton);
        }
    }

    private void closeTestButton() {
        bShowTestButton = false;
        if (btTestFreeGames != null) btTestFreeGames.setVisible(false);
        if (btTestRandomWilds != null) btTestRandomWilds.setVisible(false);
        if (demoField != null) demoField.setVisible(false);
        for (int i=0; i<demoBtns.size(); ++i) {
            demoBtns.get(i).setVisible(false);
        }
    }
}
