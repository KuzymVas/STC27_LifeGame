package org.innopolis.kuzymvas.cellular.cells;

import java.util.ArrayList;
import java.util.List;

/**
 *  Абстрактный класс, реализующий типовое поведение клетки клеточного автомата:
 *  Собирает состояния клеток-соседей и передает их списком абстрактному методу, определяющему из них
 *  каково будет новое состоние данной клетки
 */
public abstract class AbstractCell implements Cell {

    private volatile boolean aliveState;
    private boolean nextState;
    private List<Cell> neighborhood;
    private final List<Boolean> states;

    public AbstractCell(boolean aliveAtStart) {
        this.aliveState = aliveAtStart;
        states = new ArrayList<>();
    }

    @Override
    public final void calculateNextState() {
        states.clear();
        for (Cell cell : neighborhood) {
            states.add(cell.isAlive());
        }
        nextState = nextStateFromNeighborhoodState(states);
    }

    @Override
    public void updateState() {
        aliveState = nextState;
    }

    @Override
    public final boolean isAlive() {
        return aliveState;
    }

    @Override
    public final void setNeighborhood(List<Cell> neighborhood) {
        this.neighborhood = new ArrayList<>(neighborhood);
    }

    @Override
    public void setAlive(boolean aliveState) {
        this.aliveState = aliveState;
    }

    /**
     * Вычисляет новое состояние клетки по состояниям ее соседей
     * @param states - состояния соседей клетки
     * @return - новое состояние клетки
     */
    protected abstract boolean nextStateFromNeighborhoodState(List<Boolean> states);
}
