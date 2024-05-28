package dev.rafaelcordeiro.logisticsroutingapp.core.infra;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class BasicNeo4jConnection {

    private static final String DB_URI = "neo4j://localhost:7999";
    private static final String DB_USER = "neo4j";
    private static final String DB_PASSWORD = "password";

    private static Driver DRIVER = null;

    public static Driver getDriver() {
        if (DRIVER == null) {
            DRIVER = GraphDatabase.driver(DB_URI, AuthTokens.basic(DB_USER, DB_PASSWORD));
        }
        return DRIVER;
    }

}
