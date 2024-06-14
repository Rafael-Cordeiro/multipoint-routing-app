package dev.rafaelcordeiro.logisticsroutingapp.core.facade;

import dev.rafaelcordeiro.logisticsroutingapp.core.dto.MultipointRouteDTO;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.LegacyDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.MultipointDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.SimpleDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.dao.GeospatialGraphDAO;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.MultipointRoute;
import dev.rafaelcordeiro.logisticsroutingapp.model.api.RouteRequest;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MultipointRouteDTO<OSMIntersection, OSMRoadSegment> calculateMultipointRoute(RouteRequest routeRequest) {
        log.info("\n\n\n========================================================================\n\n\n");
        log.info("Gerando rota multiponto");
        var millis = System.currentTimeMillis();

        final var graph = geospatialGraphDAO.getFullGeoGraph();
        Node<OSMIntersection, OSMRoadSegment> sourceNode = graph.getNodes().get(geospatialGraphDAO.getNearestIntersection(routeRequest.getSource()).getData().getOsmid());
        Node<OSMIntersection, OSMRoadSegment> targetNode = graph.getNodes().get(geospatialGraphDAO.getNearestIntersection(routeRequest.getDestination()).getData().getOsmid());
        var intermediates = routeRequest.getIntermediates()
                .stream().map(it -> (Node<OSMIntersection, OSMRoadSegment>) graph.getNodes().get(geospatialGraphDAO.getNearestIntersection(it).getData().getOsmid())).toList();

        var dijkstra = new MultipointDijkstra();
        MultipointRoute<OSMIntersection, OSMRoadSegment> multipointRoute = dijkstra.run(graph, sourceNode, intermediates, targetNode);

        log.info("Operação concluída em {} ms", System.currentTimeMillis() - millis);
        MultipointRouteDTO<OSMIntersection, OSMRoadSegment> dto = MultipointRouteDTO.toDTO(multipointRoute);
        return MultipointRouteDTO.toDTO(multipointRoute);
    }

}
