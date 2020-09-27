package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.Cell;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Класс многопоточного клеточного автомата, использующего Fork-Join pool для расчетов, на замкнутом прямоугольном поле
 */
public class ForkJoinRWAutomata extends AbstractRectangularWraparoundAutomata {

    /**
     * Создает новый автомат
     *
     * @param width            - ширина поля
     * @param height           - высота поля
     * @param factory          - фабрика для клеток
     * @param neighborhoodType - тип локального окружения клетки: по Муру - это 8 клеток вокруг,
     *                         по Вон Нейману - 4 ортогональных клетки вокруг,
     *                         Расширенный Вон Нейман - 8 ортогональных клеток, по две в каждую сторону
     */
    public ForkJoinRWAutomata(
            int width, int height, CellFactory factory,
            NeighborhoodType neighborhoodType) {
        super(width, height, factory, neighborhoodType);
    }

    /**
     * Настоящее обновление выполняется в задачах типаа RecursiveAction на потоках Fork-Join pool.
     * Этот метод лишь  последовательно запускает две фазы обновления, создаевая исходные две задачи
     */
    @Override
    public void updateAutomata() {
        CellPrepare cellPrepareTask = new CellPrepare(cells);
        ForkJoinPool.commonPool().invoke(cellPrepareTask);
        CellUpdate cellUpdateTask = new CellUpdate(cells);
        ForkJoinPool.commonPool().invoke(cellUpdateTask);
    }

    /**
     * Класс задачи для Fork-Join pool, выполняющей вычисление нового состояния клеток
     */
    static class CellPrepare extends RecursiveAction {
        private final static int THRESHOLD = 50;
        private final List<Cell> cells;

        /**
         *  Создает новую задачу для заданного списка клеток
         * @param cells - список клеток для вычисления нового состояния
         */
        public CellPrepare(List<Cell> cells) {
            this.cells = cells;
        }

        /**
         * Либо вычисляет новое состояние клеток напрямую, либо разбивает их список пополам,
         * порождая новые две задачи, если список слишком велик
         */
        @Override
        protected void compute() {
            if (cells.size() < THRESHOLD) {
                for (Cell cell : cells) {
                    cell.calculateNextState();
                }
            } else {
                CellPrepare left = new CellPrepare(cells.subList(0, cells.size() / 2));
                CellPrepare right = new CellPrepare(cells.subList(cells.size() / 2, cells.size()));
                left.fork();
                right.fork();
                right.join();
                left.join();
            }
        }
    }

    /**
     * Класс задачи для Fork-Join pool, выполняющей обновление состояния клеток
     */
    static class CellUpdate extends RecursiveAction {
        private final static int THRESHOLD = 100;
        private final List<Cell> cells;

        /**
         *  Создает новую задачу для заданного списка клеток
         * @param cells - список клеток для обновления
         */
        public CellUpdate(List<Cell> cells) {
            this.cells = cells;
        }

        /**
         * Либо обновляет состояние клеток напрямую, либо разбивает их список пополам,
         * порождая новые две задачи, если список слишком велик
         */
        @Override
        protected void compute() {
            if (cells.size() < THRESHOLD) {
                for (Cell cell : cells) {
                    cell.updateState();
                }
            } else {
                CellUpdate left = new CellUpdate(cells.subList(0, cells.size() / 2));
                CellUpdate right = new CellUpdate(cells.subList(cells.size() / 2, cells.size()));
                left.fork();
                right.fork();
                right.join();
                left.join();
            }
        }
    }
}