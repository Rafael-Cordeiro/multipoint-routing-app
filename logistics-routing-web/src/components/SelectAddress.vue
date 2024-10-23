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
import neo4j from "neo4j-driver";
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
  mounted() {
    this.driver = neo4j.driver(
      this.NEO4J_URI,
      neo4j.auth.basic(this.NEO4J_USER, this.NEO4J_PASSWORD)
    );
  },
  watch: {
    address() {
      this.internalAddress = this.address
    },
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
      driver: {},
      internalAddress: this.address,
      internalOptions: this.options,
    };
  },
  methods: {
    filterHandler(val, update) {
      if (val.length < 4) {
        update(() => {
          this.internalOptions = [];
        });
        return;
      }
      this.driver
        .session()
        .run(this.SEARCH_FULLTEXT_QUERY, { searchString: val })
        .then((result) => {
          this.internalOptions = [];
          update(() => {
            result.records.forEach((record) => {
              this.internalOptions.push({
                name: record.get("value"),
                id: record.get("id"),
              });
            });
          });
        });
    },
  },
};
</script>
