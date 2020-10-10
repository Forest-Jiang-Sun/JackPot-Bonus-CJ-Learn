package com.aspectgaming.common.video;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author ligang.yao
 */
public class Video extends Image {

    // -----------------Native codes accessible variables--------------------//
    // read in VideoCodec.open()
    final String path;
    final boolean isCaching; // whether cache all video packets
    final boolean isSeamlessLoop;

    // write in VideoCodec.open()
    long handle;
    int width;
    int height;
    long duration; // ms
    double framesPerSecond;
    long numberOfFrames;
    long cacheSize;
    // ----------------------------------------------------------------------//

    volatile VideoFrame frameRead;
    volatile VideoFrame frameCopy;

    volatile long frameIndex = 1;
    volatile long serial = 0;

    private final VideoTextureData textureData;
    private final Logger log = LoggerFactory.getLogger(Video.class);

    private Runnable endListener = null;
    private Runnable startListener = null;
    private boolean isPlaying = false;
    private boolean isPausable = true;
    private boolean isPausing = false;
    private int pauseFrame = -1;
    private long frameTimeNS;
    private long timePlayingNS;
    private long loops = 0;

    private long decodeTimeNS;
    private int decodeFPS;
    private long copyTimeNS;
    private int copyFPS;

    private boolean alwaysShowFinalFrame;
    private boolean isReadyToPlay = false; // used to avoid showing last frame caused by always showing final frame

    private final List<Sound> sounds = new ArrayList<>();
    private boolean isAutoVisible;

    private boolean syncMode;
    private long syncTimeDelta;
    private long syncTimeCap;

    // slowly calibrate in sync mode: limit calibrate value per frame to at most (0.1 * frame time)
    private static final float CALIBRATE_FRAME_LIMIT = 0.1f;

