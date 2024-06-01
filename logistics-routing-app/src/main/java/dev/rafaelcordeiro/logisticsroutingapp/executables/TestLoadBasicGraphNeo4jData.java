package dev.rafaelcordeiro.logisticsroutingapp.executables;

import dev.rafaelcordeiro.logisticsroutingapp.core.infra.BasicNeo4jConnection;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraph;
import dev.rafaelcordeiro.logisticsroutingapp.model.graph.basicgraph.BasicGraphNode;
import org.neo4j.driver.QueryConfig;

public class TestLoadBasicGraphNeo4jData {

    public static void main(String... args) {
        try (var driver = BasicNeo4jConnection.getDriver()) {
            driver.verifyConnectivity();
            var result = driver.executableQuery("MATCH (n1)-[r]->(n2) RETURN n1, r, n2")
//                    .withParameters(Map.of("age", 26))
                    .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                    .execute();

            var basicGraph = new BasicGraph();
            result.records().forEach(it -> {
                BasicGraphNode basicStartNode;
                try {
                    basicStartNode = basicGraph.getNodes().stream()
                            .filter(n -> it.get(0).get("name").asString().equals(n.getName()))
                            .toList().getFirst();
                } catch (Exception e) {
                    basicStartNode = new BasicGraphNode();
                    basicStartNode.setName(it.get(0).get("name").asString());
                    basicStartNode.setLocation(it.get(0).get("location").asPoint());
                }

                BasicGraphNode basicEndNode;
                try {
                    basicEndNode = basicGraph.getNodes().stream()
                            .filter(n -> it.get(2).get("name").asString().equals(n.getName()))
                            .toList().getFirst();
                } catch (Exception e) {
                    basicEndNode = new BasicGraphNode();
                    basicEndNode.setName(it.get(2).get("name").asString());
                    basicEndNode.setLocation(it.get(2).get("location").asPoint());
                }
                var relation = it.get(1);
                basicStartNode.getAdjacentNodes().put(basicEndNode, relation.get("length").asInt());

                basicGraph.addNode(basicStartNode);
                basicGraph.addNode(basicEndNode);
            });
            System.out.println(basicGraph);
        }
    }

}
