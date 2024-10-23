package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Relationship;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MultipointDijkstra {

    private Map<Node<OSMIntersection, OSMRoadSegment>, DijkstraData> dijkstraDataMap = new HashMap<>();
    private Map<Node<OSMIntersection, OSMRoadSegment>, Address> addressToNodes;

    public MultipointDijkstra(Map<Node<OSMIntersection, OSMRoadSegment>, Address> addressToNodes) {
        this.addressToNodes = addressToNodes;
    }

    public MultipointRoute<OSMIntersection, OSMRoadSegment> run(
            Graph graph,
            Node<OSMIntersection, OSMRoadSegment> source,
            List<Node<OSMIntersection, OSMRoadSegment>> intermediates,
            Node<OSMIntersection, OSMRoadSegment> destination) {
        log.info("Executando Dijkstra de dois pontos com os nós de OSMID {} e {}", source.getData().getOsmid(), destination.getData().getOsmid());
        Long start = System.currentTimeMillis();

//        TODO: Ajustar ordem de preenchimento de caminhos
        var multipointRoute = new MultipointRoute<OSMIntersection, OSMRoadSegment>();
        multipointRoute.setSource(Pair.of(addressToNodes.get(source), source));
        multipointRoute.setDestination(Pair.of(addressToNodes.get(destination), destination));
        iterateThroughNodes(multipointRoute, graph, source, intermediates, destination);

        log.info("Algoritmo executou em: {} ms", System.currentTimeMillis() - start);
        return multipointRoute;
    }

    /**
     * Função recursiva para montar caminho mínimo multiponto
     *
     * @param multipointRoute
     * @param graph
     * @param source
     * @param intermediates
     * @param destination
     */
    private void iterateThroughNodes(MultipointRoute<OSMIntersection, OSMRoadSegment> multipointRoute,
                                     Graph graph, Node<OSMIntersection, OSMRoadSegment> source,
                                     List<Node<OSMIntersection, OSMRoadSegment>> intermediates,
                                     Node<OSMIntersection, OSMRoadSegment> destination) {
        // reinicia mpeamento de nós x dados de dijkstra a cada iteração
        dijkstraDataMap = new HashMap<>();
        graph.getNodes().forEach((key, value) -> dijkstraDataMap.put(value, new DijkstraData()));

        // Executa dijkstra (popula mapa de nós x dados)
        calculateShortestPathFromSource(source, intermediates, destination);

        var opt = dijkstraDataMap.entrySet().stream().filter(entry ->
                        intermediates.contains(entry.getKey()))
                .min(Comparator.comparingDouble(entries -> entries.getValue().getDijkstraDistance()));

//      Prepara nova chamada recursiva se ainda houver nó intermediário
        if (opt.isPresent()) {
            var nextNode = opt.get().getKey();
            var data = opt.get().getValue();

            var nextIntermediates = new ArrayList<>(intermediates);
            nextIntermediates.remove(nextNode);

            dijkstraDataMap.get(nextNode).getShortestPath().add(nextNode);
            multipointRoute.getPaths().put(nextNode, Pair.of(addressToNodes.get(nextNode), data.getShortestPath()));
            iterateThroughNodes(multipointRoute, graph, nextNode, nextIntermediates, destination);
        } else {
            dijkstraDataMap.get(destination).getShortestPath().add(destination);
            multipointRoute.getPaths().put(
                    destination,
                    Pair.of(addressToNodes.get(destination), dijkstraDataMap.get(destination).getShortestPath()));
        }
    }


    private void calculateShortestPathFromSource(
            Node<OSMIntersection, OSMRoadSegment> source,
            List<Node<OSMIntersection, OSMRoadSegment>> intermediates,
            Node<OSMIntersection, OSMRoadSegment> destination) {

        dijkstraDataMap.get(source).setDijkstraDistance(0.0);

        Set<Node<OSMIntersection, OSMRoadSegment>> settledNodes = new HashSet<>();
        Set<Node<OSMIntersection, OSMRoadSegment>> unsettledNodes = new HashSet<>();

        // unvisitedNodes quando vazia ativa condição de parada e encerra a execução da etapa do algoritmo
        List<Long> unvisitedNodes = new ArrayList<>(intermediates.stream().map(it -> it.getData().getOsmid()).toList());
        unvisitedNodes.add(destination.getData().getOsmid());

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty() && !unvisitedNodes.isEmpty()) {
            Node<OSMIntersection, OSMRoadSegment> currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node<OSMIntersection, OSMRoadSegment>, Relationship<OSMRoadSegment>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node<OSMIntersection, OSMRoadSegment> adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue().getData().getLength();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                    unvisitedNodes.remove(adjacentNode.getData().getOsmid());
                }
            }
            settledNodes.add(currentNode);
        }
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
