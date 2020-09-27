package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.Cell;
import org.innopolis.kuzymvas.cellular.cells.CellFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class CellularAutomataTest {

    private static final int[][] AUTOMATA_DIMS = {
            {1, 1}, {10, 10}, {8, 5}, {5, 8}
    };

    private static final int SUFFICIENT_WIDTH = 5;
    private static final int SUFFICIENT_HEIGHT = 5;

    private static final int MOORE_AREA = 8;
    private static final int VON_NEUMANN_AREA = 4;
    private static final int EXTENDED_VON_NEUMANN_AREA =8;


    private final AutomataFactory.AutomataType type;
    private final String automataName;
    private Cell mockCell;
    private CellFactory mockFactory;

    private List<CellularAutomata> automatasMoore;
    private CellularAutomata automataVonNeumann;
    private CellularAutomata automataVonNeumannExtended;

    public CellularAutomataTest(AutomataFactory.AutomataType type, String automataName) {
        this.automataName = automataName;
        this.type = type;
    }

    @Parameterized.Parameters
    public static Collection automataToTest() {
        return Arrays.asList(new Object[][]{
                {AutomataFactory.AutomataType.SINGLE_RWA, "Singlethread automata"},
                {AutomataFactory.AutomataType.MULTI_RWA, "Multithread automata"},
                {AutomataFactory.AutomataType.FORK_RWA, "Fork-Join pool automata"}

        });
    }

    @Before
    public void setUp() {
        mockCell = Mockito.mock(Cell.class);
        mockFactory = Mockito.mock(CellFactory.class);
        Mockito.when(mockFactory.createCells(Mockito.anyInt())).thenAnswer((Answer<List<Cell>>) invocation -> {
            Object[] args = invocation.getArguments();
            List<Cell> list = new ArrayList<>();
            for (int i = 0; i < (int) args[0]; i++) {
                list.add(mockCell);
            }
            return list;
        });
        automatasMoore = new ArrayList<>();
        for (int[] dims : AUTOMATA_DIMS) {
            automatasMoore.add(AutomataFactory.createAutomata(
                    type, dims[0], dims[1], mockFactory, NeighborhoodType.MOORE
            ));
        }
        automataVonNeumann = AutomataFactory.createAutomata(
                type, SUFFICIENT_WIDTH, SUFFICIENT_HEIGHT, mockFactory, NeighborhoodType.VON_NEUMANN
        );
        automataVonNeumannExtended = AutomataFactory.createAutomata(
                type, SUFFICIENT_WIDTH, SUFFICIENT_HEIGHT, mockFactory, NeighborhoodType.EXTENDED_VON_NEUMANN
        );
    }

    @Test
    public void testGetDims() {
        for (int i = 0; i < automatasMoore.size(); i++) {
            List<Integer> dims = automatasMoore.get(i).getDimensions();
            Assert.assertEquals(automataName + " returned wrong width", AUTOMATA_DIMS[i][0], dims.get(0).intValue());
            Assert.assertEquals(automataName + " returned wrong height", AUTOMATA_DIMS[i][1], dims.get(1).intValue());
        }
        List<Integer> dimsVN = automataVonNeumann.getDimensions();
        Assert.assertEquals(automataName + " returned wrong width", SUFFICIENT_WIDTH, dimsVN.get(0).intValue());
        Assert.assertEquals(automataName + " returned wrong height", SUFFICIENT_HEIGHT, dimsVN.get(1).intValue());

        List<Integer> dimsVNE = automataVonNeumannExtended.getDimensions();
        Assert.assertEquals(automataName + " returned wrong width", SUFFICIENT_WIDTH, dimsVNE.get(0).intValue());
        Assert.assertEquals(automataName + " returned wrong height", SUFFICIENT_HEIGHT, dimsVNE.get(1).intValue());
    }

    @Test
    public void testInit() {
        for (CellularAutomata automata : automatasMoore) {
            List<Integer> dims = automata.getDimensions();
            List<Boolean> states = new ArrayList<>();
            for (int j = 0; j < dims.get(0) * dims.get(1); j++) {
                states.add(false);
            }
            automata.initAutomata(states);
            Mockito.verify(mockCell, Mockito.times(dims.get(0) * dims.get(1))).setAlive(Mockito.anyBoolean());
            Mockito.clearInvocations(mockCell);
        }
        List<Boolean> states = new ArrayList<>();
        for (int j = 0; j < SUFFICIENT_WIDTH*SUFFICIENT_HEIGHT; j++) {
            states.add(false);
        }
        automataVonNeumann.initAutomata(states);
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).setAlive(Mockito.anyBoolean());
        Mockito.clearInvocations(mockCell);
        automataVonNeumannExtended.initAutomata(states);
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).setAlive(Mockito.anyBoolean());
    }

    @Test
    public void testGet() {
        Mockito.when(mockCell.isAlive()).thenReturn(true);
        for (CellularAutomata automata : automatasMoore) {
            List<Integer> dims = automata.getDimensions();
            List<Boolean> states = automata.getCurrentState();
            Mockito.verify(mockCell, Mockito.times(dims.get(0) * dims.get(1))).isAlive();
            Assert.assertEquals("Automata returned wrong number of states", dims.get(0) * dims.get(1), states.size());
            for (Boolean state : states) {
                Assert.assertTrue("While all cells are alive, automata returned a dead cell state", state);
            }
            Mockito.clearInvocations(mockCell);
        }
        List<Boolean> statesVN = automataVonNeumann.getCurrentState();
        Mockito.verify(mockCell,Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).isAlive();
        Assert.assertEquals("Automata returned wrong number of states", SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH, statesVN.size());
        for(Boolean state: statesVN) {
            Assert.assertTrue("While all cells are alive, automata returned a dead cell state",state);
        }
        Mockito.clearInvocations(mockCell);
        List<Boolean> statesVNE = automataVonNeumannExtended.getCurrentState();
        Mockito.verify(mockCell,Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).isAlive();
        Assert.assertEquals("Automata returned wrong number of states", SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH, statesVNE.size());
        for(Boolean state: statesVNE) {
            Assert.assertTrue("While all cells are alive, automata returned a dead cell state",state);
        }
    }

    @Test
    public void tesUpdate() {
        Mockito.when(mockCell.isAlive()).thenReturn(true);
        for (CellularAutomata automata : automatasMoore) {
            List<Integer> dims = automata.getDimensions();
            automata.updateAutomata();
            Mockito.verify(mockCell, Mockito.times(dims.get(0) * dims.get(1))).calculateNextState();
            Mockito.verify(mockCell, Mockito.times(dims.get(0) * dims.get(1))).updateState();
            Mockito.clearInvocations(mockCell);
        }
        automataVonNeumann.updateAutomata();
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).calculateNextState();
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).updateState();
        Mockito.clearInvocations(mockCell);
        automataVonNeumannExtended.updateAutomata();
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).calculateNextState();
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_HEIGHT*SUFFICIENT_WIDTH)).updateState();
        Mockito.clearInvocations(mockCell);
    }

    @Test
    public void testNeighborhoods() {
        ArgumentCaptor<List<Cell>> captorMoore = ArgumentCaptor.forClass(List.class);
        Mockito.clearInvocations(mockCell);
        AutomataFactory.createAutomata(
                type, SUFFICIENT_WIDTH, SUFFICIENT_HEIGHT, mockFactory, NeighborhoodType.MOORE
        );
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_WIDTH*SUFFICIENT_HEIGHT))
                .setNeighborhood(captorMoore.capture());
        List<List<Cell>> capturesMoore = captorMoore.getAllValues();
        for(List<Cell> capture: capturesMoore ) {
            Assert.assertEquals("Wrong neighborhood size provided for a Moore neighborhood", MOORE_AREA, capture.size());
        }

        ArgumentCaptor<List<Cell>> captorVN = ArgumentCaptor.forClass(List.class);
        Mockito.clearInvocations(mockCell);
        AutomataFactory.createAutomata(
                type, SUFFICIENT_WIDTH, SUFFICIENT_HEIGHT, mockFactory, NeighborhoodType.VON_NEUMANN
        );
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_WIDTH*SUFFICIENT_HEIGHT))
                .setNeighborhood(captorVN.capture());
        List<List<Cell>> capturesVN = captorVN.getAllValues();
        for(List<Cell> capture: capturesVN ) {
            Assert.assertEquals("Wrong neighborhood size provided for a Vom Neumann neighborhood", VON_NEUMANN_AREA, capture.size());
        }

        ArgumentCaptor<List<Cell>> captorVNE = ArgumentCaptor.forClass(List.class);
        Mockito.clearInvocations(mockCell);
        AutomataFactory.createAutomata(
                type, SUFFICIENT_WIDTH, SUFFICIENT_HEIGHT, mockFactory, NeighborhoodType.EXTENDED_VON_NEUMANN
        );
        Mockito.verify(mockCell, Mockito.times(SUFFICIENT_WIDTH*SUFFICIENT_HEIGHT))
                .setNeighborhood(captorVNE.capture());
        List<List<Cell>> capturesVNE = captorVNE.getAllValues();
        for(List<Cell> capture: capturesVNE ) {
            Assert.assertEquals("Wrong neighborhood size provided for a extended Von Neumann neighborhood",
                                EXTENDED_VON_NEUMANN_AREA, capture.size());
        }

    }
}