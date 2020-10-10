package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.common.loader.coordinate.Offset;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

import java.util.Random;

public class ProgressiveSpinAction extends Action {
    private static final int NUM_SYMBOLS_STARTING = 0;
    private static final int NUM_SYMBOLS_STOPPING = 1;
    private static final int NUM_SYMBOLS_BEFORE_STOP = 0;

    private static final float TIME_STARTING = 0.0f;
    private static final float TIME_STOPPING = 1.0f; // = 1 symbol / 15 symbols per seconds * 4 times
    private static final float TIME_STOP_INTERVAL = 0.0f;

    private static final Interpolation EASING_STARTING = Interpolation.pow3In;
    private static final Interpolation EASING_STOPPING = Interpolation.backOut;
    private final Random rand = new Random();

    private final int reelId;
    private final int numRows;
    private final float symbolHeight;
    private final float distanceBounce;
    private final float distanceStart;
    private float distanceStop;
    private final float distanceBeforeStop;

    private final Sound sndStop;
    private final Sound sndBrake;

    private float spinSpeed;  // pixels per second

    private float duration = 0;
    private float state = 0; // 0: not started, 1: starting, 2: spinning, 3: preparing, 4: stopping, 5: completed

    private float distance = 0;
    private float distanceBounced = 0;

    private float posPrepare;
    private float posStop = 0;

    private boolean isSoundPlayed = false;
    private boolean isBraking = false;

    ProgressiveSpinAction(int reelId, int numRows, float symbolHeight) {
        this.reelId = reelId;
        this.numRows = numRows;
        this.symbolHeight = symbolHeight;

        this.distanceBounce = symbolHeight / 5;
        this.distanceStart = NUM_SYMBOLS_STARTING * symbolHeight;
        this.distanceBeforeStop = NUM_SYMBOLS_BEFORE_STOP * symbolHeight;

        //this.spinSpeed = symbolHeight / 3.75f * 60; // spin one symbol needs 3 frames @ 60FPS;
        this.spinSpeed = 500; // spin one symbol needs 3 frames @ 60FPS;
        sndStop = SoundLoader.getInstance().get("reel/reelstop");
        sndBrake = sndStop;
    }

    @Override
    public void restart() {
        this.state = 0;
    }

    void accelerate() {
    }

    void brake() {
        if (this.state <= 2) {
            this.isBraking = true;
            float delay = 0;
            float distance = this.distance < this.distanceBeforeStop ? this.distanceBeforeStop : this.distance;

            int numSymbols = (int) Math.ceil((distance + delay * this.spinSpeed) / this.symbolHeight);

            // Must - 0.1 to ensure stops are set before spinning numSymbols. Caused by the nature of float number precision.
            this.posPrepare = numSymbols * this.symbolHeight - 0.1f;
            this.posStop = (numSymbols + this.numRows - NUM_SYMBOLS_STOPPING) * this.symbolHeight;
        }
    }

    void stopSpin() {
        if (this.state <= 2) {
            //float delay = TIME_STOP_INTERVAL * this.reelId;
            float delay = 0;
            float distance = this.distance < this.distanceBeforeStop ? this.distanceBeforeStop : this.distance;

            int numSymbols = (int) Math.ceil((distance + delay * this.spinSpeed) / this.symbolHeight);

            // Must - 0.1 to ensure stops are set before spinning numSymbols. Caused by the nature of float number precision.
            //int random = rand.nextInt(2);
            this.posPrepare = (numSymbols + 4 + 1) * this.symbolHeight - 0.1f;
            this.posStop = (numSymbols + 4 + 1 + this.numRows - NUM_SYMBOLS_STOPPING) * this.symbolHeight;
        }
    }

    void spinBy(float offset) {
        ProgressiveSingReel reel = (ProgressiveSingReel) this.actor;
        this.distance += offset;
        while (offset > this.symbolHeight) {
            reel.spinBy(this.symbolHeight);
            offset -= this.symbolHeight;
        }
        reel.spinBy(offset);
    }

