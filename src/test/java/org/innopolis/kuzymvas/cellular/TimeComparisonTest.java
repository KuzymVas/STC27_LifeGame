package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.CellFactory;
import org.innopolis.kuzymvas.cellular.cells.ConwayCellFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TimeComparisonTest {

    private final static int RUNS  = 20;
    private final static int[] SIZES = {50, 100, 150};
    private final static int STEPS = 200;

    @Test
    public  void timeComparisonTest() {
        CellFactory factory = new ConwayCellFactory();
        for(int size: SIZES) {

            List<Boolean> states = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (i % 5 == 2) {
                        if (j % 5 > 0 && j % 5 < 4) {
                            states.add(true);
                        }
                        else {
                            states.add(false);
                        }
                    } else {
                        states.add(false);
                    }
                }
            }
            CellularAutomata single = new SingleThreadRWAutomata(size, size, factory, NeighborhoodType.MOORE);
            long summaryTimeSinge = 0;
            for (int i = 0; i < RUNS; i++) {
                single.initAutomata(states);
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < STEPS; j++) {
                    single.updateAutomata();
                }
                summaryTimeSinge += System.currentTimeMillis() - startTime;
            }
            double avgTimeSingle = summaryTimeSinge/(1000.0*RUNS);

            int threadNumber = Math.max(Runtime.getRuntime().availableProcessors(), 2);
            CellularAutomata multi = new MultiThreadRWAutomata(size, size, factory, NeighborhoodType.MOORE, threadNumber);
            long summaryTimeMulti = 0;
            for (int i = 0; i < RUNS; i++) {
                multi.initAutomata(states);
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < STEPS; j++) {
                    multi.updateAutomata();
                }
                summaryTimeMulti += System.currentTimeMillis() - startTime;
            }
            double avgTimeMulti = summaryTimeMulti/(1000.0*RUNS);

            CellularAutomata forkJoin = new ForkJoinRWAutomata(size, size, factory, NeighborhoodType.MOORE, threadNumber);
            long summaryTimeFork= 0;
            for (int i = 0; i < RUNS; i++) {
                forkJoin.initAutomata(states);
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < STEPS; j++) {
                    forkJoin.updateAutomata();
                }
                summaryTimeFork += System.currentTimeMillis() - startTime;
            }
            double avgTimeFork = summaryTimeFork/(1000.0*RUNS);

            System.out.println("Average time for single-thread automata for " + RUNS + " runs of " + STEPS + " steps  on field of "
                                       + size + "^2 =" + avgTimeSingle);
            System.out.println("Average time for multi-thread automata for " + RUNS + " runs of " + STEPS + " steps  on field of "
                                       + size + "^2 =" + avgTimeMulti);
            System.out.println("Average time for fork-join pool based automata for " + RUNS + " runs of " + STEPS + " steps  on field of "
                                       + size + "^2 =" + avgTimeFork);

        }
    }

}
