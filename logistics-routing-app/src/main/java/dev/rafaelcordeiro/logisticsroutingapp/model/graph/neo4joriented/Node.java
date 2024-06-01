package dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented;

import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Node<N extends Neo4jTag, R extends Neo4jTag> {
    private N data;
    private Map<Node<N, R>, Relationship<R>> adjascentNodes;
    private List<Node<N, R>> shortestPath = new LinkedList<>();
    private Double dijkstraDistance = Double.POSITIVE_INFINITY;

    public Node() {
        adjascentNodes = new HashMap<>();
    }

    public void addAdjascentNode(Node<N, R> adjascentNode, Relationship<R> relationship) {
        if (this.adjascentNodes == null) {
            this.adjascentNodes = new HashMap<>();
        }
        adjascentNodes.put(adjascentNode, relationship);
    }
}
