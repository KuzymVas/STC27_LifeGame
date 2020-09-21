package org.innopolis.kuzymvas.cellular.cells;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class ConwayCellTest {

    Cell aliveMock, deadMock;

    @Before
    public void setUp() {
        aliveMock = Mockito.mock(Cell.class);
        Mockito.when(aliveMock.isAlive()).thenReturn(true);
        deadMock = Mockito.mock(Cell.class);
        Mockito.when(deadMock.isAlive()).thenReturn(false);
    }

    @Test
    public void testIsAlive() {
        Cell aliveCell = new ConwayCell(true);
        Cell deadCell = new ConwayCell(false);
        Assert.assertTrue("Cell that was created alive, reported itself dead", aliveCell.isAlive());
        Assert.assertFalse("Cell that was created dead, reported itself alive", deadCell.isAlive());
    }

    @Test
    public void testSetAlive() {
        Cell cell = new ConwayCell(true);
        cell.setAlive(false);
        Assert.assertFalse("Cell that was set dead, reported itself dead", cell.isAlive());
        cell.setAlive(true);
        Assert.assertTrue("Cell that was set alive, reported itself alive", cell.isAlive());
    }

    @Test
    public void testStayAlive() {
        List<Cell> stayAliveTwoNeighborhood = new ArrayList<>();
        stayAliveTwoNeighborhood.add(aliveMock);
        stayAliveTwoNeighborhood.add(deadMock);
        stayAliveTwoNeighborhood.add(aliveMock);
        stayAliveTwoNeighborhood.add(deadMock);
        Cell aliveCell = new ConwayCell(true);
        aliveCell.setNeighborhood(stayAliveTwoNeighborhood);
        aliveCell.calculateNextState();
        aliveCell.updateState();
        Mockito.verify(aliveMock, Mockito.times(2)).isAlive();
        Mockito.verify(deadMock, Mockito.times(2)).isAlive();
        Assert.assertTrue("Alive cell died while having two neighbors", aliveCell.isAlive());

        Mockito.clearInvocations(aliveMock, deadMock);
        List<Cell> stayAliveThreeNeighborhood = new ArrayList<>();
        stayAliveThreeNeighborhood.add(aliveMock);
        stayAliveThreeNeighborhood.add(aliveMock);
        stayAliveThreeNeighborhood.add(aliveMock);
        stayAliveThreeNeighborhood.add(deadMock);
        aliveCell.setNeighborhood(stayAliveThreeNeighborhood);
        aliveCell.calculateNextState();
        aliveCell.updateState();
        Mockito.verify(aliveMock, Mockito.times(3)).isAlive();
        Mockito.verify(deadMock, Mockito.times(1)).isAlive();
        Assert.assertTrue("Alive cell died while having tree neighbors", aliveCell.isAlive());
    }

    @Test
    public void testBecomeAlive() {
        List<Cell> becomeAliveThreeNeighborhood = new ArrayList<>();
        becomeAliveThreeNeighborhood.add(aliveMock);
        becomeAliveThreeNeighborhood.add(aliveMock);
        becomeAliveThreeNeighborhood.add(aliveMock);
        becomeAliveThreeNeighborhood.add(deadMock);
        Cell deadCell = new ConwayCell(false);
        deadCell.setNeighborhood(becomeAliveThreeNeighborhood);
        deadCell.calculateNextState();
        deadCell.updateState();
        Mockito.verify(aliveMock, Mockito.times(3)).isAlive();
        Mockito.verify(deadMock, Mockito.times(1)).isAlive();
        Assert.assertTrue("Dead cell didn't become alive while having three neighbors", deadCell.isAlive());
    }

    @Test
    public void testDieFromLoneliness() {
        List<Cell> dieOneNeighborhood = new ArrayList<>();
        dieOneNeighborhood.add(aliveMock);
        dieOneNeighborhood.add(deadMock);
        dieOneNeighborhood.add(deadMock);
        dieOneNeighborhood.add(deadMock);
        Cell aliveCell = new ConwayCell(true);
        aliveCell.setNeighborhood(dieOneNeighborhood);
        aliveCell.calculateNextState();
        aliveCell.updateState();
        Mockito.verify(aliveMock, Mockito.times(1)).isAlive();
        Mockito.verify(deadMock, Mockito.times(3)).isAlive();
        Assert.assertFalse("Alive cell stayed alive while having one neighbors", aliveCell.isAlive());

        Mockito.clearInvocations(aliveMock, deadMock);
        List<Cell> dieFourNeighborhood = new ArrayList<>();
        dieFourNeighborhood.add(deadMock);
        dieFourNeighborhood.add(deadMock);
        dieFourNeighborhood.add(deadMock);
        dieFourNeighborhood.add(deadMock);
        aliveCell = new ConwayCell(true);
        aliveCell.setNeighborhood(dieFourNeighborhood);
        aliveCell.calculateNextState();
        aliveCell.updateState();
        Mockito.verify(aliveMock, Mockito.times(0)).isAlive();
        Mockito.verify(deadMock, Mockito.times(4)).isAlive();
        Assert.assertFalse("Alive cell stayed alive while having zero neighbors", aliveCell.isAlive());
    }

    @Test
    public void testDieFromOvercrowding() {
        for (int neighborCount = 4; neighborCount < 9; neighborCount++) {
            List<Cell> neighborhood = new ArrayList<>();
            for (int i = 0; i < neighborCount; i++) {
                neighborhood.add(aliveMock);
            }
            for (int i = neighborCount; i < 8; i++) {
                neighborhood.add(deadMock);
            }
            Mockito.clearInvocations(aliveMock, deadMock);
            Cell aliveCell = new ConwayCell(true);
            aliveCell.setNeighborhood(neighborhood);
            aliveCell.calculateNextState();
            aliveCell.updateState();
            Mockito.verify(aliveMock, Mockito.times(neighborCount)).isAlive();
            Mockito.verify(deadMock, Mockito.times(8 - neighborCount)).isAlive();
            Assert.assertFalse("Alive cell stayed alive while having " + neighborCount + " neighbors",
                               aliveCell.isAlive());
        }
    }

    @Test
    public void testStayDead() {
        for (int neighborCount = 0; neighborCount < 9; neighborCount++) {
            if (neighborCount == 3) {
                continue;
            }
            List<Cell> neighborhood = new ArrayList<>();
            for (int i = 0; i < neighborCount; i++) {
                neighborhood.add(aliveMock);
            }
            for (int i = neighborCount; i < 8; i++) {
                neighborhood.add(deadMock);
            }
            Mockito.clearInvocations(aliveMock, deadMock);
            Cell deadCell = new ConwayCell(false);
            deadCell.setNeighborhood(neighborhood);
            deadCell.calculateNextState();
            deadCell.updateState();
            Mockito.verify(aliveMock, Mockito.times(neighborCount)).isAlive();
            Mockito.verify(deadMock, Mockito.times(8 - neighborCount)).isAlive();
            Assert.assertFalse("Dead cell become alive while having " + neighborCount + " neighbors",
                               deadCell.isAlive());
        }
    }
}