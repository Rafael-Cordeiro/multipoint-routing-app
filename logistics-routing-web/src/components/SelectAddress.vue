<template>
  <q-select
    v-model="internalAddress.attributes"
    rounded
    outlined
    use-input
    :options="internalOptions"
    :option-label="(item) => (item === null ? '' : item.name)"
    @filter="filterHandler"
    @update:model-value="internalOptions = []"
    behavior="dialog"
    class="q-ma-xs"
  />
</template>
<script>
import axios from "axios";

export default {
  name: "SelectAddress",
  props: {
    address: {
      type: Object,
      required: true,
    },
    options: {
      type: Array,
      required: true,
    },
  },
  emits: {},
  watch: {
    address() {
      this.internalAddress = this.address
    },
  },
  data() {
    return {
      internalAddress: this.address,
      internalOptions: this.options,
    };
  },
  methods: {
    async filterHandler(val, update) {
      if (val.length < 4) {
        update(() => {
          this.internalOptions = [];
        });
        return;
      }
      await axios.get("http://localhost:8080/address", {
          params: {
            name: val
          }
        })
        .then((response) => response.data.entities)
        .then((addresses) => {
          this.internalOptions = [];
          update(() => {
            this.internalOptions = addresses
          })})
    },
  },
};
</script>
