<script>
import {initMap} from "./mapRenderer.js";

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
  }
}
</script>

<template>
  <canvas id="railSystemMapCanvas" width="1000" height="600">
      Your browser doesn't support canvas!
  </canvas>
</template>

<style>
canvas {
  border: 1px solid black;
}
</style>