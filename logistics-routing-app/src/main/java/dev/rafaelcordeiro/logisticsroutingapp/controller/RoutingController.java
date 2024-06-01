package dev.rafaelcordeiro.logisticsroutingapp.controller;

import dev.rafaelcordeiro.logisticsroutingapp.core.facade.GraphFacade;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    public @ResponseBody ResponseEntity<List> testGet(@RequestParam String sourceId, @RequestParam String destinationId) {
        return ResponseEntity.ok(graphFacade.getRoute(sourceId, destinationId));
    }
}
