package com.LT.math.JackpotBonus;


public class JackpotBonusMathConstants {
    static final String NAME = "JackpotBonusMath_H";
    static final String VERSION = "1.0.0"/*AntTag*/;

    //+NEW_SAPC
    //only need to return progressive categories for now
    static final int NUM_WIN_CATEGORIES = 2;
    static final int WIN_CATEGORY_PROGRESSIVE_L1 = 1;
    static final int WIN_CATEGORY_PROGRESSIVE_L2 = 2;

    static final int MAX_MULTIPLIER_INDEXES = 5;
    static final int NUM_REELS = 3;
    static final int NUM_ROWS = 5;
    static final public int MAX_LINES = 5;
    static final int NUMBER_WAGER_CATEGORIES = 5;
    static final int NUM_SYMBOLS_PER_SCREEN = 15;
    static final int NUM_SC_TO_TRIGGER_FREE_GAMES = 3;
    static final int MAX_CONFIGURABLE_CPS = 1;
    static final int[] PAYLINE_SELECTIONS = {
            5
    };
    static final int SCATTER_FREE_GAMES_AWARDED_IN_BASEGAME = 10;
    static final int SCATTER_FREE_GAMES_AWARDED_IN_FREEGAME = 10;


    public static long[] Weight_BaseR1 = new long[22];
    public static long[] Weight_BaseR2 = new long[22];
    public static long[] Weight_BaseR3 = new long[22];
    public static long Total_Weight_BaseR1 = 0;
    public static long Total_Weight_BaseR2 = 0;
    public static long Total_Weight_BaseR3 = 0;

    public static long[] Weight_FreeR1 = new long[22];
    public static long[] Weight_FreeR2 = new long[22];
    public static long[] Weight_FreeR3 = new long[22];
    public static long Total_Weight_FreeR1 = 0;
    public static long Total_Weight_FreeR2 = 0;
    public static long Total_Weight_FreeR3 = 0;

    public static long[] Weight_Max_Bet_Jackpot_BaseR3 = new long[22];
    public static long[] Weight_No_Max_Bet_Jackpot_BaseR3 = new long[22];
    public static long[] Weight_Max_Bet_Jackpot_FreeR3 = new long[22];
    public static long[] Weight_No_Max_Bet_Jackpot_FreeR3 = new long[22];
    public static long Total_Weitht_Max_Bet_Jackpot_Base = 0;
    public static long Total_Weitht_No_Max_Bet_Jackpot_Base = 0;
    public static long Total_Weitht_Max_Bet_Jackpot_Free = 0;
    public static long Total_Weitht_No_Max_Bet_Jackpot_Free = 0;

    public static long[] Weight_Table_Base1 = new long[22];
    public static long[] Weight_Table_Base2 = new long[22];
    public static long[] Weight_Table_Base3 = new long[22];
    public static long[] Weight_Table_Free1 = new long[22];
    public static long[] Weight_Table_Free2 = new long[22];
    public static long[] Weight_Table_Free3 = new long[22];
    public static long[] Weight_Table_Max_Bet_Jackpot_Base3 = new long[22];
    public static long[] Weight_Table_No_Max_Bet_Jackpot_Base3 = new long[22];
    public static long[] Weight_Table_Max_Bet_Jackpot_Free3 = new long[22];
    public static long[] Weight_Table_No_Max_Bet_Jackpot_Free3 = new long[22];


    //+NEW_SAPC
    // Jackpot levels need to match the total levels in the game not total options
    static final int JACKPOT_LEVELS = 2;

    static final int[] AVAIABLE_BET_MULTIPLER = {
            1
    };

    static final int JACKPOTWIN = 10000;
    static final int P2WIN = 800;


    static final int LINE_POSITIONS[][] = new int[][]{
            {6, 7, 8}, // 1
            {3, 4, 5}, // 2
            {9, 10, 11}, // 3
            {3, 7, 11}, // 4
            {5, 7, 9}, // 5
    };

    static final int LINE_POSITIONSRecall[][] = new int[][]{
            {3, 4, 5}, // 1
            {0, 1, 2}, // 2
            {6, 7, 8}, // 3
            {0, 4, 8}, // 4
            {2, 4, 6}, // 5
    };

