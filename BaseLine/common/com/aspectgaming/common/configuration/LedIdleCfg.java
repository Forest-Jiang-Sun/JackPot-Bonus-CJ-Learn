package com.aspectgaming.common.configuration;

import javax.xml.bind.annotation.XmlElement;

public class LedIdleCfg {
    @XmlElement
    public LedChaseCfg Chase;
    @XmlElement
    public LedFadeCfg[] Fade;
    @XmlElement
    public LedSetSolidColorCfg[] SetSolidColor;

    public LedFadeCfg getLedFadeCfg(String stringId){

        LedFadeCfg ledFadeCfg =null;
        for (LedFadeCfg fadeCfg : Fade) {
          if (fadeCfg.stringId.equals(stringId)){
              ledFadeCfg=fadeCfg;
              break;
          }
        }

        return ledFadeCfg;
    }

    public LedSetSolidColorCfg getLedSetSolidColorCfg(String stringId){
        LedSetSolidColorCfg ledSetSolidColorCfg=null;
        for (LedSetSolidColorCfg SetSolidColorCfg : SetSolidColor) {
            if (SetSolidColorCfg.stringId.equals(stringId)){
                ledSetSolidColorCfg=SetSolidColorCfg;
                break;
            }
        }
        return ledSetSolidColorCfg;
    }

}
