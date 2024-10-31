package dev.rafaelcordeiro.logisticsroutingapp.model.api.dto;

import dev.rafaelcordeiro.logisticsroutingapp.model.route.RoutePoint;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoutePointDTO {
    private OSMIntersection node;
    private Address address;

    public static RoutePointDTO toDTO(RoutePoint routePoint) {
        return new RoutePointDTO(routePoint.getNode().getData(), routePoint.getAddress());
    }
}
