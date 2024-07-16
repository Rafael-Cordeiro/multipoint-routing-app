package dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.IGraph;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class Graph implements IGraph {
    private Map<Long, Node> nodes;

    public Graph() {
        this.nodes = new HashMap();
    }
}
