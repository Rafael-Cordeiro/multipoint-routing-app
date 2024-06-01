package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
    
    public BasicGraph run(BasicGraph incomingGraph, BasicGraphNode source) {
        Long start = System.currentTimeMillis();
        BasicGraph returnedGraph = calculateShortestPathFromSource(incomingGraph, source);
        Long end = System.currentTimeMillis();
        System.out.println("Algoritmo executou em: " + (end - start) + " milissegundos");
        return returnedGraph;
    }
    
    private BasicGraph calculateShortestPathFromSource(BasicGraph incomingGraph, BasicGraphNode source) {
        source.setDijkstraDistance(0);

        Set<BasicGraphNode> settledNodes = new HashSet<>();
        Set<BasicGraphNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            BasicGraphNode currentNode = this.getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<BasicGraphNode, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                BasicGraphNode adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return incomingGraph;
    }

    private BasicGraphNode getLowestDistanceNode(Set<BasicGraphNode> unsettledNodes) {
        BasicGraphNode lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (BasicGraphNode node : unsettledNodes) {
            int nodeDistance = node.getDijkstraDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private void calculateMinimumDistance(BasicGraphNode evaluationNode, Integer edgeWeight, BasicGraphNode sourceNode) {
        Integer sourceDistance = sourceNode.getDijkstraDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDijkstraDistance()) {
            evaluationNode.setDijkstraDistance(sourceDistance + edgeWeight);
            LinkedList<BasicGraphNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
