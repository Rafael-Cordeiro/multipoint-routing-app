package dev.rafaelcordeiro.logisticsroutingapp.executables;

import dev.rafaelcordeiro.logisticsroutingapp.controller.GraphController;

public class TestDijkstra {
    public static void main(String[] args) {
        GraphController graphController = new GraphController();
        graphController.runDijkstra();
    }
}
