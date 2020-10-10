package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlElement;
import java.util.Random;

public class WinMeterConfiguration {
	@XmlElement
	public SoundConfiguration[] sound;
	private final Random rand = new Random();

	public SoundCfg getSoundCfg(float multiple) {
		multiple = (float)(Math.round(multiple * 1000)) / 1000;
		SoundCfg soundCfg = null;
		for (SoundConfiguration configuration : sound) {
			if (multiple >= configuration.lowMultiple && multiple <= configuration.highMultiple) {
				soundCfg = new SoundCfg();
				switch (configuration.type) {
					case 0:
						soundCfg = configuration.soundInfo[0];
						soundCfg.rollUpTime = soundCfg.lowTime;

						break;
					case 1:
						soundCfg = configuration.soundInfo[0];
						int low = (int)(soundCfg.lowTime * 1000);
						int hight = (int)(soundCfg.hightTime * 1000);
						int random = rand.nextInt(hight) % (hight - low + 1) + low;
						soundCfg.rollUpTime = (float)(random) / 1000;

						break;
					case 2:
						soundCfg = configuration.soundInfo[rand.nextInt(configuration.soundInfo.length)];
						soundCfg.rollUpTime = soundCfg.lowTime;

						break;
				}
			}
		}
		return soundCfg;
	}
}
