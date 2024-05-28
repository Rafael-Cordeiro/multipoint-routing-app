package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OSMRoadSegment implements Neo4jTag {
    private Long osmid;
    private String name;
    private String lanes;
    private Double length;
    private String highway;
    private Boolean oneway;
    private String ref;
}
