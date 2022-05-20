<template>
  <q-page>
    <div v-if="railSystem">
      <q-tabs
        v-model="panel"
        align="left"
        active-bg-color="positive"
        class="bg-secondary"
      >
        <q-tab name="map" label="Map"/>
        <q-tab name="segments" label="Segments"/>
        <q-tab name="components" label="Components"/>
        <q-tab name="settings" label="Settings"/>
      </q-tabs>
      <q-tab-panels v-model="panel">
        <q-tab-panel name="map">
          <map-view :rail-system="railSystem"/>
        </q-tab-panel>
        <q-tab-panel name="segments">
          <segments-view :rail-system="railSystem"/>
        </q-tab-panel>
        <q-tab-panel name="components">
          <components-view :rail-system="railSystem"/>
        </q-tab-panel>
        <q-tab-panel name="settings">
          <settings-view :rail-system="railSystem"/>
        </q-tab-panel>
      </q-tab-panels>
      <router-view />
    </div>

  </q-page>
</template>

<script>
import { useRailSystemsStore } from "stores/railSystemsStore";
import MapView from "components/rs/MapView.vue";
import SegmentsView from "components/rs/SegmentsView.vue";
import ComponentsView from "components/rs/ComponentsView.vue";
import SettingsView from "components/rs/SettingsView.vue";

export default {
  name: "RailSystemPage",
  components: { SettingsView, ComponentsView, SegmentsView, MapView },
  data() {
    return {
      panel: "map",
      railSystem: null
    }
  },
  async beforeRouteEnter(to, from, next) {
    const id = parseInt(to.params.id);
    const rsStore = useRailSystemsStore();
    await rsStore.selectRailSystem(id);
    next(vm => vm.railSystem = rsStore.selectedRailSystem);
  },
  async beforeRouteUpdate(to, from) {
    const id = parseInt(to.params.id);
    const rsStore = useRailSystemsStore();
    await rsStore.selectRailSystem(id);
    this.railSystem = rsStore.selectedRailSystem;
  }
};
</script>

<style scoped>

</style>
