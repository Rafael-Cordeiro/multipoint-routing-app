package dev.rafaelcordeiro.logisticsroutingapp.core.facade;

import dev.rafaelcordeiro.logisticsroutingapp.core.algorithm.dijkstra.Dijkstra;
import dev.rafaelcordeiro.logisticsroutingapp.core.dao.GeospatialGraphDAO;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Dijkstra dijkstra = new Dijkstra();
        return dijkstra.run(graph, sourceNode);
    }

    public void testGeoGraphQuery() {
        geospatialGraphDAO.getFullGeoGraph();
    }

    public List<Node<OSMIntersection, OSMRoadSegment>> getRoute(String source, String destination) {
        var sourceNode = geospatialGraphDAO.getNearestIntersection(source);
        var destinationNode = geospatialGraphDAO.getNearestIntersection(destination);
        var graph = geospatialGraphDAO.getFullGeoGraph();
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.run(graph, graph.getNodes().get(sourceNode.getData().getOsmid()));
        return List.of(sourceNode, destinationNode);
    }

}
