package dev.rafaelcordeiro.logisticsroutingapp.model.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class OpenStreetMapTag extends Neo4jTag {
    private Long osmid;
}
