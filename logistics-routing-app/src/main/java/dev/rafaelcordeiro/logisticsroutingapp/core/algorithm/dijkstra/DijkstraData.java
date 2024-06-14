package dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class DijkstraData {
    private List<Node<OSMIntersection, OSMRoadSegment>> shortestPath;
    private Double dijkstraDistance;

    public DijkstraData() {
        this.shortestPath = new LinkedList<>();
        this.dijkstraDistance = Double.POSITIVE_INFINITY;
    }
}
