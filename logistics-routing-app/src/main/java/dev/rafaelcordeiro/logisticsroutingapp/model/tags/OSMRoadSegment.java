package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

import java.util.List;

@Getter
@Setter
public class OSMRoadSegment extends OpenStreetMapTag {
    private String name;
    private String lanes;
    private Double length;
    private String highway;
    private Boolean oneway;
    private String ref;
    private List<Point> geometry;

    public OSMRoadSegment(Long osmid, String name, String lanes, Double length, String highway, Boolean oneway, String ref, List<Point> geometry) {
        super(osmid);
        this.name = name;
        this.lanes = lanes;
        this.length = length;
        this.highway = highway;
        this.oneway = oneway;
        this.ref = ref;
        this.geometry = geometry;
    }
}
