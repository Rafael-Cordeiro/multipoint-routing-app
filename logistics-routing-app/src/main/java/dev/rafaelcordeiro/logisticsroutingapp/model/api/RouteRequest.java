package dev.rafaelcordeiro.logisticsroutingapp.model.api;

import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteRequest {
    private Address source;
    private List<Address> intermediates;
    private Address destination;
}