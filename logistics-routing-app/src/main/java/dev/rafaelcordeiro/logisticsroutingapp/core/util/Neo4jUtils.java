package dev.rafaelcordeiro.logisticsroutingapp.core.util;

import org.neo4j.driver.Value;
import org.neo4j.driver.types.Point;

import java.util.List;

public class Neo4jUtils {
    public static <T> T ensureNullSafetyRecordValueExtraction(Value value, Class<T> tClass) {
        try {
            if (tClass == Integer.class) {
                return (T) Integer.valueOf(value.asInt());
            }
            if (tClass == Long.class) {
                return (T) Long.valueOf(value.asLong());
            }
            if (tClass == Float.class) {
                return (T) Float.valueOf(value.asFloat());
            }
            if (tClass == Double.class) {
                return (T) Double.valueOf(value.asDouble());
            }
            if (tClass == Boolean.class) {
                return (T) Boolean.valueOf(value.asBoolean());
            }
            if (tClass == String.class) {
                return (T) value.asString();
            }
            if (tClass == Point.class) {
                return (T) value.asPoint();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Point> extractGeometry(Value value) {
        return value.asList().stream().map(it -> (Point) it).toList();
    }
}
