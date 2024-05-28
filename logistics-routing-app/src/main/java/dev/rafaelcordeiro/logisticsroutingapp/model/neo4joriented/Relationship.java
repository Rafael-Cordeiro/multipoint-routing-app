package dev.rafaelcordeiro.logisticsroutingapp.model.neo4joriented;

import dev.rafaelcordeiro.logisticsroutingapp.model.tags.Neo4jTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Relationship<T extends Neo4jTag> {
    private T data;
    private Node start;
    private Node end;
}
