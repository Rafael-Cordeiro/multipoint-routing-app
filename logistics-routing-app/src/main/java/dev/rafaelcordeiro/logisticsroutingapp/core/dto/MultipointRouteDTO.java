package dev.rafaelcordeiro.logisticsroutingapp.core.dto;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Triple;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MultipointRouteDTO<N extends Neo4jTag, R extends Neo4jTag> {
    private Pair<Address, N> source;
    private Pair<Address, N> destination;
    private List<Triple<N, Address, ArrayList<Point>>> paths = new ArrayList<>();

    public static MultipointRouteDTO<OSMIntersection, OSMRoadSegment> toDTO(MultipointRoute<OSMIntersection, OSMRoadSegment> object) {
        MultipointRouteDTO<OSMIntersection, OSMRoadSegment> dto = new MultipointRouteDTO<>();
        dto.setSource(Pair.of(object.getSource().getLeft(), object.getSource().getRight().getData()));
        dto.setDestination(Pair.of(object.getDestination().getLeft(), object.getDestination().getRight().getData()));

        object.getPaths().forEach((node, path) -> {
            var triple = new Triple<OSMIntersection, Address, ArrayList<Point>>();
            triple.setFirst(node.getData());
            triple.setSecond(path.getLeft());
            triple.setThird(path.getRight().stream().map(it -> it.getData().getLocation())
                    .collect(
                            Collectors.toCollection(ArrayList::new)
                    ));
            dto.getPaths().add(triple);
        });
        return dto;
    }
}
