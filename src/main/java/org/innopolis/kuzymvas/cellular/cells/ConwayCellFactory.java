package org.innopolis.kuzymvas.cellular.cells;

import java.util.ArrayList;
import java.util.List;

/**
 *  Фабрика изначально мертвых клеток Конвея.
 */
public class ConwayCellFactory implements  CellFactory {

    @Override
    public Cell createCelL() {
        return new ConwayCell(false);
    }

    @Override
    public List<Cell> createCells(int amount) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            cells.add(createCelL());
        }
        return cells;
    }
}
