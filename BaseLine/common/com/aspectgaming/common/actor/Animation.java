package com.aspectgaming.common.actor;

import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.LoaderUtil;

/**
 * The animation sprite, provided play, stop function
 * 
 * Animation frames should named in 0.png, 1.png, 2.png......
 * 
 * @author ligang.yao
 */
public class Animation extends Image {

    public static final int DEFAULT_FPS = 30;

    private final float S_2_NS = 1000000000;

    private final String path;
    private final int totalFrames;
    private int stopFrame;

    private float duration;
    private float delayTime;
    private long currentFrame;
    private boolean isAutoVisible;
    private Runnable endListener = null;

    private boolean isPlaying;
    private long timePlayingNS;
    private long timeFrameNS;
    private long loops;
    private float width;
    private float height;

    /**
     * Default Constructor, use 30fps as default
     * 
     * @param path
     *            the frames path, should include "/" at the end. ex. "Animation/WinShow/Scatter/"
     */
    public Animation(String path) {
        super();

        this.path = LoaderUtil.filterPath(path);
        this.totalFrames = ImageLoader.getInstance().getAnimFrames(this.path);
        this.stopFrame = this.totalFrames;
        setDuration(((float) stopFrame) / DEFAULT_FPS);

        Image image = ImageLoader.getInstance().load(this.path + currentFrame);
        setSprite(image.getSprite());

        setWidthAndHeight();
        setWidth(width);
        setHeight(height);
    }

    /**
     * Constructor
     * 
     * @param path
     *            the frames path, should include "/" at the end. ex. "Animation/WinShow/Scatter/"
     * @param duration
     *            time to play all the frames
     */
    public Animation(String path, float duration) {
        this(path);
        setDuration(duration);

        setWidthAndHeight();
        setWidth(width);
        setHeight(height);
    }

    /**
     * Constructor
     * 
     * @param path
     *            the frames path, should include "/" at the end. ex. "Animation/WinShow/Scatter/"
     * @param fps
     *            frame per seconds
     */
    public Animation(String path, int fps) {
        this(path);
        setDuration(((float) stopFrame) / fps);

        setWidthAndHeight();
        setWidth(width);
        setHeight(height);
    }

    private void setDuration(float seconds) {
        this.duration = seconds;
        timeFrameNS = ((long) (seconds * S_2_NS)) / stopFrame;
    }

    private void setWidthAndHeight() {
        Image image = ImageLoader.getInstance().load(this.path + this.totalFrames/2);
        width = image.getWidth();
        height = image.getHeight();
    }
    /**
     * Constructor
     * 
     * @param path
     *            the frames path, should include "/" at the end. ex. "Animation/WinShow/Scatter/"
     * @param duration
     *            time to play all the frames
     * @param delay
     *            delay time before start playing
     */
    public Animation(String path, float duration, float delay) {
        this(path, duration);
        this.delayTime = delay;
    }

    /**
     * Constructor
     * 
     * @param path
     *            the frames path, should with "/" in the end. ex. "Animation/WinShow/Scatter/"
     * @param fps
     *            frame per seconds
     */
    public Animation(String path, int fps, float delay) {
        this(path, fps);
        this.delayTime = delay;
    }

    /**
     * @return
     *         total frames
     */
    public int getTotalFrames() {
        return totalFrames;
    }

    /**
     * @return
     *         the time to play all frames, in unit of seconds
     */

    public float duration() {
        return duration;
    }

    public float delayTime() {
        return delayTime;
    }

    public void setDelayTime(float val) {
        delayTime = val;
    }

    public void setEndListener(Runnable val) {
        endListener = val;
    }

    public void setAutoVisible(boolean val) {
        isAutoVisible = val;
        if (isAutoVisible) {
            // consider delay time before start playing
            setVisible(isPlaying && isStarted());
        }
    }

    public void setFrame(long val) {
        if (val >= stopFrame) {
            val = stopFrame - 1;
        }

        updateFrame(val);
    }

    private void updateFrame(long frame) {
        if (currentFrame != frame) {
            currentFrame = frame;
            ImageLoader.getInstance().reload(this, path + currentFrame);
            setWidth(width);
            setHeight(height);
        }
    }

    public void updateCurrentFrame() {
        ImageLoader.getInstance().reload(this, path + currentFrame);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void loop() {
        play(0);
    }

    public void play() {
        play(1);
    }

    /**
     * Play the animation
     * 
     * @param loops
     *            loop times
     */
    public void play(long loops) {
        if (isPlaying) return;

        isPlaying = true;

        this.loops = loops;
        timePlayingNS = 0 - (long) (delayTime * S_2_NS);

        updateFrame(0);

        if (isAutoVisible && isStarted()) {
            setVisible(true);
        }
    }

    private boolean isStarted() {
        return timePlayingNS >= 0;
    }

    /**
     * Play the animation, and stop at last frame.
     */
    public void stopAtFrame(long frame) {
        if (frame >= stopFrame) {
            frame = stopFrame - 1;
        }

        updateFrame(frame);

        onStopped();
    }

    public void playTo(int frame) {
        if (frame >= totalFrames) {
            frame = totalFrames - 1;
        }

        stopFrame = frame;
        play();
    }

    public void continuePlay() {
        stopFrame = totalFrames;
        if (isPlaying) return;

        loops = 1;
        isPlaying = true;
        timePlayingNS = currentFrame * timeFrameNS;
        if (isAutoVisible && isStarted()) {
            setVisible(true);
        }
    }

    public void stopAtLastFrame() {
        stopAtFrame(stopFrame - 1);
    }

    public void stop() {
        onStopped();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isPlaying) {
            timePlayingNS += delta * S_2_NS;

            if (timePlayingNS >= 0) { // need to consider delay time before start playing
                if (isAutoVisible) {
                    setVisible(true);
                }

                long frame = timePlayingNS / timeFrameNS;

                if (frame < stopFrame) {
                    updateFrame(frame);
                } else {
                    long num = frame / stopFrame;
                    frame = frame % stopFrame;

                    timePlayingNS -= (timeFrameNS * stopFrame) * num;

                    if (loops <= 0) { // endless loops
                        updateFrame(frame);

                    } else {
                        loops -= num;

                        if (loops > 0) { // has remaining loops
                            updateFrame(frame);

                        } else { // final loop
                            updateFrame(stopFrame - 1); // show final frame

                            onStopped();
                        }
                    }
                }
            }
        }
    }

    private void onStopped() {
        if (isPlaying) {
            isPlaying = false;

            if (isAutoVisible) {
                setVisible(false);
            }

            if (endListener != null) {
                endListener.run();
            }
        }
    }
}
