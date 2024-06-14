package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Relationship;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class SimpleDijkstra {

    private Map<Node<OSMIntersection, OSMRoadSegment>, DijkstraData> dijkstraDataMap = new HashMap<>();

    public List<List<Double>> run(Graph incomingGraph, Node<OSMIntersection, OSMRoadSegment> source, Node<OSMIntersection, OSMRoadSegment> target) {
        log.info("Executando Dijkstra de dois pontos com os nós de OSMID {} e {}", source.getData().getOsmid(), target.getData().getOsmid());
        Long start = System.currentTimeMillis();
        incomingGraph.getNodes().forEach((key, value) -> dijkstraDataMap.put(value, new DijkstraData()));
        calculateShortestPathFromSource(incomingGraph, source, target);
        dijkstraDataMap.get(target).getShortestPath().add(target);
        log.info("Algoritmo executou em: {} ms", System.currentTimeMillis() - start);
        return dijkstraDataMap.get(target).getShortestPath().stream()
                .map(node ->
                        List.of(node.getData().getLocation().y(), node.getData().getLocation().x())
                ).toList();
    }

    private Graph calculateShortestPathFromSource(Graph incomingGraph, Node<OSMIntersection, OSMRoadSegment> source, Node<OSMIntersection, OSMRoadSegment> target) {
        dijkstraDataMap.get(source).setDijkstraDistance(0.0);

        Set<Node<OSMIntersection, OSMRoadSegment>> settledNodes = new HashSet<>();
        Set<Node<OSMIntersection, OSMRoadSegment>> unsettledNodes = new HashSet<>();
        boolean destinationNodeFound = false;

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty() || !destinationNodeFound) {
            Node<OSMIntersection, OSMRoadSegment> currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node<OSMIntersection, OSMRoadSegment>, Relationship<OSMRoadSegment>> adjacencyPair : currentNode.getAdjascentNodes().entrySet()) {
                Node<OSMIntersection, OSMRoadSegment> adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue().getData().getLength();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                    if (adjacentNode.getData().getOsmid().equals(target.getData().getOsmid())) {
                        destinationNodeFound = true;
                        break;
                    }
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
            Double nodeDistance = dijkstraDataMap.get(node).getDijkstraDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private void calculateMinimumDistance(Node<OSMIntersection, OSMRoadSegment> evaluationNode, Double edgeWeight, Node<OSMIntersection, OSMRoadSegment> sourceNode) {
        Double sourceDistance = dijkstraDataMap.get(sourceNode).getDijkstraDistance();
        if (sourceDistance + edgeWeight < dijkstraDataMap.get(evaluationNode).getDijkstraDistance()) {
            dijkstraDataMap.get(evaluationNode).setDijkstraDistance(sourceDistance + edgeWeight);
            LinkedList<Node<OSMIntersection, OSMRoadSegment>> shortestPath = new LinkedList<>(dijkstraDataMap.get(sourceNode).getShortestPath());
            shortestPath.add(sourceNode);
            dijkstraDataMap.get(evaluationNode).setShortestPath(shortestPath);
        }
    }

}
