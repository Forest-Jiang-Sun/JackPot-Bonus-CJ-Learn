package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.SingleReelConfiguration;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.game.ProgressiveSingleReelStoppedEvent;
import com.aspectgaming.common.event.game.SingleReelStoppedEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

public class ProgressiveSingReel extends Group {
    final static int OFFSET_0 = 860;
    final static int OFFSET_1 = 650;
    final static int OFFSET_2 = 440;
    final static int OFFSET_3 = 230;

    ProgressiveReelRollComponent proReelComponent;
    SingleReelConfiguration proReelConfig;

    private int proReelId;
    private float gap;
    private int numCols;
    private int numRows;

    private int[] reelStrip;

    private float symbolHeight;
    private float top;
    private float bottom;

    private final ProgressiveSpinAction action;

    public final List<ProgressiveReelSymbol> symbols = new ArrayList<>();
    private Group symbolLayer;
    private final LinkedList<Integer> stops = new LinkedList<>();

    private int stripIndex = 0;
    private boolean isSpinning = false;
    private final Random rand = new Random();

    int pos;
    int level;

//    public Rectangle scissor;
//    public Rectangle clipbounds;

    public ProgressiveSingReel(ProgressiveReelRollComponent proReelComponent, SingleReelConfiguration proReelConfig, int proReelId) {
        setTouchable(Touchable.childrenOnly);
        setTransform(true);

        this.proReelComponent = proReelComponent;
        this.proReelConfig = proReelConfig;
        this.proReelId = proReelId;
        gap = proReelConfig.symbolInterval;

        numCols = GameConfiguration.getInstance().reel.reels.length;
        //numRows = GameData.getInstance().Context.Result.Stops.length / numCols;
        numRows = 1;

        reelStrip = proReelConfig.getReeStrip("progressive", GameData.getInstance().Context.Selections, "progressive");

        symbolHeight = proReelConfig.symbolHeight;
        setWidth(proReelConfig.width);

        this.action = new ProgressiveSpinAction(this.proReelId, numRows, symbolHeight);

        this.top = (this.symbolHeight + gap) * numRows;
        this.bottom = -this.symbolHeight;

        setHeight(numRows * symbolHeight + (numRows - 1) * gap);

        Vector2 pos = CoordinateLoader.getInstance().getCoordinate(this, "SingleReel4");
        setPosition(pos.x, pos.y - proReelConfig.symbolHeight * this.proReelId);

        symbolLayer = new Group();
        for (int i = 0; i <= numRows; i++) {
            ProgressiveReelSymbol symbol = new ProgressiveReelSymbol(proReelConfig);
            symbol.setOffset(getX(), getY());

            symbols.add(symbol);
            symbolLayer.addActor(symbol);
        }
        this.addActor(symbolLayer);

        this.setDefaultStopSymbols(); // set to default stops
        this.resetPositions();
    }

    public void onLanguageChanged() {
        for (ProgressiveReelSymbol symbol : symbols) {
            symbol.onLanguageChanged();
        }
    }

    public void setDefaultStopSymbols() {
        this.stops.clear();

//        String defaultStops = GameConfiguration.getInstance().progressiveReel.defaultStops;
//        int[] allStops = CommonUtil.stringToArray(defaultStops);
//
//        for (int i = 0; i < numRows ; i ++) {
//            this.stops.push(allStops[i]);
//            this.stops.push(2);
//        }
    }

    public void resetPositions() {
        for (int i = 0; i < this.symbols.size(); i++) {
            ProgressiveReelSymbol symbol = this.symbols.get(i);
            this.updateSymbol(symbol);
            symbol.setY((gap + symbolHeight) * i);
        }
    }

    public void updateSymbol(ProgressiveReelSymbol symbol) {
        int numReels = GameConfiguration.getInstance().reel.reels.length;
        if (!this.stops.isEmpty()) {
            symbol.setSymbol(this.stops.pop());
            symbol.setPositionId(this.stops.size() * numReels + this.proReelId);
        } else {
            symbol.setSymbol(this.getNextSymbol());
            symbol.setPositionId(-1);
        }
    }

    private int getNextSymbol() {
        int symbolID;

        if (stripIndex >= reelStrip.length) {
            symbolID = rand.nextInt(4) + 1;
        } else {
            symbolID = reelStrip[stripIndex];
            stripIndex++;
        }

        return symbolID;
    }

    private void resetStripIndex() {
        stripIndex = 0;
    }

    private int levelToSymbolId(int level) {
        if (level > 0) {
            return level;
        } else {
            return ProgressiveReelSymbol.PRO_NOTHING;
        }
    }

    public void setStopSymbols() {
        setDefaultStopSymbols();
        int symbolId = levelToSymbolId(this.level);
        this.stops.push(symbolId);
    }

    public void spinBy(float offset) {
        for (ProgressiveReelSymbol symbol : this.symbols) {
            float y = symbol.getY() - offset;
            if (y < this.bottom) {
                y += this.top - this.bottom;
                this.updateSymbol(symbol);
            }
            symbol.setY(y);
        }
    }

    public void startSpin(int pos, int level) {
        if (!this.isSpinning) {
            this.isSpinning = true;

            this.pos = pos;
            this.level = level;

            this.action.reset();
            this.addAction(this.action);

            float spinDuration = GameConfiguration.getInstance().progressiveReel.spinDuration;

            this.addAction(Actions.delay(spinDuration, Actions.run(new Runnable() {
                @Override
                public void run() {
                    stopSpin();
                }
            })));
        }
    }

    public void stopSpin() {
        if (!this.isSpinning) return;
        this.action.stopSpin();
    }

    public void onSpinStopped() {
        this.isSpinning = false;

        // Change float positions to integer positions
        for (ProgressiveReelSymbol symbol : this.symbols) {
            symbol.adjustPosition();
        }

        EventMachine.getInstance().offerEvent(ProgressiveSingleReelStoppedEvent.class, this.pos, this.level);
    }

    public void onGameReset() {
        this.isSpinning = false;
        resetStripIndex();
        setDefaultStopSymbols();
        resetPositions();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        batch.flush();
//
//        ScissorStack.calculateScissors(getStage().getCamera(), batch.getTransformMatrix(), this.clipbounds, this.scissor);
//        ScissorStack.pushScissors(this.scissor);

        super.draw(batch, parentAlpha);
//        batch.flush();
//
//        ScissorStack.popScissors();
//        getStage().getCamera().update(false);
    }
}
