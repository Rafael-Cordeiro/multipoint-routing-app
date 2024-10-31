package dev.rafaelcordeiro.logisticsroutingapp.model.route;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class MultipointRoute {
    private RoutePoint source;
    private RoutePoint destination;
    private List<RoutePath> paths = new LinkedList<>();
}
