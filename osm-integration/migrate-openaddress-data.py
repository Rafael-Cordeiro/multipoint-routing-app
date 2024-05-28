import osmnx as ox
import neo4j

NEO4J_URI = "neo4j://localhost:7999"
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "password"
GEOJSON_FILENAME = "sampa.geojson"

driver = neo4j.GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))

# We'll use apoc.load.json to import a JSON file of address data
# 'São Paulo' injetado no lugar da propriedade city para o caso da base da cidade de são paulo
add_addresses_query = """
CALL apoc.periodic.iterate(
  'CALL apoc.load.json("file:///""" + GEOJSON_FILENAME + """"   ) YIELD value',
  'MERGE (a:ADDRESS {id: value.properties.id})
SET a.location = 
  point({latitude: value.geometry.coordinates[1], longitude: value.geometry.coordinates[0]}),
  a.full_address = value.properties.street + ", " + value.properties.number + " - São Paulo - SP " + value.properties.postcode

SET a += value.properties',
  {batchSize:10000, parallel:true})
"""

# Next, connect each address to the road network at the nearest intersection
near_intersection_query = """
CALL apoc.periodic.iterate(
  'MATCH (p:ADDRESS) WHERE NOT EXISTS ((p)-[:NEAREST_INTERSECTION]->(:INTERSECTION)) RETURN p',
  'CALL {
  WITH p
  MATCH (i:INTERSECTION)
  USING INDEX i:INTERSECTION(location)
  WHERE point.distance(i.location, p.location) < 200

  WITH i
  ORDER BY point.distance(p.location, i.location) ASC 
  LIMIT 1
  RETURN i
}
WITH p, i

MERGE (p)-[r:NEAREST_INTERSECTION]->(i)
SET r.length = point.distance(p.location, i.location)
RETURN COUNT(p)',
  {batchSize:1000, parallel:false})
"""

# Create a full text index to enable search in our web app

full_text_query = "CREATE FULLTEXT INDEX search_index IF NOT EXISTS FOR (p:PointOfInterest|ADDRESS) ON EACH [p.name, p.full_address]"

# Transaction function to execute our address import queries
def enrich_addresses(tx):
    print('[INFO] Creating address nodes')
    results = tx.run(add_addresses_query)
    print('[INFO] Binding address nodes with nearest intersections')
    results = tx.run(near_intersection_query)

def run():
    # Execute address import
    with driver.session() as session:
        session.execute_write(enrich_addresses)

    # Execute full text index query
    with driver.session() as session:
        print('[INFO] Creating FULLTEXT Index')
        results = session.execute_write(lambda tx: tx.run(full_text_query))

    print("[INFO] All address from {} were persisted".format(GEOJSON_FILENAME))

if __name__ == '__main__':
    run()