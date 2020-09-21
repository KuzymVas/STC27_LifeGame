package org.innopolis.kuzymvas.cellular.cells;

import java.util.List;

/**
 * Конкретная реализация абстрактной клетки. Использует правила клеток Конвея для обновления состояния:
 * - Клетка остается жива при 2 или 3 живых соседях.
 * - Клетка умирает при любом другом числе живых соседей
 * - Клетка оживает при 3 живых соседях
 * - Клетка остается мертва при любом другом числе живых соседей
 */
public class ConwayCell extends AbstractCell {

    public ConwayCell(boolean aliveAtStart) {
        super(aliveAtStart);
    }

    @Override
    protected boolean nextStateFromNeighborhoodState(List<Boolean> states) {
        int totalAliveNeighboors = 0;
        for (Boolean state : states) {
            if (state) {
                totalAliveNeighboors++;
            }
        }
        if (isAlive()) {
            return (totalAliveNeighboors == 2) || (totalAliveNeighboors == 3);
        } else {
            return (totalAliveNeighboors == 3);
        }
    }
}
