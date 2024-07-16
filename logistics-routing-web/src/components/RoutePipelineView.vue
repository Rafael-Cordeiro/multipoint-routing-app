<template>
  <div :class="[index > 0 && index+1 < route.length ? 'q-ml-md' : '', 'q-my-md']" v-for="(section, index) in route" v-bind:key="index">
    <q-avatar :size="'xs'" :style="styles[index].style" /> {{ section.address.name }}
  </div>
</template>
<script>
export default {
  name: 'RoutePipelineView',
  props: {
    route: {
      type: Array,
      required: true,
    },
  },
  computed: {
    styles() {
      return this.route.map((section, index) => {
        return {
          index,
          style: {
            "background-color": section.color
          }
        }
      })
    }
  }
}
</script>
