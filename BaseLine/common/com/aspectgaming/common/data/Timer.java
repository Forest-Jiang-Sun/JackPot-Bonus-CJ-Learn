package com.aspectgaming.common.data;

import java.util.concurrent.TimeUnit;

/**
 * @author ligang.yao
 */
public class Timer {

    private volatile long timeOut = 0;
    private volatile long timeStart = 0;

    public Timer() {}

    public Timer(long millis) {
        setTimeOutMS(millis);
    }

    public void setTimeOutNS(long nanos) {
        timeOut = nanos;
    }

    public void setTimeOutMS(long millis) {
        setTimeOutNS(TimeUnit.MILLISECONDS.toNanos(millis));
    }

    public void restart() {
        timeStart = System.nanoTime();
    }

    public void stop() {
        timeStart = 0;
    }

    public boolean isEnabled() {
        return timeStart != 0 && timeOut > 0;
    }

    public boolean isExpired() {
        return isEnabled() && (System.nanoTime() - timeStart) >= timeOut;
    }
}
