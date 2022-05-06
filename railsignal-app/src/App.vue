<template>
  <header>
    <h1>RailSignal</h1>
  </header>
  <RailSystemsManager />

  <RailSystem
      v-if="rsStore.selectedRailSystem !== null"
      :railSystem="rsStore.selectedRailSystem"
  />
</template>

<script>
import RailSystem from "./components/RailSystem.vue";
import RailSystemsManager from "./components/RailSystemsManager.vue";
import {useRailSystemsStore} from "./stores/railSystemsStore";

export default {
  components: {
    RailSystem,
    RailSystemsManager
  },
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  }
}
</script>

<style>
body {
  padding: 0;
  margin: 0;
}
</style>
