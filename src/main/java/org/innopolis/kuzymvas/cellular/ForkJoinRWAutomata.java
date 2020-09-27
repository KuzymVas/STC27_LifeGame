package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.Cell;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ForkJoinRWAutomata extends AbstractRectangularWraparoundAutomata {

    CyclicBarrier barrier;
    boolean isOkay;
    List<Thread> updatersThread;

    /**
     * Создает новый автомат
     *
     * @param width            - ширина поля
     * @param height           - высота поля
     * @param factory          - фабрика для клеток
     * @param neighborhoodType - тип локального окружения клетки: по Муру - это 8 клеток вокруг,
     *                         по Вон Нейману - 4 ортогональных клетки вокруг,
     *                         Расширенный Вон Нейман - 8 ортогональных клеток, по две в каждую сторону
     * @param threadsNumber    - число потоков для использования в обновлениях
     */
    public ForkJoinRWAutomata(
            int width, int height, CellFactory factory,
            NeighborhoodType neighborhoodType, int threadsNumber) {
        super(width, height, factory, neighborhoodType);
        isOkay = true;
        barrier = new CyclicBarrier(threadsNumber + 1);
        updatersThread = new ArrayList<>();
        int share = cells.size() / threadsNumber;
        for (int i = 0; i < threadsNumber - 1; i++) {
            List<Cell> currThreadCells = cells.subList(i * share, (i + 1) * share);
            CellUpdater updater = new CellUpdater(currThreadCells, barrier);
            Thread updaterThread = new Thread(updater);
            updaterThread.setDaemon(true);
            updaterThread.start();
            updatersThread.add(updaterThread);
        }
        List<Cell> lastThreadCells = cells.subList((threadsNumber - 1) * share, cells.size());
        CellUpdater updater = new CellUpdater(lastThreadCells, barrier);
        Thread updaterThread = new Thread(updater);
        updaterThread.setDaemon(true);
        updaterThread.start();
        updatersThread.add(updaterThread);
    }

    /**
     * Настоящее обновление выполняется в других потоках. Этот метод лишь управляет ими,
     * пропуская их через барьер по мере выполнения фаз обновления.
     */
    @Override
    public void updateAutomata() {
        if (!isOkay) {
            return;
        }
        try {
            barrier.await();
            barrier.await();
            barrier.await();
        } catch (InterruptedException e) {
            System.out.println("Attempt to interrupt main automata thread. Shutting down automata");
            shutDown();
        } catch (BrokenBarrierException e) {
            System.out.println("Automata cyclic barrier was broken. Shutting down automata");
            shutDown();
        }
    }

    /**
     * Прерывает потоки обновления и выключает возможность обновления у автомата
     */
    private void shutDown() {
        for (Thread updaterThread : updatersThread) {
            updaterThread.interrupt();
        }
        isOkay = false;
    }

    /**
     * Класс обновителя клеток для многопоточного автомата
     */
    private static class CellUpdater implements Runnable {

        private final List<Cell> cells;
        private final CyclicBarrier barrier;

        /**
         * Создает новый обновитель
         *
         * @param cells   - клетки, которые следует обновлять
         * @param barrier - барьер для синхронизации потоков
         */
        public CellUpdater(List<Cell> cells, CyclicBarrier barrier) {
            this.cells = cells;
            this.barrier = barrier;
        }

        /**
         * Выполняет фазы обновления до тех пор, пока поток не будет прерван.
         * Фазы разделены барьерами:
         * - фаза простоя, пока обновление не начато
         * - фаза перерасчета состояния
         * - фаза записи перерасчитанных состояний
         * - фаза простоя, пока обновление не начато...
         */
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    barrier.await();
                    for (Cell cell : cells) {
                        cell.calculateNextState();
                    }
                    barrier.await();
                    for (Cell cell : cells) {
                        cell.updateState();
                    }
                    barrier.await();
                }
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
        }
    }
}