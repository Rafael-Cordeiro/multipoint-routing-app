<template>
  <q-page class="q-pl-xl q-pr-xl">
    <q-form class="row justify-evenly items-center" @submit.prevent="handleSubmit">
      <div v-if="path.route.length > 0" class="col-4">
        <span class="text-h5">Rota multiponto ordenada</span>
        <RoutePipelineView :route="path.route.map(it => {
          return {
            address: it.address,
            color: it.color,
          }
        })" />
      </div>
      <div class="col-4">
        <span class="text-h5">Insira os endereços</span>
        <SelectAddress :address="addresses.source" :options="addresses.options" />
        <div v-for="(intermediate, index) in addresses.intermediates" :key="index" class="q-ml-xl">
          <SelectAddress :address="intermediate" :options="addresses.options" />
        </div>
        <div class="column items-center">
          <q-btn v-bind:class="addresses.intermediates.length > 0 ? 'q-ml-xl q-ma-sm' : 'q-ma-sm'
            " round icon="add" :size="'md'" color="green" @click="addIntermediate" />
        </div>
        <SelectAddress :address="addresses.destination" :options="addresses.options" />
      </div>
      <div class="col-2 q-px-md">
        <div class="column">
          <q-btn color="primary" type="submit" label="Calcular rota" icon="directions" :loading="submitting">
            <template v-slot:loading>
              <q-spinner-facebook />
            </template>
          </q-btn>
          <q-btn @click="clearData" icon="clear" label="Limpar formulário e mapa" />
        </div>
      </div>
    </q-form>
    <div id="map" class="leaflet-container q-my-xl" style="width: 100%; height: 70vh"></div>
  </q-page>
</template>

<script setup>
import neo4j from "neo4j-driver";
import L from "leaflet";
import axios from "axios";
import SelectAddress from "src/components/SelectAddress.vue";
import RoutePipelineView from 'src/components/RoutePipelineView.vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

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
      path: {
        route: [],
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
      loadingNotify: {},
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
    async handleSubmit(event) {
      event.preventDefault();
      this.submitting = true;
      try {
        await this.fetchRoute({
          source: this.addresses.source.attributes,
          intermediates: this.addresses.intermediates.map((it) => (it.attributes.id != '') ? it.attributes : null),
          destination: this.addresses.destination.attributes,
        });
      } catch (error) {
        console.error(`An error was occurred: ${error}`)
      }
      this.submitting = false;
    },
    async fetchRoute(body) {
      const start = Date.now();
      this.displayLoadingNotify();
      await axios
        .post("http://localhost:8080/routing", body)
        .then((response) => response.data.entity)
        .then((multipointRoute) => {
          var startMarker = L.marker(
            L.latLng(
              multipointRoute.source.address.location.y,
              multipointRoute.source.address.location.x
            )).bindPopup(multipointRoute.source.address.name).addTo(this.map).openPopup()

          this.path.route.push({
            address: multipointRoute.source.address,
            color: '#000000',
            marker: startMarker,
            polyline: [],
          })

          multipointRoute.paths.forEach((path) => {
            let routeItem = {}
            routeItem.marker = L.marker(L.latLng(path.point.address.location.y, path.point.address.location.x))
              .bindPopup(path.point.address.name).addTo(this.map).openPopup();

            routeItem.color = this.getRandomColor()
            routeItem.polyline = L.polyline(path.line.map((item) => [item.y, item.x]))
              .setStyle({ color: routeItem.color, weight: 7, className: 'bordered-polyline' })
              .addTo(this.map);

            routeItem.address = path.point.address
            this.path.route.push(routeItem)
          });

          // this.map.panInsideBounds(L.latLngBounds(marker1, marker2));
          var end = Date.now()
          this.closeLoadingNotifyThenDisplayDoneNotify(end - start)
          console.log(`Consulta de rota e montagem de mapa executou em ${end - start} ms`);

        })
        .catch((error) => console.error(`[Error from axios]: ${error}`));
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
        this.path.route.forEach(item => {
          layer === item.polyline && this.map.removeLayer(layer);
          layer === item.marker && this.map.removeLayer(layer);
        })
      });
      this.path.route = []
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
    getRandomColor() {
      var letters = "0123456789ABCDEF";
      var color = "#";
      for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
      }
      return color;
    },
    displayLoadingNotify() {
      this.loadingNotify = this.$q.notify({
        group: false,
        timeout: 0,
        spinner: true,
        message: 'Calculando rota',
      })
    },
    closeLoadingNotifyThenDisplayDoneNotify(millis) {
      this.loadingNotify({
        group: false,
        timeout: 2500,
        spinner: false,
        message: 'Rota calculada com sucesso',
        caption: `Tempo de execução: ${millis} ms`,
      })
    }
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

.bordered-polyline {
  filter: drop-shadow(1px 1px 0 #000) drop-shadow(-1px -1px 0 #000) drop-shadow(1px -1px 0 #000) drop-shadow(-1px 1px 0 #000);
}
</style>
