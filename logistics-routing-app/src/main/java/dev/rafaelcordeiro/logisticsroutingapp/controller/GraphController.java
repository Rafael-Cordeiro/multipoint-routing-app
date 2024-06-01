package dev.rafaelcordeiro.logisticsroutingapp.controller;

import dev.rafaelcordeiro.logisticsroutingapp.core.facade.GraphFacade;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;

public class GraphController {
    private GraphFacade graphFacade;

    public GraphController() {
        graphFacade = new GraphFacade();
    }

    public void runDijkstra() {
        BasicGraph graph = graphFacade.fetchBasicraph();
        var sourceNode = graph.getNodes().stream().findFirst();
        if (sourceNode.isPresent()) {
            BasicGraph returnedGraph = graphFacade.runDijkstra(graph, sourceNode.get());
            var destinationNode = returnedGraph.getNodes().stream().filter(node -> node.getName().equals("F")).findFirst();
            if (destinationNode.isPresent()) {
                destinationNode.get().getShortestPath().stream().map(BasicGraphNode::getName)
                        .reduce((node, node2) -> node.concat(" -> ").concat(node2))
                        .ifPresent(shortestPathView -> {
                            shortestPathView = shortestPathView.concat(" -> ").concat(destinationNode.get().getName());
                            System.out.println("Shortest path from node " +
                                    sourceNode.get().getName() +
                                    " to node " +
                                    destinationNode.get().getName() + ":\n" + shortestPathView);
                        });
            }
        }
    }

    public void testGeoGraphQuery() {
        graphFacade.testGeoGraphQuery();
    }

}
