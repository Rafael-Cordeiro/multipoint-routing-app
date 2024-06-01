package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.astar;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.bettergraph.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.bettergraph.GraphNode;
import lombok.AllArgsConstructor;

import java.util.List;

@Deprecated
@AllArgsConstructor
public class RouteFinder <T extends GraphNode> {
    private final Graph<T> graph;
    private final Scorer<T> nextNodeScorer;
    private final Scorer<T> targetScorer;

    public List<T> findRoute(T from, T to) {
        throw new IllegalStateException("No route found");
    }
}