    static final int[][] PAYTABLE = new int[9][500];

    static void init() {
        setWeightTable();
        setPayTable();
    }

    public static void setWeightTable() {
        long total = 0;
        for (int i = 0; i < Weight_Table_Base1.length; i++) {
            total += Weight_Table_Base1[i];
            Weight_BaseR1[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_Table_Base2.length; i++) {
            total += Weight_Table_Base2[i];
            Weight_BaseR2[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_Table_Base3.length; i++) {
            total += Weight_Table_Base3[i];
            Weight_BaseR3[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_FreeR1.length; i++) {
            total += Weight_Table_Free1[i];
            Weight_FreeR1[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_FreeR2.length; i++) {
            total += Weight_Table_Free2[i];
            Weight_FreeR2[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_FreeR3.length; i++) {
            total += Weight_Table_Free3[i];
            Weight_FreeR3[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_Max_Bet_Jackpot_BaseR3.length; i++) {
            total += Weight_Table_Max_Bet_Jackpot_Base3[i];
            Weight_Max_Bet_Jackpot_BaseR3[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_No_Max_Bet_Jackpot_BaseR3.length; i++) {
            total += Weight_Table_No_Max_Bet_Jackpot_Base3[i];
            Weight_No_Max_Bet_Jackpot_BaseR3[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_Max_Bet_Jackpot_FreeR3.length; i++) {
            total += Weight_Table_Max_Bet_Jackpot_Free3[i];
            Weight_Max_Bet_Jackpot_FreeR3[i] = total;
        }
        total = 0;
        for (int i = 0; i < Weight_No_Max_Bet_Jackpot_FreeR3.length; i++) {
            total += Weight_Table_No_Max_Bet_Jackpot_Free3[i];
            Weight_No_Max_Bet_Jackpot_FreeR3[i] = total;
        }
    }

    static void setPayTable() {
        PAYTABLE[D7][calculateIndex(D7, D7)] = 4000;
        PAYTABLE[D7][calculateIndex(D7, D3)] = 1470;
        PAYTABLE[D7][calculateIndex(D7, D2)] = 980;
        PAYTABLE[D7][calculateIndex(D7, R7)] = 490;
        PAYTABLE[D7][calculateIndex(D7, FA)] = 245;
        PAYTABLE[D7][calculateIndex(D7, FB)] = 147;
        PAYTABLE[D7][calculateIndex(D7, FC)] = 98;

        PAYTABLE[D7][calculateIndex(D3, D7)] = 1470;
        PAYTABLE[D7][calculateIndex(D3, D3)] = 630;
        PAYTABLE[D7][calculateIndex(D3, D2)] = 420;
        PAYTABLE[D7][calculateIndex(D3, R7)] = 210;
        PAYTABLE[D7][calculateIndex(D3, FA)] = 105;
        PAYTABLE[D7][calculateIndex(D3, FB)] = 63;
        PAYTABLE[D7][calculateIndex(D3, FC)] = 42;

        PAYTABLE[D7][calculateIndex(D2, D7)] = 980;
        PAYTABLE[D7][calculateIndex(D2, D3)] = 420;
        PAYTABLE[D7][calculateIndex(D2, D2)] = 280;
        PAYTABLE[D7][calculateIndex(D2, R7)] = 140;
        PAYTABLE[D7][calculateIndex(D2, FA)] = 70;
        PAYTABLE[D7][calculateIndex(D2, FB)] = 42;
        PAYTABLE[D7][calculateIndex(D2, FC)] = 28;

        PAYTABLE[D7][calculateIndex(R7, D7)] = 490;
        PAYTABLE[D7][calculateIndex(R7, D3)] = 210;
        PAYTABLE[D7][calculateIndex(R7, D2)] = 140;
        PAYTABLE[D7][calculateIndex(R7, R7)] = 70;
        PAYTABLE[D7][calculateIndex(R7, FA)] = 0;
        PAYTABLE[D7][calculateIndex(R7, FB)] = 0;
        PAYTABLE[D7][calculateIndex(R7, FC)] = 0;

        PAYTABLE[D7][calculateIndex(FA, D7)] = 245;
        PAYTABLE[D7][calculateIndex(FA, D3)] = 105;
        PAYTABLE[D7][calculateIndex(FA, D2)] = 70;
        PAYTABLE[D7][calculateIndex(FA, R7)] = 0;
        PAYTABLE[D7][calculateIndex(FA, FA)] = 35;
        PAYTABLE[D7][calculateIndex(FA, FB)] = 7;
        PAYTABLE[D7][calculateIndex(FA, FC)] = 7;

        PAYTABLE[D7][calculateIndex(FB, D7)] = 147;
        PAYTABLE[D7][calculateIndex(FB, D3)] = 63;
        PAYTABLE[D7][calculateIndex(FB, D2)] = 42;
        PAYTABLE[D7][calculateIndex(FB, R7)] = 0;
        PAYTABLE[D7][calculateIndex(FB, FA)] = 7;
        PAYTABLE[D7][calculateIndex(FB, FB)] = 21;
        PAYTABLE[D7][calculateIndex(FB, FC)] = 7;

        PAYTABLE[D7][calculateIndex(FC, D7)] = 98;
        PAYTABLE[D7][calculateIndex(FC, D3)] = 42;
        PAYTABLE[D7][calculateIndex(FC, D2)] = 28;
        PAYTABLE[D7][calculateIndex(FC, R7)] = 0;
        PAYTABLE[D7][calculateIndex(FC, FA)] = 7;
        PAYTABLE[D7][calculateIndex(FC, FB)] = 7;
        PAYTABLE[D7][calculateIndex(FC, FC)] = 14;

        PAYTABLE[D3][calculateIndex(D7, D7)] = 1470;
        PAYTABLE[D3][calculateIndex(D7, D3)] = 630;
        PAYTABLE[D3][calculateIndex(D7, D2)] = 420;
        PAYTABLE[D3][calculateIndex(D7, R7)] = 210;
        PAYTABLE[D3][calculateIndex(D7, FA)] = 105;
        PAYTABLE[D3][calculateIndex(D7, FB)] = 63;
        PAYTABLE[D3][calculateIndex(D7, FC)] = 42;

        PAYTABLE[D3][calculateIndex(D3, D7)] = 630;
        PAYTABLE[D3][calculateIndex(D3, D3)] = 270;
        PAYTABLE[D3][calculateIndex(D3, D2)] = 180;
        PAYTABLE[D3][calculateIndex(D3, R7)] = 90;
        PAYTABLE[D3][calculateIndex(D3, FA)] = 45;
        PAYTABLE[D3][calculateIndex(D3, FB)] = 27;
        PAYTABLE[D3][calculateIndex(D3, FC)] = 18;

        PAYTABLE[D3][calculateIndex(D2, D7)] = 420;
        PAYTABLE[D3][calculateIndex(D2, D3)] = 180;
        PAYTABLE[D3][calculateIndex(D2, D2)] = 120;
        PAYTABLE[D3][calculateIndex(D2, R7)] = 60;
        PAYTABLE[D3][calculateIndex(D2, FA)] = 30;
        PAYTABLE[D3][calculateIndex(D2, FB)] = 18;
        PAYTABLE[D3][calculateIndex(D2, FC)] = 12;

        PAYTABLE[D3][calculateIndex(R7, D7)] = 210;
        PAYTABLE[D3][calculateIndex(R7, D3)] = 90;
        PAYTABLE[D3][calculateIndex(R7, D2)] = 60;
        PAYTABLE[D3][calculateIndex(R7, R7)] = 30;
        PAYTABLE[D3][calculateIndex(R7, FA)] = 0;
        PAYTABLE[D3][calculateIndex(R7, FB)] = 0;
        PAYTABLE[D3][calculateIndex(R7, FC)] = 0;

        PAYTABLE[D3][calculateIndex(FA, D7)] = 105;
        PAYTABLE[D3][calculateIndex(FA, D3)] = 45;
        PAYTABLE[D3][calculateIndex(FA, D2)] = 30;
        PAYTABLE[D3][calculateIndex(FA, R7)] = 0;
        PAYTABLE[D3][calculateIndex(FA, FA)] = 15;
        PAYTABLE[D3][calculateIndex(FA, FB)] = 3;
        PAYTABLE[D3][calculateIndex(FA, FC)] = 3;

        PAYTABLE[D3][calculateIndex(FB, D7)] = 63;
        PAYTABLE[D3][calculateIndex(FB, D3)] = 27;
        PAYTABLE[D3][calculateIndex(FB, D2)] = 18;
        PAYTABLE[D3][calculateIndex(FB, R7)] = 0;
        PAYTABLE[D3][calculateIndex(FB, FA)] = 3;
        PAYTABLE[D3][calculateIndex(FB, FB)] = 9;
        PAYTABLE[D3][calculateIndex(FB, FC)] = 3;

        PAYTABLE[D3][calculateIndex(FC, D7)] = 42;
        PAYTABLE[D3][calculateIndex(FC, D3)] = 18;
        PAYTABLE[D3][calculateIndex(FC, D2)] = 12;
        PAYTABLE[D3][calculateIndex(FC, R7)] = 0;
        PAYTABLE[D3][calculateIndex(FC, FA)] = 3;
        PAYTABLE[D3][calculateIndex(FC, FB)] = 3;
        PAYTABLE[D3][calculateIndex(FC, FC)] = 6;

        PAYTABLE[D2][calculateIndex(D7, D7)] = 980;
        PAYTABLE[D2][calculateIndex(D7, D3)] = 420;
        PAYTABLE[D2][calculateIndex(D7, D2)] = 280;
        PAYTABLE[D2][calculateIndex(D7, R7)] = 140;
        PAYTABLE[D2][calculateIndex(D7, FA)] = 70;
        PAYTABLE[D2][calculateIndex(D7, FB)] = 42;
        PAYTABLE[D2][calculateIndex(D7, FC)] = 28;

        PAYTABLE[D2][calculateIndex(D3, D7)] = 420;
        PAYTABLE[D2][calculateIndex(D3, D3)] = 180;
        PAYTABLE[D2][calculateIndex(D3, D2)] = 120;
        PAYTABLE[D2][calculateIndex(D3, R7)] = 60;
        PAYTABLE[D2][calculateIndex(D3, FA)] = 30;
        PAYTABLE[D2][calculateIndex(D3, FB)] = 18;
        PAYTABLE[D2][calculateIndex(D3, FC)] = 12;

        PAYTABLE[D2][calculateIndex(D2, D7)] = 280;
        PAYTABLE[D2][calculateIndex(D2, D3)] = 120;
        PAYTABLE[D2][calculateIndex(D2, D2)] = 80;
        PAYTABLE[D2][calculateIndex(D2, R7)] = 40;
        PAYTABLE[D2][calculateIndex(D2, FA)] = 20;
        PAYTABLE[D2][calculateIndex(D2, FB)] = 12;
        PAYTABLE[D2][calculateIndex(D2, FC)] = 8;

        PAYTABLE[D2][calculateIndex(R7, D7)] = 140;
        PAYTABLE[D2][calculateIndex(R7, D3)] = 60;
        PAYTABLE[D2][calculateIndex(R7, D2)] = 40;
        PAYTABLE[D2][calculateIndex(R7, R7)] = 20;
        PAYTABLE[D2][calculateIndex(R7, FA)] = 0;
        PAYTABLE[D2][calculateIndex(R7, FB)] = 0;
        PAYTABLE[D2][calculateIndex(R7, FC)] = 0;

        PAYTABLE[D2][calculateIndex(FA, D7)] = 70;
        PAYTABLE[D2][calculateIndex(FA, D3)] = 30;
        PAYTABLE[D2][calculateIndex(FA, D2)] = 20;
        PAYTABLE[D2][calculateIndex(FA, R7)] = 0;
        PAYTABLE[D2][calculateIndex(FA, FA)] = 10;
        PAYTABLE[D2][calculateIndex(FA, FB)] = 2;
        PAYTABLE[D2][calculateIndex(FA, FC)] = 2;

        PAYTABLE[D2][calculateIndex(FB, D7)] = 42;
        PAYTABLE[D2][calculateIndex(FB, D3)] = 18;
        PAYTABLE[D2][calculateIndex(FB, D2)] = 12;
        PAYTABLE[D2][calculateIndex(FB, R7)] = 0;
        PAYTABLE[D2][calculateIndex(FB, FA)] = 2;
        PAYTABLE[D2][calculateIndex(FB, FB)] = 6;
        PAYTABLE[D2][calculateIndex(FB, FC)] = 2;

        PAYTABLE[D2][calculateIndex(FC, D7)] = 28;
        PAYTABLE[D2][calculateIndex(FC, D3)] = 12;
        PAYTABLE[D2][calculateIndex(FC, D2)] = 8;
        PAYTABLE[D2][calculateIndex(FC, R7)] = 0;
        PAYTABLE[D2][calculateIndex(FC, FA)] = 2;
        PAYTABLE[D2][calculateIndex(FC, FB)] = 2;
        PAYTABLE[D2][calculateIndex(FC, FC)] = 4;

        PAYTABLE[R7][calculateIndex(D7, D7)] = 490;
        PAYTABLE[R7][calculateIndex(D7, D3)] = 210;
        PAYTABLE[R7][calculateIndex(D7, D2)] = 140;
        PAYTABLE[R7][calculateIndex(D7, R7)] = 70;
        PAYTABLE[R7][calculateIndex(D7, FA)] = 0;
        PAYTABLE[R7][calculateIndex(D7, FB)] = 0;
        PAYTABLE[R7][calculateIndex(D7, FC)] = 0;

        PAYTABLE[R7][calculateIndex(D3, D7)] = 210;
        PAYTABLE[R7][calculateIndex(D3, D3)] = 90;
        PAYTABLE[R7][calculateIndex(D3, D2)] = 60;
        PAYTABLE[R7][calculateIndex(D3, R7)] = 30;
        PAYTABLE[R7][calculateIndex(D3, FA)] = 0;
        PAYTABLE[R7][calculateIndex(D3, FB)] = 0;
        PAYTABLE[R7][calculateIndex(D3, FC)] = 0;

        PAYTABLE[R7][calculateIndex(D2, D7)] = 140;
        PAYTABLE[R7][calculateIndex(D2, D3)] = 60;
        PAYTABLE[R7][calculateIndex(D2, D2)] = 40;
        PAYTABLE[R7][calculateIndex(D2, R7)] = 20;
        PAYTABLE[R7][calculateIndex(D2, FA)] = 0;
        PAYTABLE[R7][calculateIndex(D2, FB)] = 0;
        PAYTABLE[R7][calculateIndex(D2, FC)] = 0;

        PAYTABLE[R7][calculateIndex(R7, D7)] = 70;
        PAYTABLE[R7][calculateIndex(R7, D3)] = 30;
        PAYTABLE[R7][calculateIndex(R7, D2)] = 20;
        PAYTABLE[R7][calculateIndex(R7, R7)] = 10;
        PAYTABLE[R7][calculateIndex(R7, FA)] = 0;
        PAYTABLE[R7][calculateIndex(R7, FB)] = 0;
        PAYTABLE[R7][calculateIndex(R7, FC)] = 0;

        PAYTABLE[R7][calculateIndex(FA, D7)] = 0;
        PAYTABLE[R7][calculateIndex(FA, D3)] = 0;
        PAYTABLE[R7][calculateIndex(FA, D2)] = 0;
        PAYTABLE[R7][calculateIndex(FA, R7)] = 0;
        PAYTABLE[R7][calculateIndex(FA, FA)] = 0;
        PAYTABLE[R7][calculateIndex(FA, FB)] = 0;
        PAYTABLE[R7][calculateIndex(FA, FC)] = 0;

        PAYTABLE[R7][calculateIndex(FB, D7)] = 0;
        PAYTABLE[R7][calculateIndex(FB, D3)] = 0;
        PAYTABLE[R7][calculateIndex(FB, D2)] = 0;
        PAYTABLE[R7][calculateIndex(FB, R7)] = 0;
        PAYTABLE[R7][calculateIndex(FB, FA)] = 0;
        PAYTABLE[R7][calculateIndex(FB, FB)] = 0;
        PAYTABLE[R7][calculateIndex(FB, FC)] = 0;

        PAYTABLE[R7][calculateIndex(FC, D7)] = 0;
        PAYTABLE[R7][calculateIndex(FC, D3)] = 0;
        PAYTABLE[R7][calculateIndex(FC, D2)] = 0;
        PAYTABLE[R7][calculateIndex(FC, R7)] = 0;
        PAYTABLE[R7][calculateIndex(FC, FA)] = 0;
        PAYTABLE[R7][calculateIndex(FC, FB)] = 0;
        PAYTABLE[R7][calculateIndex(FC, FC)] = 0;

        PAYTABLE[FA][calculateIndex(D7, D7)] = 245;
        PAYTABLE[FA][calculateIndex(D7, D3)] = 105;
        PAYTABLE[FA][calculateIndex(D7, D2)] = 70;
        PAYTABLE[FA][calculateIndex(D7, R7)] = 0;
        PAYTABLE[FA][calculateIndex(D7, FA)] = 35;
        PAYTABLE[FA][calculateIndex(D7, FB)] = 7;
        PAYTABLE[FA][calculateIndex(D7, FC)] = 7;

        PAYTABLE[FA][calculateIndex(D3, D7)] = 105;
        PAYTABLE[FA][calculateIndex(D3, D3)] = 45;
        PAYTABLE[FA][calculateIndex(D3, D2)] = 30;
        PAYTABLE[FA][calculateIndex(D3, R7)] = 0;
        PAYTABLE[FA][calculateIndex(D3, FA)] = 15;
        PAYTABLE[FA][calculateIndex(D3, FB)] = 3;
        PAYTABLE[FA][calculateIndex(D3, FC)] = 3;

        PAYTABLE[FA][calculateIndex(D2, D7)] = 70;
        PAYTABLE[FA][calculateIndex(D2, D3)] = 30;
        PAYTABLE[FA][calculateIndex(D2, D2)] = 20;
        PAYTABLE[FA][calculateIndex(D2, R7)] = 0;
        PAYTABLE[FA][calculateIndex(D2, FA)] = 10;
        PAYTABLE[FA][calculateIndex(D2, FB)] = 2;
        PAYTABLE[FA][calculateIndex(D2, FC)] = 2;

        PAYTABLE[FA][calculateIndex(R7, D7)] = 0;
        PAYTABLE[FA][calculateIndex(R7, D3)] = 0;
        PAYTABLE[FA][calculateIndex(R7, D2)] = 0;
        PAYTABLE[FA][calculateIndex(R7, R7)] = 0;
        PAYTABLE[FA][calculateIndex(R7, FA)] = 0;
        PAYTABLE[FA][calculateIndex(R7, FB)] = 0;
        PAYTABLE[FA][calculateIndex(R7, FC)] = 0;

        PAYTABLE[FA][calculateIndex(FA, D7)] = 35;
        PAYTABLE[FA][calculateIndex(FA, D3)] = 15;
        PAYTABLE[FA][calculateIndex(FA, D2)] = 10;
        PAYTABLE[FA][calculateIndex(FA, R7)] = 0;
        PAYTABLE[FA][calculateIndex(FA, FA)] = 5;
        PAYTABLE[FA][calculateIndex(FA, FB)] = 1;
        PAYTABLE[FA][calculateIndex(FA, FC)] = 1;

        PAYTABLE[FA][calculateIndex(FB, D7)] = 7;
        PAYTABLE[FA][calculateIndex(FB, D3)] = 3;
        PAYTABLE[FA][calculateIndex(FB, D2)] = 2;
        PAYTABLE[FA][calculateIndex(FB, R7)] = 0;
        PAYTABLE[FA][calculateIndex(FB, FA)] = 1;
        PAYTABLE[FA][calculateIndex(FB, FB)] = 1;
        PAYTABLE[FA][calculateIndex(FB, FC)] = 1;

        PAYTABLE[FA][calculateIndex(FC, D7)] = 7;
        PAYTABLE[FA][calculateIndex(FC, D3)] = 3;
        PAYTABLE[FA][calculateIndex(FC, D2)] = 2;
        PAYTABLE[FA][calculateIndex(FC, R7)] = 0;
        PAYTABLE[FA][calculateIndex(FC, FA)] = 1;
        PAYTABLE[FA][calculateIndex(FC, FB)] = 1;
        PAYTABLE[FA][calculateIndex(FC, FC)] = 1;

        PAYTABLE[FB][calculateIndex(D7, D7)] = 147;
        PAYTABLE[FB][calculateIndex(D7, D3)] = 63;
        PAYTABLE[FB][calculateIndex(D7, D2)] = 42;
        PAYTABLE[FB][calculateIndex(D7, R7)] = 0;
        PAYTABLE[FB][calculateIndex(D7, FA)] = 7;
        PAYTABLE[FB][calculateIndex(D7, FB)] = 21;
        PAYTABLE[FB][calculateIndex(D7, FC)] = 7;

        PAYTABLE[FB][calculateIndex(D3, D7)] = 63;
        PAYTABLE[FB][calculateIndex(D3, D3)] = 27;
        PAYTABLE[FB][calculateIndex(D3, D2)] = 18;
        PAYTABLE[FB][calculateIndex(D3, R7)] = 0;
        PAYTABLE[FB][calculateIndex(D3, FA)] = 3;
        PAYTABLE[FB][calculateIndex(D3, FB)] = 9;
        PAYTABLE[FB][calculateIndex(D3, FC)] = 3;

        PAYTABLE[FB][calculateIndex(D2, D7)] = 42;
        PAYTABLE[FB][calculateIndex(D2, D3)] = 18;
        PAYTABLE[FB][calculateIndex(D2, D2)] = 12;
        PAYTABLE[FB][calculateIndex(D2, R7)] = 0;
        PAYTABLE[FB][calculateIndex(D2, FA)] = 2;
        PAYTABLE[FB][calculateIndex(D2, FB)] = 6;
        PAYTABLE[FB][calculateIndex(D2, FC)] = 2;

        PAYTABLE[FB][calculateIndex(R7, D7)] = 0;
        PAYTABLE[FB][calculateIndex(R7, D3)] = 0;
        PAYTABLE[FB][calculateIndex(R7, D2)] = 0;
        PAYTABLE[FB][calculateIndex(R7, R7)] = 0;
        PAYTABLE[FB][calculateIndex(R7, FA)] = 0;
        PAYTABLE[FB][calculateIndex(R7, FB)] = 0;
        PAYTABLE[FB][calculateIndex(R7, FC)] = 0;

        PAYTABLE[FB][calculateIndex(FA, D7)] = 7;
        PAYTABLE[FB][calculateIndex(FA, D3)] = 3;
        PAYTABLE[FB][calculateIndex(FA, D2)] = 2;
        PAYTABLE[FB][calculateIndex(FA, R7)] = 0;
        PAYTABLE[FB][calculateIndex(FA, FA)] = 1;
        PAYTABLE[FB][calculateIndex(FA, FB)] = 1;
        PAYTABLE[FB][calculateIndex(FA, FC)] = 1;

        PAYTABLE[FB][calculateIndex(FB, D7)] = 21;
        PAYTABLE[FB][calculateIndex(FB, D3)] = 9;
        PAYTABLE[FB][calculateIndex(FB, D2)] = 6;
        PAYTABLE[FB][calculateIndex(FB, R7)] = 0;
        PAYTABLE[FB][calculateIndex(FB, FA)] = 1;
        PAYTABLE[FB][calculateIndex(FB, FB)] = 3;
        PAYTABLE[FB][calculateIndex(FB, FC)] = 1;

        PAYTABLE[FB][calculateIndex(FC, D7)] = 7;
        PAYTABLE[FB][calculateIndex(FC, D3)] = 3;
        PAYTABLE[FB][calculateIndex(FC, D2)] = 2;
        PAYTABLE[FB][calculateIndex(FC, R7)] = 0;
        PAYTABLE[FB][calculateIndex(FC, FA)] = 1;
        PAYTABLE[FB][calculateIndex(FC, FB)] = 1;
        PAYTABLE[FB][calculateIndex(FC, FC)] = 1;

        PAYTABLE[FC][calculateIndex(D7, D7)] = 98;
        PAYTABLE[FC][calculateIndex(D7, D3)] = 42;
        PAYTABLE[FC][calculateIndex(D7, D2)] = 28;
        PAYTABLE[FC][calculateIndex(D7, R7)] = 0;
        PAYTABLE[FC][calculateIndex(D7, FA)] = 7;
        PAYTABLE[FC][calculateIndex(D7, FB)] = 7;
        PAYTABLE[FC][calculateIndex(D7, FC)] = 14;

        PAYTABLE[FC][calculateIndex(D3, D7)] = 42;
        PAYTABLE[FC][calculateIndex(D3, D3)] = 18;
        PAYTABLE[FC][calculateIndex(D3, D2)] = 12;
        PAYTABLE[FC][calculateIndex(D3, R7)] = 0;
        PAYTABLE[FC][calculateIndex(D3, FA)] = 3;
        PAYTABLE[FC][calculateIndex(D3, FB)] = 3;
        PAYTABLE[FC][calculateIndex(D3, FC)] = 6;

        PAYTABLE[FC][calculateIndex(D2, D7)] = 28;
        PAYTABLE[FC][calculateIndex(D2, D3)] = 12;
        PAYTABLE[FC][calculateIndex(D2, D2)] = 8;
        PAYTABLE[FC][calculateIndex(D2, R7)] = 0;
        PAYTABLE[FC][calculateIndex(D2, FA)] = 2;
        PAYTABLE[FC][calculateIndex(D2, FB)] = 2;
        PAYTABLE[FC][calculateIndex(D2, FC)] = 4;

        PAYTABLE[FC][calculateIndex(R7, D7)] = 0;
        PAYTABLE[FC][calculateIndex(R7, D3)] = 0;
        PAYTABLE[FC][calculateIndex(R7, D2)] = 0;
        PAYTABLE[FC][calculateIndex(R7, R7)] = 0;
        PAYTABLE[FC][calculateIndex(R7, FA)] = 0;
        PAYTABLE[FC][calculateIndex(R7, FB)] = 0;
        PAYTABLE[FC][calculateIndex(R7, FC)] = 0;

        PAYTABLE[FC][calculateIndex(FA, D7)] = 7;
        PAYTABLE[FC][calculateIndex(FA, D3)] = 3;
        PAYTABLE[FC][calculateIndex(FA, D2)] = 2;
        PAYTABLE[FC][calculateIndex(FA, R7)] = 0;
        PAYTABLE[FC][calculateIndex(FA, FA)] = 1;
        PAYTABLE[FC][calculateIndex(FA, FB)] = 1;
        PAYTABLE[FC][calculateIndex(FA, FC)] = 1;

        PAYTABLE[FC][calculateIndex(FB, D7)] = 7;
        PAYTABLE[FC][calculateIndex(FB, D3)] = 3;
        PAYTABLE[FC][calculateIndex(FB, D2)] = 2;
        PAYTABLE[FC][calculateIndex(FB, R7)] = 0;
        PAYTABLE[FC][calculateIndex(FB, FA)] = 1;
        PAYTABLE[FC][calculateIndex(FB, FB)] = 1;
        PAYTABLE[FC][calculateIndex(FB, FC)] = 1;

        PAYTABLE[FC][calculateIndex(FC, D7)] = 14;
        PAYTABLE[FC][calculateIndex(FC, D3)] = 6;
        PAYTABLE[FC][calculateIndex(FC, D2)] = 4;
        PAYTABLE[FC][calculateIndex(FC, R7)] = 0;
        PAYTABLE[FC][calculateIndex(FC, FA)] = 1;
        PAYTABLE[FC][calculateIndex(FC, FB)] = 1;
        PAYTABLE[FC][calculateIndex(FC, FC)] = 2;
    }

    static int calculateIndex(int second, int third) {
        return second * second + 2 * third * third + 4 * second * third;
    }


    static final int BL = 0;
    static final int D7 = 1;
    static final int D3 = 2;
    static final int D2 = 3;
    static final int R7 = 4;
    static final int FA = 5;
    static final int FB = 6;
    static final int FC = 7;
    static final int BN = 8;

    static final int NUM_SYMBOLS = 9;

    static final String[] SYMBOL_NAMES = {"BL", "D7", "D3", "D2", "R7", "FA", "FB", "FC", "BN"};
}