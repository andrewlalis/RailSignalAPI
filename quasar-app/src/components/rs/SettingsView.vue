<template>
  <div>
    <p>
      Settings view. Nothing here yet.
    </p>
  </div>
</template>

<script>
import { RailSystem } from "src/api/railSystems";

export default {
  name: "SettingsView",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
