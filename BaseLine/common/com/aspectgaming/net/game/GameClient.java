package com.aspectgaming.net.game;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.progressive.ProgressiveSkipEvent;
import com.aspectgaming.net.game.data.CommandData;
import com.aspectgaming.net.game.data.ProtocolTypes;
import com.aspectgaming.net.game.data.RegisterData;
import com.badlogic.gdx.Gdx;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static com.aspectgaming.common.data.State.ProgressiveResults;

public class GameClient extends Thread {

    private volatile GameServerHandler handler = null;
    private final Logger log = LoggerFactory.getLogger(GameClient.class);
    private Channel channel = null;

    private static final GameClient instance = new GameClient();

    public static GameClient getInstance() {
        return instance;
    }

    private GameClient() {}

    public void connect() {
        if (channel == null) {
            start();

            while (GameData.getInstance().Setting == null) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }

                while (processMessage());
            }
        }
    }

    public void onConnected(GameServerHandler ch) {
        handler = ch;
    }

    public void onDisconnected(GameServerHandler ch) {
        handler = null;
        log.info("Game Server disconnected");
        Gdx.app.exit();
    }

    public boolean processMessage() {
        return (handler != null && handler.processMessage());
    }

    public void register() {
        GameConfiguration cfg = GameConfiguration.getInstance();
        RegisterData msg = new RegisterData();
        msg.Screen = cfg.type;
        if (msg.Screen.equals("MainScreen")) {
            msg.Game = cfg.gameName;
            msg.Version = cfg.version;
            msg.DefaultStops = cfg.reel.defaultStops;
        }
        msg.RegisterData = cfg.listener.data;
        msg.RegisterActions = cfg.listener.action;
        msg.Properties = "intro,outro";
        send(ProtocolTypes.REGISTER_C2S, msg);
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    /**************************************************************************
     * Critical operations must be added as actions instead of calling
     * directly to avoid to be executed in tilt states.
     * Example: addAction(run(GameClient.getInstance().startPlay));
     **************************************************************************/

    public final Runnable startPlay = new Runnable() {
        @Override
        public void run() {
            gameAction("StartPlay");
        }
    };

    public final Runnable gameEnd = new Runnable() {
        @Override
        public void run() {
            if (GameData.getInstance().Context.GameState == ProgressiveResults) {
                EventMachine.getInstance().offerEvent(ProgressiveSkipEvent.class, false);
            }
            gameAction("GameEnd");
        }
    };

    public final Runnable freeGameIntroEnd = new Runnable() {
        @Override
        public void run() {
            gameAction("FreeGameIntroEnd");
        }
    };

    public final Runnable freeGameOutroEnd = new Runnable() {
        @Override
        public void run() {
            gameAction("FreeGameOutroEnd");
        }
    };

    public final Runnable progressiveIntroEnd = new Runnable() {
        @Override
        public void run() {
            gameAction("ProgressiveIntroEnd");
        }
    };

    public final Runnable awardProgressive = new Runnable() {
        @Override
        public void run() {
            gameAction("AwardProgressive");
        }
    };

    public final Runnable miniGameEnd = new Runnable() {
        @Override
        public void run() {
            gameAction("MiniGameEnd");
        }
    };

    /**************************************************************************
     * Normal operations, can be called directly
     **************************************************************************/

    public void enterRecall(){
        gameAction("EnterRecall");
    }

    public void exitRecall(){
        gameAction("ExitRecall");
    }

    public void nextGameRecall(){
        gameAction("NextGameRecall");
    }

    public void prevGameRecall(){
        gameAction("PrevGameRecall");
    }

    public void jackpotRecall(){
        gameAction("JackpotRecall");
    }

    public void baseGameRecall(){
        gameAction("BaseGameRecall");
    }

    public void freeGameRecall(){
        gameAction("FreeGameRecall");
    }

    public void bonusPickRecall(){
        gameAction("BonusPickRecall");
    }

    public void gambleRecall(){
        gameAction("GambleRecall");
    }

    public void gameLoaded() {
        gameAction("GameLoaded");
    }

    public void gameLocked() {
        gameAction("GameLocked");
    }

    public void gameUnlocked() {
        gameAction("GameUnlocked");
    }

    public void startAutoPlay() {
        gameAction("StartAutoPlay");
    }

    public void stopAutoPlay() {
        gameAction("StopAutoPlay");
    }

    public void selectLanguage(String language) {
        select("Language", language);
    }

    public void selectTestFeature(String feature) {
        select("TESTFEATURE", feature);
    }

    public void selectProgressiveWin(String win){
        select("ProgressiveWin",win);
    }

    public void selectFreeGameProgressiveTotalWin(long win) {
        select("FREEGAMEPROGRESSIVETOTALWIN", String.valueOf(win));
    }

    public void selectProNum(int level, int num) {
        select("PRONUM" + level, String.valueOf(num));
    }

    public void selectJackpotPos(int pos) {
        select("JACKPOTPOS", String.valueOf(pos));
    }

    public void selectJackpotSpinedCount(int spinedCount) {
        select("JACKPOTSPINEDCOUNT", String.valueOf(spinedCount));
    }

    public void selectJackpotLineIdx(int line) {
        select("JACKPOTPOSLINEIDX", String.valueOf(line));
    }

    public void selectJackpotLineWin(int line, int level, long win) {
        select("JACKPOTLINEWIN" + line, String.valueOf(level) + "," +String.valueOf(win));
    }

    public void selectFreeLastTotalWin(long win) {
        select("FREELASTTOTALWIN", String.valueOf(win));
    }

    public void selectDenomination(int Denom) {
        select("DENOMINATION", String.valueOf(Denom));
    }

    public void selectReturnFreeGame(boolean isReturnFreeGame) {
        if (isReturnFreeGame) {
            select("ISRETURNFREEGAME", "true");
        } else {
            select("ISRETURNFREEGAME", "false");
        }
    }

    public void selectBeforFreeGameJackPotWin(long beforFreeGameJackPotWin) {
        select("BEFORFREEGAMEJACKPOTWIN", String.valueOf(beforFreeGameJackPotWin));

    }

    public void selectFreeGameWin(long freeGameWin) {
        select("FREEGAMEWIN", String.valueOf(freeGameWin));
    }

    public void selectProgressiveDemo() {
        select("PROGRESSIVEDEMO", "2");
    }

    public void selectBonus(long pick) {
        select("Bonus", String.valueOf(pick));
    }

    public void selectDenom(int denom) {
        select("Denom", String.valueOf(denom));
    }

    public void selectPlayAgainAndUnGamble(boolean isCycle) {
        if (isCycle) {
            select("PLAYAGAINANDUNGAMBLE", "true");
        } else {
            select("PLAYAGAINANDUNGAMBLE", "false");
        }
    }

    public void gambleHalf() {
        select("Gamble", "Half");
    }

    public void gambleAll() {
        select("Gamble", "All");
    }

    public void gamblePick(int value) {
        select("Gamble", String.valueOf(value));
    }

    public void gambleTakeWin() {
        select("Gamble", "TakeWin");
    }

    public final Runnable buttonPlay = new Runnable() {
        @Override
        public void run() {
            pressButton("Play");
        }
    };

    public void buttonPlay() {
        pressButton("Play");
    }

    public void buttonGamble() {
        pressButton("Gamble");
    }

    public void buttonCashout() {
        pressButton("Cashout");
    }

    public void buttonAttendant() {
        pressButton("Attendant");
    }

    public void addCredit(int val) {
        controlPlatform("AddCredit", String.valueOf(val));
    }

    public void changeLines(int value) {
        pressButton("ChangeSelection", String.valueOf(value));
    }

    public void changeBet(int value) {
        pressButton("ChangeBet", String.valueOf(value));
    }

    public void changeBetMultiplierCredits(int value) {
        pressButton("BetMultiSelection", String.valueOf(value));
    }


    public void setTestStops(String stops) {
        controlPlatform("SetTestStops", "main.stops=" + stops);
    }

    public void setTestReelStripIndices(String val) {
        controlPlatform("SetTestStops", "main.stops.indices=" + val);
    }

    public void sendToTopScreen(String action, String... params) {
        sendToScreen("TopScreen", action, params);
    }

    public void sendToButtonDeckScreen(String action, String... params) {
        sendToScreen("ButtonDeckScreen", action, params);
    }

    public void sendToMainScreen(String action, String... params) {
        sendToScreen("MainScreen", action, params);
    }

    private void gameAction(String action) {
        sendCommands("Action", action);
    }

    public void select(String name, String value) {
        sendCommands("Select", name, value);
    }

    private void pressButton(String button, String value) {
        sendCommands("Button", button, value);
    }

    private void pressButton(String button) {
        pressButton(button, null);
    }

    public void enablePhysicalButton(int lines){
        pressButton("SetButtonEnabled", String.valueOf(lines));
    }

    public void disablePhysicalButton(int lines){
        pressButton("SetButtonDisabled", String.valueOf(lines));
    }

    private void controlPlatform(String option, String... params) {
        sendCommands("Control", option, params);
    }

    private void sendToScreen(String screen, String action, String... params) {
        sendCommands(screen, action, params);
    }

    private void sendCommands(String type, String name, String... values) {
        CommandData msg = new CommandData();
        msg.Type = type;
        msg.Name = name;
        msg.Values = values.length > 0 ? values : null;
        send(ProtocolTypes.COMMAND, msg);
    }

    private void send(ProtocolTypes type, Object cmd) {
        channel.writeAndFlush(new Message(type, cmd));
    }

    @Override
    public void run() {
        setName("GameClient");
        setPriority(MIN_PRIORITY);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
                    p.addLast(new MessageDecoder(Constants.MAX_OBJECT_SIZE));
                    p.addLast(new MessageEncoder(Constants.MAX_OBJECT_SIZE));
                    p.addLast(new GameServerHandler());
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            // PooledByteBufAllocator is not default in Netty 4.0, need manually set it
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // Start the client and wait until connected
            ChannelFuture f = b.connect(Constants.HOST, Constants.PORT).sync();

            log.info("Connected to Game Server!");

            channel = f.channel();

            register();

            // Wait until the connection is closed.
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
