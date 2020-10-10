package com.LT.math.JackpotBonus;

import com.aspectgaming.core.Context;
import com.aspectgaming.math.*;
import com.aspectgaming.math.gamble.GambleMath;
import com.aspectgaming.math.gamble.GambleResult;
import com.aspectgaming.math.progressive.ProgressiveLevel;
import com.aspectgaming.random.RandomNumberGenerator;
import com.aspectgaming.util.CommonUtil;
import com.ltgame.bedrock.math.MathConfigData;
import com.ltgame.bedrock.math.MathProgressiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static com.LT.math.JackpotBonus.JackpotBonusMathConstants.*;

public class JackpotBonusMath implements GamingMath {
    private static final RandomNumberGenerator random = RandomNumberGenerator.getInstance();
    private static final GambleMath gamble = new GambleMath();
    private static final com.LT.math.JackpotBonus.JackpotBonusMathReels reels = new com.LT.math.JackpotBonus.JackpotBonusMathReels();
    private com.LT.math.JackpotBonus.JackpotBonusMathReelSet currentReelSet;

    private int gambleTestNum = 0;
    private boolean bIsTest;
    boolean isScript=false;

    private int currentMaxPaylines = 1;
    private int currentMaxCps;
    private int currentCps = 1;
    private WinLossTracker winLossTracker = new WinLossTracker();
    private final boolean isSimulation;
    private final ReelStrip[] reelStrips = reels.getAllReelStrips();
    private final Logger log = LoggerFactory.getLogger(JackpotBonusMath.class);

    private TestFeature currentTest;
    private TestFeature[] testGamesInfo = new TestFeature[10];
    private int testGameNum = 0;
    private int testGameIndex1 = 0;
    private static final boolean ENABLE_DEMO = true;
    private float progressiveProportion = 0.1f;
    private int[] p1Stop = {3, 15, 7};
    private int[] p2Stop = {8, 2, 16};
    private int[][] jfStop = {{4,16,8},{4, 15, 6},{2,14,6},{2,15,8}};
    private long reelRng0=0;
    private long reelRng1=0;
    private long reelRng2=0;
    //+NEW_SAPC
    private MathProgressiveType[] availableProgressiveTypes;
    private int currentProgressiveTypeIndex;
    private static SlotGameMode gameMode;
    private final String P2Gamerecall="0,1,2,3,4,5,6,7,8";


    //+NEW_SAPC
    private List<Integer> categoriesHitList;

    public enum SlotGameMode
    {
        None,
        StandAlone,
        Linked
    }

    class TestFeature {
        public int scatterNum = 0;
        public boolean p1Progressive = false;
        public boolean p2Progressive = false;
        public boolean p1Test = false;
        public boolean jf = false;
        public int[] progressiveLevel = new int[4];
        String gambleResult = new String();
    }

    //1
    public JackpotBonusMath() {
        this(false);
    }

    public JackpotBonusMath(boolean simulation) {
        JackpotBonusMathConstants.init();
        isSimulation = simulation;
        //+NEW_SAPC
        currentProgressiveTypeIndex = 0;
        availableProgressiveTypes = new MathProgressiveType[]
                {
                        MathProgressiveType.PROGRESSIVE_TYPE_STAND_ALONE,
                        MathProgressiveType.PROGRESSIVE_TYPE_NONE,
                        MathProgressiveType.PROGRESSIVE_TYPE_LINKED
                };
    }
    //+NEW_SAPC
    @Override
    public void init(MathConfigData mathConfigData)
    {
        for(int i = 0; i < availableProgressiveTypes.length; i++)
        {
            if(availableProgressiveTypes[i] == mathConfigData.getProgressiveMode())
            {
                currentProgressiveTypeIndex = i;
                break;
            }
        }

        MathData mathData= Context.gameContext().getMathState();

        switch (availableProgressiveTypes[currentProgressiveTypeIndex])
        {
            case PROGRESSIVE_TYPE_NONE:
                gameMode=SlotGameMode.None;
                break;
            case PROGRESSIVE_TYPE_STAND_ALONE:
                gameMode=SlotGameMode.StandAlone;
                break;
            case PROGRESSIVE_TYPE_LINKED:
                gameMode=SlotGameMode.Linked;
                break;
        }

        if(gameMode==SlotGameMode.Linked)
        {
            reels.setAsLinked();
        }

    }

    @Override
    public void initialize(Properties properties) {
    }

    @Override
    public void initialize(MathData data, Properties properties) {
        //+NEW_SAPC
        // In order to support both SAP and Linked need to create the empty array for the levels
        long[] values = data.getProgressiveValues();
        if (values == null) {
            values = new long[JACKPOT_LEVELS];
            data.setProgressiveValues(values);
        }
    }

