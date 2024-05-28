import osmnx as ox
import matplotlib.pyplot as plt


def show_map():
    G = ox.graph_from_place("Sampa", network_type="drive")
    # fig, ax = ox.plot_graph(G)
    gdf_nodes, gdf_relationships = ox.graph_to_gdfs(G)
    gdf_nodes.reset_index(inplace=True)
    gdf_relationships.reset_index(inplace=True)
    # gdf_nodes.plot(markersize=0.1)
    # gdf_relationships.plot(markersize=0.01, linewidth=0.5)
    ox.plot_graph(G)
    plt.show()


if __name__ == "__main__":
    show_map()
