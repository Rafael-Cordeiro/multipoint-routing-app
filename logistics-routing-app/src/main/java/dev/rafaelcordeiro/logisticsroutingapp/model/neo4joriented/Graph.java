package dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Graph {
    private Map<Long, Node> nodes;

    public Graph() {
        this.nodes = new HashMap();
    }
}
