package dev.rafaelcordeiro.logisticsroutingapp.executables;

import dev.rafaelcordeiro.logisticsroutingapp.controller.GraphController;

public class TestGeoGraphQuery {
    public static void main(String[] args) {
        Long millis = System.currentTimeMillis();
        GraphController graphController = new GraphController();
        graphController.testGeoGraphQuery();
        System.out.println("Total time (ms): " + (System.currentTimeMillis() - millis));
    }
}
