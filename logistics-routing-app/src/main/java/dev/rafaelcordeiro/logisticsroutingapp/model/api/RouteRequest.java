package dev.rafaelcordeiro.logisticsroutingapp.model.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteRequest {
    private String source;
    private List<String> intermediates;
    private String destination;
}