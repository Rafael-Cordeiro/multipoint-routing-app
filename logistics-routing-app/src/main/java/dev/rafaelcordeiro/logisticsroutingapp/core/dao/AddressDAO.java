package dev.rafaelcordeiro.logisticsroutingapp.core.dao;

import dev.rafaelcordeiro.logisticsroutingapp.core.infra.BasicNeo4jConnection;
import dev.rafaelcordeiro.logisticsroutingapp.core.util.Neo4jUtils;
import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Address;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.types.Point;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AddressDAO {
    private String addressByIdQuery = "MATCH (a:ADDRESS {id: $id}) RETURN a;";
    private String searchAddressesQuery = "CALL db.index.fulltext.queryNodes(\"search_index\", $searchString) YIELD node, score RETURN coalesce(node.name, node.full_address) AS value, score, labels(node)[0] AS label, node.id AS id ORDER BY score DESC LIMIT 25";

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

    public List<Address> searchAddressesByMatchingName(String name) {
        log.info("Pesquisando endereços com o nome {}", name);
        Long millis = System.currentTimeMillis();
        var result = BasicNeo4jConnection.getDriver()
                .executableQuery(searchAddressesQuery)
                .withParameters(Map.of("searchString", name))
                .withConfig(QueryConfig.builder().withDatabase("neo4j").build())
                .execute();

        return result.records().stream().map(record -> new Address(
                Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get("id"), String.class),
                Neo4jUtils.ensureNullSafetyRecordValueExtraction(record.get("value"), String.class),
                null
        )).toList();
    }
}
