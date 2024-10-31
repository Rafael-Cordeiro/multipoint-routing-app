package dev.rafaelcordeiro.logisticsroutingapp.model.api.dto;

import dev.rafaelcordeiro.logisticsroutingapp.model.route.RoutePath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class RoutePathDTO {
    private RoutePointDTO point;
    private ArrayList<Point> line;

    public static RoutePathDTO toDTO(RoutePath routePath) {
        ArrayList<Point> linestring = new ArrayList<>();
        var line = routePath.getLine();
        for (int i = 0; i < line.size(); i++) {
            linestring.add(line.get(i).getData().getLocation());
            if (i == line.size() - 1) break;
            linestring.addAll(line.get(i).getAdjacentNodes().get(line.get(i+1)).getData().getGeometry());
        }
        return new RoutePathDTO(RoutePointDTO.toDTO(routePath.getPoint()), linestring);
    }
}
