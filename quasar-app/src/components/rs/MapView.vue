<template>
  <div class="row">
    <div class="col-md-8" id="railSystemMapCanvasContainer">
      <canvas id="railSystemMapCanvas" height="800">
        Your browser doesn't support canvas.
      </canvas>
    </div>
    <div class="col-md-4" v-if="railSystem.selectedComponent">
      <selected-component-view :component="railSystem.selectedComponent" />
    </div>
  </div>

</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { draw, initMap } from "src/render/mapRenderer";
import SelectedComponentView from "components/rs/SelectedComponentView.vue";

export default {
  name: "MapView",
  components: { SelectedComponentView },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  mounted() {
    initMap(this.railSystem);
  },
  updated() {
    initMap(this.railSystem);
  },
  watch: {
    railSystem: {
      handler() {
        draw();
      },
      deep: true
    }
  }
};
</script>

<style scoped>

</style>
