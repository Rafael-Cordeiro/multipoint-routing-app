package dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Graph {
    private Map<Long, Node> nodes;

    public Graph() {
        this.nodes = new HashMap();
    }
}
