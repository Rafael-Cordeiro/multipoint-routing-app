package dev.rafaelcordeiro.logisticsroutingapp.core.dto;

import dev.rafaelcordeiro.logisticsroutingapp.core.util.Pair;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Triple;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MultipointRouteDTO {
    private Pair<Address, OSMIntersection> source;
    private Pair<Address, OSMIntersection> destination;
    private List<Triple<OSMIntersection, Address, ArrayList<Point>>> paths = new ArrayList<>();

    public static MultipointRouteDTO toDTO(MultipointRoute<OSMIntersection, OSMRoadSegment> object) {
        MultipointRouteDTO dto = new MultipointRouteDTO();
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
