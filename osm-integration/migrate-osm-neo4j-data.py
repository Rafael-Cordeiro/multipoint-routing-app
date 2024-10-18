import osmnx as ox
import neo4j
import shapely
import json
import time

NEO4J_URI = "neo4j://localhost:7999"
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "password"

driver = neo4j.GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))

PLACE_QUERY = "Sampa"

# Defining constraints
constraint_query = "CREATE CONSTRAINT IF NOT EXISTS FOR (i:INTERSECTION) REQUIRE i.osmid IS UNIQUE"
rel_index_query = "CREATE INDEX IF NOT EXISTS FOR ()-[r:ROAD_SEGMENT]-() ON r.osmids"
address_constraint_query = "CREATE CONSTRAINT IF NOT EXISTS FOR (a:ADDRESS) REQUIRE a.id IS UNIQUE"
point_index_query = "CREATE POINT INDEX IF NOT EXISTS FOR (i:INTERSECTION) ON i.location"

# Import nodes from GeoDataFrames
node_query = '''
    UNWIND $rows AS row
    WITH row WHERE row.osmid IS NOT NULL
    MERGE (i:INTERSECTION {osmid: row.osmid})
        SET i.location =
            point({latitude: row.y, longitude: row.x}),
                i.ref          = row.ref,
                i.highway      = row.highway,
                i.street_count = toInteger(row.street_count)
    RETURN COUNT(*) AS TOTAL
'''

rels_query = '''
    UNWIND $rows AS road
    MATCH (u:INTERSECTION {osmid: road.u})
    MATCH (v:INTERSECTION {osmid: road.v})
    MERGE (u)-[r:ROAD_SEGMENT {osmid: road.osmid}]->(v)
        SET r.oneway    = road.oneway,
            r.lanes     = road.lanes,
            r.ref       = road.ref,
            r.name      = road.name,
            r.highway   = road.highway,
            r.max_speed = road.max_speed,
            r.length    = toFloat(road.length),
            r.geometry  = [p IN road.geometry | point({latitude:p.y, longitude:p.x})]
    RETURN COUNT(*) AS total
'''


def create_constraints(tx):
    print('[INFO] Creating constraints')
    tx.run(constraint_query)
    tx.run(rel_index_query)
    tx.run(address_constraint_query)
    tx.run(point_index_query)
    print('[INFO] Constraints created')


# Batch GeoDataFrames
def insert_data(tx, query, rows, batch_size=10000):
    total = 0
    batch = 0
    while batch * batch_size < len(rows):
        results = tx.run(
            query, parameters = {'rows': rows[batch*batch_size:(batch+1)*batch_size].to_dict('records')}
        ).data()
        print(results)
        if 'total' in results[0]:
            total += results[0]['total']
        else:
            total += results[0]['TOTAL']
        batch += 1

def point_to_neo4j_point(point):
    geojson = json.loads(shapely.to_geojson(point))
    geojson['x'] = geojson['coordinates'][0]
    geojson['y'] = geojson['coordinates'][1]
    if type(geojson['x']) != int and type(geojson['x']) != float and type(geojson['x']) != complex:
        return None
    if type(geojson['y']) != int and type(geojson['y']) != float and type(geojson['y']) != complex:
        return None
    del geojson['coordinates']
    del geojson['type']
    return geojson

def linestring_to_neo4j_point_array(geometry):
    a = list(filter(lambda row : row != None, map(lambda coords: point_to_neo4j_point(shapely.Point(coords)), geometry.coords)))
    return a

def run_migrate():
    # Import GeoDataFrame from OSM
    print('[INFO] Importing data from OSM to GeoDataFrames')
    G = ox.graph_from_place(PLACE_QUERY, network_type="drive")
    gdf_nodes, gdf_relationships = ox.graph_to_gdfs(G)
    gdf_nodes.reset_index(inplace=True)
    gdf_relationships.reset_index(inplace=True)
    print('[INFO] Importing concluded')

    print('[INFO] Modifying relationships GDF: transforming geometry column from Linestring to GeoJSON Point array')
    gdf_relationships['geometry'] = gdf_relationships['geometry'].apply(linestring_to_neo4j_point_array)
    print('[INFO] Modifying relationships GDF: Geometry transformation concluded')

    # Persist on neo4j
    with driver.session() as session:
        session.execute_write(create_constraints)
        print('[INFO] Creating nodes')
        session.execute_write(insert_data, node_query, gdf_nodes.drop(columns=['geometry']))
        print('[INFO] Creating relationships')
        session.execute_write(insert_data, rels_query, gdf_relationships)
        print('[INFO] Graph persisted')


if __name__ == "__main__":
    start = time.time()
    run_migrate()
    end = time.time()
    millis = (end - start) * 1000
    print(f"Tempo de execução: {millis:.2f} ms")