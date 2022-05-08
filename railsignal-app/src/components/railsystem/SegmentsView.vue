<template>
  <h5>Segments</h5>
  <ul class="list-group overflow-auto mb-2" style="max-height: 200px;">
    <li
        v-for="segment in rsStore.selectedRailSystem.segments"
        :key="segment.id"
        class="list-group-item"
    >
      {{segment.name}}
      <button @click.prevent="rsStore.removeSegment(segment.id)" class="btn btn-sm btn-danger float-end">Remove</button>
    </li>
  </ul>
</template>

<script>
import {useRailSystemsStore} from "../../stores/railSystemsStore";

export default {
  name: "SegmentsView.vue",
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