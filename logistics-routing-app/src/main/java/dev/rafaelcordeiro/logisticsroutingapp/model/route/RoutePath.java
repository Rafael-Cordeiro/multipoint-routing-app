package dev.rafaelcordeiro.logisticsroutingapp.model.route;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
@AllArgsConstructor
public class RoutePath {
    private RoutePoint point;
    private LinkedList<Node<OSMIntersection, OSMRoadSegment>> line;
}
