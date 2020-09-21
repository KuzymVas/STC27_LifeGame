package org.innopolis.kuzymvas.cellular;

import org.innopolis.kuzymvas.cellular.cells.CellFactory;

public class AutomataFactory {

    public static CellularAutomata createAutomata(AutomataType type, int width, int height,
                                                  CellFactory factory, NeighborhoodType neighborhoodType) {
       switch (type) {
           case SINGLE_RWA: {
               return new SingleThreadRWAutomata(width, height, factory, neighborhoodType);
           }
           case MULTI_RWA: {
               return null;
           }
           default: {
               return null;
           }
       }
    }

    public enum AutomataType {
        SINGLE_RWA,
        MULTI_RWA
    }
}
