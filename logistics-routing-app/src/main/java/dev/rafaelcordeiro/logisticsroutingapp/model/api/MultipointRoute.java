package dev.rafaelcordeiro.logisticsroutingapp.model.api;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
public class MultipointRoute<N extends Neo4jTag, R extends Neo4jTag> {
    private Node<N, R> source;
    private Node<N, R> destination;
    private Map<Node<N, R>, LinkedList<Node<N, R>>> paths = new HashMap<>();
}
