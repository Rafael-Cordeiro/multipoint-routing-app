package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.astar;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.bettergraph.GraphNode;

@Deprecated
public interface Scorer <T extends GraphNode> {
    double computeCost(T from, T to);
}
