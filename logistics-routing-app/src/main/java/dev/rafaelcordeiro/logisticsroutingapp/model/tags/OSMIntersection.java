package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

@AllArgsConstructor
@Getter
@Setter
public class OSMIntersection implements Neo4jTag {
    private Long osmid;
    private Point location;
    private Integer streetCount;
    private String highway;
    private String ref;
}
