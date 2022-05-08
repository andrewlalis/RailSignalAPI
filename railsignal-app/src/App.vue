<template>
  <AppNavbar />
  <RailSystem v-if="rsStore.selectedRailSystem !== null" :railSystem="rsStore.selectedRailSystem"/>
</template>

<script>
import {useRailSystemsStore} from "./stores/railSystemsStore";
import AppNavbar from "./components/AppNavbar.vue";
import RailSystem from "./components/RailSystem.vue";

export default {
  components: {
    AppNavbar,
    RailSystem
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