    public Video(String path, boolean isCaching, boolean isSeamlessLoop, boolean usePBO) {
        this.path = path;
        this.isCaching = isCaching;
        this.isSeamlessLoop = isSeamlessLoop;

        int ret = VideoCodec.open(this); // handle, width, height .... are updated here
        if (ret != 0) throw new RuntimeException("Failed to open:" + path + " Error:" + ret);

        frameRead = new VideoFrame();
        frameCopy = new VideoFrame();

        textureData = new VideoTextureData(this, usePBO);

        // make texture's width/height power of two, so may be bigger than video size
        setSprite(new Sprite(new TextureRegion(new Texture(textureData), 0, 0, width, height)));
        getSprite().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

        setColor(1, 1, 1, 1);
        setSize(width, height);
        setOrigin(0, height); // change original pos to bottom left // setOrigin(width / 2, height / 2);
        setPosition(0, 0);

        forceFPS(framesPerSecond);

        syncTimeCap = (long) (CALIBRATE_FRAME_LIMIT * frameTimeNS);

        log.info("{}", this);
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    /**
     * @return video duration in seconds
     */
    public float getDuration() {
        return ((float) duration) / 1000;
    }

    public long getDurationNS() {
        return frameTimeNS * numberOfFrames;
    }

    public float getTimePlayed() {
        if (isPlaying) return 0;
        return (float) ((double) timePlayingNS / TimeUnit.SECONDS.toNanos(1));
    }

    public float getTimeLeft() {
        float time = getDuration() - getTimePlayed();
        return time > 0 ? time : 0;
    }

    // for the video to use this fps instead of original fps
    public void forceFPS(double fps) {
        frameTimeNS = (long) (TimeUnit.SECONDS.toNanos(1) / fps);
    }

    public void setPausable(boolean val) {
        isPausable = val;
        isPausing = false;
    }

    public void setSyncLoopMode(boolean val) {
        syncMode = val;
    }

    public void setSyncTimeDelta(long val) {
        syncTimeDelta = val;
    }

    public void loop() {
        play(0);
    }

    public void play() {
        play(1);
    }

    public void play(long loops) {
        play(loops, 0);
    }

    public void play(long loops, long startTimeNS) {
        if (isPlaying) return;

        isReadyToPlay = frameIndex == 1; // if current is not first frame, need to wait first frame decoded and uploaded
        timePlayingNS = startTimeNS;
        isPlaying = true;
        isPausing = false;
        frameIndex = 1;
        setLoops(loops);

        if (startListener != null) {
            startListener.run();
        }

        for (Sound snd : sounds) {
            snd.play();
        }

        if (isAutoVisible) {
            setVisible(true);
        }
    }

    public void playTo(int frame) {
        pauseFrame = frame;
        play(0);
    }

    @Override
    public void pause() {
        if (isPausable) {
            isPausing = true;
            for (Sound snd : sounds) {
                snd.pause();
            }
        }
    }

    @Override
    public void resume() {
        isPausing = false;
        for (Sound snd : sounds) {
            snd.resume();
        }
    }

    public void stop() {
        isPlaying = false;
        isPausing = false;
        frameIndex = 1;
        for (Sound snd : sounds) {
            snd.stop();
        }

        if (isAutoVisible) {
            setVisible(false);
        }
    }

    public Runnable stop = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * @param true:
     *            keep at last frame after playback
     *            false: return to first frame after playback
     */
    public void setAlwaysShowFinalFrame(boolean b) {
        alwaysShowFinalFrame = b;

        // return to first frame
        if (!isPlaying && !alwaysShowFinalFrame) {
            frameIndex = 1;
        }
    }

    public long getFrame() {
        return frameIndex;
    }

    public void setFrame(long val) {
        frameIndex = val;
    }
    @Override
    public void act(float delta) {
        super.act(delta);

        if (frameRead.getState() == VideoFrame.READ_OK) {
            updateTexture();
        }

        if (isPlaying && !isPausing) {
            long timeDelta = (long) (delta * TimeUnit.SECONDS.toNanos(1));

            if (syncMode) {
                long timeNet = System.nanoTime() + syncTimeDelta; // could be negative

                long timeLoop = frameTimeNS * numberOfFrames;
                long timeIdeal = timeNet % timeLoop;
                if (timeIdeal < 0) {
                    timeIdeal += timeLoop; // make sure positive
                }

                long dif = timeIdeal - (timePlayingNS + timeDelta);

                if (dif > (timeLoop / 2)) {
                    dif -= timeLoop;
                }

                if (dif < -(timeLoop / 2)) {
                    dif += timeLoop;
                }

                if (dif < -TimeUnit.SECONDS.toNanos(1)) {
                    // keep current image until starting time
                } else if (dif > TimeUnit.SECONDS.toNanos(1)) {
                    timePlayingNS += dif;
                } else {
                    // adjust slowly
                    dif /= 3;
                    if (dif > syncTimeCap) {
                        dif = syncTimeCap;
                    } else if (dif < -syncTimeCap) {
                        dif = -syncTimeCap;
                    }
                    timePlayingNS += timeDelta + dif;
                }
            } else {
                timePlayingNS += timeDelta;
            }

            long idx = 1 + Math.round(((double) timePlayingNS) / frameTimeNS);
            if (idx > numberOfFrames) {
                frameIndex = alwaysShowFinalFrame ? numberOfFrames : 1;
                onVideoEnd(timePlayingNS - frameTimeNS * numberOfFrames);
            } else {
                frameIndex = idx;
            }
        }

        if (!isPausing && pauseFrame == frameIndex ) {
            pause();
            if (endListener != null) {
                endListener.run();
            }
        }

        if (frameIndex != serial) {
            textureData.startRead();
        }
    }

    private void updateTexture() {
        serial = frameRead.getSerial();

        VideoFrame vf = frameRead;
        frameRead = frameCopy;
        frameCopy = vf;

        getSprite().getTexture().bind();
        textureData.updateTexture();

        if (frameIndex >= serial) {
            isReadyToPlay = true;
        }
    }

    private void onVideoEnd(long timeDelta) {
        if (decodeTimeNS != 0 && copyTimeNS != 0) {
            // decodeTimeNS may be 0 if playing broadcasted video on game boot
            decodeFPS = (int) (TimeUnit.SECONDS.toNanos(numberOfFrames) / decodeTimeNS);
            copyFPS = (int) (TimeUnit.SECONDS.toNanos(numberOfFrames) / copyTimeNS);
            log.debug("{} decoded@{}fps uploaded@{}fps", path, decodeFPS, copyFPS);
        }

        timePlayingNS = timeDelta;
        decodeTimeNS = 0;
        copyTimeNS = 0;
        serial = 0;

        if (--loops <= 0) {
            isPlaying = false;
            if (isAutoVisible) {
                setVisible(false);
            }
            if (endListener != null) {
                endListener.run();
            }
        } else {
            if (startListener != null) {
                startListener.run();
            }

            for (Sound snd : sounds) {
                snd.play();
            }
        }
    }

    public void setEndListener(Runnable val) {
        endListener = val;
    }

    public void dispose() {
        if (handle != 0) {
            synchronized (this) {
                VideoCodec.close(handle);
                textureData.disposePixmap();
                getSprite().getTexture().dispose();
                handle = 0;
            }
            log.info("video: {} closed", path);
        }
    }

    void recordFrameDecodeTime(long serial, long deltaTimeNS) {
        decodeTimeNS += deltaTimeNS;
    }

    void recordFrameCopyTime(long deltaTimeNS) {
        copyTimeNS += deltaTimeNS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(path).append(" ");
        sb.append(width).append('x').append(height).append(" ");
        sb.append(numberOfFrames).append("frames ");
        sb.append(duration).append("ms ");
        sb.append(framesPerSecond).append("fps ");
        sb.append(String.format("cache:%,dKB", cacheSize / 1024));
        return sb.toString();
    }

    public void cancelLoop() {
        loops = 1;
    }

    public void setLoops(long val) {
        loops = val > 0 ? val : Long.MAX_VALUE;
    }

    public void setStartListener(Runnable val) {
        startListener = val;
    }

    public void setSound(Sound... val) {
        sounds.clear();

        addSound(val);
    }

    public void addSound(Sound... val) {
        for (Sound snd : val) {
            if (snd != null) {
                sounds.add(snd);
            }
        }
    }

    /**
     * @param true:
     *            automatically show during playback and hide after playback
     *            false: visible need to be controlled manually
     */
    public void setAutoVisible(boolean val) {
        isAutoVisible = val;
        if (isAutoVisible) {
            setVisible(isPlaying);
        }
    }

    public long getNumberOfFrames() {
        return numberOfFrames;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isReadyToPlay) {
            super.draw(batch, parentAlpha);
        }
    }
}
