import neo4j

NEO4J_URI="neo4j://localhost:7999"
NEO4J_USER="neo4j"
NEO4J_PASSWORD="password"

driver = neo4j.GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))

CYPHER_QUERY = """
MATCH (n) RETURN count(n) AS node_count;
"""

def get_node_count(tx):
    results = tx.run(CYPHER_QUERY)
    df = results.to_df()
    return df

if __name__ == '__main__':
    with driver.session() as session:
        df = session.execute_read(get_node_count)

    print(df)