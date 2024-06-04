package dev.rafaelcordeiro.logisticsroutingapp.core.facade;

import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.LegacyDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.SingleDijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.dao.GeospatialGraphDAO;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public Node<OSMIntersection, OSMRoadSegment> getRoute(String sourceId, String targetId) {
        log.info("Gerando rota para os endereços de ID {} e {}", sourceId, targetId);
        var sourceNode = geospatialGraphDAO.getNearestIntersection(sourceId);
        var targetNode = geospatialGraphDAO.getNearestIntersection(targetId);
        var graph = geospatialGraphDAO.getFullGeoGraph();
        SingleDijkstra dijkstra = new SingleDijkstra();
        dijkstra.run(graph, graph.getNodes().get(sourceNode.getData().getOsmid()), graph.getNodes().get(targetNode.getData().getOsmid()));
        log.info("Retornando caminho curto do nó alvo");
        var returningNode = graph.getNodes().get(targetNode.getData().getOsmid());
        returningNode.getShortestPath().add(returningNode);
        return returningNode;
    }

}
