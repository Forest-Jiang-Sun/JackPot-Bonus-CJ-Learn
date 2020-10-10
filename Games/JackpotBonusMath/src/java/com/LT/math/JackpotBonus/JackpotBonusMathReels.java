package com.LT.math.JackpotBonus;

import com.aspectgaming.math.BonusType;
import com.aspectgaming.math.GamingMath;
import com.aspectgaming.math.progressive.ProgressiveLevel;
import com.ltgame.bedrock.math.GameWinCategory;
import com.ltgame.bedrock.math.ProgressiveLevelData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.LT.math.JackpotBonus.JackpotBonusMathConstants.*;

public class JackpotBonusMathReels {
    private Map<String, JackpotBonusMathReelSet> reelSets = new LinkedHashMap<String, JackpotBonusMathReelSet>();

    //+NEW_SAPC
    private GameWinCategory winCatP1;
    private GameWinCategory winCatP2;

    public JackpotBonusMathReels() {
        this.initialize();
    }

    private void initialize() {

        //+NEW_SAPC
        // In order to support both linked and stand alone, the win category must contain a Progressive level object
        // set with groupID = 1, levelIds matching those configured, and both canStandAlone and canLink set to true.
        short groupID = 1;
        ProgressiveLevelData p1LD = new ProgressiveLevelData(groupID, 1, true, true);
        ProgressiveLevelData p2LD = new ProgressiveLevelData(groupID, 2, false, true);
        winCatP1 = new GameWinCategory(WIN_CATEGORY_PROGRESSIVE_L1, "Progressive Win Level 1", p1LD);
        winCatP2 = new GameWinCategory(WIN_CATEGORY_PROGRESSIVE_L2, "Progressive Win Level 2", p2LD);

        initRTP87();
        initRTP90();
        initRTP92();
        initRTP95();
        initRTP97();
        initRTP99();
    }

    public String[] getReelSetNames() {
        return reelSets.keySet().toArray(new String[]{});
    }

    public JackpotBonusMathReelSet getReelSet(String name) {
        JackpotBonusMathReelSet reelSet = reelSets.get(name);
        changeTheWeight(reelSet);
        return reelSet;
    }

    private void changeTheWeight(JackpotBonusMathReelSet reelSet) {
        Total_Weight_BaseR1 = reelSet.Total_Weight_BaseR1;
        Total_Weight_BaseR2 = reelSet.Total_Weight_BaseR2;
        Total_Weight_BaseR3 = reelSet.Total_Weight_BaseR3;
        Total_Weight_FreeR1 = reelSet.Total_Weight_FreeR1;
        Total_Weight_FreeR2 = reelSet.Total_Weight_FreeR2;
        Total_Weight_FreeR3 = reelSet.Total_Weight_FreeR3;

        Total_Weitht_No_Max_Bet_Jackpot_Base = reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base;
        Total_Weitht_Max_Bet_Jackpot_Base = reelSet.Total_Weitht_Max_Bet_Jackpot_Base;
        Total_Weitht_Max_Bet_Jackpot_Free = reelSet.Total_Weitht_Max_Bet_Jackpot_Free;
        Total_Weitht_No_Max_Bet_Jackpot_Free = reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free;

        Weight_Table_Base1 = reelSet.Weight_Table_Base1;
        Weight_Table_Base2 = reelSet.Weight_Table_Base2;
        Weight_Table_Base3 = reelSet.Weight_Table_Base3;
        Weight_Table_Free1 = reelSet.Weight_Table_Free1;
        Weight_Table_Free2 = reelSet.Weight_Table_Free2;
        Weight_Table_Free3 = reelSet.Weight_Table_Free3;

        Weight_Table_Max_Bet_Jackpot_Base3 = reelSet.Weight_Table_Max_Bet_Jackpot_Base3;
        Weight_Table_No_Max_Bet_Jackpot_Base3 = reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3;
        Weight_Table_Max_Bet_Jackpot_Free3 = reelSet.Weight_Table_Max_Bet_Jackpot_Free3;
        Weight_Table_No_Max_Bet_Jackpot_Free3 = reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3;
        setWeightTable();
    }

