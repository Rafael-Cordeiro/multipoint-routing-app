package dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class BasicGraphNode {

    private String name;
    private Long id;
    private Point location;
    private List<BasicGraphNode> shortestPath = new LinkedList<>();
    private Integer dijkstraDistance = Integer.MAX_VALUE;
    Map<BasicGraphNode, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(BasicGraphNode destination, int dijkstraDistance) {
        adjacentNodes.put(destination, dijkstraDistance);
    }

    public BasicGraphNode(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicGraphNode that)) return false;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getId(), that.getId()) && Objects.equals(getLocation(), that.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getLocation());
    }
}
