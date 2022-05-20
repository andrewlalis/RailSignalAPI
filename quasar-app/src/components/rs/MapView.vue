<template>
  <div class="row bg-green-4">
    <div class="col-md-8 bg-blue-2" id="railSystemMapCanvasContainer">
      <canvas id="railSystemMapCanvas" height="800">
        Your browser doesn't support canvas.
      </canvas>
    </div>
    <div class="col-md-4 bg-amber">
      <p>
        Info panel
      </p>
    </div>
  </div>

</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { draw, initMap } from "src/render/mapRenderer";

export default {
  name: "MapView",
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
