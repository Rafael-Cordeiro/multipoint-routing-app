package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Relationship;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SingleDijkstra {

    public Graph run(Graph incomingGraph, Node<OSMIntersection, OSMRoadSegment> source) {
        Long start = System.currentTimeMillis();
        Graph returnedGraph = calculateShortestPathFromSource(incomingGraph, source);
        Long end = System.currentTimeMillis();
        System.out.println("Algoritmo executou em: " + (end - start) + " milissegundos");
        return returnedGraph;
    }

    private Graph calculateShortestPathFromSource(Graph incomingGraph, Node<OSMIntersection, OSMRoadSegment> source) {
        source.setDijkstraDistance(0.0);

        Set<Node<OSMIntersection, OSMRoadSegment>> settledNodes = new HashSet<>();
        Set<Node<OSMIntersection, OSMRoadSegment>> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node<OSMIntersection, OSMRoadSegment> currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node<OSMIntersection, OSMRoadSegment>, Relationship<OSMRoadSegment>> adjacencyPair : currentNode.getAdjascentNodes().entrySet()) {
                Node<OSMIntersection, OSMRoadSegment> adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue().getData().getLength();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return incomingGraph;
    }

    private Node<OSMIntersection, OSMRoadSegment> getLowestDistanceNode(Set<Node<OSMIntersection, OSMRoadSegment>> unsettledNodes) {
        Node<OSMIntersection, OSMRoadSegment> lowestDistanceNode = null;
        Double lowestDistance = Double.POSITIVE_INFINITY;
        for (Node<OSMIntersection, OSMRoadSegment> node : unsettledNodes) {
            Double nodeDistance = node.getDijkstraDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private void calculateMinimumDistance(Node<OSMIntersection, OSMRoadSegment> evaluationNode, Double edgeWeight, Node<OSMIntersection, OSMRoadSegment> sourceNode) {
        Double sourceDistance = sourceNode.getDijkstraDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDijkstraDistance()) {
            evaluationNode.setDijkstraDistance(sourceDistance + edgeWeight);
            LinkedList<Node<OSMIntersection, OSMRoadSegment>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

}