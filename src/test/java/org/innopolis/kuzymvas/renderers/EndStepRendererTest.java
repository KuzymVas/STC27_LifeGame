package org.innopolis.kuzymvas.renderers;

import org.innopolis.kuzymvas.cellular.CellularAutomata;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndStepRendererTest {

    private final static List<Integer> DIMS_2D = Arrays.asList(5,5);
    private final static List<Integer> DIMS_3D = Arrays.asList(5,5,5);
    private final static List<Boolean> EMPTY = Collections.emptyList();
    private final static int STEPS = 10;

    @Test
    public void testConstructor() {
        CellularAutomata mockAutomata2D = Mockito.mock(CellularAutomata.class);
        Mockito.when(mockAutomata2D.getDimensions()).thenReturn(DIMS_2D);
        CellularAutomata mockAutomata3D = Mockito.mock(CellularAutomata.class);
        Mockito.when(mockAutomata3D.getDimensions()).thenReturn(DIMS_3D);
        new EndStepRenderer(mockAutomata2D);
        Mockito.verify(mockAutomata2D,Mockito.times(1)).getDimensions();
        try {
            new  EndStepRenderer(mockAutomata3D);
            Assert.fail("Renderer accepted 3D automata");
        } catch (IllegalArgumentException ignored){

        }
    }

    @Test
    public void testRenderProcess() {
        CellularAutomata mockAutomata2D = Mockito.mock(CellularAutomata.class);
        Mockito.when(mockAutomata2D.getDimensions()).thenReturn(DIMS_2D);
        Mockito.when(mockAutomata2D.getCurrentState()).thenReturn(EMPTY);
        OutputStream mockStream = Mockito.mock(OutputStream.class);
        new EndStepRenderer(mockAutomata2D).render(mockStream, STEPS, EMPTY);
        Mockito.verify(mockAutomata2D, Mockito.times(1)).initAutomata(EMPTY);
        Mockito.verify(mockAutomata2D, Mockito.times(STEPS)).updateAutomata();
        Mockito.verify(mockAutomata2D, Mockito.times(1)).getCurrentState();
    }

}