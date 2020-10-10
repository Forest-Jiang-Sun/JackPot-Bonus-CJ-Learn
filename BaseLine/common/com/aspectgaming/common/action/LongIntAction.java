package com.aspectgaming.common.action;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

/**
 * Dualistic function to control a long linear changes
 * 
 * @author johnny.shi
 *
 */
public class LongIntAction extends TemporalAction {

    private long start, end;
    private long value;

    private boolean isStarted;
    private boolean isStopped;

    public LongIntAction() {
        start = 0;
        end = 1;
    }

    public LongIntAction(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected void begin() {
        value = start;
        isStarted = true;
        isStopped = false;
    }

    @Override
    protected void end() {
        isStarted = false;
        isStopped = true;
    }

    @Override
    public void restart() {
        super.restart();
        isStarted = false;
        isStopped = false;
    }

    @Override
    protected void update(float percent) {
        value = Math.round(start + (end - start) * (double) percent);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }
    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isRunning() {
        return isStarted && !isStopped;
    }
}
