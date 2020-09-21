package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.Cell;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Абстрактный класс прямогольного 2Д клеточного автомата с замкнутым полем
 */
public abstract class AbstractRectangularWraparoundAutomata implements CellularAutomata {

    private final static int[][] MOORE_DELTAS = {
            {-1, -1, 0, 1, 1, 1, 0, -1},
            {0, 1, 1, 1, 0, -1, -1, -1}
    };
    private final static int[][] VON_NEUMANN_DELTAS = {
            {-1, 0, 0, 1},
            {0, -1, 1, 0}
    };
    private final static int[][] EXTENDED_VON_NEUMANN_DELTAS = {
            {-2, -1, 0, 0, 2, 1, 0, 0},
            {0, 0, -2, -1, 0, 0, 2, 1}
    };
    protected final List<Cell> cells;
    private final int height;
    private final int width;

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
    public AbstractRectangularWraparoundAutomata(
            int width, int height,
            CellFactory factory, NeighborhoodType neighborhoodType) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Automata grid dimensions can't be negative");
        }
        this.height = height;
        this.width = width;
        this.cells = factory.createCells(width * height);

        int[][] deltas = new int[0][];
        switch (neighborhoodType) {
            case MOORE: {
                deltas = MOORE_DELTAS;
                break;
            }
            case VON_NEUMANN: {
                deltas = VON_NEUMANN_DELTAS;
                break;
            }
            case EXTENDED_VON_NEUMANN: {
                deltas = EXTENDED_VON_NEUMANN_DELTAS;
            }
        }

        for (int i = 0; i < height * width; i++) {
            initNeighborhood(i, deltas);
        }
    }

    @Override
    public void initAutomata(List<Boolean> initialStates) {
        if (initialStates.size() != width * height) {
            throw new IllegalArgumentException("Provided states list doesn't match automata grid size");
        }
        for (int i = 0; i < width * height; i++) {
            cells.get(i).setAlive(initialStates.get(i));
        }
    }

    @Override
    public final List<Boolean> getCurrentState() {
        List<Boolean> states = new ArrayList<>();
        for (Cell cell : cells) {
            states.add(cell.isAlive());
        }
        return states;
    }

    @Override
    public List<Integer> getDimensions() {
        return Arrays.asList(width, height);
    }

    /**
     * Возвращает клетку с указанными координатами по отношению к данной
     *
     * @param ownIndex - индекс исходной клетки
     * @param deltaX   - смещение по горизонтали до соседа
     * @param deltaY   - смещение по вертикали до соседа
     * @return - клетка-сосед
     */
    protected final Cell getNeighbor(int ownIndex, int deltaX, int deltaY) {
        if (ownIndex < 0 || ownIndex > cells.size()) {
            throw new IllegalArgumentException("Original cell index in list is out of bounds");
        }
        int ownX = ownIndex % width;
        int ownY = ownIndex / width;
        int neighborX = (ownX + deltaX) % width;
        if (neighborX < 0) {
            neighborX = width + neighborX;
        }
        int neighborY = (ownY + deltaY) % height;
        if (neighborY < 0) {
            neighborY = height + neighborY;
        }
        return cells.get(neighborY * width + neighborX);
    }

    /**
     * Задает данной клетке ее соседей, определяемых заданными смещениями
     *
     * @param ownIndex - индекс исходной клетки
     * @param deltas   - двумерный массив смещений: первое измерение - количество соседей,
     *                 второе = 2: смещения по Х и по Y соответственно.
     */
    private void initNeighborhood(int ownIndex, int[][] deltas) {
        List<Cell> neighborhood = new ArrayList<>();
        for (int i = 0; i < deltas[0].length; i++) {
            neighborhood.add(getNeighbor(ownIndex, deltas[0][i], deltas[1][i]));
        }
        cells.get(ownIndex).setNeighborhood(neighborhood);
    }
}
