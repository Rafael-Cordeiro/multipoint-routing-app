package dev.rafaelcordeiro.logisticsroutingapp.model.api.dto;

import dev.rafaelcordeiro.logisticsroutingapp.model.route.MultipointRoute;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MultipointRouteDTO {
    private RoutePointDTO source;
    private RoutePointDTO destination;
    private List<RoutePathDTO> paths;

    public static MultipointRouteDTO toDTO(MultipointRoute object) {
        MultipointRouteDTO dto = new MultipointRouteDTO();
        dto.setSource(RoutePointDTO.toDTO(object.getSource()));
        dto.setDestination(RoutePointDTO.toDTO(object.getDestination()));
        dto.setPaths(object.getPaths().stream().map(RoutePathDTO::toDTO).toList());
        return dto;
    }
}