package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OSMRoadSegment extends OpenStreetMapTag {
    private String name;
    private String lanes;
    private Double length;
    private String highway;
    private Boolean oneway;
    private String ref;

    public OSMRoadSegment(Long osmid, String name, String lanes, Double length, String highway, Boolean oneway, String ref) {
        super(osmid);
        this.name = name;
        this.lanes = lanes;
        this.length = length;
        this.highway = highway;
        this.oneway = oneway;
        this.ref = ref;
    }
}
