package org.innopolis.kuzymvas.renderers;

import org.innopolis.kuzymvas.cellular.CellularAutomata;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Класс рендерера конечного состояния клеточного автомата
 */
public class EndStepRenderer {

    private final CellularAutomata automata;
    private final int width;

    /**
     * Создает новый рендерер для заданного клеточного автомата
     *
     * @param automata - клеточный автомат
     */
    public EndStepRenderer(CellularAutomata automata) {
        this.automata = automata;
        List<Integer> dims = automata.getDimensions();
        if (dims.size() != 2) {
            throw new IllegalArgumentException("This renderer only works for 2D automatas");
        }
        this.width = dims.get(0);
    }

    /**
     * Проходит автоматом заданное число шагов от заданного начального состояния и рендерит итоговое состояния в выходной поток
     *
     * @param out          - целевой поток вывода
     * @param stepNumber   - число шагов
     * @param initialState - исходное состояние автомата
     */
    public void render(OutputStream out, int stepNumber, List<Boolean> initialState) {
        automata.initAutomata(initialState);
        for (int i = 0; i < stepNumber; i++) {
            automata.updateAutomata();
        }
        PrintStream printer = new PrintStream(out);
        List<Boolean> states = automata.getCurrentState();
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i)) {
                printer.print("O");
            } else {
                printer.print("_");
            }
            if ((i % width) == (width - 1)) {
                printer.println();
            }
        }
    }
}
