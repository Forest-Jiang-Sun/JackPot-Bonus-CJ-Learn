package com.aspectgaming.gdx;

import static org.lwjgl.opengl.ARBBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBPixelBufferObject.GL_PIXEL_UNPACK_BUFFER_ARB;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.opengl.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.configuration.ComponentConfiguration;
import com.aspectgaming.common.configuration.ComponentsConfiguration;
import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventBody;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.GameEvent;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.gdx.component.Component;
import com.aspectgaming.gdx.component.drawable.exception.ExceptionComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.util.WindowUtil;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * @author ligang.yao & johnny.shi
 */
public class BaseLine implements ApplicationListener {

    private Stage stage;
    private final Logger log = LoggerFactory.getLogger(BaseLine.class);

    @Override
    public void create() {
        if (!GameConfiguration.getInstance().debug) {
            hideMouseCursor();
        }

        if (!GameConfiguration.getInstance().display.continuousRendering) {
            Gdx.graphics.setContinuousRendering(false);
        }

        DisplayConfiguration display = GameConfiguration.getInstance().display;

        Viewport vp;

        // use scaling viewport if needed
        if (Gdx.graphics.getWidth() == display.width && Gdx.graphics.getHeight() == display.height) {
            vp = new ScreenViewport();
        } else {
            vp = new ScalingViewport(Scaling.stretch, display.width, display.height);
        }

        stage = new Stage(vp);

        GameData.Window = WindowUtil.find(GameConfiguration.getInstance().type);

        if (!GameConfiguration.getInstance().display.visible) {
            WindowUtil.hide(GameData.Window);
        }
        WindowUtil.allowSetForeground();

        try {
            ComponentsConfiguration components = GameConfiguration.getInstance().currentResolution().components;
            if (components.component != null) {
                for (ComponentConfiguration component : components.component) {
                    if (!component.debug || GameConfiguration.getInstance().debug) {
                        Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher matcher = pattern.matcher(component.value);
                        String componentClass = matcher.replaceAll("");
                        Class<?> clazz = Class.forName(componentClass);
                        Component comp = (Component) clazz.newInstance();
                        stage.addActor(comp);
                        Content.getInstance().addComponent(comp);

                        // process windows message to avoid game window long time no response during loading.
                        Display.processMessages();
                        // release memory immediately to avoid huge memory usage on boot.
                        System.gc();
                    }
                }
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder(AspectGamingUtil.WORKING_DIR + "\n" + e.toString());
            StackTraceElement[] sTraceElements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : sTraceElements) {
                sb.append("\n\r        at ");
                sb.append(stackTraceElement.toString());
            }
            log.error(sb.toString());

            glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
            Array<Actor> array = stage.getActors();
            for (Actor actor : array) {
                ((Component) actor).dispose();
            }
            stage.clear();
            ExceptionComponent component = new ExceptionComponent(e);
            stage.addActor(component);
        }
        Gdx.input.setInputProcessor(stage);
        GameClient.getInstance().gameLoaded();
    }

    private void hideMouseCursor() {
        try {
            Cursor emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
            org.lwjgl.input.Mouse.setNativeCursor(emptyCursor);
        } catch (LWJGLException e) {
            log.error("Failed to hide mouse cursor!");
        }
    }

    @Override
    public void dispose() {
        Array<Actor> array = stage.getActors();
        for (Actor actor : array) {
            ((Component) actor).dispose();
        }
        stage.dispose();
    }

    private void processEvents() {
        EventBody evt;

        while ((evt = EventMachine.getInstance().takeEvent()) != null) {
            log.info("Event: {}", evt.event.getSimpleName());
            for (Actor actor : stage.getActors()) {
                GameEvent event = ((Component) actor).getEvent(evt.event);
                if (event != null) {
                    event.execute(evt.args);
                }
            }
        }
    }

    private void processPlatformMessages() {
        while (GameClient.getInstance().processMessage()) {
            // need to process all events triggered by current message before handling next message
            processEvents();
        }
    }

    @Override
    public void render() {
        synchronized (GameData.getInstance()) {
            try {
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

                processPlatformMessages();
                processEvents();
                stage.act(Gdx.graphics.getDeltaTime());
                processPlatformMessages();
                processEvents();

                stage.draw();

            } catch (Exception e) {
                StringBuilder sb = new StringBuilder(AspectGamingUtil.WORKING_DIR + "\n" + e.toString());
                StackTraceElement[] sTraceElements = e.getStackTrace();
                for (StackTraceElement stackTraceElement : sTraceElements) {
                    sb.append("\n\r        at ");
                    sb.append(stackTraceElement.toString());
                }
                log.error(sb.toString());

                glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
                Array<Actor> array = stage.getActors();
                for (Actor actor : array) {
                    ((Component) actor).dispose();
                }
                stage.clear();

                stage.dispose();

                ExceptionComponent component = new ExceptionComponent(e);
                stage.addActor(component);
                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
