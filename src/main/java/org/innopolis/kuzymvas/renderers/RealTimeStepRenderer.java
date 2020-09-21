package org.innopolis.kuzymvas.renderers;

import org.innopolis.kuzymvas.cellular.CellularAutomata;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Класс рендерера состояний клеточного автомата в реальном времени
 */
public class RealTimeStepRenderer implements Runnable {

     private final CellularAutomata automata;
     private final int delay;
     private final PrintStream out;
     private final int width;
     private int stepNumber;

    /**
     *  Создает новый рендерер с заданными параметрами
     * @param automata - автомат, состояния которого будут выводиться рендерером
     * @param out - поток для вывода состояний
     * @param delay - минимальная задержка в миллисекундах между выводами состояний
     * @param stepNumber - число шагов, которое нужно вывести
     */
    public RealTimeStepRenderer(CellularAutomata automata, OutputStream out, int delay,
                                int stepNumber) {
        List<Integer> dims = automata.getDimensions();
        if (dims.size() !=  2) {
            throw new IllegalArgumentException("This renderer only works for 2D automatas");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Задержка между рендером шагов не может быть меньше нуля");
        }
        if (stepNumber < 0) {
            throw new IllegalArgumentException("Число шагов для рендера не может быть меньше нуля");
        }
        this.width = dims.get(0);
        this.automata = automata;
        this.delay = delay;
        this.out = new PrintStream(out);
        this.stepNumber = stepNumber;
    }

    /**
     * Выполняет рендер заданного числа шагов.
     */
    @Override
    public void run() {
        while(stepNumber >= 0) {
            out.print("\033[2J");
            List<Boolean> states = automata.getCurrentState();
            for (int i = 0; i < states.size(); i++) {
                if(states.get(i)){
                    out.print("O");
                } else{
                    out.print("_");
                }
                if((i % width) == (width-1)) {
                    out.println();
                }
            }
            out.println();
            automata.updateAutomata();
            stepNumber--;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
