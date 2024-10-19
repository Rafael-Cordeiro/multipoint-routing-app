package dev.rafaelcordeiro.logisticsroutingapp.core.dto;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Triple;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.List;

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
            ArrayList<Point> linestring = new ArrayList<>();
            var nodes = path.getRight();
            for (int i = 0; i < path.getRight().size(); i++) {
                linestring.add(nodes.get(i).getData().getLocation());
                if (i == path.getRight().size() - 1) break;
                linestring.addAll(nodes.get(i).getAdjacentNodes().get(nodes.get(i+1)).getData().getGeometry());
            }
            triple.setThird(linestring);
            dto.getPaths().add(triple);
        });
        return dto;
    }
}
