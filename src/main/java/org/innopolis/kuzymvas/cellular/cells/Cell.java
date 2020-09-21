package org.innopolis.kuzymvas.cellular.cells;

import java.util.List;

/**
 * Интерфейс клетки клеточного автомата
 */
public interface Cell {

    /**
     * Вычисляет новое состояние клетки, исходя из текущего состояния соседей, но не изменяет ее текущее состояние
     */
    void calculateNextState();

    /**
     * Заменяет текущее состояние клетки на последнее  вычисленное новое состояние
     */
    void updateState();

    /**
     * Возвращает статус клетки
     *
     * @return - true, если клетка жива, false - если мертва
     */
    boolean isAlive();

    /**
     * Задает состояние клетки
     *
     * @param isAlive - новое состояние клетки: жива или нет
     */
    void setAlive(boolean isAlive);

    /**
     * Задает список соседей клетки
     *
     * @param neighborhood - список соседей клетки
     */
    void setNeighborhood(List<Cell> neighborhood);
}
