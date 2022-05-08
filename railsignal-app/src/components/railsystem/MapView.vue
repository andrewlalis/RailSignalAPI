<script>
import {initMap, draw} from "./mapRenderer.js";

export default {
  props: {
    railSystem: {
      type: Object,
      required: true
    }
  },
  mounted() {
    // The first time this map is mounted, initialize the map.
    initMap(this.railSystem);
  },
  updated() {
    // Also, re-initialize any time this view is updated.
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
}
</script>

<template>
  <div class="canvas-container" id="railSystemMapCanvasContainer">
    <canvas id="railSystemMapCanvas">
      Your browser doesn't support canvas!
    </canvas>
  </div>
</template>

<style>
.canvas-container {
  width: 100%;
  height: 800px;
}
canvas {
  width: 100%;
  height: 100%;
}
</style>