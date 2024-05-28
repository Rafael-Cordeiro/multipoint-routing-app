package dev.rafaelcordeiro.logisticsroutingapp.core.dao;

import dev.rafaelcordeiro.logisticsroutingapp.core.infra.BasicNeo4jConnection;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Neo4jUtils;
import dev.rafaelcordeiro.logisticsroutingapp.model.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented.Relationship;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.types.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GeospatialGraphDAO {
    private static final Logger log = LoggerFactory.getLogger(GeospatialGraphDAO.class);
    private String nodesAndRelationsRecordsQuery = "MATCH (n1)-[r]->(n2) RETURN n1, r, n2";
    private String intersectionsAndSegmentsQuery = "MATCH (n1:INTERSECTION)-[r:ROAD_SEGMENT]->(n2:INTERSECTION) RETURN n1, r, n2";

    public BasicGraph fetchBasicGraph() {
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(nodesAndRelationsRecordsQuery)
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();

        var basicGraph = new BasicGraph();

        result.records().forEach(record -> {
            BasicGraphNode basicStartNode;
            try {
                basicStartNode = basicGraph.getNodes().stream()
                        .filter(node -> record.get(0).get("name").asString().equals(node.getName()))
                        .toList().getFirst();
            } catch (Exception e) {
                basicStartNode = new BasicGraphNode();
                basicStartNode.setName(record.get(0).get("name").asString());
                basicStartNode.setLocation(record.get(0).get("location").asPoint());
            }

            BasicGraphNode basicEndNode;
            try {
                basicEndNode = basicGraph.getNodes().stream()
                        .filter(node -> record.get(2).get("name").asString().equals(node.getName()))
                        .toList().getFirst();
            } catch (Exception e) {
                basicEndNode = new BasicGraphNode();
                basicEndNode.setName(record.get(2).get("name").asString());
                basicEndNode.setLocation(record.get(2).get("location").asPoint());
            }

            var relation = record.get(1);
            basicStartNode.getAdjacentNodes().put(basicEndNode, relation.get("length").asInt());

            basicGraph.getNodes().add(basicStartNode);
            basicGraph.getNodes().add(basicEndNode);
        });

        return basicGraph;
    }

    public void testGeoGraphQuery() {
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(intersectionsAndSegmentsQuery)
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();

        var graph = new Graph();

        result.records().forEach(record -> {
            AtomicReference<Node<OSMIntersection, OSMRoadSegment>> startNode = new AtomicReference<>(new Node<>());
            Optional.ofNullable(graph.getNodes().get(record.get(0).get("osmid").asLong())).ifPresentOrElse(startNode::set, () -> {
                var node = startNode.get();
                node.setData(new OSMIntersection(
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("osmid"), Long.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("location"), Point.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("streetCount"), Integer.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("highway"), String.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("ref"), String.class)
                ));
                startNode.set(node);
                graph.getNodes().put(startNode.get().getData().getOsmid(), startNode.get());
            });

            AtomicReference<Node<OSMIntersection, OSMRoadSegment>> endNode = new AtomicReference<>(new Node<>());
            Optional.ofNullable(graph.getNodes().get(record.get(2).get("osmid").asLong())).ifPresentOrElse(endNode::set, () -> {
                var node = endNode.get();
                node.setData(new OSMIntersection(
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(2).get("osmid"), Long.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(2).get("location"), Point.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(2).get("streetCount"), Integer.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(2).get("highway"), String.class),
                        Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(2).get("ref"), String.class)
                ));
                endNode.set(node);
                graph.getNodes().put(endNode.get().getData().getOsmid(), endNode.get());
            });

            var segmentData = new OSMRoadSegment(
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("osmid"), Long.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("name"), String.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("lanes"), String.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("length"), Double.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("highway"), String.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("oneway"), Boolean.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("ref"), String.class)
            );
            var relationship = new Relationship<>(segmentData, startNode.get(), endNode.get());
            startNode.get().addAdjascentNode(endNode.get(), relationship);
        });
        System.out.println(graph);
    }

}
