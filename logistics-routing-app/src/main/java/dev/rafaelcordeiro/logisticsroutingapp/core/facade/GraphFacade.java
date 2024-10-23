package dev.rafaelcordeiro.logisticsroutingapp.core.facade;

import dev.rafaelcordeiro.logisticsroutingapp.core.dto.MultipointRouteDTO;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.LegacyDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.MultipointDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.SimpleDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.dao.GeospatialGraphDAO;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.RouteRequest;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GraphFacade {

    private GeospatialGraphDAO geospatialGraphDAO;

    public GraphFacade() {
        geospatialGraphDAO = new GeospatialGraphDAO();
    }

    public BasicGraph fetchBasicGraph() {
        return geospatialGraphDAO.fetchBasicGraph();
    }

    public BasicGraph runDijkstra(BasicGraph graph, BasicGraphNode sourceNode) {
        LegacyDijkstra dijkstra = new LegacyDijkstra();
        return dijkstra.run(graph, sourceNode);
    }

    public void testGeoGraphQuery() {
        geospatialGraphDAO.getFullGeoGraph();
    }

    public List<List<Double>> calculateSimpleRoute(String sourceId, String targetId) {
        log.info("\n\n\n========================================================================\n\n\n");
        log.info("Gerando rota para os endereços de ID {} e {}", sourceId, targetId);
        var millis = System.currentTimeMillis();
        var sourceNode = geospatialGraphDAO.getNearestIntersection(sourceId);
        var targetNode = geospatialGraphDAO.getNearestIntersection(targetId);
        var graph = geospatialGraphDAO.getFullGeoGraph();
        SimpleDijkstra dijkstra = new SimpleDijkstra();
        var shortestPath = dijkstra.run(graph, graph.getNodes().get(sourceNode.getData().getOsmid()), graph.getNodes().get(targetNode.getData().getOsmid()));
        graph = null;
        log.info("Operação concluída em {} ms", System.currentTimeMillis() - millis);
        return shortestPath;
    }

    public MultipointRouteDTO calculateMultipointRoute(RouteRequest routeRequest) {
        log.info("\n\n\n========================================================================\n\n\n");
        log.info("Gerando rota multiponto");
        var millis = System.currentTimeMillis();

        final var graph = geospatialGraphDAO.getFullGeoGraph();

        Map<Node<OSMIntersection, OSMRoadSegment>, Address> addressToNodes = new HashMap<>();
        routeRequest.setSource(geospatialGraphDAO.getAddressById(routeRequest.getSource().getId()));
        Node<OSMIntersection, OSMRoadSegment> sourceNode = graph.getNodes().get(
                geospatialGraphDAO.getNearestIntersection(routeRequest.getSource().getId())
                        .getData().getOsmid()
        );
        addressToNodes.put(sourceNode, routeRequest.getSource());

        routeRequest.setDestination(geospatialGraphDAO.getAddressById(routeRequest.getDestination().getId()));
        Node<OSMIntersection, OSMRoadSegment> destinationNode = graph.getNodes().get(
                geospatialGraphDAO.getNearestIntersection(routeRequest.getDestination().getId())
                        .getData().getOsmid()
        );
        addressToNodes.put(destinationNode, routeRequest.getDestination());

        List<Node<OSMIntersection, OSMRoadSegment>> intermediates = new ArrayList<>();
        routeRequest.getIntermediates().forEach(it -> {
            it = geospatialGraphDAO.getAddressById(it.getId());
            var node = graph.getNodes().get(geospatialGraphDAO.getNearestIntersection(it.getId())
                            .getData()
                            .getOsmid()
            );
            addressToNodes.put(node, it);
            intermediates.add(node);
        });

        var dijkstra = new MultipointDijkstra(addressToNodes);
        MultipointRoute<OSMIntersection, OSMRoadSegment> multipointRoute = dijkstra.run(graph, sourceNode, intermediates, destinationNode);

//      Garante o indexação dos endereços conrretos na origem e no destino em caso de ocorrência
//      de bug de perda de endereços relacionados a mesma interseção
        multipointRoute.getSource().setLeft(routeRequest.getSource());
        multipointRoute.getDestination().setLeft(routeRequest.getDestination());
        log.info("Operação concluída em {} ms", System.currentTimeMillis() - millis);
        return MultipointRouteDTO.toDTO(multipointRoute);
    }

}
