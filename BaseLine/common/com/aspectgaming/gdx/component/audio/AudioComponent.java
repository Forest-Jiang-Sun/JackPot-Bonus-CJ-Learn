package com.aspectgaming.gdx.component.audio;

import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.event.machine.VolumeChangeEvent;
import com.aspectgaming.common.loader.SoundLoader;
import com.aspectgaming.gdx.component.Component;

/**
 * @author ligang.yao
 */
public class AudioComponent extends Component {

    public AudioComponent() {
        registerEvent(new GameResetEvent() {
            @Override
            public void execute(Object... obj) {
                SoundLoader.getInstance().stopAllSounds();
            }
        });

        registerEvent(new InTiltEvent() {
            @Override
            public void execute(Object... obj) {
                SoundLoader.getInstance().pauseAllSounds();
            }
        });

        registerEvent(new OutTiltEvent() {
            @Override
            public void execute(Object... obj) {
                SoundLoader.getInstance().resumeAllSounds();
            }
        });

        registerEvent(new VolumeChangeEvent() {
            @Override
            public void execute(Object... obj) {
                SoundLoader.getInstance().updateVolume();
            }
        });
    }

    @Override
    public void dispose() {
        SoundLoader.getInstance().disposeAllSounds();
    }
}
