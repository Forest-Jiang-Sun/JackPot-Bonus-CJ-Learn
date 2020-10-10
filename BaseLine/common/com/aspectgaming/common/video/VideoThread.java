package com.aspectgaming.common.video;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ligang.yao
 */
public class VideoThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(VideoThread.class);

    private final BlockingQueue<Video> queue = new ArrayBlockingQueue<>(10);

    private volatile boolean isRunning;

    private static final VideoThread instance = new VideoThread();

    public static VideoThread getInstance() {
        return instance;
    }

    private VideoThread() {
        start();
    }

    void startRead(Video video) {
        try {
            queue.put(video);
        } catch (InterruptedException e) {
            log.warn("{}", e);
        }
    }

    public void terminate() {
        interrupt();
        while (isRunning) {
            try {
                sleep(5);
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void run() {
        setName("Video");
        setPriority(MIN_PRIORITY);
        isRunning = true;

        while (!isInterrupted()) {
            try {
                Video video = queue.take();
                synchronized (video) {
                    VideoFrame frame = video.frameRead;
                    long frameIdx = video.frameIndex;

                    if (frame.getData() != null && video.handle != 0 && frameIdx != 0 && frameIdx != video.serial) {
                        long timeStart = System.nanoTime();
                        long ret = VideoCodec.read_frame(video.handle, frameIdx, frame.getData());
                        if (ret >= 0) {
                            video.recordFrameDecodeTime(ret, System.nanoTime() - timeStart);
                            frame.setSerial(ret);
                            frame.setState(VideoFrame.READ_OK);
                        } else {
                            log.warn("Failed to decode:{} Reason:{}", video.path, ret);
                            break; // error condition
                        }
                    } else {
                        frame.setState(VideoFrame.IDLE);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        queue.clear();
        log.info("Video Thread Stopped");
        isRunning = false;
    }
}
