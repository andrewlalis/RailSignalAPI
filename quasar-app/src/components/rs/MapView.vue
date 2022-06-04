<template>
  <div class="row">
    <div class="col-md-8" id="railSystemMapCanvasContainer">
      <canvas id="railSystemMapCanvas" height="700">
        Your browser doesn't support canvas.
      </canvas>
    </div>
    <q-scroll-area class="col-md-4">
      <div class="row" v-for="component in railSystem.selectedComponents" :key="component.id">
        <div class="col full-width">
          <selected-component-view :component="component"/>
        </div>
      </div>
      <add-component-form v-if="addComponent.visible" :rail-system="railSystem" @created="addComponent.visible = false"/>
    </q-scroll-area>
  </div>
  <q-page-sticky position="bottom-right" :offset="[25, 25]">
    <q-fab icon="add" color="accent" v-model="addComponent.visible"/>
  </q-page-sticky>
</template>

<script>
import {RailSystem} from "src/api/railSystems";
import {draw, initMap} from "src/map/mapRenderer";
import SelectedComponentView from "components/rs/SelectedComponentView.vue";
import {useQuasar} from "quasar";
import AddComponentForm from "components/rs/add_component/AddComponentForm.vue";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { registerComponentSelectionListener } from "src/map/mapEventListener";

export default {
  name: "MapView",
  components: { AddComponentForm, SelectedComponentView },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {quasar, rsStore};
  },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  data() {
    return {
      addComponent: {
        visible: false
      }
    }
  },
  mounted() {
    initMap(this.railSystem);
    registerComponentSelectionListener("addComponentFormHide", () => this.addComponent.visible = false);
  },
  updated() {
    initMap(this.railSystem);
    registerComponentSelectionListener("addComponentFormHide", () => this.addComponent.visible = false);
  },
  watch: {
    railSystem: {
      handler() {
        draw();
      },
      deep: true
    },
    'addComponent.visible'(newValue) { // Deselect all components when the user opens the "Add Component" form.
      if (newValue === true) {
        this.rsStore.selectedRailSystem.selectedComponents.length = 0;
      }
    }
  }
};
</script>

<style scoped>

</style>
