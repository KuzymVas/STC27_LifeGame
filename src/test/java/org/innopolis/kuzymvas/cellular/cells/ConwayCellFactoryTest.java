package org.innopolis.kuzymvas.cellular.cells;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ConwayCellFactoryTest {

    @Test
    public void testCreate() {
        ConwayCellFactory factory = new ConwayCellFactory();
        Cell cell = factory.createCelL();
        Assert.assertFalse("Returned cell is alive instead of dead",cell.isAlive());
        Assert.assertTrue("Returned cell is not a ConwayCell",cell instanceof  ConwayCell);
    }

    @Test
    public void testCreateMultiple() {
        ConwayCellFactory factory = new ConwayCellFactory();
        int[] testAmounts = {0, 1, 5, 10000};
        for(int amount:testAmounts) {
            List<Cell> cells = factory.createCells(amount);
            Assert.assertEquals("Factory returned wrong number of cells for amount " + amount, amount, cells.size());
        }
    }

}