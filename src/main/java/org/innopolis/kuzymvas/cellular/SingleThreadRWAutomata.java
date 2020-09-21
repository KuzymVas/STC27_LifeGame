package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.Cell;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;

/**
 * Класс однопоточного клеточного автомата на замкнутом прямоугольном поле
 */
public class SingleThreadRWAutomata extends  AbstractRectangularWraparoundAutomata {

    public SingleThreadRWAutomata(
            int width, int height, CellFactory factory, NeighborhoodType neighborhoodType) {
        super(width, height, factory, neighborhoodType);
    }

    @Override
    public void updateAutomata() {
        for (Cell cell: cells) {
            cell.calculateNextState();
        }
        for (Cell cell: cells) {
            cell.updateState();
        }
    }
}
