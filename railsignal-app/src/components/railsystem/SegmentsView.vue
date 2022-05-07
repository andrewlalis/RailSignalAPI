<template>
  <h3>Segments</h3>
  <ul>
    <li v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id">
      {{segment.name}} <button @click.prevent="rsStore.removeSegment(segment.id)">Remove</button>
    </li>
  </ul>
  <AddSegment />
</template>

<script>
import AddSegment from "./AddSegment.vue";
import {useRailSystemsStore} from "../../stores/railSystemsStore";

export default {
  name: "SegmentsView.vue",
  components: {
    AddSegment
  },
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    }
  }
}
</script>

<style scoped>

</style>