package com.aspectgaming.gdx.component.drawable.reel;

import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.SingleReelConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.game.SingleReelBounceDownEvent;
import com.aspectgaming.common.event.game.SingleReelStoppedEvent;
import com.aspectgaming.common.event.machine.ChangeTestReelEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.gdx.component.drawable.meter.MetersComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.MathParam;
import com.aspectgaming.net.game.data.RegisterData;
import com.aspectgaming.util.CommonUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.*;

import static com.aspectgaming.gdx.component.drawable.reel.Symbol.BN;

/**
 * @author johnny.shi & ligang.yao
 */
public class SingleReel extends Group {

    private final Logger log = LoggerFactory.getLogger(SingleReel.class);
    private final ReelComponent reelComponent;
    private final int reelID; // reel index from 0
    private final SingleReelConfiguration cfg;
    private final float gap;
    private final int numRows;
    private final int numCols;

    public final List<Symbol> symbols = new ArrayList<>();

    private Group symbolLayer;
    protected Image bgMask;
    private Image[] masks;

    private int baseStripIndex = 0;
    private int freeStripIndex = 0;
    private int[] baseStrip;
    private int[] freeStrip;

    private int testModeStripIndex = 0;
    private int[] testModeStrip;

    private Image reelPosBg;
    private TextureLabel reelPosTxt;

    private float endPositionY;

    private boolean isSpinning;

    private boolean inTestMode;
    private boolean isSpinOnce;

    private boolean isFastSpin = false;
    private final float symbolHeight;

    private final ShaderProgram blurShader;
    private final ShaderProgram blurShaderFastSpin;
    private final FrameBuffer blurTargetA;
    private final TextureRegion fboRegion;

    private final SpinAction action;
    private final float top;
    private final float bottom;
    private final LinkedList<Integer> stops = new LinkedList<>();

    private final Random rand = new Random();

    public SingleReel(ReelComponent reels, SingleReelConfiguration reelConfig) {
        setTouchable(Touchable.childrenOnly);

        setTransform(true);

        reelComponent = reels;
        cfg = reelConfig;
        reelID = cfg.index;
        gap = cfg.symbolInterval;
        numCols = getNumofReels();
        numRows = GameData.getInstance().Context.Result.Stops.length / numCols;

        baseStrip = cfg.getReeStrip("BaseGame", GameData.getInstance().Context.Selections, GameData.getInstance().Context.Paytable);
        freeStrip = cfg.getReeStrip("FreeGame", GameData.getInstance().Context.Selections, GameData.getInstance().Context.Paytable);

        loadBackground();

        symbolHeight = cfg.symbolHeight;
        setWidth(cfg.width);

        this.action = new SpinAction(reelID, numRows, symbolHeight);
        this.top = (this.symbolHeight + gap) * numRows;
        this.bottom = -this.symbolHeight+gap;

        setHeight(numRows * symbolHeight + (numRows - 1) * gap);

        Vector2 pos = CoordinateLoader.getInstance().getCoordinate(this, "SingleReel" + reelID);
        setPosition(pos.x, pos.y);

        symbolLayer = new Group();

        for (int i = 0; i <= numRows; i++) {
            Symbol symbol = new Symbol(cfg);
            symbol.setOffset(getX(), getY());

            symbols.add(symbol);
            symbolLayer.addActor(symbol);
        }
        this.addActor(symbolLayer);

        this.setStopSymbols(); // set to default stops
        this.resetPositions();


        endPositionY = -symbolHeight;

        reelPosBg = ImageLoader.getInstance().load("Test/ReelMask");
        reelPosBg.setVisible(false);
        reelPosBg.setPosition(0, getHeight() - reelPosBg.getHeight()-85);
        addActor(reelPosBg);

        reelPosTxt = new TextureLabel("MeterFont", Align.center, Align.center);
        Rectangle rect = new Rectangle(reelPosBg.getX(), reelPosBg.getY()-10, reelPosBg.getWidth(), reelPosBg.getHeight());
        reelPosTxt.setBounds(rect);
        reelPosTxt.setVisible(false);
        addActor(reelPosTxt);

        // important since we aren't using some uniforms and attributes that SpriteBatch expects
        ShaderProgram.pedantic = false;

        if (GameConfiguration.getInstance().reel.motionBlur != 0) {
            blurShader = new ShaderProgram(MotionBlurShaderSpec.VERTEX, MotionBlurShaderSpec.FRAGMENT_8);
            if (!blurShader.isCompiled()) {
                log.error(blurShader.getLog());
                System.exit(0);
            }
            // setup uniforms for our shader
            blurShader.begin();
            blurShader.setUniformf("resolution", GameConfiguration.getInstance().display.height);
            blurShader.setUniformf("radius", GameConfiguration.getInstance().reel.motionBlur);
            blurShader.end();
        } else {
            blurShader = null;
        }

        if (GameConfiguration.getInstance().reel.fastSpin.motionBlur != 0) {
            blurShaderFastSpin = new ShaderProgram(MotionBlurShaderSpec.VERTEX, MotionBlurShaderSpec.FRAGMENT_16);
            if (!blurShaderFastSpin.isCompiled()) {
                log.error(blurShaderFastSpin.getLog());
                System.exit(0);
            }

            blurShaderFastSpin.begin();
            blurShaderFastSpin.setUniformf("resolution", GameConfiguration.getInstance().display.height);
            blurShaderFastSpin.setUniformf("radius", GameConfiguration.getInstance().reel.fastSpin.motionBlur);
            blurShaderFastSpin.end();
        } else {
            blurShaderFastSpin = null;
        }

        if (blurShader != null || blurShaderFastSpin != null) {
            blurTargetA = new FrameBuffer(Format.RGBA8888, (int) getWidth(), (int) getHeight(), false);
            fboRegion = new TextureRegion(blurTargetA.getColorBufferTexture());
            fboRegion.flip(false, true);
        } else {
            blurTargetA = null;
            fboRegion = null;
        }
    }

