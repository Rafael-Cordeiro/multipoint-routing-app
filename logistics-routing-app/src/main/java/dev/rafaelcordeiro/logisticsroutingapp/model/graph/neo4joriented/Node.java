package dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented;

import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Node<N extends Neo4jTag, R extends Neo4jTag> {
    private N data;
    private Map<Node<N, R>, Relationship<R>> adjacentNodes;

    public Node() {
        adjacentNodes = new HashMap<>();
    }

    public void addAdjacentNode(Node<N, R> adjascentNode, Relationship<R> relationship) {
        if (this.adjacentNodes == null) {
            this.adjacentNodes = new HashMap<>();
        }
        adjacentNodes.put(adjascentNode, relationship);
    }
}
