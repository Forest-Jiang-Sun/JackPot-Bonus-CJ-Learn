package com.aspectgaming.gdx.component.drawable.reel;

import com.aspectgaming.common.actor.Sound;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.net.game.data.MathParam;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;

/**
 * @author ligang.yao
 */
class SpinAction extends Action {

    private static final int NUM_SYMBOLS_STARTING = 1;
    private static final int NUM_SYMBOLS_STOPPING = 1;
    private static final int NUM_SYMBOLS_BEFORE_STOP = 0;

    private static final float TIME_STARTING = 0.1f;
    private static final float TIME_STOPPING = 0.15f; // = 1 symbol / 15 symbols per seconds * 4 times
    private static final float TIME_STOP_INTERVAL = 0.45f;
    private static final float TIME_BOUNCING = 0.08f;
    private static final float TIME_STOP = 0.1f;

    private static final Interpolation EASING_STARTING = Interpolation.pow3In;
    private static final Interpolation EASING_STOPPING = Interpolation.backOut;

    private final int reelId;
    private final int numRows;
    private final float symbolHeight;
    private final float distanceBounce;
    private final float distanceStart;
    private final float distanceStop;
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

    private int fgAnticipationReelId;

    private int gap=-20;

    SpinAction(int reelId, int numRows, float symbolHeight) {
        this.reelId = reelId;
        this.numRows = numRows;
        this.symbolHeight = symbolHeight;

        this.distanceBounce = symbolHeight/3.0f;
        this.distanceStart = NUM_SYMBOLS_STARTING * symbolHeight;
        this.distanceStop = NUM_SYMBOLS_STOPPING * symbolHeight + this.distanceBounce;
        this.distanceBeforeStop = NUM_SYMBOLS_BEFORE_STOP * symbolHeight;

        this.spinSpeed = symbolHeight / 3f * 60; // spin one symbol needs 3 frames @ 60FPS;
//        this.spinSpeed = symbolHeight / 3.75f * 60;
        //this.spinSpeed = symbolHeight / 4.2f * 60;
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
            float delay = 0;
            if (this.reelId > this.fgAnticipationReelId) {
                delay = TIME_STOP_INTERVAL * this.fgAnticipationReelId + 1.5f * (this.reelId - this.fgAnticipationReelId);
            } else {
                delay = TIME_STOP_INTERVAL * this.reelId;
            }

            float distance = this.distance < this.distanceBeforeStop ? this.distanceBeforeStop : this.distance;

            int numSymbols = (int) Math.ceil((distance + delay * this.spinSpeed) / this.symbolHeight);

            // Must - 0.1 to ensure stops are set before spinning numSymbols. Caused by the nature of float number precision.
            this.posPrepare = (numSymbols + 4 + 9) * this.symbolHeight - 0.1f;
            this.posStop = (numSymbols + 4 + 9 + this.numRows - NUM_SYMBOLS_STOPPING) * this.symbolHeight;
        }
    }

    void spinBy(float offset) {
        SingleReel reel = (SingleReel) this.actor;
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

        this.fgAnticipationReelId = Integer.MAX_VALUE;

        for (MathParam param: GameData.getInstance().Context.MathParams) {
            if (param.Key.equals("FREEGAMEANTICIPATION")) {
                this.fgAnticipationReelId = Integer.parseInt(param.Value);
                break;
            }
        }
    }

    float actStarting(float delta) {
        float time = this.duration + delta;

        if (time < TIME_STARTING) {
            this.duration = time;
            float percent = time / TIME_STARTING;
            this.spinBy(distanceStart * EASING_STARTING.apply(percent) - distance);
            return 0;

        } else {
            this.duration = TIME_STARTING;
            this.spinBy(distanceStart - distance);
            this.state = 2;
            return (time - TIME_STARTING);
        }
    }

    float actSpinning(float delta) {
        float distanceRemaining = this.posPrepare - distance;
        float distanceDelta = this.spinSpeed * delta;

        if (distanceDelta < distanceRemaining) {
            this.spinBy(distanceDelta);
            return 0;
        } else {
            this.spinBy(distanceRemaining);
            this.state = 3;
            ((SingleReel) this.actor).setStopSymbols();
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
        float time = this.duration + delta;

        if (time < TIME_STOPPING) {
            this.duration = time;
            float percent = time / TIME_STOPPING;
            this.spinBy(this.distanceStop * EASING_STOPPING.apply(percent) + this.posStop - this.distance);
        } else {
            this.duration = TIME_STOPPING;
            this.spinBy(this.distanceStop + this.posStop - this.distance);
            this.duration = 0;
            this.state = 5;

            ((SingleReel) this.actor).onBounceDown();
        }
    }

    void actBouncing(float delta) {
        float time = this.duration + delta;

        if (time < TIME_BOUNCING) {
            this.duration = time;
            float percent = time / TIME_BOUNCING;
            if (this.distanceBounced + this.distanceBounce * EASING_STOPPING.apply(percent) < this.distanceBounce) {
                this.spinBy(-this.distanceBounce * EASING_STOPPING.apply(percent));
                this.distanceBounced += this.distanceBounce * EASING_STOPPING.apply(percent);
            }
        } else {
            this.duration = TIME_BOUNCING;
            this.spinBy(-(this.distanceBounce - this.distanceBounced));
            this.duration = 0;
            this.state = 6;
        }

        if (!this.isSoundPlayed && this.duration >= TIME_BOUNCING * 0.5) {
            this.isSoundPlayed = true;
            if (((SingleReel) this.actor).playStopSound(reelId)) {
                if (this.isBraking) {
                    sndBrake.play();
                } else {
                    if (sndStop != null)
                        sndStop.play();
                }
            }
        }
    }

    void actStopped(float delta) {
        float time = this.duration + delta;
        if (time < TIME_STOP) {
            this.duration = time;
        } else {
            ((SingleReel) this.actor).onSpinStopped();
            this.state = 7;
        }
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
            this.actStopped(delta);
            if (this.state == 6) return false;
        }

        return true;
    }
}