    @Override
    public String getMathName() {
        return NAME;
    }

    @Override
    public String getMathVersion() {
        return VERSION;
    }

    @Override
    public MathType getMathType() {
        return MathType.Standalone;
    }

    @Override
    public String[] getConfigurableReelSetNames() {
        return reels.getReelSetNames();
    }

    @Override
    public ReelSet getCurrentReelSet() {
        return currentReelSet;
    }

    @Override
    public void setCurrentReelSet(String reelSetName) {
        this.currentReelSet = reels.getReelSet(reelSetName);
        if (availableProgressiveTypes[currentProgressiveTypeIndex] != MathProgressiveType.PROGRESSIVE_TYPE_STAND_ALONE) {
            for (int i=0; i<NUMBER_WAGER_CATEGORIES; ++i) {
                this.currentReelSet.wagerCategories[i].rtpLP = 0;
                this.currentReelSet.wagerCategories[i].rtpSAP = 0;
            }
        }
    }

    @Override
    public int[] getConfigurableSelections() {
        return PAYLINE_SELECTIONS;
    }

    @Override
    public int getCurrentMaxSelection() {
        return this.currentMaxPaylines;
    }

    @Override
    public void setCurrentMaxSelection(int selection) {
        this.currentMaxPaylines = selection;
    }

    @Override
    public int getPaylines(int selections) {
        return selections>5?5:selections;
    }

    @Override
    public void setCurrentMaxCreditPerSelection(int maxCps) {
        this.currentMaxCps = maxCps;
    }

    @Override
    public int getCurrentMaxCreditPerSelection() {
        return this.currentMaxCps;
    }

    @Override
    public void bonusPick(MathData data, int pickIndex) {
    }


    @Override
    public void call(MathData data) {
        //+NEW_SAPC
        categoriesHitList = new ArrayList<>();

        bIsTest = false;
        isScript=false;
        this.validate(data);
        this.spinReels(data);
        this.populateStops(data);
        //计算freegame数量和赢钱
        this.scoreScatter(data);
        //计算每条线赢钱
        this.scoreLines(data);

        this.winLossTracker.update(data);
        //+NEW_SAPC
        populateWinCategories(data, categoriesHitList);
        logMath(data);
        //+NEW_SAPC
        categoriesHitList = null;

    }

    @Override
    public void emulate(MathData data) {
        //+NEW_SAPC
        categoriesHitList = new ArrayList<>();

        this.validate(data);
        isScript=false;

        Map<String, String> storedMathParams = data.getStoredMathParams();
        Set<String> keySet = storedMathParams.keySet();

        for (String key : keySet) {
            if (key.equals("REEL_0_RNG")){
                reelRng0 = Long.parseLong(storedMathParams.get(key));
                isScript=true;
            }
            if (key.equals("REEL_1_RNG")){
                reelRng1 = Long.parseLong(storedMathParams.get(key));
                isScript=true;
            }
            if (key.equals("REEL_2_RNG")){
                reelRng2 = Long.parseLong(storedMathParams.get(key));
                isScript=true;
            }
        }
        if (data.getStopsIndices() != null) {
            initTestFeatures();
            populateStops(data);
        } else if (data.getStops() == null) {
            spinReels(data);
            populateStops(data);
        }
        if (isScript){
            data.getStoredMathParams().remove("REEL_0_RNG");
            data.getStoredMathParams().remove("REEL_1_RNG");
            data.getStoredMathParams().remove("REEL_2_RNG");
        }

        this.scoreScatter(data);
        this.scoreLines(data);
        this.winLossTracker.update(data);
        //+NEW_SAPC
        populateWinCategories(data, categoriesHitList);
        logMath(data);
        //+NEW_SAPC
        categoriesHitList = null;
    }

