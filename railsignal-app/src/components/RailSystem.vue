<template>
  <h2>{{railSystem.name}}</h2>
  <div>
    <MapView :railSystem="railSystem" v-if="railSystem.segments && railSystem.components" />
    <ComponentView v-if="railSystem.selectedComponent" :component="railSystem.selectedComponent"/>
  </div>
  <SegmentsView />
  <AddSignal v-if="railSystem.segments && railSystem.segments.length > 0" />
  <AddSegmentBoundary v-if="railSystem.segments && railSystem.segments.length > 0" />
</template>

<script>
import MapView from './railsystem/MapView.vue'
import ComponentView from './railsystem/component/ComponentView.vue'
import SegmentsView from "./railsystem/SegmentsView.vue";
import AddSignal from "./railsystem/component/AddSignal.vue";
import AddSegmentBoundary from "./railsystem/component/AddSegmentBoundary.vue";

export default {
  components: {
    AddSignal,
    AddSegmentBoundary,
    SegmentsView,
    MapView,
    ComponentView
  },
  props: {
    railSystem: {
      type: Object,
      required: true
    }
  }
}
</script>

<style>
</style>
