<template>
  <h5>Segments</h5>
  <input type="search" class="form-control-sm w-100 mb-1" placeholder="Filter by name" v-model="segmentNameFilter" />
  <ul class="list-group overflow-auto mb-2" style="max-height: 200px;">
    <li
        v-for="segment in filteredSegments()"
        :key="segment.id"
        class="list-group-item"
    >
      {{segment.name}}
      <button @click.prevent="removeSegment(rsStore.selectedRailSystem, segment.id)" class="btn btn-sm btn-danger float-end">Remove</button>
    </li>
  </ul>
</template>

<script>
import {useRailSystemsStore} from "../../stores/railSystemsStore";
import {removeSegment} from "../../api/segments";

export default {
  name: "SegmentsView.vue",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore,
      removeSegment
    }
  },
  props: {
    segments: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      segmentNameFilter: null
    }
  },
  methods: {
    filteredSegments() {
      if (this.segmentNameFilter === null || this.segmentNameFilter.trim().length === 0) return this.segments;
      const filterString = this.segmentNameFilter.trim().toLowerCase();
      return this.segments.filter(segment => segment.name.toLowerCase().includes(filterString));
    }
  }
}
</script>

<style scoped>

</style>