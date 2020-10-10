package com.aspectgaming.gdx.component.drawable.background;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aspectgaming.common.actor.Animation;
import com.aspectgaming.common.actor.Image;
import com.aspectgaming.common.actor.ShapeAnimation;
import com.aspectgaming.common.actor.TextureLabel;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.configuration.MeterConfiguration;
import com.aspectgaming.common.configuration.ResolutionConfiguration;
import com.aspectgaming.common.configuration.common.SpriteConfiguration;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.data.GameMode;
import com.aspectgaming.common.event.GameModeChangeEvent;
import com.aspectgaming.common.event.freegame.InFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.InFreeGameOutroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameIntroEvent;
import com.aspectgaming.common.event.freegame.OutFreeGameOutroEvent;
import com.aspectgaming.common.event.game.DoveFlyEvent;
import com.aspectgaming.common.event.game.GameResetEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.screen.AttractStartEvent;
import com.aspectgaming.common.event.screen.AttractStopEvent;
import com.aspectgaming.common.event.screen.ChangeLinkedMediaEvent;
import com.aspectgaming.common.loader.CoordinateLoader;
import com.aspectgaming.common.loader.ImageLoader;
import com.aspectgaming.common.loader.VideoLoader;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.LinkedMediaData;
import com.aspectgaming.util.StringUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

/**
 * Show top screen background image and video.
 * 
 * @author ligang.yao
 */
public class TopScreenBackgroundComponent extends DrawableComponent {
    private Image img_copyright;
    private Image bg;
    public TopScreenBackgroundComponent() {



   }
}
