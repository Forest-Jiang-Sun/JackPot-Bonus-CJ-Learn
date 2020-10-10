package com.LT.math.JackpotBonus;

import com.aspectgaming.math.ReelSet;
import com.aspectgaming.math.WagerCategory;
import com.ltgame.bedrock.math.GameWinCategory;

public class JackpotBonusMathReelSet implements ReelSet {
    String reelSetName;
    //float rtp;
    float minRtp;
    float maxRtp;
    //+NEW_SAPC
    GameWinCategory[] winCategories;

    JackpotBonusWagerCategory[] wagerCategories=new JackpotBonusWagerCategory[JackpotBonusMathConstants.NUMBER_WAGER_CATEGORIES];

    int[][] gameReels;

    public long Total_Weight_BaseR1;
    public long Total_Weight_BaseR2;
    public long Total_Weight_BaseR3;
    public long Total_Weight_FreeR1;
    public long Total_Weight_FreeR2;
    public long Total_Weight_FreeR3;
    public long Total_Weitht_Max_Bet_Jackpot_Base;
    public long Total_Weitht_No_Max_Bet_Jackpot_Base;
    public long Total_Weitht_Max_Bet_Jackpot_Free;
    public long Total_Weitht_No_Max_Bet_Jackpot_Free;
    public long[] Weight_Table_Base1;
    public long[] Weight_Table_Base2;
    public long[] Weight_Table_Base3;
    public long[] Weight_Table_Free1;
    public long[] Weight_Table_Free2;
    public long[] Weight_Table_Free3;
    public long[] Weight_Table_Max_Bet_Jackpot_Base3;
    public long[] Weight_Table_No_Max_Bet_Jackpot_Base3;
    public long[] Weight_Table_Max_Bet_Jackpot_Free3;
    public long[] Weight_Table_No_Max_Bet_Jackpot_Free3;


    JackpotBonusMathReelSet(){
        //+NEW_SAPC
        winCategories = new GameWinCategory[JackpotBonusMathConstants.NUM_WIN_CATEGORIES];
    }

    @Override
    public String getReelSetName() {
        return this.reelSetName;
    }

//    Removed getRTP() method
//    @Override
//    public float getRTP() {
//        return this.rtp;
//    }

    @Override
    public boolean isProgressive() {
        return false;
    }

    @Override
    public WagerCategory[] getWagerCategories()
    {
        return wagerCategories;
    }

    @Override
    public float getMinRTP() {
        return minRtp;
    }

    @Override
    public float getMaxRTP() {
        return maxRtp;
    }

    @Override
    public float getMaxBetRTP() {
        return minRtp;
    }

    //+NEW_SAPC
    @Override
    public GameWinCategory[] getwinCategories()
    {
        return winCategories;
    }
}
