package dev.rafaelcordeiro.logisticsroutingapp.controller;

import dev.rafaelcordeiro.logisticsroutingapp.core.facade.GraphFacade;
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
    public @ResponseBody ResponseEntity<Object> testGet(@RequestParam String sourceId, @RequestParam String targetId) {
        var response = graphFacade.getRoute(sourceId, targetId).getShortestPath().stream().map(node -> List.of(node.getData().getLocation().y(), node.getData().getLocation().x())).toList();
        return ResponseEntity.ok(response);
    }
}
