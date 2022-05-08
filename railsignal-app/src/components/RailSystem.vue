<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-md-8 p-0">
        <MapView :railSystem="railSystem" v-if="railSystem.segments && railSystem.components" />
      </div>
      <div class="col-md-4">
        <ComponentView v-if="railSystem.selectedComponent" :component="railSystem.selectedComponent" :railSystem="railSystem"/>
        <RailSystemPropertiesView v-if="!railSystem.selectedComponent" :railSystem="railSystem"/>
      </div>
    </div>
  </div>
</template>

<script>
import MapView from './railsystem/MapView.vue'
import ComponentView from './railsystem/component/ComponentView.vue'
import RailSystemPropertiesView from "./railsystem/RailSystemPropertiesView.vue";

export default {
  components: {
    RailSystemPropertiesView,
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
