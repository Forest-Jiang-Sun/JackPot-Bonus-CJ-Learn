package com.aspectgaming.media.onewall;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.screen.ChangeLinkedMediaEvent;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.media.onewall.data.AnimationAct;
import com.aspectgaming.media.onewall.data.AnimationInfo;
import com.aspectgaming.media.onewall.data.MediaData;
import com.aspectgaming.media.onewall.data.SettingData;
import com.aspectgaming.media.onewall.data.SoundInfo;
import com.aspectgaming.media.onewall.data.VideoInfo;
import com.aspectgaming.media.onewall.net.Message;
import com.aspectgaming.media.onewall.net.MessageUdpDecoder;
import com.aspectgaming.net.game.data.LinkedMediaData;

/**
 * @author ligang.yao
 */
public class OneWallMediaComponent extends DrawableComponent {

    private static final int BUFFER_SIZE = 102400;
    private static final long POLL_TIME_MS = 16;
    private static final long TIME_WINDOW = TimeUnit.MILLISECONDS.toNanos(POLL_TIME_MS) * 3;

    private final String screen;
    private final boolean usePBO;

    private EventLoopGroup groupClient;
    private Bootstrap client;

    private long id = 0;
    private int pos = 0;
    private String strPos = null;

    private volatile long timeDelta;
    private int volume = 10;

    private AnimationAct action;

    private volatile Map<String, ActionScript> scripts = new HashMap<>();
    private boolean dirty;

    public OneWallMediaComponent() {
        setVisible(false);
        usePBO = GameConfiguration.getInstance().openGL.pixelBufferObject;

        screen = GameData.Screen;

        registerEvent(new ChangeLinkedMediaEvent() {
            @Override
            public void execute(Object... obj) {
                reset();
            }
        });

        reset();
    }

