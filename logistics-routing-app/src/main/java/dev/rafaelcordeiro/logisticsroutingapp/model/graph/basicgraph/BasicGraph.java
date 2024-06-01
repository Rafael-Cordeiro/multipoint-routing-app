package dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph;

import dev.rafaelcordeiro.logisticsroutingapp.model.graph.IGraph;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BasicGraph implements IGraph {
    private Set<BasicGraphNode> nodes = new HashSet<>();

    public void addNode(BasicGraphNode nodeA) {
        nodes.add(nodeA);
    }
}
