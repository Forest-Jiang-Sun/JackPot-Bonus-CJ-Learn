package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.util.AspectGamingUtil;
import com.aspectgaming.common.util.XMLUtil;

@XmlRootElement
public class GameConfiguration {

    @XmlAttribute
    public boolean debug;
    @XmlAttribute
    public String gameName;
    @XmlAttribute
    public String version;
    @XmlAttribute
    public String type;

    @XmlElement
    public DisplayConfiguration display;
    @XmlElement
    public ListenerConfiguration listener;
    @XmlElement
    public ResolutionConfiguration[] resolutions;
    @XmlElement
    public ReelConfiguration reel;
    @XmlElement
    public ProgressiveReelConfiguration progressiveReel;
    @XmlElement
    public RandomWildConfiguration randomWild;
    @XmlElement
    public OpenGLConfiguration openGL;
    @XmlElement
    public WinMeterConfiguration winMeter;
    @XmlElement
    public WinShowConfiguration winShow;
    @XmlElement
    public PayLineConfiguration payLine;
    @XmlElement
    public DenomConfiguration denom = new DenomConfiguration();
    @XmlElement
    public HelpConfiguration help;
    @XmlElement
    public GameRecallConfiguration gameRecall = new GameRecallConfiguration();
    @XmlElement
    public MessageBarConfiguration messageBar;
    @XmlElement
    public MessageConfiguration message;
    @XmlElement
    public ButtonsConfiguration buttons;
    @XmlElement
    public MetersConfiguration meters;
    @XmlElement
    public ProgressiveConfiguration progressive;
    @XmlElement
    public WinBoxConfiguration winBox = new WinBoxConfiguration();
    @XmlElement
    public RollingMeterConfiguration rollingMeter;
    @XmlElement
    public LedConfiguration led;

    private static final GameConfiguration instance = load();

    private static GameConfiguration load() {
        String file = AspectGamingUtil.WORKING_DIR + "/" + GameData.Screen + ".xml";
        return XMLUtil.unmarshal(file, GameConfiguration.class);
    }

    public static final GameConfiguration getInstance() {
        return instance;
    }

    public ResolutionConfiguration currentResolution() {
        for (ResolutionConfiguration resolution : resolutions) {
            if (resolution.width == display.width && resolution.height == display.height) return resolution;
        }
        return null;
    }
}
