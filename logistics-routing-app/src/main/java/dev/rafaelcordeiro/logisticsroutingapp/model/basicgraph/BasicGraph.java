package dev.rafaelcordeiro.logisticsroutingapp.model.basicgraph;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BasicGraph {
    private Set<BasicGraphNode> nodes = new HashSet<>();

    public void addNode(BasicGraphNode nodeA) {
        nodes.add(nodeA);
    }
}
