package dev.rafaelcordeiro.logisticsroutingapp.model.route;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
public class MultipointRoute<N extends Neo4jTag, R extends Neo4jTag> {
    private Pair<Address, Node<N, R>> source;
    private Pair<Address, Node<N, R>> destination;
    private Map<Node<N, R>, Pair<Address, LinkedList<Node<N, R>>>> paths = new LinkedHashMap<>();
}
