package dev.rafaelcordeiro.logisticsroutingapp.core.dao;

import dev.rafaelcordeiro.logisticsroutingapp.core.infra.BasicNeo4jConnection;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Neo4jUtils;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Graph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Node;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.neo4joriented.Relationship;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMIntersection;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.OSMRoadSegment;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.types.Point;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class GeospatialGraphDAO {
    private String nodesAndRelationsRecordsQuery = "MATCH (n1)-[r]->(n2) RETURN n1, r, n2";
    private String intersectionsAndSegmentsQuery = "MATCH (n1:INTERSECTION)-[r:ROAD_SEGMENT]->(n2:INTERSECTION) RETURN n1, r, n2";
    private String addressByIdQuery = "MATCH (a:ADDRESS {id: $id}) RETURN a;";
    private String nearestIntersectionQuery = "MATCH (address:ADDRESS {id: $id})-[:NEAREST_INTERSECTION]->(target:INTERSECTION) RETURN target LIMIT 1";
//    private String nearestIntersectionQuery      = "MATCH (address)-[:NEAREST_INTERSECTION]->(target:INTERSECTION)\nWHERE address.id = $code\nRETURN target";

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

    public Graph getFullGeoGraph() {
        log.info("Iniciando busca do grafo completo no banco de dados...");
        Long millis = System.currentTimeMillis();
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(intersectionsAndSegmentsQuery)
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();
        log.info("Consulta finalizada em {} ms", System.currentTimeMillis() - millis);

        log.info("Montando grafo em objeto...");
        millis = System.currentTimeMillis();
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
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(1).get("ref"), String.class),
                    Neo4jUtils.extractGeometry(record.get(1).get("geometry"))
            );
            var relationship = new Relationship<>(segmentData, startNode.get(), endNode.get());
            startNode.get().addAdjacentNode(endNode.get(), relationship);
        });
        log.info("Grafo montado em {} ms", System.currentTimeMillis() - millis);
        return graph;
    }

    public Node<OSMIntersection, OSMRoadSegment> getNearestIntersection(String id) {
        log.info("Buscando intersecção mais próxima do endereço com ID {}", id);
        Long millis = System.currentTimeMillis();
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(nearestIntersectionQuery)
                .withParameters(Map.of("id", id))
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();

        AtomicReference<Node<OSMIntersection, OSMRoadSegment>> target = new AtomicReference<>(new Node<>());
        result.records().forEach(record -> {
            var node = target.get();
            node.setData(new OSMIntersection(
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("osmid"), Long.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("location"), Point.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("streetCount"), Integer.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("highway"), String.class),
                    Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("ref"), String.class)
            ));
            target.set(node);
        });
        log.info("Intersecção retornada em {} ms", System.currentTimeMillis() - millis);
        return target.get();
    }
    
    public Address getAddressById(String id) {
        log.info("Buscando dados do endereço de ID {}", id);
        Long millis = System.currentTimeMillis();
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(addressByIdQuery)
                .withParameters(Map.of("id", id))
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();

        var record = result.records().getFirst();
        Address address = new Address(
                Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("id"), String.class),
                Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("full_address"), String.class),
                Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get(0).get("location"), Point.class)
        );

        log.info("Dados de endereço consultado em {} ms", System.currentTimeMillis() - millis);
        return address;
    }

}