    @Override
    public GambleResult gamble(long creditsRisked, int playerPick) {
        int result = -1;
        boolean testGamble = false;

        if (testGameNum > 0 && testGamesInfo[0].gambleResult.length() > 0) {
            testGamble = true;
            result = (int) (testGamesInfo[0].gambleResult.charAt(0)) - 48;
            testGamesInfo[0].gambleResult = testGamesInfo[0].gambleResult.substring(1);
            if (result > 3) {
                result = random.random(4);
            }
        }

        if (testGamble) {
            boolean win = false;
            int multiplier = 2;
            long creditsChange;
            switch (playerPick) {
                case 0:
                    multiplier = 2;
                    if ((result == 0) || (result == 1)) {
                        win = true;
                    }
                    break;
                case 1:
                    multiplier = 2;
                    if ((result == 2) || (result == 3)) {
                        win = true;
                    }
                    break;
                case 2:
                    multiplier = 4;
                    if (result == 0) {
                        win = true;
                    }
                    break;
                case 3:
                    multiplier = 4;
                    if (result == 1) {
                        win = true;
                    }
                    break;
                case 4:
                    multiplier = 4;
                    if (result == 2) {
                        win = true;
                    }
                    break;
                case 5:
                    multiplier = 4;
                    if (result == 3) {
                        win = true;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown gamble playerPick " + playerPick + " selected");
            }

            if (win) {
                creditsChange = creditsRisked * (multiplier - 1);
            } else {
                creditsChange = -creditsRisked;
            }

            return new GambleResult(result, win, creditsChange, multiplier, playerPick, creditsRisked);
        } else {
            return gamble.gamble(creditsRisked, playerPick);
        }
    }

    //+NEW_SAPC
//    private void resetProgressiveValue(int level, MathData data) {
//        ProgressiveLevel[] pls = currentReelSet.getWagerCategories()[0].getProgressiveLevels();
//        long startCents = pls[level - 1].Min * data.getGameDenomination();
//        data.setProgressiveCents(level, startCents);
//    }

    @Override
    public PositionWin[] translateSelectionWinSymMaskToPositions(MathData mathData) {
        assert mathData != null;
        if (mathData.getSelectionSymMask() == null) {
            return null;
        }
        //Update for ints to longs
        //int[] symMasks = mathData.getSelectionSymMask();
        long[] symMasks = mathData.getSelectionSymMask();
        PositionWin[] ret = new PositionWin[symMasks.length];
        for (int line = 0; line < symMasks.length; line++) {
            if (symMasks[line] == 0) {
                ret[line] = null;
                continue;
            }
            //Update for ints to longs
            //int[] linePositions = CommonUtil.getSetBitPositions((int)symMasks[line]);
            int[] linePositions = CommonUtil.getSetBitPositions(Math.toIntExact(symMasks[line]));
            StringBuilder builder = new StringBuilder();
            for (int linePosition : linePositions) {
                builder.append(Integer.toString(LINE_POSITIONSRecall[line][linePosition]));
                builder.append(",");
            }
            if (builder.length() > 1) {
                builder.replace(builder.length() - 1, builder.length(), "");
            }
            int selectionWin = 0;
            int selectionWinMultiplier = 0;
            if (mathData.getSelectionWin() != null && mathData.getSelectionWin().length > 0) {
                selectionWin = mathData.getSelectionWin()[line];
            }
            if (mathData.getSelectionWinMultiplier() != null && mathData.getSelectionWinMultiplier().length > 0) {
                selectionWinMultiplier = mathData.getSelectionWinMultiplier()[line];
            }
            for (Map.Entry<String, String> stringEntry : mathData.getStoredMathParams().entrySet()) {
                if(stringEntry.getKey().equals("P2Win")){
                    if(stringEntry.getValue().equals("true")){
                        builder.setLength(0);
                        builder.append(P2Gamerecall);
                    }
                    break;
                }
            }
            ret[line] = new PositionWin(0, builder.toString(), selectionWin, selectionWinMultiplier);
        }
        return ret;
    }

    @Override
    public PositionWin[] translateScatterWinSymMaskToPositions(MathData mathData) {
        int symMask = mathData.getScatterWinMask();
        if (symMask == 0) {
            return null;
        }
        int[] positions = CommonUtil.getSetBitPositions(symMask);
        PositionWin[] ret = new PositionWin[1];
        ret[0] = new PositionWin(0, CommonUtil.convertToCommaSeparatedString(positions), mathData.getScatterWin(), mathData.getScatterWinMultiplier());
        return ret;
    }

    @Override
    public ReelStrip[] getReelStrips() {
        return reelStrips;
    }

    @Override
    public WagerCategory getWagerCategory(MathData data) {
        return currentReelSet.wagerCategories[data.getBetCredits() - 1];
    }

    @Override
    public int calculateBet(int lines, int cpl, int anteBetLevel, int betCredits) {
        return betCredits;
    }

    @Override
    public int getNumberSymbolsPerScreen() {
        return NUM_SYMBOLS_PER_SCREEN;
    }

    @Override
    public int getDefaultSelections() {
        return 1;
    }

    @Override
    public int getDefaultBet() {
        return 1;
    }

    @Override
    public int getDefaultBetCredits() {
        return 1;
    }

    @Override
    public int[] getAnteBetLevels() {
        return null;
    }

    @Override
    public int calculateMaxBet() {
        return 5;
    }


    @Override
    public int getConfigurableMaxCreditPerSelection() {
        return MAX_CONFIGURABLE_CPS;
    }

    private int getTableIndex(int multiplier) {
        for (int i = 0; i < AVAIABLE_BET_MULTIPLER.length; ++i) {
            if (multiplier == AVAIABLE_BET_MULTIPLER[i]) {
                return i;
            }
        }

        return 0;
    }

    void scoreLines(MathData data) {
        data.setStoredMathParam("P2Win", "false");
        data.setStoredMathParam("JACKPOTWIN", "false");
        data.setStoredMathParam("Win4000", "false");
        data.getStoredMathParams().remove("JACKPOTINFO");
        data.getStoredMathParams().remove("JACKPOTLINE0");
        data.getStoredMathParams().remove("JACKPOTLINE1");

        for (int line = 0; line < data.getSelections(); line++) {
            scoreLine(line, data);
        }
    }

    private void initTestFeatures() {
        testGameNum = 0;
        testGameIndex1 = 0;
    }

    private void scoreLine(int line, MathData data) {
        int currentSymbol1 = data.getStops()[LINE_POSITIONS[line][0]];
        int currentSymbol2 = data.getStops()[LINE_POSITIONS[line][1]];
        int currentSymbol3 = data.getStops()[LINE_POSITIONS[line][2]];

        int symbolPosition1 = data.getStops()[LINE_POSITIONS[0][0]];
        int symbolPosition2 = data.getStops()[LINE_POSITIONS[0][1]];
        int symbolPosition3 = data.getStops()[LINE_POSITIONS[0][2]];
        int symbolPosition4 = data.getStops()[LINE_POSITIONS[1][0]];
        int symbolPosition5 = data.getStops()[LINE_POSITIONS[1][1]];
        int symbolPosition6 = data.getStops()[LINE_POSITIONS[1][2]];
        int symbolPosition7 = data.getStops()[LINE_POSITIONS[2][0]];
        int symbolPosition8 = data.getStops()[LINE_POSITIONS[2][1]];
        int symbolPosition9 = data.getStops()[LINE_POSITIONS[2][2]];
        int symbolPosition10 = data.getStops()[LINE_POSITIONS[3][0]];
        int symbolPosition11 = data.getStops()[LINE_POSITIONS[3][1]];
        int symbolPosition12 = data.getStops()[LINE_POSITIONS[3][2]];
        int symbolPosition13 = data.getStops()[LINE_POSITIONS[4][0]];
        int symbolPosition14 = data.getStops()[LINE_POSITIONS[4][1]];
        int symbolPosition15 = data.getStops()[LINE_POSITIONS[4][2]];

        int index = 0;
        int win = PAYTABLE[currentSymbol1][calculateIndex(currentSymbol2, currentSymbol3)];

        if (currentSymbol1 == BL || currentSymbol2 == BL || currentSymbol3 == BL) {
            win = 0;
        }

        if ((symbolPosition4 == D7 && symbolPosition5 == D7 && symbolPosition6 == D7)||(symbolPosition7 == D7 && symbolPosition8 == D7 && symbolPosition9 == D7)||(symbolPosition10 == D7 && symbolPosition11 == D7 && symbolPosition12 == D7)||(symbolPosition13 == D7 && symbolPosition14 == D7 && symbolPosition15 == D7)) {
            data.setStoredMathParam("Win4000", "true");
        }


        if (symbolPosition1 == D7 && symbolPosition2 == D7 && symbolPosition3 == D7 && data.getSelections() == PAYLINE_SELECTIONS[0]) {
            data.setStoredMathParam("JACKPOTWIN", "true");
            if (line == 0) {
                if (gameMode==SlotGameMode.StandAlone||gameMode==SlotGameMode.Linked) {
                    win = 0;
                    //+NEW_SAPC
                    //awardProgressiveWin(index+1, data);
                    categoriesHitList.add(WIN_CATEGORY_PROGRESSIVE_L1);
                    int[] jackpos = {10000};
                    data.setStoredMathParam("JACKPOTINFO", CommonUtil.convertToCommaSeparatedString(jackpos));
                    data.setStoredMathParam("JACKPOTLINE0", CommonUtil.convertToCommaSeparatedString(jackpos));
                } else {
                    win = JACKPOTWIN;
                }
            }
        }
        //enter p2 progressive
        else if (symbolPosition1 == D3 && symbolPosition2 == D3 && symbolPosition3 == D3 && symbolPosition4 == D2 && symbolPosition5 == D2 && symbolPosition6 == D2) {
            win = 0;
            data.setStoredMathParam("P2Win", "true");
            if (line == 0) {
                if (gameMode==SlotGameMode.StandAlone) {
                    //+NEW_SAPC
                    //awardProgressiveWin(index+2, data);
                    categoriesHitList.add(WIN_CATEGORY_PROGRESSIVE_L2);
                    int[] jackpos = {P2WIN};
                    data.setStoredMathParam("JACKPOTINFO", CommonUtil.convertToCommaSeparatedString(jackpos));
                    data.setStoredMathParam("JACKPOTLINE1", CommonUtil.convertToCommaSeparatedString(jackpos));
                    win=P2WIN;
                } else {
                    win = P2WIN;
                }
            }
        }

        data.getSelectionWinMultiplier()[line] = 1;

        if (win == 0) {
            return;
        }
        data.setTotalWin(data.getTotalWin() + win);
        data.getSelectionWin()[line] = win;

        //Update for ints to longs
        data.getSelectionSymMask()[line] = 7L;
    }

    private void scoreScatter(MathData data) {
        data.getStoredMathParams().remove("FREEGAMEANTICIPATION");
        data.setScatterWinMask(0);
        data.setScatterWin(0);

        int count = 0;
        int scatterMask = 0;

        for (int reelId = 0; reelId < NUM_REELS; reelId++) {
            for (int i = 1; i < NUM_ROWS - 1; i++) {
                int stopIndex = reelId + i * NUM_REELS;
                int symbolVal = data.getStops()[stopIndex];

                if (symbolVal == BN) {
                    count++;
                    scatterMask |= (1 << stopIndex);

                    if (count == 2) {
                        if (reelId < NUM_REELS - 1) {
                            data.setStoredMathParam("FREEGAMEANTICIPATION", String.valueOf(reelId));
                        }
                    }
                }
            }
        }

        if (count == 0) {
            return;
        }

        if (count > NUM_REELS) {
            count = NUM_REELS; // sanity check for emulation
        }

        if (!data.getFreeSpin()) {
            data.setStoredMathParam("FreeGameTotalWon", "0");
        }

        int freeGamesWon = 0;
        if (count >= NUM_SC_TO_TRIGGER_FREE_GAMES) {
            if (Integer.parseInt(data.getStoredMathParam("FreeGameTotalWon")) < 510) {
                if (data.getFreeSpin()) {
                    freeGamesWon = SCATTER_FREE_GAMES_AWARDED_IN_FREEGAME;
                } else {
                    freeGamesWon = SCATTER_FREE_GAMES_AWARDED_IN_BASEGAME;
                }
            }
        }
        if (freeGamesWon > 0) {
            data.setStoredMathParam("FreeGameTotalWon", (Integer.parseInt(data.getStoredMathParam("FreeGameTotalWon")) + freeGamesWon) + "");
            data.setFreeSpinsWon(freeGamesWon);
            data.setActiveBonusType(0);
        }
        data.setScatterWinMask(scatterMask);
    }


//    private void awardProgressiveWin(int level, MathData data) {
//        ProgressiveLevel[] pls = currentReelSet.getWagerCategories()[0].getProgressiveLevels();
//        int type = pls[level - 1].Type;
//        if (!BonusType.isLinked(type)) {
//            long cents = data.getProgressiveCents(level);
//            cents = cents * data.getCreditPerSelection();
//            Progressives.offer(-1, level, cents, type); // support simultaneous progressives
//            //resetProgressiveValue(level, data);
//        } else {
//            Progressives.offer(-1, level, -1, type); // support simultaneous progressives
//        }
//    }

    private void parseTestFeature(MathData data) {
        initTestFeatures();
        if (ENABLE_DEMO) {
            String featureString = data.getStoredMathParam("TESTFEATURE");
            data.getStoredMathParams().remove("TESTFEATURE");
            if (featureString != null) {
                String[] games = featureString.split(";");
                for (int i = 0; i < games.length; ++i) {
                    testGameNum++;
                    testGamesInfo[i] = new TestFeature();
                    String[] features = games[i].split(",");
                    for (int j = 0; j < features.length; ++j) {
                        String feature = features[j].trim();
                        if (feature.toUpperCase().startsWith("SC")) {
                            try {
                                testGamesInfo[i].scatterNum = Integer.valueOf(feature.toUpperCase().substring(2)).intValue();
                            } catch (NumberFormatException e) {
                                log.error("test feature param error");
                                continue;
                            }
                        } else if (feature.toUpperCase().startsWith("G")) {
                            if (i == 0) {
                                testGamesInfo[i].gambleResult = feature.substring(1);
                            }
                        } else if (feature.toUpperCase().startsWith("P1")) {
                            testGamesInfo[i].p1Progressive = true;
                        } else if (feature.toUpperCase().startsWith("P2")) {
                            testGamesInfo[i].p2Progressive = true;
                        } else if (feature.toUpperCase().startsWith("TESTP1")) {
                            testGamesInfo[i].p1Test = true;
                        } else if (feature.toUpperCase().startsWith("JF")){
                            testGamesInfo[i].jf = true;
                        }

                    }

                    if (i == 0 && testGamesInfo[i].scatterNum < 3) {
                        break;
                    }
                }
            }
        }
    }

    private int getScatterStopIndice(int[] strip) {
        int indice = 0;
        while (true) {
            indice = random.random(strip.length);
            boolean hasScatter = false;
            for (int i = 1; i < NUM_ROWS - 1; ++i) {
                if (strip[indice + i < strip.length ? indice + i : (indice + i) % strip.length] == BN) {
                    hasScatter = true;
                    break;
                }
            }
            if (hasScatter) break;
        }

        return indice;
    }

    private void spinReels(MathData data) {


        long totalWeightBaseR1 = Total_Weight_BaseR1;
        long totalWeightBaseR2 = Total_Weight_BaseR2;
        long totalWeightBaseR3 = Total_Weight_BaseR3;
        long weightBaseR1[] = Weight_BaseR1;
        long weightBaseR2[] = Weight_BaseR2;
        long weightBaseR3[] = Weight_BaseR3;

        long totalWeightFreeR1 = Total_Weight_FreeR1;
        long totalWeightFreeR2 = Total_Weight_FreeR2;
        long totalWeightFreeR3 = Total_Weight_FreeR3;
        long weightFreeR1[] = Weight_FreeR1;
        long weightFreeR2[] = Weight_FreeR2;
        long weightFreeR3[] = Weight_FreeR3;

        long totalWeightMaxBetJackpotBase = Total_Weitht_Max_Bet_Jackpot_Base;
        long totalWeightNoMaxBetJackpotBase = Total_Weitht_No_Max_Bet_Jackpot_Base;
        long totalWeightMaxBetJackpotFree = Total_Weitht_Max_Bet_Jackpot_Free;
        long totalWeightNoMaxBetJackpotFree = Total_Weitht_No_Max_Bet_Jackpot_Free;

        long weightMaxBetBaseR3[] = Weight_Max_Bet_Jackpot_BaseR3;
        long weightNoMaxBetBaseR3[] = Weight_No_Max_Bet_Jackpot_BaseR3;
        long weightMaxBetFreeR3[] = Weight_Max_Bet_Jackpot_FreeR3;
        long weightNoMaxBetFreeR3[] = Weight_No_Max_Bet_Jackpot_FreeR3;


        if (availableProgressiveTypes[currentProgressiveTypeIndex] != MathProgressiveType.PROGRESSIVE_TYPE_NONE) {
            long progressiveValues[] = new long[JACKPOT_LEVELS];
            long add = (int) (data.PROGRESSIVE_UNIT * progressiveProportion);
        }
        data.setStopsIndices(new int[NUM_REELS]);
        int[][] reelStrip = this.currentReelSet.gameReels;
        if (!data.getFreeSpin()) {
            parseTestFeature(data);
        }
        currentTest = null;
        if (testGameIndex1 < testGameNum) {
            currentTest = testGamesInfo[testGameIndex1];
            testGameIndex1++;

            if (!currentTest.p1Progressive && !currentTest.p2Progressive && !currentTest.p1Test&& !currentTest.jf) {
                for (int reel = 0; reel < NUM_REELS; reel++) {
                    if (currentTest.scatterNum-- > 0) {
                        data.getStopsIndices()[reel] = getScatterStopIndice(reelStrip[reel]);
                    } else {
                        data.getStopsIndices()[reel] = random.random(reelStrip[reel].length);
                    }
                }
            } else {
                if (currentTest.p1Progressive) {
                    for (int reel = 0; reel < NUM_REELS; reel++) {
                        data.getStopsIndices()[reel] = p1Stop[reel];
                    }
                }
                if (currentTest.p2Progressive) {
                    for (int reel = 0; reel < NUM_REELS; reel++) {
                        data.getStopsIndices()[reel] = p2Stop[reel];
                    }
                }
                if (currentTest.p1Test) {
                    for (int reel = 0; reel < NUM_REELS - 1; reel++) {
                        data.getStopsIndices()[reel] = p1Stop[reel];
                    }
                    long rand;
                    if (data.getStopsIndices()[0] == p1Stop[0] && data.getStopsIndices()[1] == p1Stop[1]) {
                        if (data.getSelections() == PAYLINE_SELECTIONS[0]) {
                            rand = random.random(0,totalWeightMaxBetJackpotBase);
                            if (isScript){
                                rand=reelRng2;
                            }
                            data.getStopsIndices()[2] = calculateIndice(rand, weightMaxBetBaseR3);
                        } else {
                            rand = random.random(0,totalWeightNoMaxBetJackpotBase);
                            if (isScript){
                                rand=reelRng2;
                            }
                            data.getStopsIndices()[2] = calculateIndice(rand, weightNoMaxBetBaseR3);
                        }

                    } else {
                        rand = random.random(0,totalWeightBaseR3);
                        data.getStopsIndices()[2] = calculateIndice(rand, weightBaseR3);
                    }
                }
                if (currentTest.jf) {
                    int rand = new Random().nextInt(4);
                    for (int reel = 0; reel < NUM_REELS; reel++) {
                        data.getStopsIndices()[reel] = jfStop[rand][reel];
                    }
                }
            }
        } else {
            if (data.getFreeSpin() == false) {
                long rand = random.random(0,totalWeightBaseR1);
                if (isScript){
                    rand=reelRng0;
                }
                data.getStopsIndices()[0] = calculateIndice(rand, weightBaseR1);

                rand = random.random(0,totalWeightBaseR2);
                if (isScript){
                    rand=reelRng1;
                }
                data.getStopsIndices()[1] = calculateIndice(rand, weightBaseR2);

                //假设前两列第一行都是D7
                if (data.getStopsIndices()[0] == p1Stop[0] && data.getStopsIndices()[1] == p1Stop[1]) {
                    if (data.getSelections() == PAYLINE_SELECTIONS[0]) {
                        rand = random.random(0,totalWeightMaxBetJackpotBase);
                        if (isScript){
                            rand=reelRng2;
                        }
                        data.getStopsIndices()[2] = calculateIndice(rand, weightMaxBetBaseR3);
                    } else {
                        rand = random.random(0,totalWeightNoMaxBetJackpotBase);
                        if (isScript){
                            rand=reelRng2;
                        }
                        data.getStopsIndices()[2] = calculateIndice(rand, weightNoMaxBetBaseR3);
                    }

                } else {
                    rand = random.random(0,totalWeightBaseR3);
                    if (isScript){
                        rand=reelRng2;
                    }
                    data.getStopsIndices()[2] = calculateIndice(rand, weightBaseR3);
                }
            } else {
                long rand = random.random(0,totalWeightFreeR1);
                if (isScript){
                    rand=reelRng0;
                }
                data.getStopsIndices()[0] = calculateIndice(rand, weightFreeR1);

                rand = random.random(0,totalWeightFreeR2);
                if (isScript){
                    rand=reelRng1;
                }
                data.getStopsIndices()[1] = calculateIndice(rand, weightFreeR2);

                if (data.getStopsIndices()[0] == p1Stop[0] && data.getStopsIndices()[1] == p1Stop[1]) {
                    if (data.getSelections() == PAYLINE_SELECTIONS[0]) {
                        rand = random.random(0,totalWeightMaxBetJackpotFree);
                        if (isScript){
                            rand=reelRng2;
                        }
                        data.getStopsIndices()[2] = calculateIndice(rand, weightMaxBetFreeR3);
                    } else {
                        rand = random.random(0,totalWeightNoMaxBetJackpotFree);
                        if (isScript){
                            rand=reelRng2;
                        }
                        data.getStopsIndices()[2] = calculateIndice(rand, weightNoMaxBetFreeR3);
                    }
                } else {
                    rand = random.random(0,totalWeightFreeR3);
                    if (isScript){
                        rand=reelRng2;
                    }
                    data.getStopsIndices()[2] = calculateIndice(rand, weightFreeR3);
                }
            }
        }
    }

    private int calculateIndice(long rand, long[] weightArr) {
        int indice;
        for (indice = 0; indice < weightArr.length; indice++) {
            if (weightArr[indice] > rand) {
                break;
            }
        }
        indice -= 1;
        if (indice < 0) {
            indice = weightArr.length - 1;
        }

        indice -= 1;
        if (indice < 0) {
            indice = weightArr.length - 1;
        }

        return indice;
    }

    private void populateStops(MathData data) {
        data.setStops(new int[NUM_SYMBOLS_PER_SCREEN]);

        int[][] reelStrip = this.currentReelSet.gameReels;

        int stopIndex;
        for (int reel = 0; reel < NUM_REELS; reel++) {
            stopIndex = data.getStopsIndices()[reel];
            // Get 4 symbols per reel.
            for (int row = 0; row < NUM_ROWS; row++) {
                int symbol = reelStrip[reel][stopIndex];
                data.getStops()[reel + (NUM_REELS * row)] = symbol;
                stopIndex++;
                if (stopIndex >= reelStrip[reel].length) {
                    stopIndex -= reelStrip[reel].length;
                }
            }
        }

        data.setValidStops(NUM_SYMBOLS_PER_SCREEN);
    }

    private void validate(MathData data) {
        log.info("getSelections " + data.getSelections());
        if (data.getSelections() > this.currentMaxPaylines) {
            throw new RuntimeException("Lines played is greater than currentMaxPaylines");
        }
        if (data.getCreditPerSelection() >
                this.getCurrentMaxCreditPerSelection()) {
            throw new RuntimeException("Credit played is greater than maxCreditPerSelection");
        }
        if (data.getSelections() > this.getCurrentMaxSelection() || data.getSelections() < 1) {
            throw new RuntimeException("Max lines being played exceeds configured max lines.");
        }
        data.setTotalWager(this.calculateBet(data.getSelections(), data.getCreditPerSelection(), 0, data.getBetCredits()));
        data.setValidSelections(MAX_LINES);
        data.setSelectionWinMultiplier(new int[MAX_MULTIPLIER_INDEXES]);

//        data.setWagerCategoryBet(new long[]{data.getTotalWager()});
        long[] wagCatBets = new long[NUMBER_WAGER_CATEGORIES];
        long totWager = data.getTotalWager();
        for(int i= 0; i < NUMBER_WAGER_CATEGORIES; i++)
        {
            if(totWager == i+1)
            {
                wagCatBets[i] = totWager;
            }
            else
            {
                wagCatBets[i] = 0;
            }
        }
        data.setWagerCategoryBet(wagCatBets);
    }

    @Override
    public void updateWager(MathData mathData) {
    }


    @Override
    public int[][] getPayTable(int selections, int creditPerSelection, int anteBetLevel, int betCredits) {
        int[][] ret = new int[9][150];

        int totalWager = calculateBet(selections, creditPerSelection, anteBetLevel, betCredits);
        // do base game first
        for (int i = 0; i < NUM_SYMBOLS; i++) {
            for (int j = 0; j < NUM_REELS; j++) {
                ret[i][j] = PAYTABLE[i][j] * creditPerSelection;
            }
        }
        return ret;
    }

    @Override
    public int calculateMaxWinPerPayline()
    {
        if(gameMode==SlotGameMode.StandAlone||gameMode==SlotGameMode.Linked) {
            return 4000;
        }
        else
        {
            return 10000;
        }
    }

    @Override
    public int getNumberOfReels() {
        return 0;
    }

    @Override
    public int getNumberOfRows() {
        return 0;
    }

    @Override
    public boolean isWaysGame() {
        return false;
    }

    @Override
    public int[] getSupportedDenominations() {
        return null;
    }

    @Override
    public boolean isMultiDenomSupported() {
        return false;
    }

    @Override
    public MathProgressiveType[] getProgressiveModes()
    {
        return availableProgressiveTypes;
    }


    @Override
    public boolean isProgressiveApplicable(int denom, int selections, int creditsPerSelection) {
        return true;
    }

    @Override
    public ProgressiveLevel[] getProgressiveLevels()
    {
        ProgressiveLevel[] result = null;
        if(availableProgressiveTypes[currentProgressiveTypeIndex] != MathProgressiveType.PROGRESSIVE_TYPE_NONE)
        {
            result = currentReelSet.wagerCategories[NUMBER_WAGER_CATEGORIES - 1].getProgressiveLevels();
        }
        return result;
    }

    private void logMath(MathData data) {
        // skip log to avoid low performance in simulation tests
        if (isSimulation) return;

        // only save to log if "math.log" file exists
        File file = new File("math.log");
        if (!file.exists()) return;

        StringBuilder sb = new StringBuilder();

        if (data.getFreeSpin()) {
            sb.append("Free Game Reels:");
        } else {
            sb.append("Base Game Reels:");
        }

        for (int reel = 0; reel < NUM_REELS; reel++) {
            if (reel != 0) sb.append(',');

            sb.append(SYMBOL_NAMES[data.getStops()[reel]]);
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
            out.write(sb.toString());
            out.newLine();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //+NEW_SAPC
    private void populateWinCategories(MathData data, List<Integer> winCats)
    {
        int[] winCategoryArray = new int[winCats.size()];
        int index = 0;
        for(Integer cat: winCats)
        {
            winCategoryArray[index++] = cat;
        }
        data.setWinCategoriesHit(winCategoryArray);
    }
}
