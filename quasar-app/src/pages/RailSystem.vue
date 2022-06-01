<template>
  <q-page>
    <div v-if="railSystem && railSystem.loaded">
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

    <q-inner-loading
      :showing="!railSystem || !railSystem.loaded"
      label="Loading rail system..."
    />
  </q-page>
</template>

<script>
import { useRailSystemsStore } from "stores/railSystemsStore";
import MapView from "components/rs/MapView.vue";
import SegmentsView from "components/rs/SegmentsView.vue";
import SettingsView from "components/rs/SettingsView.vue";
import { loadData, unloadData } from "src/api/railSystems";

export default {
  name: "RailSystemPage",
  components: { SettingsView, SegmentsView, MapView },
  data() {
    return {
      panel: "map",
      railSystem: null,
      loading: false
    }
  },
  setup() {
    const rsStore = useRailSystemsStore();
    return {rsStore};
  },
  mounted() {
    this.updateRailSystem();
  },
  created() {
    this.$watch(
      () => this.$route.params,
      this.updateRailSystem,
      {
        immediate: true
      }
    )
  },
  methods: {
    async updateRailSystem() {
      if (this.loading) return;
      this.loading = true;
      console.log(">>>> updating rail system.")
      if (this.railSystem) {
        this.rsStore.selectedRailSystem = null;
        await unloadData(this.railSystem);
      }
      if (this.$route.params.id) {
        const newRsId = parseInt(this.$route.params.id);
        const rs = this.rsStore.railSystems.find(r => r.id === newRsId);
        if (rs) {
          this.railSystem = rs;
          this.rsStore.selectedRailSystem = rs;
          await loadData(rs);
        }
      }
      this.loading = false;
    }
  }
};
</script>

<style scoped>

</style>
