<template>
  <div class="row">
    <div class="col-md-8" id="railSystemMapCanvasContainer">
      <canvas id="railSystemMapCanvas" height="600">
        Your browser doesn't support canvas.
      </canvas>
    </div>
    <q-scroll-area class="col-md-4" v-if="railSystem.selectedComponents.length > 0">
      <div class="row" v-for="component in railSystem.selectedComponents" :key="component.id">
        <div class="col full-width">
          <selected-component-view :component="component"/>
        </div>

      </div>
    </q-scroll-area>
  </div>
  <q-page-sticky position="bottom-right" :offset="[25, 25]">
    <q-fab icon="add" direction="up" color="accent">
      <q-fab-action @click="addSignalData.toggle = true">
        <q-icon><img src="~assets/icons/signal_icon.svg"/></q-icon>
        <q-tooltip>Add Signal</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addSegmentBoundaryData.toggle = true">
        <q-icon><img src="~assets/icons/segment-boundary_icon.svg"/></q-icon>
        <q-tooltip>Add Segment Boundary</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addSwitchData.toggle = true">
        <q-icon><img src="~assets/icons/switch_icon.svg"/></q-icon>
        <q-tooltip>Add Switch</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addLabelData.toggle = true">
        <q-icon><img src="~assets/icons/label_icon.svg"/></q-icon>
        <q-tooltip>Add Label</q-tooltip>
      </q-fab-action>
    </q-fab>
  </q-page-sticky>

  <!-- Add Signal Dialog -->
  <add-component-dialog
    v-model="addSignalData"
    type="SIGNAL"
    :rail-system="railSystem"
    title="Add Signal"
    success-message="Signal added."
  >
    <template #subtitle>
      <p>
        Add a signal to the rail system.
      </p>
    </template>
    <template #default>
      <q-card-section>
        <q-select
          v-model="addSignalData.segment"
          :options="railSystem.segments"
          option-value="id"
          option-label="name"
          label="Segment"
        />
      </q-card-section>
    </template>
  </add-component-dialog>

  <!-- Add Segment boundary -->
  <add-component-dialog
    title="Add Segment Boundary"
    success-message="Segment boundary added."
    :rail-system="railSystem"
    type="SEGMENT_BOUNDARY"
    v-model="addSegmentBoundaryData"
  >
    <template #subtitle>
      <p>
        Add a segment boundary to the rail system.
      </p>
    </template>
    <template #default>
      <q-card-section>
        <q-select
          v-model="addSegmentBoundaryData.segments"
          :options="railSystem.segments"
          multiple
          :option-value="segment => segment"
          :option-label="segment => segment.name"
          label="Segments"
        />
      </q-card-section>
    </template>
  </add-component-dialog>

  <!-- Add Switch dialog -->
  <add-component-dialog
    title="Add Switch"
    success-message="Switch added."
    :rail-system="railSystem"
    type="SWITCH"
    v-model="addSwitchData"
  >
    <template #subtitle>
      <p>
        Add a switch to the rail system.
      </p>
    </template>
    <template #default>
      <q-card-section>
        <div class="row">
          <q-list>
            <q-item
              v-for="config in addSwitchData.possibleConfigurations"
              :key="config.key"
            >
              <q-item-section>
                <q-item-label>
                  <q-chip
                    v-for="node in config.nodes"
                    :key="node.id"
                    :label="node.name"
                  />
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </div>

        <div class="row">
          <div class="col-sm-6">
            <q-select
              v-model="addSwitchData.pathNode1"
              :options="getEligibleSwitchNodes()"
              :option-value="segment => segment"
              :option-label="segment => segment.name"
              label="First Path Node"
            />
          </div>
          <div class="col-sm-6">
            <q-select
              v-model="addSwitchData.pathNode2"
              :options="getEligibleSwitchNodes()"
              :option-value="segment => segment"
              :option-label="segment => segment.name"
              label="Second Path Node"
            />
          </div>
        </div>
        <div class="row">
          <q-btn label="Add Configuration" color="primary" @click="addSwitchConfiguration"/>
        </div>

      </q-card-section>
    </template>
  </add-component-dialog>

  <!-- Add Label dialog -->
  <add-component-dialog
    title="Add Label"
    success-message="Added label."
    :rail-system="railSystem"
    type="LABEL"
    v-model="addLabelData"
  >
    <template #subtitle>
      <p>
        Add a label to the rail system as a piece of text on the map. Labels
        are purely a visual component, and do not interact with the system
        in any way, besides being a helpful point of reference for users.
      </p>
    </template>
    <template #default>
      <q-card-section>
        <q-input
          label="Label Text"
          type="text"
          v-model="addLabelData.text"
        />
      </q-card-section>
    </template>
  </add-component-dialog>
</template>

<script>
import {RailSystem} from "src/api/railSystems";
import {draw, initMap} from "src/render/mapRenderer";
import SelectedComponentView from "components/rs/SelectedComponentView.vue";
import {useQuasar} from "quasar";
import AddComponentDialog from "components/rs/add_component/AddComponentDialog.vue";

export default {
  name: "MapView",
  components: { AddComponentDialog, SelectedComponentView },
  setup() {
    const quasar = useQuasar();
    return {quasar};
  },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  data() {
    return {
      addSignalData: {
        name: "",
        position: {
          x: 0, y: 0, z: 0
        },
        segment: null,
        toggle: false
      },
      addSegmentBoundaryData: {
        name: "",
        position: {
          x: 0, y: 0, z: 0
        },
        toggle: false,
        segments: [],
        connectedNodes: []
      },
      addSwitchData: {
        name: "",
        position: {
          x: 0, y: 0, z: 0
        },
        toggle: false,
        possibleConfigurations: [],
        // Utility properties for the UI for adding configurations.
        pathNode1: null,
        pathNode2: null
      },
      addLabelData: {
        position: {
          x: 0, y: 0, z: 0
        },
        toggle: false,
        text: ""
      }
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
  },
  methods: {
    getEligibleSwitchNodes() {
      return this.railSystem.components.filter(component => {
        return component.connectedNodes !== undefined && component.connectedNodes !== null;
      });
    },
    addSwitchConfiguration() {
      if (
        this.addSwitchData.pathNode1 === null ||
        this.addSwitchData.pathNode2 === null ||
        this.addSwitchData.pathNode1.id === this.addSwitchData.pathNode2.id ||
        this.addSwitchData.possibleConfigurations.some(config => {
          // Check if there's already a configuration containing both of these nodes.
          return config.nodes.every(node =>
            node.id === this.addSwitchData.pathNode1.id ||
            node.id === this.addSwitchData.pathNode2.id);
        })
      ) {
        this.quasar.notify({
          color: "warning",
          message: "Invalid switch configuration."
        });
        return;
      }
      // All good!
      this.addSwitchData.possibleConfigurations.push({
        nodes: [
          this.addSwitchData.pathNode1,
          this.addSwitchData.pathNode2
        ],
        // A unique key, just for the frontend to use. This is not used by the API.
        key: this.addSwitchData.pathNode1.id + "_" + this.addSwitchData.pathNode2.id
      });
    }
  }
};
</script>

<style scoped>

</style>
