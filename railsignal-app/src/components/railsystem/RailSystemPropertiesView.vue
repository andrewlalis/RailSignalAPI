<template>
  <h3>Rail System: <em>{{railSystem.name}}</em></h3>
  <SegmentsView />
  <button
      type="button"
      class="btn btn-success btn-sm me-2"
      data-bs-toggle="modal"
      data-bs-target="#addSegmentModal"
  >
    Add Segment
  </button>
  <AddSegmentModal />
  <span v-if="railSystem.segments && railSystem.segments.length > 0">
    <button
        type="button"
        class="btn btn-success btn-sm me-2"
        data-bs-toggle="modal"
        data-bs-target="#addSignalModal"
    >
      Add Signal
    </button>
    <AddSignalModal />
    <button
      type="button"
      class="btn btn-success btn-sm me-2"
      data-bs-toggle="modal"
      data-bs-target="#addSegmentBoundaryModal"
    >
      Add Segment Boundary
    </button>
    <AddSegmentBoundaryModal />
  </span>
  <span v-if="railSystem.components && railSystem.components.length > 1">
    <button
        type="button"
        class="btn btn-success btn-sm"
        data-bs-toggle="modal"
        data-bs-target="#addSwitchModal"
    >
      Add Switch
    </button>
    <AddSwitchModal />
  </span>
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
  }
}
</script>

<style scoped>

</style>