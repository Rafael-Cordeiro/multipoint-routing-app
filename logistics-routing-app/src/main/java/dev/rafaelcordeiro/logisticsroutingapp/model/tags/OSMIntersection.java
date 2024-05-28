package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.driver.types.Point;

@Getter
@Setter
public class OSMIntersection extends OpenStreetMapTag {
    private Point location;
    private Integer streetCount;
    private String highway;
    private String ref;

    public OSMIntersection(Long osmid, Point location, Integer streetCount, String highway, String ref) {
        super(osmid);
        this.location = location;
        this.streetCount = streetCount;
        this.highway = highway;
        this.ref = ref;
    }
}