    void actInit() {
        this.isBraking = false;
        this.distance = 0;
        this.duration = 0;
        this.posPrepare = Integer.MAX_VALUE;
        this.isSoundPlayed = false;
        this.distanceBounced = 0;
        //this.distanceStop = NUM_SYMBOLS_STOPPING * symbolHeight + this.distanceBounce - ((ProgressiveSingReel) this.actor).getFinalOffset();
        this.distanceStop = NUM_SYMBOLS_STOPPING * symbolHeight + this.distanceBounce;
    }

    float actStarting(float delta) {
        float time = this.duration + delta;
        this.state = 2;
        return (time - TIME_STARTING);

//        float time = this.duration + delta;
//
//        if (time < TIME_STARTING) {
//            this.duration = time;
//            float percent = time / TIME_STARTING;
//            this.spinBy(distanceStart * EASING_STARTING.apply(percent) - distance);
//            return 0;
//
//        } else {
//            this.duration = TIME_STARTING;
//            this.spinBy(distanceStart - distance);
//            this.state = 2;
//            return (time - TIME_STARTING);
//        }
    }

    float actSpinning(float delta) {
        float distanceRemaining = this.posPrepare - distance;
        float distanceDelta = this.spinSpeed * delta;
        //System.out.println("===========delta: " + delta + "============distanceDelta: " + distanceDelta);

        if (distanceDelta < distanceRemaining) {
            this.spinBy(distanceDelta);
            return 0;
        } else {
            this.spinBy(distanceRemaining);
            this.state = 3;
            ((ProgressiveSingReel) this.actor).setStopSymbols();
            return delta - distanceRemaining / this.spinSpeed;
        }

    }

    float actFilling(float delta) {
        float distanceRemaining = this.posStop - this.distance;
        float distanceDelta = this.spinSpeed * delta;

        if (distanceDelta < distanceRemaining) {
            this.spinBy(distanceDelta);
            return 0;
        } else {
            this.spinBy(distanceRemaining);
            this.state = 4;
            this.duration = 0;
            return delta - distanceRemaining / this.spinSpeed;
        }
    }

    void actStopping(float delta) {
        float distanceDelta = this.spinSpeed * delta;
        float distanceRemaining = this.distanceStop + this.posStop - this.distance;
        if (distanceDelta < distanceRemaining) {
            this.spinBy(distanceDelta);
        } else {
            this.duration = TIME_STOPPING;
            this.spinBy(distanceRemaining);
            this.state = 5;
        }
    }

    void actBouncing(float delta) {
        float dicDelta = this.spinSpeed * delta;
        float dicRemaingBounce = this.distanceBounce - this.distanceBounced;
        if (dicDelta < dicRemaingBounce) {
            this.spinBy(-dicDelta);
            this.distanceBounced += dicDelta;
        } else {
            this.spinBy(-dicRemaingBounce);
            this.duration = 0;
            this.state = 6;
        }
    }

    void actStopped() {
        ((ProgressiveSingReel) this.actor).onSpinStopped();
    }

    @Override
    public boolean act(float delta) {
        Pool<?> pool = getPool();
        setPool(null); // Ensure this action can't be returned to the pool while executing.
        try {
            return update(delta);
        } finally {
            setPool(pool);
        }
    }

    boolean update(float delta) {
        if (this.state == 0) { // starting
            this.actInit();
            this.state = 1;
        }

        if (this.state == 1) {
            delta = this.actStarting(delta);
            if (this.state == 1) return false;
        }

        if (this.state == 2) {
            delta = this.actSpinning(delta);
            if (this.state == 2) return false;
        }

        if (this.state == 3) {
            delta = this.actFilling(delta);
            if (this.state == 3) return false;
        }

        if (this.state == 4) {
            this.actStopping(delta);
            if (this.state == 4) return false;
        }

        if (this.state == 5) {
            this.actBouncing(delta);
            if (this.state == 5) return false;
        }

        if (this.state == 6) {
            this.actStopped();
        }

        return true;
    }
}
