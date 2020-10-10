package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

/** An OpenGL surface fullscreen or in a lightweight window. */
public class LwjglApplication implements Application {
    protected final LwjglGraphics graphics;
    protected OpenALAudio audio;
    protected final LwjglFiles files;
    protected final LwjglInput input;
    protected final LwjglNet net;
    protected final ApplicationListener listener;
    protected Thread mainLoopThread;
    protected boolean running = true;
    protected final Array<Runnable> runnables = new Array<Runnable>();
    protected final Array<Runnable> executedRunnables = new Array<Runnable>();
    protected final Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>();
    protected int logLevel = LOG_INFO;
    protected String preferencesdir;
    protected ApplicationLogger applicationLogger;

    public LwjglApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
        LwjglNativesLoader.load();
        setApplicationLogger(new LwjglApplicationLogger());

        if (config.title == null) config.title = listener.getClass().getSimpleName();

        graphics = new LwjglGraphics(config);
        if (!LwjglApplicationConfiguration.disableAudio) audio = new OpenALAudio(config.audioDeviceSimultaneousSources, config.audioDeviceBufferCount, config.audioDeviceBufferSize);
        files = new LwjglFiles();
        input = new LwjglInput();
        net = new LwjglNet();
        this.listener = listener;
        this.preferencesdir = config.preferencesDirectory;

        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.audio = audio;
        Gdx.files = files;
        Gdx.input = input;
        Gdx.net = net;
        initialize();
    }

    private void initialize() {
        mainLoopThread = new Thread("App") {
            @Override
            public void run() {
                graphics.setVSync(graphics.config.vSyncEnabled);
                try {
                    LwjglApplication.this.mainLoop();
                } catch (Throwable t) {
                    if (audio != null) audio.dispose();
                    if (t instanceof RuntimeException) throw (RuntimeException) t;
                    else throw new GdxRuntimeException(t);
                }
            }
        };
        mainLoopThread.start();
    }

    void mainLoop() {
        Array<LifecycleListener> lifecycleListeners = this.lifecycleListeners;

        try {
            graphics.setupDisplay();
        } catch (LWJGLException e) {
            throw new GdxRuntimeException(e);
        }

        listener.create();

        graphics.lastTime = System.nanoTime();

        while (running) {
            Display.processMessages();
            if (Display.isCloseRequested()) exit();

            executeRunnables();

            // If one of the runnables set running to false, for example after an exit().
            if (!running) break;

            input.update();
            input.processEvents();
            if (audio != null) audio.update();

            graphics.updateTime();
            graphics.frameId++;
            listener.render();
            Display.update(false);
            Display.sync(graphics.config.foregroundFPS);
        }

        synchronized (lifecycleListeners) {
            for (LifecycleListener listener : lifecycleListeners) {
                listener.pause();
                listener.dispose();
            }
        }
        listener.pause();
        listener.dispose();
        Display.destroy();
        if (audio != null) audio.dispose();
        if (graphics.config.forceExit) System.exit(-1);
    }

    public boolean executeRunnables() {
        synchronized (runnables) {
            executedRunnables.addAll(runnables);
            runnables.clear();
        }
        if (executedRunnables.size == 0) return false;
        for (int i = 0; i < executedRunnables.size; i++)
            executedRunnables.get(i).run();
        executedRunnables.clear();
        return true;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public LwjglGraphics getGraphics() {
        return graphics;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.Desktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    public void stop() {
        running = false;
        try {
            mainLoopThread.join();
        } catch (Exception ex) {}
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return getJavaHeap();
    }

    ObjectMap<String, Preferences> preferences = new ObjectMap<String, Preferences>();

    @Override
    public Preferences getPreferences(String name) {
        if (preferences.containsKey(name)) {
            return preferences.get(name);
        } else {
            Preferences prefs = new LwjglPreferences(name, this.preferencesdir);
            preferences.put(name, prefs);
            return prefs;
        }
    }

    @Override
    public Clipboard getClipboard() {
        return new LwjglClipboard();
    }

    @Override
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) {
            System.out.println(tag + ": " + message);
        }
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) {
            System.out.println(tag + ": " + message);
        }
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) {
            System.err.println(tag + ": " + message);
        }
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) {
            System.err.println(tag + ": " + message);
            exception.printStackTrace(System.err);
        }
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void exit() {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                running = false;
            }
        });
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.add(listener);
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.removeValue(listener, true);
        }
    }

    @Override
    public void setApplicationLogger (ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger () {
        return applicationLogger;
    }
}
