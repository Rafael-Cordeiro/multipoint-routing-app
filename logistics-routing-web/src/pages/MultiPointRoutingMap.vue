<template>
  <q-page class="q-pl-xl q-pr-xl">
    <q-form
      class="row justify-evenly items-center"
      @submit.prevent="handleSubmit"
    >
      <div class="col-4">
        <SelectAddress
          :address="addresses.source"
          :options="addresses.options"
        />
        <div
          v-for="(intermediate, index) in addresses.intermediates"
          :key="index"
          class="q-ml-xl"
        >
          <SelectAddress :address="intermediate" :options="addresses.options" />
        </div>
        <div class="column items-center">
          <q-btn
            v-bind:class="
              addresses.intermediates.length > 0 ? 'q-ml-xl q-ma-sm' : 'q-ma-sm'
            "
            round
            icon="add"
            :size="'md'"
            color="green"
            @click="addIntermediate"
          />
        </div>
        <SelectAddress
          :address="addresses.destination"
          :options="addresses.options"
        />
      </div>
      <div class="col-1">
        <div class="column">
          <q-btn
            color="primary"
            type="submit"
            label="Route"
            icon="directions"
            :loading="submitting"
          >
            <template v-slot:loading>
              <q-spinner-facebook />
            </template>
          </q-btn>
          <q-btn @click="clearData" icon="clear" label="Clear" />
        </div>
      </div>
    </q-form>
    <div
      id="map"
      class="leaflet-container q-my-xl"
      style="width: 100%; height: 70vh"
    ></div>
  </q-page>
</template>

<script setup>
import neo4j from "neo4j-driver";
import L from "leaflet";
import axios from "axios";
import SelectAddress from "src/components/SelectAddress.vue";

defineOptions({
  name: "MultiPointRoutingMap",
  components: {
    SelectAddress,
  },
  data() {
    return {
      NEO4J_URI: "neo4j://localhost:7999",
      NEO4J_USER: "neo4j",
      NEO4J_PASSWORD: "password",
      driver: {},
      map: {},
      route: {
        startMarker: {},
        endMarker: {},
        polyline: [],
      },
      submitting: false,
      addresses: {
        options: [],
        source: {
          attributes: {
            name: "",
            id: "",
          },
        },
        destination: {
          attributes: {
            name: "",
            id: "",
          },
        },
        intermediates: [],
      },
    };
  },
  mounted() {
    this.driver = neo4j.driver(
      this.NEO4J_URI,
      neo4j.auth.basic(this.NEO4J_USER, this.NEO4J_PASSWORD)
    );
    this.reloadMap();
    L.control.zoom({ position: "bottomright" }).addTo(this.map);
    L.tiles = L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
    const popup = L.popup();
  },
  methods: {
    handleSubmit(event) {
      event.preventDefault();
      this.submitting = true;
      this.fetchRoute({
        source: this.addresses.source.attributes.id,
        intermediates: this.addresses.intermediates.map(
          (it) => it.attributes.id
        ),
        destination: this.addresses.destination.attributes.id,
      });
    },
    async fetchRoute(body) {
      const millis = Date.now();
      const response = await axios
        .post("http://localhost:8080/routing", body)
        .catch((error) => console.error(`[Error from axios]: ${error}`));
      const multipointRoute = response.data;

      var marker1 = L.marker(
        L.latLng(
          multipointRoute.source.location.y,
          multipointRoute.source.location.x
        )
      ).addTo(this.map);
      var marker2 = null;

      multipointRoute.paths.forEach((path) => {
        if (path.left.osmid == multipointRoute.destination.osmid) {
          marker2 = L.marker(
            L.latLng(path.left.location.y, path.left.location.x)
          ).addTo(this.map);
        } else {
          L.marker(L.latLng(path.left.location.y, path.left.location.x)).addTo(
            this.map
          );
        }
        L.polyline(
          path.right.map((item) => {
            return [item.y, item.x];
          })
        )
          .setStyle({ color: "blue", weight: 7 })
          .addTo(this.map);
      });

      this.route.startMarker = marker1;
      this.route.endMarker = marker2;

      // this.map.panInsideBounds(L.latLngBounds(marker1, marker2));
      this.submitting = false;
      console.log(
        `Consulta de rota e montagem de mapa executou em ${
          Date.now() - millis
        } ms`
      );
    },
    clearData() {
      this.addresses = {
        options: [],
        source: {
          attributes: {
            name: "",
            id: "",
          },
        },
        destination: {
          attributes: {
            name: "",
            id: "",
          },
        },
        intermediates: [],
      };
      this.map.eachLayer((layer) => {
        layer === this.route.polyline && this.map.removeLayer(layer);
        layer === this.route.startMarker && this.map.removeLayer(layer);
        layer === this.route.endMarker && this.map.removeLayer(layer);
      });
    },
    reloadMap() {
      this.map = {};
      this.map = L.map("map", { zoomControl: false }).setView(
        [-23.550164, -46.633664],
        13
      );
    },
    addIntermediate() {
      this.addresses.intermediates.push({
        attributes: {
          name: "",
          id: "",
        },
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
</style>
