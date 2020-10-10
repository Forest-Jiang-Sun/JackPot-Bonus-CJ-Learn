/*
package com.aspectgaming.math.luckydog;

import com.aspectgaming.math.GamingMath;
import com.aspectgaming.math.MathData;
import com.aspectgaming.math.simulation.MathSimulator;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.aspectgaming.math.luckydog.LuckyDogMathConstants.*;

*/
/*
 * @author ligang.yao
 *//*


public class MathSimulationTest {

    private static final int CYCLE_SIZE = 10_000_000;
    private static final int[] DENOMS = { 10 };
    private static final int[] SELECTIONS = { 30, 15, 10, 5, 1 };
    private static final int[] BET_MULTIPLIERS = { 1 };

    @Test
    public void test() {
        // use all CPU cores to speed up simulation
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(numProcessors);

        GamingMath math = new LuckyDogMath(true);
        String[] reelSets = math.getConfigurableReelSetNames();

        for (final String reelSet : reelSets) {
            for (final int denom : DENOMS) {
                for (final int selection : SELECTIONS) {
                    for (final int betMultiplier : BET_MULTIPLIERS) {
                        exec.execute(new Runnable() {
                            public void run() {
                                testBetChoice(reelSet, denom, selection, betMultiplier);
                            }
                        });
                    }
                }
            }
        }

        // wait all threads completed
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {}
    }

    private void testBetChoice(String reelSet, int denom, int selection, int betMultiplier) {
        GamingMath math = new LuckyDogMath(true);
        MathData data = new MathData();

        data.setGameDenomination(denom);
        data.setSelections(selection);
        data.setCreditPerSelection(betMultiplier);

        math.initialize(data, null);
        math.setCurrentReelSet(reelSet);
        math.setCurrentMaxCreditPerSelection(MAX_CONFIGURABLE_CPS);
        math.setCurrentMaxSelection(MAX_LINES);

        MathSimulator simulator = new MathSimulator(math, data);
        simulator.simulate(CYCLE_SIZE);

        // avoid concurrent println
        synchronized (this) {
            System.out.println(simulator.report());
        }
    }
}
*/
