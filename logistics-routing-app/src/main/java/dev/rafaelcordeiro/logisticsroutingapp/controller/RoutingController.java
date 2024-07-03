package dev.rafaelcordeiro.logisticsroutingapp.controller;

import dev.rafaelcordeiro.logisticsroutingapp.core.dto.MultipointRouteDTO;
import dev.rafaelcordeiro.logisticsroutingapp.core.facade.GraphFacade;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.RouteRequest;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("routing")
@CrossOrigin(origins="*")
public class RoutingController {

    @Autowired
    GraphFacade graphFacade;

    @GetMapping
    public @ResponseBody ResponseEntity<List<List<Double>>> calculateSimpleRoute(@RequestParam String sourceId, @RequestParam String targetId) {
        sourceId = sourceId.replace("\"", "");
        targetId = targetId.replace("\"", "");
        var response = graphFacade.calculateSimpleRoute(sourceId, targetId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public @ResponseBody ResponseEntity<MultipointRouteDTO<OSMIntersection, OSMRoadSegment>> calculateMultiPointRoute(@RequestBody RouteRequest routeRequest) {
        var response = graphFacade.calculateMultipointRoute(routeRequest);
        return ResponseEntity.ok(response);
    }
}
