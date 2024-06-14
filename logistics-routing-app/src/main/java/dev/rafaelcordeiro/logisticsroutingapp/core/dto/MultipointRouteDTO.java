package dev.rafaelcordeiro.logisticsroutingapp.core.dto;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class MultipointRouteDTO<N extends Neo4jTag, R extends Neo4jTag> {
    private N source;
    private N destination;
    private List<Pair<N, ArrayList<Point>>> paths = new ArrayList<>();

    public static MultipointRouteDTO<OSMIntersection, OSMRoadSegment> toDTO(MultipointRoute<OSMIntersection, OSMRoadSegment> object) {
        MultipointRouteDTO<OSMIntersection, OSMRoadSegment> dto = new MultipointRouteDTO<>();
        dto.setSource(object.getSource().getData());
        dto.setDestination(object.getDestination().getData());
        object.getPaths().forEach((node, path) -> {
            var pair = new Pair<OSMIntersection, ArrayList<Point>>();
            pair.setLeft(node.getData());
            pair.setRight(path.stream().map(it -> it.getData().getLocation())
                    .collect(
                            Collectors.toCollection(ArrayList::new)
                    ));
            dto.getPaths().add(pair);
        });
        return dto;
    }

}
