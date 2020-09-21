package org.innopolis.kuzymvas.cellular.cells;

import java.util.List;

/**
 * Интерфейс фабрики клеток для клеточного автомата
 */
public interface CellFactory {

    /**
     * Создает новую клетку
     * @return - вновь созданная клетка
     */
    Cell createCelL();

    /**
     * Создает заданное количество клеток
     * @param amount - требуемое количество клеток
     * @return - список вновь созданных клеток
     */
    List<Cell> createCells(int amount);
}
