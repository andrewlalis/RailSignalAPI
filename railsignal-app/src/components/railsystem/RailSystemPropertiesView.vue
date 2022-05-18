<template>
  <h3>Rail System: <em>{{railSystem.name}}</em></h3>
  <SegmentsView :segments="railSystem.segments" v-if="railSystem.segments"/>
  <div class="dropdown">
    <button class="btn btn-success btn-sm dropdown-toggle" type="button" id="railSystemAddComponentsToggle" data-bs-toggle="dropdown" aria-expanded="false">
      Add Component
    </button>
    <ul class="dropdown-menu" aria-labelledby="railSystemAddComponentsToggle">
      <li>
        <button
            type="button"
            class="dropdown-item"
            data-bs-toggle="modal"
            data-bs-target="#addSegmentModal"
        >
          Add Segment
        </button>
      </li>
      <li v-if="addSignalAllowed()">
        <button
            type="button"
            class="dropdown-item"
            data-bs-toggle="modal"
            data-bs-target="#addSignalModal"
        >
          Add Signal
        </button>
      </li>
      <li v-if="addSegmentBoundaryAllowed()">
        <button
            type="button"
            class="dropdown-item"
            data-bs-toggle="modal"
            data-bs-target="#addSegmentBoundaryModal"
        >
          Add Segment Boundary
        </button>
      </li>
      <li v-if="addSwitchAllowed()">
        <button
            type="button"
            class="dropdown-item"
            data-bs-toggle="modal"
            data-bs-target="#addSwitchModal"
        >
          Add Switch
        </button>
      </li>
    </ul>
  </div>
  <AddSegmentModal />
  <AddSignalModal v-if="addSignalAllowed()" />
  <AddSegmentBoundaryModal v-if="addSegmentBoundaryAllowed()" />
  <AddSwitchModal v-if="addSwitchAllowed()" />
</template>

<script>
import SegmentsView from "./SegmentsView.vue";
import AddSegmentModal from "./AddSegmentModal.vue";
import AddSignalModal from "./component/AddSignalModal.vue";
import AddSegmentBoundaryModal from "./component/AddSegmentBoundaryModal.vue";
import AddSwitchModal from "./component/AddSwitchModal.vue";

export default {
  name: "RailSystemPropertiesView",
  components: {
    AddSwitchModal,
    AddSegmentBoundaryModal,
    AddSignalModal,
    AddSegmentModal,
    SegmentsView
  },
  props: {
    railSystem: {
      type: Object,
      required: true
    }
  },
  methods: {
    addSignalAllowed() {
      return this.railSystem.segments && this.railSystem.segments.length > 0
    },
    addSegmentBoundaryAllowed() {
      return this.railSystem.segments && this.railSystem.segments.length > 1
    },
    addSwitchAllowed() {
      return this.railSystem.components && this.railSystem.components.length > 1
    }
  }
}
</script>

<style scoped>

</style>