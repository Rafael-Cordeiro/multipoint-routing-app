<template>
  <q-page class="q-pl-xl q-pr-xl">
    <q-form
      class="autocomplete row justify-around items-center"
      @submit.prevent="handleSubmit"
    >
      <div class="col-3">
        <SelectAddress
          :address="search.source"
          :options="search.source.options"
        />
        <SelectAddress
          :address="search.destination"
          :options="search.destination.options"
        />
      </div>
      <div class="col-3">
        <q-btn color="teal" type="submit" label="Route" :loading="submitting">
          <template v-slot:loading>
            <q-spinner-facebook />
          </template>
        </q-btn>
      </div>
    </q-form>
    <div
      id="map"
      class="leaflet-container"
      style="width: 100%; height: 70vh"
    ></div>
  </q-page>
</template>

<script setup>
import neo4j from "neo4j-driver";
import L from "leaflet";
import SelectAddress from "src/components/SelectAddress.vue";

defineOptions({
  name: "LegacyMap",
  components: {
    SelectAddress,
  },
  data() {
    return {
      NEO4J_URI: "neo4j://localhost:7999",
      NEO4J_USER: "neo4j",
      NEO4J_PASSWORD: "password",
      SEARCH_FULLTEXT_QUERY: `
        CALL db.index.fulltext.queryNodes("search_index", $searchString)
        YIELD node, score
        RETURN coalesce(node.name, node.full_address) AS value, score, labels(node)[0] AS label, node.id AS id
        ORDER BY score DESC LIMIT 25`,
      ROUTE_QUERY: `
        MATCH (to {id: $dest})-[:NEAREST_INTERSECTION]->(source:INTERSECTION)
        MATCH (from {id: $source})-[:NEAREST_INTERSECTION]->(target:INTERSECTION)
        CALL apoc.algo.dijkstra(source, target, 'ROAD_SEGMENT', 'length')
        YIELD path, weight
        RETURN [n in nodes(path) | [n.location.latitude, n.location.longitude]] AS route`,
      driver: {},
      map: {},
      submitting: false,
      search: {
        source: {
          options: [],
          attributes: {
            name: "",
            id: "",
          },
        },
        destination: {
          options: [],
          attributes: {
            name: "",
            id: "",
          },
        },
      },
    };
  },
  mounted() {
    this.driver = neo4j.driver(
      this.NEO4J_URI,
      neo4j.auth.basic(this.NEO4J_USER, this.NEO4J_PASSWORD)
    );
    this.map = L.map("map", { zoomControl: false }).setView(
      [-23.550164, -46.633664],
      13
    );
    L.control.zoom({ position: "bottomright" }).addTo(this.map);
    L.tiles = L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
    const popup = L.popup();
  },
  methods: {
    filterHandler(val, update, target) {
      if (val.length < 4) {
        update(() => {
          this.search[target].options = [];
        });
        return;
      }
      this.driver
        .session()
        .run(this.SEARCH_FULLTEXT_QUERY, { searchString: val })
        .then((result) => {
          this.search[target].options = [];
          update(() => {
            result.records.forEach((record) => {
              this.search[target].options.push({
                name: record.get("value"),
                id: record.get("id"),
              });
            });
          });
        });
      update(() => {});
    },
    handleSubmit(event) {
      event.preventDefault();
      this.submitting = true;
      this.fetchRoute({
        source: this.search.source.attributes.id,
        dest: this.search.destination.attributes.id,
      });
    },
    fetchRoute({ source, dest }) {
      const millis = Date.now();
      var session = this.driver.session({
        database: this.NEO4J_USER,
        defaultAccessMode: neo4j.session.read,
      });
      session
        .run(this.ROUTE_QUERY, { source, dest })
        .then((routeResult) => {
          routeResult.records.forEach((routeRecord) => {
            const routeCoords = routeRecord.get("route");
            var polyline = L.polyline(routeCoords)
              .setStyle({ color: "blue", weight: 7 })
              .addTo(this.map);

            var corner1 = L.latLng(routeCoords[0][0], routeCoords[0][1]);
            var corner2 = L.latLng(
              routeCoords[routeCoords.length - 1][0],
              routeCoords[routeCoords.length - 1][1]
            );

            L.marker(corner1).addTo(this.map);
            L.marker(corner2).addTo(this.map);

            this.map.panInsideBounds(L.latLngBounds(corner1, corner2));
          });
        })
        .catch((error) => {
          console.log(error);
        })
        .then(() => {
          session.close();
          this.submitting = false;
          console.log(
            `Consulta de rota e montagem de mapa executou em ${
              Date.now() - millis
            } ms`
          );
        });
    },
  },
});
</script>

<style>
@import "https://unpkg.com/leaflet@1.9.2/dist/leaflet.css";
@import "https://unpkg.com/@geoman-io/leaflet-geoman-free@latest/dist/leaflet-geoman.css";
html,
body {
  height: 100%;
  margin: 0;
}

.leaflet-container {
  width: 1000px;
  height: 1000px;
}

* {
  box-sizing: border-box;
}

body {
  font: 16px Ubuntu;
}

/* .autocomplete {
  position: relative;
  display: inline-block;
  width: 300px;
} */
</style>