    private void reset() {
        timeDelta = 0;

        if (groupClient != null) {
            groupClient.shutdownGracefully();
            groupClient = null;
        }

        LinkedMediaData cfg = GameData.getInstance().Setting.LinkedMedia;
        if (cfg != null && cfg.Enabled) {
            pos = cfg.Location;
            strPos = Integer.toString(pos);

            groupClient = new NioEventLoopGroup();
            client = new Bootstrap();
            client.group(groupClient);
            client.channel(NioDatagramChannel.class);
            client.option(ChannelOption.SO_BROADCAST, true);
            client.option(ChannelOption.SO_REUSEADDR, true);
            // PooledByteBufAllocator is not default in Netty 4.0, need manually set it
            client.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            client.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new MessageUdpDecoder(BUFFER_SIZE));
                    pipeline.addLast(new OneWallMediaClient());
                }
            });
            client.localAddress(new InetSocketAddress(cfg.BroadcastPort));

            try {
                client.bind().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void update(float delta) {
        if (dirty) {
            dirty = false;
            loadVideos();
            loadSounds();
        }

        if (timeDelta != 0 && action != null && !scripts.isEmpty()) {
            long syncTime = System.nanoTime() + timeDelta;
            boolean visible = false;
            ActionScript as = scripts.get(action.getName());

            for (VideoScript vs : as.Videos) {
                long timeStart = as.StartTime + vs.Info.getStartTime() + (pos - 1) * vs.Info.getMachineOffsetTime();
                long diff = syncTime - timeStart;
                if (diff >= 0 && diff < TIME_WINDOW) {
                    if (!vs.Video.isPlaying()) {
                        vs.Video.play(vs.Info.getLoop());
                    }
                }
                if (vs.Video.isPlaying()) {
                    visible = true;
                }
            }

            for (SoundScript ss : as.Sounds) {
                if (!ss.Sound.isRunning()) {
                    long timeStart = as.StartTime + ss.Info.getStartTime() + (pos - 1) * ss.Info.getMachineOffsetTime();
                    long diff = syncTime - timeStart;
                    if (diff >= 0 && diff < TIME_WINDOW) {
                        ss.Sound.stop();
                        ss.Sound.setMicrosecondPosition(TimeUnit.NANOSECONDS.toMicros(diff));
                        ss.Sound.loop(ss.Info.getLoop() - 1);
                    }
                }
            }

            setVisible(visible);
        }
    }

    private void updateVolume(Clip clip) {
        if (clip == null) return;

        double fvol = ((double) volume) / 10;
        float dB = (float) (Math.log10(fvol) * 20);
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(dB);
    }

    private Clip loadSound(String fileName) {
        try {
            AudioInputStream as = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(as);
            updateVolume(clip);
            log.info("Sound loaded: {}", fileName);
            return clip;
        } catch (Exception e) {
            log.error("Failed to load sound: {}", fileName);
            return null;
        }
    }

    private void loadSounds() {
        for (ActionScript as : scripts.values()) {
            for (SoundScript ss : as.Sounds) {
                ss.Sound = loadSound(ss.Info.getFile());
            }
        }
    }

    private Video loadVideo(String fileName, int x, int y) {
        try {
            Video v = new Video(fileName, false, true, usePBO);
            v.setPosition(x, y);
            v.setPausable(false);
            v.setAutoVisible(true);
            addActor(v);
            return v;
        } catch (Throwable e) {
            log.error("Failed to load video: " + fileName);
            return null;
        }
    }

    private void loadVideos() {
        for (ActionScript as : scripts.values()) {
            for (VideoScript ss : as.Videos) {
                ss.Video = loadVideo(ss.Info.getFile(), ss.Info.getX(), ss.Info.getY());
                if (ss.Video != null && ss.Info.getForcedFPS() != 0) {
                    ss.Video.forceFPS(ss.Info.getForcedFPS());
                }
            }
        }
    }

    private static class VideoScript {
        public VideoInfo Info;
        public Video Video;
    }

    private static class SoundScript {
        public SoundInfo Info;
        public Clip Sound;
    }

    private static class ActionScript {
        public long StartTime;
        public final List<VideoScript> Videos = new ArrayList<>();
        public final List<SoundScript> Sounds = new ArrayList<>();
    }

    class OneWallMediaClient extends SimpleChannelInboundHandler<Message> {

        private final long CALIBRATABLE_TIME_CHANGE = TimeUnit.SECONDS.toNanos(5);

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            Thread.currentThread().setName("OneWallClient");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            switch (msg.type) {
            case SETTING_S2C:
                onSettingMessage((SettingData) msg.message);
                break;

            case MEDIA_S2C:
                onMediaMessage((MediaData) msg.message);
                break;

            default:
                break;
            }
        }

        private void onSettingMessage(SettingData pkt) {
            if (id == pkt.getID()) return;

            id = pkt.getID();

            Map<String, ActionScript> actionScripts = new HashMap<>();

            for (AnimationInfo ai : pkt.getAnimationsList()) {
                ActionScript as = new ActionScript();

                if (ai.getVideosList() != null && !ai.getVideosList().isEmpty()) {
                    for (VideoInfo vi : ai.getVideosList()) {
                        if (vi.getScreen().equals(screen)) {
                            if (vi.getMask() == null || vi.getMask().charAt(pos - 1) != '0') {
                                vi.setFile(vi.getFile().replace("*", strPos));
                                VideoScript vs = new VideoScript();
                                vs.Info = vi;
                                as.Videos.add(vs);
                            }
                        }
                    }
                }

                if (ai.getSoundsList() != null && !ai.getSoundsList().isEmpty()) {
                    for (SoundInfo si : ai.getSoundsList()) {
                        if (si.getScreen().equals(screen)) {
                            if (si.getMask() == null || si.getMask().charAt(pos - 1) != '0') {
                                si.setFile(si.getFile().replace("*", strPos));
                                SoundScript ss = new SoundScript();
                                ss.Info = si;
                                as.Sounds.add(ss);
                            }
                        }
                    }
                }

                if (!as.Videos.isEmpty() || !as.Sounds.isEmpty()) {
                    actionScripts.put(ai.getName(), as);
                }
            }

            scripts = actionScripts;
            dirty = true;
        }

        private void onMediaMessage(MediaData pkt) {
            calibrateTime(pkt.getTime());

            if (volume != pkt.getVolume()) {
                volume = pkt.getVolume();
                udpateVolume();
            }

            AnimationAct act = pkt.getAnimation();

            if (act != null) {
                ActionScript as = scripts.get(act.getName());
                if (as != null) {
                    as.StartTime = act.getStartTime();
                    action = act;
                }
            }
        }

        private void calibrateTime(long serverTime) {
            long timeDeltaNew = serverTime - System.nanoTime();
            long diff = timeDeltaNew - timeDelta;

            if (timeDelta == 0 || diff > CALIBRATABLE_TIME_CHANGE || diff < -CALIBRATABLE_TIME_CHANGE) {
                // quick change if needed
                timeDelta = timeDeltaNew;
            } else {
                // calibrate time smoothly
                timeDelta += diff / 10;
            }
        }

        private void udpateVolume() {
            for (ActionScript as : scripts.values()) {
                for (SoundScript ss : as.Sounds) {
                    updateVolume(ss.Sound);
                }
            }
        }
    }
}
