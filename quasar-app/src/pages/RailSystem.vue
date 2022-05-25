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
        <q-tab name="settings" label="Settings"/>
      </q-tabs>
      <q-tab-panels v-model="panel">
        <q-tab-panel name="map">
          <map-view :rail-system="railSystem"/>
        </q-tab-panel>
        <q-tab-panel name="segments">
          <segments-view :rail-system="railSystem"/>
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
import SettingsView from "components/rs/SettingsView.vue";

export default {
  name: "RailSystemPage",
  components: { SettingsView, SegmentsView, MapView },
  data() {
    return {
      panel: "map",
      railSystem: null,

      linkTokens: []
    }
  },
  beforeRouteEnter(to, from, next) {
    const id = parseInt(to.params.id);
    const rsStore = useRailSystemsStore();
    rsStore.selectRailSystem(id).then(() => {
      next(vm => vm.railSystem = rsStore.selectedRailSystem);
    });
  },
  beforeRouteUpdate(to, from) {
    const id = parseInt(to.params.id);
    const rsStore = useRailSystemsStore();
    rsStore.selectRailSystem(id).then(() => {
      this.railSystem = rsStore.selectedRailSystem;
    });
  }
};
</script>

<style scoped>

</style>
