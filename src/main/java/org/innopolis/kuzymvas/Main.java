package org.innopolis.kuzymvas;

import org.innopolis.kuzymvas.cellular.CellularAutomata;
import org.innopolis.kuzymvas.cellular.MultiThreadRWAutomata;
import org.innopolis.kuzymvas.cellular.NeighborhoodType;
import org.innopolis.kuzymvas.cellular.SingleThreadRWAutomata;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;
import org.innopolis.kuzymvas.cellular.cells.ConwayCellFactory;
import org.innopolis.kuzymvas.renderers.EndStepRenderer;
import org.innopolis.kuzymvas.renderers.RealTimeStepRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int RENDER_DELAY = 1000;

    public static void main(String[] args) {
        ParsedArgs parsedArgs = parseArgs(args);
        if (!parsedArgs.valid) {
            return;
        }
        InitialState initialState = readInitialState(parsedArgs.inputFile);
        if (initialState == null || initialState.width == 0 || initialState.height == 0) {
            return;
        }
        CellularAutomata automata;
        CellFactory factory = new ConwayCellFactory();
        if (parsedArgs.singleThread) {
            automata = new SingleThreadRWAutomata(initialState.width, initialState.height, factory,
                                                  NeighborhoodType.MOORE);
        } else {
            int threadsToMake = Math.max(Runtime.getRuntime().availableProcessors(), 2);
            automata = new MultiThreadRWAutomata(initialState.width, initialState.height, factory,
                                                 NeighborhoodType.MOORE, threadsToMake);
        }
        if (parsedArgs.realTime) {
            automata.initAutomata(initialState.states);
            RealTimeStepRenderer renderer = new RealTimeStepRenderer(automata, System.out,
                                                                     RENDER_DELAY, parsedArgs.stepNumber);
            renderer.run();
        } else {
            try (OutputStream out = new FileOutputStream(parsedArgs.outputFile)) {
                EndStepRenderer renderer = new EndStepRenderer(automata);
                renderer.render(out, parsedArgs.stepNumber, initialState.states);
            } catch (FileNotFoundException e) {
                System.out.println("Error. Output file not found. Aborting");
            } catch (IOException e) {
                System.out.println("Error. IO exception, while writing output file: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Считывает исходное состояние автомата из файла
     *
     * @param inputFile - путь к исходному файлу
     * @return - исходное состояние автомата или null, если при чтении возникли проблемы
     */
    private static InitialState readInitialState(String inputFile) {
        int width = -1;
        int height = 0;
        List<Boolean> states = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
            String line = br.readLine();
            while (line != null && line.length() != 0) {
                char[] chars = line.toCharArray();
                for (char c : chars) {
                    states.add(!(c == ' ' || c == '_'));
                }
                if (width == -1) {
                    width = line.length();
                }
                if (width != line.length()) {
                    System.out.println("Error. Initial file is malformed. Board should be rectangular. Aborting");
                    return null;
                }
                height++;
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error. Input file not found. Aborting");
            return null;
        } catch (IOException e) {
            System.out.println("Error. IO exception, while reading input file: " + e.getLocalizedMessage());
            return null;
        }
        return new InitialState(width, height, states);
    }

    /**
     * Разбирает данные аргументы командной строки
     *
     * @param args - массив аргументов командной строки в виде строк
     * @return - структура распознанных аргументов
     */
    private static ParsedArgs parseArgs(String[] args) {
        boolean valid = true;
        String inputFile = "";
        String outputFile = "";
        int stepNumber = 0;
        boolean singleThread = false;
        boolean realTime = false;
        if (args.length < 1) {
            System.out.println("No arguments provided. Aborting");
            return new ParsedArgs(valid, inputFile, outputFile, stepNumber, singleThread, realTime);
        }
        if (args[0].equals("-help")
                || args[0].equals("-h")
                || args[0].equals("?")) {
            valid = false;
            outputHelp();
        } else {
            if (args.length == 3 || args.length == 4) {
                inputFile = args[0];
                if (args[1].equals("-realtime")) {
                    realTime = true;
                } else {
                    outputFile = args[1];
                }
                try {
                    stepNumber = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    valid = false;
                    System.out.println(
                            "Error. Parsing step number argument failed. Exception: " + e.getLocalizedMessage());
                }
                if (stepNumber < 0) {
                    valid = false;
                    System.out.println("Error. Negative value given for a step number argument. Aborting");
                }
                if (args.length == 4) {
                    switch (args[3]) {
                        case "-single": {
                            singleThread = true;
                            break;
                        }
                        case "-multi": {
                            singleThread = false;
                            break;
                        }
                        default: {
                            valid = false;
                            System.out.println(
                                    "Error. Invalid value for a fourth argument. Only '-single' and '-multi' are accepted.");
                        }
                    }
                }
            } else {
                valid = false;
                System.out.println("Wrong number of input arguments. Only 3 or 4 arguments are allowed. Aborting");
            }
        }
        return new ParsedArgs(valid, inputFile, outputFile, stepNumber, singleThread, realTime);
    }

    /**
     * Выводит в консоль справку по программе
     */
    private static void outputHelp() {
        System.out.println("Application reads initial state of cellular Convey automata from the input file");
        System.out.println("and writes automata state after given number of steps to the output file.");
        System.out.println("Arguments:");
        System.out.println(" 1) Input file name");
        System.out.println(" 2) Output file name or '-realtime' to instead render every step to the console output");
        System.out.println(" 2) Number of steps");
        System.out.println(
                " 4) (OPTIONAL) '-single' or '-multi'(default) to use either singlethread or multithread versions of automata");
    }

    /**
     * Структруа распознанных аргументов командной строки
     */
    private static class ParsedArgs {
        final boolean valid;
        final String inputFile;
        final String outputFile;
        final int stepNumber;
        final boolean singleThread;
        final boolean realTime;

        /**
         * Создает новую структуру, содердащую распознанные аргументы программы
         *
         * @param valid        - были ли аргументы валидны и нужно ли исполнять программу дальше
         * @param inputFile    - пусть к входному файлу
         * @param outputFile   - путь к выходному файлу
         * @param stepNumber   - число шагов для автомата
         * @param singleThread - нужно ли использовать однопоточный режим
         * @param realTime     - был ли запрошен вывод в реальном времени в консоль
         */
        public ParsedArgs(
                boolean valid, String inputFile, String outputFile,
                int stepNumber, boolean singleThread, boolean realTime) {
            this.valid = valid;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.stepNumber = stepNumber;
            this.singleThread = singleThread;
            this.realTime = realTime;
        }
    }

    /**
     * Структура,, описывающая начальное состояние автомата
     */
    private static class InitialState {
        final int width;
        final int height;
        final List<Boolean> states;

        /**
         * Создает новую структуру описания начального состояния автомата
         *
         * @param width  - ширина поля автомата
         * @param height - высота поля автомтаа
         * @param states - список начальных состояний клеток автомата
         */
        public InitialState(int width, int height, List<Boolean> states) {
            this.width = width;
            this.height = height;
            this.states = states;
        }
    }
}