    protected void loadBackground() {
        if (GameData.getInstance().isWaysGame()) {
            bgMask = ImageLoader.getInstance().load("Background/reelmask");

            Vector2 delta = CoordinateLoader.getInstance().getOffset("ReelMask");
            if (delta != null) {
                bgMask.setPosition(delta.x, delta.y);
            }

            addActor(bgMask);
        }
    }

    protected int getNumofReels() {
        return GameConfiguration.getInstance().reel.reels.length;
    }

    protected int getStopIndex(int reelIndex) {
        return reelIndex * numCols + reelID;
    }

    public void showMask(boolean visible) {
        if (bgMask != null) bgMask.setVisible(visible);
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] != null) {
                masks[i].setVisible(visible);
            }
        }
    }

    public void spinFast(int index) {
        if (isFastSpin) return;

        isFastSpin = true;
        this.action.accelerate();
    }

    private void stopAction() {

    }

    public void reelStop() {
        this.action.brake();
    }

    public int getReelID() {
        return reelID;
    }

    public boolean playStopSound(int reelID) {
        return !reelComponent.anticipationSpincmp.haveSmartSound(reelID)&&!reelComponent.have7XSound(reelID);
    }

    public void testReelID() {
        if (Gdx.input.getX() > getX() && Gdx.input.getX() < getX() + getWidth()) {
            EventMachine.getInstance().offerEvent(ChangeTestReelEvent.class, reelID);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (blurShader != null || (blurShaderFastSpin != null && isFastSpin)) {
            applyTransform(batch, computeTransform());

            if (bgMask != null && bgMask.isVisible()) bgMask.draw(batch, parentAlpha);

            if (isSpinning) {
                resetTransform(batch);

                // Start rendering to an offscreen color buffer
                blurTargetA.begin();

                Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                // resize the batch projection matrix before drawing with it
                resizeBatch(batch, blurTargetA.getWidth(), blurTargetA.getHeight());

                // draw symbols
                for (Symbol symbol : symbols) {
                    symbol.draw(batch, parentAlpha);
                }

                // finish rendering to the offscreen buffer
                batch.flush();

                // finish rendering to the offscreen buffer
                blurTargetA.end();

                batch.setShader(isFastSpin ? blurShaderFastSpin : blurShader);

                // update our projection matrix to match the game screen
                DisplayConfiguration world = GameConfiguration.getInstance().display;
                resizeBatch(batch, world.width, world.height);

                // draw target B to the screen with a vertical blur effect
                fboRegion.setTexture(blurTargetA.getColorBufferTexture());

                batch.draw(fboRegion, getX(), getY());

                // reset to default shader without blurs
                batch.setShader(null);
                applyTransform(batch, computeTransform());
            } else {
                for (Symbol symbol : symbols) {
                    symbol.draw(batch, parentAlpha);
                }
            }

            for (int i = 0; i < masks.length; i++) {
                if (masks[i] != null && masks[i].isVisible()) masks[i].draw(batch, parentAlpha);
            }

            if (reelPosBg != null && reelPosBg.isVisible()) reelPosBg.draw(batch, parentAlpha);
            if (reelPosTxt != null && reelPosTxt.isVisible()) reelPosTxt.draw(batch, parentAlpha);

            resetTransform(batch);
        } else {
            super.draw(batch, parentAlpha);
        }
    }

    void resizeBatch(Batch batch, int width, int height) {
        OrthographicCamera cam = (OrthographicCamera) getStage().getCamera();
        cam.setToOrtho(false, width, height);
        batch.setProjectionMatrix(cam.combined);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (inTestMode && isSpinOnce) {
            if (isSpinning) {
                for (Symbol symbol : symbols) {
                    if (symbol.getY() <= endPositionY) {
                        for (Symbol symbol2 : symbols) {
                            symbol2.reelIndex++;
                            symbol2.stopIndex = getStopIndex(symbol2.reelIndex);
                        }
                        symbol.reelIndex = -1;
                        symbol.stopIndex = -1;

                        Symbol topSymbol = null;
                        for (Symbol symbol2 : symbols) {
                            if (symbol2.reelIndex == 0) {
                                topSymbol = symbol2;
                                break;
                            }
                        }
                        symbol.setY(topSymbol.getY() + (gap + symbolHeight));

                        stopAction();
                        isSpinning = false;
                        isSpinOnce = false;
                        GameClient.getInstance().setTestStops(reelComponent.getStops());
                    }
                }
                if (!isSpinning) {
                    for (Symbol symbol : symbols) {
                        float y = (numRows - 1 - symbol.reelIndex) * (gap + symbolHeight);
                        symbol.setPosition(symbol.getX(), y);
                    }
                }
            }
        }
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public Symbol getSymbol(int stopIndex) {
        for (Symbol symbol : symbols) {
            if (symbol.stopIndex == stopIndex) {
                return symbol;
            }
        }
        return null;
    }

    private void resetTestStrips() {
        testModeStripIndex = 0;
        reelPosTxt.setValue(testModeStripIndex);
        testModeStrip = cfg.getReeStrip("BaseGame", GameData.getInstance().Context.Selections, GameData.getInstance().Context.Paytable);

        for (int i = 0; i < symbols.size() - 1; i++) {
            Symbol symbol = getSymbol(getStopIndex(i));
            symbol.setSymbol(testModeStrip[i]);
        }
        for (int i = 0; i < this.symbols.size() - 1; i++) {
            Symbol symbol = getSymbol(getStopIndex(this.symbols.size() - 2 - i));
            symbol.setY((gap + symbolHeight) * i);
        }
        arrangeZOrder();
    }

    public void changeTestReelStopDown() {
        if (GameData.dignosticReelIndex == reelID) {
            if (testModeStripIndex == 0) {
                testModeStripIndex = testModeStrip.length - 1;
            } else {
                testModeStripIndex--;
            }
            reelPosTxt.setValue(testModeStripIndex);

            for (int i = 0; i < this.symbols.size() - 1; i++) {
                Symbol symbol = getSymbol(getStopIndex(i));
                symbol.setSymbol(testModeStrip[(testModeStripIndex + i) % testModeStrip.length]);
            }
            GameClient.getInstance().setTestStops(reelComponent.getStops());
            for (int i = 0; i < this.symbols.size() - 1; i++) {
                Symbol symbol = getSymbol(getStopIndex(this.symbols.size() - 2 - i));
                symbol.setY((gap + symbolHeight) * i);
            }
            arrangeZOrder();
        }
    }

    public void changeTestReelStopUp() {
        if (GameData.dignosticReelIndex == reelID) {
            if (testModeStripIndex == testModeStrip.length - 1) {
                testModeStripIndex = 0;
            } else {
                testModeStripIndex++;
            }
            reelPosTxt.setValue(testModeStripIndex);

            for (int i = 0; i < this.symbols.size() - 1; i++) {
                Symbol symbol = getSymbol(getStopIndex(i));
                symbol.setSymbol(testModeStrip[(testModeStripIndex + i) % testModeStrip.length]);
            }
            GameClient.getInstance().setTestStops(reelComponent.getStops());
            for (int i = 0; i < this.symbols.size() - 1; i++) {
                Symbol symbol = getSymbol(getStopIndex(this.symbols.size() - 2 - i));
                symbol.setY((gap + symbolHeight) * i);
            }
            arrangeZOrder();
        }
    }

    protected void setReelPosVisible(boolean bVisible){
        reelPosBg.setVisible(bVisible);
        reelPosTxt.setVisible(bVisible);
    }

    // --------------------------------- new solution

    public void onGameReset() {
        inTestMode = GameData.getInstance().Context.TestMode;
        this.isSpinning = false;
        clearActions();
        onLanguageChanged();

        stopAction();

        if (inTestMode) {
            setTouchable(Touchable.enabled);
            resetTestStrips();

            reelPosBg.setVisible(true);
            reelPosTxt.setVisible(true);
        } else {
            setTouchable(Touchable.childrenOnly);
            if (this.isSpinning) {
                this.setStopSymbols();
                this.reelStop();
            } else {
                this.resetStopSymbols();
                this.resetPositions();
            }

            reelPosBg.setVisible(false);
            reelPosTxt.setVisible(false);
        }
    }

    private void resetStopSymbols() {
        this.stops.clear();

        int[] allStops = GameData.getInstance().Context.Result.Stops;
        int numReels = GameConfiguration.getInstance().reel.reels.length;

        for (int idx = this.getReelID(); idx < allStops.length; idx += numReels) {
            this.stops.push(allStops[idx]);
        }
    }

    public void onLanguageChanged() {
        for (Symbol symbol : symbols) {
            symbol.onLanguageChanged();
        }
    }

    public void onGameModeChanged() {
        for (Symbol symbol : symbols) {
            symbol.onGameModeChanged();
        }
        if (GameData.currentGameMode == GameMode.FreeGame) {
            setFreeGameStartSymbos();
            this.resetPositions();
        }
    }

    public void resetPositions() {
        for (int i = 0; i < this.symbols.size(); i++) {
            Symbol symbol = this.symbols.get(i);

            this.updateSymbol(symbol);

            symbol.setY((gap + symbolHeight) * i);
        }
        arrangeZOrder();
    }

    private List<Symbol> symbolCache = new ArrayList<>();

    private void arrangeZOrder() {
        symbolCache.clear();
        symbolCache.addAll(this.symbols);
        Collections.sort(symbolCache);

        int z = 10;
        for (Symbol symbol : this.symbolCache) {
            symbol.setZIndex(z++);
        }
    }

    public void startSpin() {
        if (!this.isSpinning) {
            this.isSpinning = true;

            isFastSpin = false;
            isSpinOnce = false;

            stopWinShow();

            for (Symbol symbol : this.symbols) {
                symbol.setWinShowMode(false);
            }

            this.stops.clear();  // set this value just before stopping
            this.action.reset();
              this.addAction(this.action);

            float spinDuration = GameConfiguration.getInstance().reel.spinDuration;

            if (reelComponent.preFeatureDuration > 0) {
                spinDuration = reelComponent.preFeatureDuration + 1.0f;
            }
            int[] stops = GameData.getInstance().Context.Result.Stops;
            if((stops[3]==1 ||stops[6]==1 || stops[9]==1) && (stops[4]==1 || stops[7]==1 || stops[10]==1 ) && reelID==2) {
                spinDuration += 1.4f;
            }
            if(stops[3]==3  && stops[4]==3  && reelID==2){
                spinDuration += 1.4f;
            }
            if ((stops[3]==BN || stops[6]==BN || stops[9]==BN) && reelID!=0 ){
                spinDuration += 1f;
                if ((stops[4]==BN || stops[7]==BN || stops[10]==BN) && reelID==2){
                    spinDuration += 1f;
                }
            }
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

    public void onBounceDown() {
        EventMachine.getInstance().offerEvent(SingleReelBounceDownEvent.class, reelID);
    }

    public void onSpinStopped() {
        this.isSpinning = false;
        EventMachine.getInstance().offerEvent(SingleReelStoppedEvent.class, reelID);
        this.resetStopSymbols();
        this.resetPositions();
        // Change float positions to integer positions
        for (Symbol symbol : this.symbols) {
            symbol.adjustPosition();
        }
    }

    public void setFreeGameStartSymbos() {
        this.stops.clear();
        GameConfiguration cfg = GameConfiguration.getInstance();
        int[] allStops = CommonUtil.stringToArray(cfg.reel.defaultFreeGameStops);
        int numReels = GameConfiguration.getInstance().reel.reels.length;

        for (int idx = this.reelID; idx < allStops.length; idx += numReels) {
            this.stops.push(allStops[idx]);
        }
    }

    public void setStopSymbols() {
        this.stops.clear();

        int[] allStops = GameData.getInstance().Context.Result.Stops;
        int numReels = GameConfiguration.getInstance().reel.reels.length;

        for (int idx = this.reelID; idx < allStops.length; idx += numReels) {
            this.stops.push(allStops[idx]);
        }
    }

    public void spinBy(float offset) {
        for (Symbol symbol : this.symbols) {
            float y = symbol.getY() - offset;
            if (y < this.bottom) {
                y += this.top - this.bottom;
                this.updateSymbol(symbol);
            }
            symbol.setY(y);
        }
    }

    public void setSymbolsVisible()
    {
        for (Symbol symbol : this.symbols) {
            symbol.setAlpha(1);
        }
    }

    public void updateSymbol() {
        this.setStopSymbols();
        this.resetPositions();
    }

    public void updateSymbol(Symbol symbol) {
        int numReels = GameConfiguration.getInstance().reel.reels.length;
        if (!this.stops.isEmpty()) {
            symbol.setSymbol(this.stops.pop());
            symbol.setPositionId(this.stops.size() * numReels + this.reelID);

        } else {
            symbol.setSymbol(this.getNextSymbol());
            symbol.setPositionId(-1);
        }
        arrangeZOrder();
    }

    public void updateStrip() {
        baseStripIndex = 0;
        freeStripIndex = 0;
        baseStrip = cfg.getReeStrip("BaseGame", GameData.getInstance().Context.Selections, GameData.getInstance().Context.Paytable);
        freeStrip = cfg.getReeStrip("FreeGame", GameData.getInstance().Context.Selections, GameData.getInstance().Context.Paytable);

        if (GameData.getInstance().Context.TestMode) {
            resetTestStrips();
        }
    }

    private int getNextSymbol() {
        int symbolID;
        if (GameData.currentGameMode == GameMode.BaseGame) {
            if (baseStripIndex >= baseStrip.length) {
                baseStripIndex = 0;
            }
            symbolID = baseStrip[baseStripIndex];
            baseStripIndex++;
        } else {
            if (freeStripIndex >= freeStrip.length) {
                freeStripIndex = 0;
            }
            symbolID = freeStrip[freeStripIndex];
            freeStripIndex++;
        }
        return symbolID;
    }

//    private int getNextSymbol() {
//        int symbolID;
//        if (GameData.currentGameMode == GameMode.BaseGame) {
//            int[] allStops=GameData.getInstance().Context.Result.Stops;
//            baseStripIndex=allStops[this.reelID];
//            if (baseStripIndex >= baseStrip.length) {
//                baseStripIndex = 0;
//            }
//            symbolID = baseStrip[baseStripIndex];
//        } else {
//            if (freeStripIndex >= freeStrip.length) {
//                freeStripIndex = 0;
//            }
//            symbolID = freeStrip[freeStripIndex];
//            freeStripIndex++;
//        }
//        return symbolID;
//    }

    public void stopWinShow() {
        for (Symbol symbol : this.symbols) {
                symbol.stopWinShow();
        }
    }
}