    private JackpotBonusWagerCategory buildWC(String name, float baseRTP, float rtpSAP) {
        JackpotBonusWagerCategory wc = new JackpotBonusWagerCategory(name);
        wc.rtpBaseGame = 0f;
        wc.rtpFreeGame = 0f;
        wc.rtpBonus = 0f;
        wc.rtpMinBase = baseRTP;
        wc.rtpMaxBase = 0f;
        wc.rtpSAP = rtpSAP;
        wc.setProgressiveLevels(new ProgressiveLevel[]{
                new ProgressiveLevel("P1", BonusType.SymbolDrivenProgressive, "0.667%"),
                new ProgressiveLevel("P2", BonusType.SymbolDrivenProgressive, "0.00%"),
        });
        return wc;
    }


    private void initRTP87() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP87";
        reelSet.minRtp = 85.020f;
        reelSet.maxRtp = 87.000f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 85.02f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 85.64f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 85.72f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 85.81f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 87.00f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 5079;
        reelSet.Total_Weight_BaseR2 = 3928;
        reelSet.Total_Weight_BaseR3 = 5383;
        reelSet.Total_Weight_FreeR1 = 3010;
        reelSet.Total_Weight_FreeR2 = 4145;
        reelSet.Total_Weight_FreeR3 = 4370;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 187400;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{290, 1040, 413, 665, 10, 30, 10, 29, 40, 150, 230, 64, 174, 544, 401, 548, 86, 24, 76, 20, 99, 136};
        reelSet.Weight_Table_Base2 = new long[]{121, 96, 382, 109, 275, 115, 34, 32, 194, 442, 89, 52, 761, 256, 282, 33, 10, 31, 10, 417, 130, 57};
        reelSet.Weight_Table_Base3 = new long[]{714, 254, 349, 234, 63, 37, 195, 478, 7, 0, 8, 68, 797, 268, 1190, 157, 92, 51, 140, 40, 147, 94};
        reelSet.Weight_Table_Free1 = new long[]{657, 43, 308, 35, 14, 90, 11, 51, 55, 27, 376, 57, 792, 51, 76, 54, 56, 55, 29, 20, 75, 78};
        reelSet.Weight_Table_Free2 = new long[]{65, 59, 623, 64, 85, 348, 49, 20, 44, 1095, 59, 26, 88, 293, 308, 36, 12, 23, 13, 679, 67, 89};
        reelSet.Weight_Table_Free3 = new long[]{327, 334, 1197, 36, 78, 57, 46, 522, 14, 0, 14, 58, 918, 134, 299, 55, 29, 27, 49, 19, 105, 52};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 25000, 0, 25000, 0, 10000, 0, 25000, 0, 13400, 0, 10000, 0, 0, 0, 30000, 0, 8000, 8000, 8000, 0, 25000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }

    private void initRTP90() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP90";
        reelSet.minRtp = 87.652f;
        reelSet.maxRtp = 89.999f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 87.65f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 88.63f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 88.75f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 88.85f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 90.00f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 5005;
        reelSet.Total_Weight_BaseR2 = 3765;
        reelSet.Total_Weight_BaseR3 = 5398;
        reelSet.Total_Weight_FreeR1 = 2992;
        reelSet.Total_Weight_FreeR2 = 4153;
        reelSet.Total_Weight_FreeR3 = 4401;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 186603;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{290, 906, 408, 658, 10, 30, 10, 29, 40, 145, 237, 64, 174, 571, 440, 543, 85, 24, 76, 20, 108, 137};
        reelSet.Weight_Table_Base2 = new long[]{125, 97, 376, 114, 291, 115, 34, 32, 191, 405, 87, 50, 717, 238, 285, 33, 10, 31, 10, 344, 122, 58};
        reelSet.Weight_Table_Base3 = new long[]{773, 252, 343, 260, 64, 37, 210, 488, 7, 0, 8, 66, 825, 263, 1110, 162, 92, 50, 127, 40, 128, 93};
        reelSet.Weight_Table_Free1 = new long[]{638, 44, 307, 36, 14, 91, 11, 50, 55, 27, 378, 56, 783, 52, 74, 55, 56, 55, 29, 20, 78, 83};
        reelSet.Weight_Table_Free2 = new long[]{68, 61, 626, 67, 82, 361, 49, 20, 45, 1117, 59, 26, 89, 292, 309, 35, 12, 23, 13, 642, 69, 88};
        reelSet.Weight_Table_Free3 = new long[]{329, 346, 1197, 36, 80, 58, 47, 546, 14, 0, 14, 57, 912, 135, 301, 54, 29, 27, 50, 19, 99, 51};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 25000, 0, 25000, 0, 10000, 0, 25000, 0, 12603, 0, 10000, 0, 0, 0, 30000, 0, 8000, 8000, 8000, 0, 25000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }


    private void initRTP92() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP92";
        reelSet.minRtp = 90.261f;
        reelSet.maxRtp = 92.499f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 90.26f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 91.16f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 91.21f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 91.33f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 92.50f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 4956;
        reelSet.Total_Weight_BaseR2 = 3767;
        reelSet.Total_Weight_BaseR3 = 5493;
        reelSet.Total_Weight_FreeR1 = 2921;
        reelSet.Total_Weight_FreeR2 = 4185;
        reelSet.Total_Weight_FreeR3 = 4395;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 181119;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{285, 857, 411, 631, 10, 30, 10, 29, 40, 142, 233, 64, 178, 559, 486, 530, 85, 24, 74, 20, 117, 141};
        reelSet.Weight_Table_Base2 = new long[]{135, 99, 377, 115, 314, 110, 34, 32, 196, 414, 88, 52, 701, 229, 279, 33, 10, 31, 10, 329, 122, 57};
        reelSet.Weight_Table_Base3 = new long[]{813, 249, 363, 270, 64, 37, 220, 469, 7, 0, 8, 67, 848, 258, 1127, 165, 90, 50, 131, 40, 124, 93};
        reelSet.Weight_Table_Free1 = new long[]{643, 45, 311, 35, 14, 100, 11, 48, 54, 27, 366, 55, 718, 53, 70, 53, 55, 56, 30, 20, 76, 81};
        reelSet.Weight_Table_Free2 = new long[]{68, 64, 671, 69, 81, 399, 49, 20, 45, 1156, 59, 26, 89, 258, 300, 35, 12, 23, 13, 591, 72, 85};
        reelSet.Weight_Table_Free3 = new long[]{341, 330, 1200, 36, 73, 59, 49, 561, 14, 0, 14, 56, 919, 131, 294, 55, 29, 27, 50, 19, 88, 50};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 25000, 0, 25000, 0, 10000, 0, 25000, 0, 12119, 0, 10000, 0, 0, 0, 25000, 0, 8000, 8000, 8000, 0, 25000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }


    private void initRTP95() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP95";
        reelSet.minRtp = 92.936f;
        reelSet.maxRtp = 95.000f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 92.94f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 93.73f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 93.76f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 93.86f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 95.00f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 5017;
        reelSet.Total_Weight_BaseR2 = 3852;
        reelSet.Total_Weight_BaseR3 = 5507;
        reelSet.Total_Weight_FreeR1 = 2675;
        reelSet.Total_Weight_FreeR2 = 4123;
        reelSet.Total_Weight_FreeR3 = 4422;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 199842;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{288, 857, 435, 626, 10, 30, 10, 29, 40, 136, 238, 65, 172, 551, 514, 549, 86, 24, 73, 20, 116, 148};
        reelSet.Weight_Table_Base2 = new long[]{132, 104, 377, 125, 352, 111, 34, 32, 200, 415, 88, 51, 734, 225, 288, 33, 10, 31, 10, 316, 127, 57};
        reelSet.Weight_Table_Base3 = new long[]{850, 249, 360, 276, 64, 37, 225, 457, 7, 0, 8, 68, 838, 247, 1139, 150, 89, 51, 132, 40, 126, 94};
        reelSet.Weight_Table_Free1 = new long[]{615, 42, 286, 33, 14, 100, 11, 47, 53, 27, 324, 59, 586, 55, 65, 55, 53, 51, 29, 19, 71, 80};
        reelSet.Weight_Table_Free2 = new long[]{69, 62, 707, 59, 85, 414, 48, 19, 42, 1177, 53, 26, 89, 238, 292, 35, 12, 23, 13, 510, 71, 79};
        reelSet.Weight_Table_Free3 = new long[]{370, 328, 1200, 40, 78, 57, 48, 520, 14, 0, 14, 57, 1005, 128, 259, 58, 29, 27, 49, 19, 74, 48};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 25000, 0, 25000, 0, 10000, 0, 25000, 0, 13842, 0, 10000, 0, 0, 0, 38000, 0, 5000, 5000, 5000, 0, 38000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }

    private void initRTP97() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP97";
        reelSet.minRtp = 94.674f;
        reelSet.maxRtp = 96.999f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 94.67f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 95.19f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 95.72f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 95.93f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 97.00f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 4933;
        reelSet.Total_Weight_BaseR2 = 3890;
        reelSet.Total_Weight_BaseR3 = 5571;
        reelSet.Total_Weight_FreeR1 = 2675;
        reelSet.Total_Weight_FreeR2 = 4123;
        reelSet.Total_Weight_FreeR3 = 4422;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 181482;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{291, 824, 423, 597, 10, 30, 10, 29, 40, 133, 248, 65, 168, 550, 527, 517, 86, 24, 73, 20, 121, 147};
        reelSet.Weight_Table_Base2 = new long[]{124, 103, 386, 128, 366, 106, 34, 32, 204, 416, 88, 51, 743, 220, 289, 33, 10, 31, 10, 334, 125, 57};
        reelSet.Weight_Table_Base3 = new long[]{864, 252, 353, 269, 64, 37, 231, 450, 7, 0, 8, 68, 843, 250, 1190, 155, 89, 51, 133, 40, 123, 94};
        reelSet.Weight_Table_Free1 = new long[]{615, 42, 286, 33, 14, 100, 11, 47, 53, 27, 324, 59, 586, 55, 65, 55, 53, 51, 29, 19, 71, 80};
        reelSet.Weight_Table_Free2 = new long[]{69, 62, 707, 59, 85, 414, 48, 19, 42, 1177, 53, 26, 89, 238, 292, 35, 12, 23, 13, 510, 71, 79};
        reelSet.Weight_Table_Free3 = new long[]{370, 328, 1200, 40, 78, 57, 48, 520, 14, 0, 14, 57, 1005, 128, 259, 58, 29, 27, 49, 19, 74, 48};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 22000, 0, 22000, 0, 10000, 0, 20000, 0, 12482, 0, 10000, 0, 0, 0, 35000, 0, 5000, 5000, 5000, 0, 35000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }


    private void initRTP99() {
        JackpotBonusMathReelSet reelSet = new JackpotBonusMathReelSet();
        reelSet.reelSetName = "RTP99";
        reelSet.minRtp = 96.644f;
        reelSet.maxRtp = 98.999f;

        reelSet.wagerCategories[0] = buildWC("1 Line", 96.64f, 0.667f);
        reelSet.wagerCategories[1] = buildWC("2 Lines", 97.47f, 0.667f);
        reelSet.wagerCategories[2] = buildWC("3 Lines", 97.64f, 0.667f);
        reelSet.wagerCategories[3] = buildWC("4 Lines", 97.78f, 0.667f);
        reelSet.wagerCategories[4] = buildWC("5 Lines", 99.00f, 0.667f);

        reelSets.put(reelSet.reelSetName, reelSet);
        reelSet.gameReels = new int[][]{
                {BL, FC, FC, FC, BL, D7, BL, FA, BL, D2, D3, D2, BL, FB, FB, FB, BL, FA, BL, R7, BL, BN},
                {FA, FA, BL, D2, D3, D2, BL, FC, FC, FC, BL, R7, BL, BN, BL, FB, BL, D7, BL, FB, BL, FA},
                {BL, FB, FB, FB, BL, R7, BL, FA, BL, D7, BL, FA, BL, BN, BL, FC, BL, D2, D3, D2, BL, FC}
        };

        //+NEW_SAPC
        reelSet.winCategories[0] = winCatP1;
        reelSet.winCategories[1] = winCatP2;

        reelSet.Total_Weight_BaseR1 = 4867;
        reelSet.Total_Weight_BaseR2 = 3898;
        reelSet.Total_Weight_BaseR3 = 5505;
        reelSet.Total_Weight_FreeR1 = 2675;
        reelSet.Total_Weight_FreeR2 = 4123;
        reelSet.Total_Weight_FreeR3 = 4422;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Base = 159871;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Base = 985;
        reelSet.Total_Weitht_Max_Bet_Jackpot_Free = 14500019927L;
        reelSet.Total_Weitht_No_Max_Bet_Jackpot_Free = 1340;
        reelSet.Weight_Table_Base1 = new long[]{295, 781, 408, 573, 10, 30, 10, 29, 40, 135, 251, 65, 170, 556, 533, 511, 86, 24, 73, 20, 120, 147};
        reelSet.Weight_Table_Base2 = new long[]{127, 107, 381, 121, 373, 107, 34, 32, 208, 415, 88, 51, 737, 224, 293, 33, 10, 31, 10, 331, 128, 57};
        reelSet.Weight_Table_Base3 = new long[]{859, 240, 349, 268, 64, 37, 224, 438, 7, 0, 8, 68, 838, 245, 1174, 151, 89, 51, 135, 40, 126, 94};
        reelSet.Weight_Table_Free1 = new long[]{615, 42, 286, 33, 14, 100, 11, 47, 53, 27, 324, 59, 586, 55, 65, 55, 53, 51, 29, 19, 71, 80};
        reelSet.Weight_Table_Free2 = new long[]{69, 62, 707, 59, 85, 414, 48, 19, 42, 1177, 53, 26, 89, 238, 292, 35, 12, 23, 13, 510, 71, 79};
        reelSet.Weight_Table_Free3 = new long[]{370, 328, 1200, 40, 78, 57, 48, 520, 14, 0, 14, 57, 1005, 128, 259, 58, 29, 27, 49, 19, 74, 48};
        reelSet.Weight_Table_Max_Bet_Jackpot_Base3 = new long[]{0, 20000, 0, 14000, 0, 10000, 0, 20000, 0, 10871, 0, 10000, 0, 0, 0, 30000, 0, 5000, 5000, 5000, 0, 30000};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[]{0, 50, 0, 25, 0, 15, 0, 50, 0, 5, 0, 25, 0, 0, 0, 400, 0, 5, 5, 5, 0, 400};
        reelSet.Weight_Table_Max_Bet_Jackpot_Free3 = new long[]{0, 2000000000, 0, 1000000000, 0, 1000000000, 0, 2000000000, 0, 19927, 0, 1000000000, 0, 0, 0, 3000000000L, 0, 500000000, 500000000, 500000000, 0, 3000000000L};
        reelSet.Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[]{0, 200, 0, 100, 0, 100, 0, 200, 0, 10, 0, 100, 0, 0, 0, 300, 0, 10, 10, 10, 0, 300};
    }

    public GamingMath.ReelStrip[] getAllReelStrips() {
        List<GamingMath.ReelStrip> strips = new ArrayList<GamingMath.ReelStrip>();

        for (JackpotBonusMathReelSet rs : reelSets.values()) {
            for (int reel = 0; reel < rs.gameReels.length; reel++) {
                strips.add(new GamingMath.ReelStrip(rs.reelSetName, reel, "BaseGame", 0, rs.gameReels[reel]));
            }

            for (int reel = 0; reel < rs.gameReels.length; reel++) {
                strips.add(new GamingMath.ReelStrip(rs.reelSetName, reel, "FreeGame", 0, rs.gameReels[reel]));
            }
        }

        return strips.toArray(new GamingMath.ReelStrip[strips.size()]);
    }

    public void setAsLinked()
    {
        for(JackpotBonusMathReelSet reelSet: reelSets.values())
        {
            //Just P1
            for (int i=0; i<5; ++i) {
                reelSet.wagerCategories[i].setProgressiveLevels(new ProgressiveLevel[]{
                    new ProgressiveLevel("P1", BonusType.SymbolDrivenProgressive, "0.667%"),
                });
            }

            reelSet.winCategories = new GameWinCategory[]{winCatP1};
        }
    }
}
