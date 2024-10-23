package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

@Getter
@Setter
@AllArgsConstructor
public class Address implements Neo4jTag {
    private String id;
    private String name;
    private Point location;
}
