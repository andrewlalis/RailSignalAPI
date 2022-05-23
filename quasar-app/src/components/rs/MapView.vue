<template>
  <div class="row">
    <div class="col-md-8" id="railSystemMapCanvasContainer">
      <canvas id="railSystemMapCanvas" height="800">
        Your browser doesn't support canvas.
      </canvas>
    </div>
    <q-scroll-area class="col-md-4" v-if="railSystem.selectedComponents.length > 0">
      <div class="row" v-for="component in railSystem.selectedComponents" :key="component.id">
        <div class="col full-width">
          <selected-component-view :component="component" :rail-system="railSystem" />
        </div>

      </div>
    </q-scroll-area>
  </div>
  <q-page-sticky position="bottom-right" :offset="[25, 25]">
    <q-fab icon="add" direction="up" color="accent">
      <q-fab-action @click="addSignalDialog = true">
        <q-icon><img src="~assets/icons/signal_icon.svg"/></q-icon>
        <q-tooltip>Add Signal</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addSegmentBoundaryDialog = true">
        <q-icon><img src="~assets/icons/segment-boundary_icon.svg"/></q-icon>
        <q-tooltip>Add Segment Boundary</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addSwitchDialog = true">
        <q-icon><img src="~assets/icons/switch_icon.svg"/></q-icon>
        <q-tooltip>Add Switch</q-tooltip>
      </q-fab-action>
      <q-fab-action @click="addLabelDialog = true">
        <q-icon><img src="~assets/icons/label_icon.svg"/></q-icon>
        <q-tooltip>Add Label</q-tooltip>
      </q-fab-action>
    </q-fab>
  </q-page-sticky>

  <!-- Add Signal Dialog -->
  <q-dialog v-model="addSignalDialog" style="min-width: 400px" @hide="resetAll">
    <q-card>
      <q-form @submit="onAddSignalSubmit" @reset="resetAll">
        <q-card-section>
          <div class="text-h6">Add Signal</div>
          <p>
            Add a signal to the rail system.
          </p>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Name"
            type="text"
            v-model="addComponentData.name"
          />
          <div class="row">
            <q-input
              label="X"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.x"
            />
            <q-input
              label="Y"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.y"
            />
            <q-input
              label="Z"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.z"
            />
          </div>
        </q-card-section>
        <q-card-section>
          <q-select
            v-model="addSignalData.segment"
            :options="railSystem.segments"
            option-value="id"
            option-label="name"
            label="Segment"
          />
        </q-card-section>
        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancel" type="reset" @click="addSignalDialog = false"/>
          <q-btn flat label="Add Signal" type="submit"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>

  <!-- Add Segment boundary -->
  <q-dialog v-model="addSegmentBoundaryDialog" style="min-width: 400px" @hide="resetAll">
    <q-card>
      <q-form @submit="onAddSegmentBoundarySubmit" @reset="resetAll">
        <q-card-section>
          <div class="text-h6">Add Segment Boundary</div>
          <p>
            Add a segment boundary to the rail system. A segment boundary is a
            point where a train can cross between two segments, or "blocks" in
            the system. For example, a train may move from a junction segment to
            a main line. This boundary is a place where a detector component can
            update the system as trains pass.
          </p>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Name"
            type="text"
            v-model="addComponentData.name"
          />
          <div class="row">
            <q-input
              label="X"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.x"
            />
            <q-input
              label="Y"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.y"
            />
            <q-input
              label="Z"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.z"
            />
          </div>
        </q-card-section>
        <q-card-section>
          <q-select
            v-model="addSegmentBoundaryData.segments"
            :options="railSystem.segments"
            multiple
            option-value="id"
            option-label="name"
            label="Segments"
          />
        </q-card-section>
        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancel" type="reset" @click="addSegmentBoundaryDialog = false"/>
          <q-btn flat label="Add Segment Boundary" type="submit"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>

  <!-- Add Switch dialog -->
  <q-dialog v-model="addSwitchDialog" @hide="resetAll"></q-dialog>

  <!-- Add Label dialog -->
  <q-dialog v-model="addLabelDialog" style="min-width: 400px" @hide="resetAll">
    <q-card>
      <q-form @submit="onAddLabelSubmit" @reset="resetAll">
        <q-card-section>
          <div class="text-h6">Add Label</div>
          <p>
            Add a label to the rail system as a piece of text on the map. Labels
            are purely a visual component, and do not interact with the system
            in any way, besides being a helpful point of reference for users.
          </p>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Name"
            type="text"
            v-model="addComponentData.name"
          />
          <div class="row">
            <q-input
              label="X"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.x"
            />
            <q-input
              label="Y"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.y"
            />
            <q-input
              label="Z"
              type="number"
              class="col-sm-4"
              v-model="addComponentData.position.z"
            />
          </div>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Label Text"
            type="text"
            v-model="addLabelData.text"
          />
        </q-card-section>
        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancel" type="reset" @click="addLabelDialog = false"/>
          <q-btn flat label="Add Label" type="submit"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { draw, initMap } from "src/render/mapRenderer";
import SelectedComponentView from "components/rs/SelectedComponentView.vue";
import { useQuasar } from "quasar";
import { createComponent } from "src/api/components";

export default {
  name: "MapView",
  components: { SelectedComponentView },
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
      addSignalDialog: false,
      addSegmentBoundaryDialog: false,
      addSwitchDialog: false,
      addLabelDialog: false,
      addComponentData: {
        name: "",
        position: {
          x: 0, y: 0, z: 0
        }
      },
      addSignalData: {
        segment: null
      },
      addSegmentBoundaryData: {
        segments: []
      },
      addLabelData: {
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
    onAddSignalSubmit() {
      const data = {...this.addComponentData, ...this.addSignalData};
      data.type = "SIGNAL";
      this.attemptCreateComponent(data, "Signal added.", () => this.addSignalDialog = false);
    },
    onAddSegmentBoundarySubmit() {
      const data = {...this.addComponentData, ...this.addSegmentBoundaryData};
      data.type = "SEGMENT_BOUNDARY";
      this.attemptCreateComponent(data, "Segment boundary added.", () => this.addSegmentBoundaryDialog = false);
    },
    segmentBoundarySegmentsValid() {
      const set = new Set(this.addSegmentBoundaryData.segments);
      return set.size === 2 && set.size === this.addSegmentBoundaryData.segments.length;
    },
    onAddLabelSubmit() {
      const data = {...this.addComponentData, ...this.addLabelData};
      data.type = "LABEL";
      this.attemptCreateComponent(data, "Label added.", () => this.addLabelDialog = false);
    },
    resetAll() {
      this.addComponentData.name = "";
      this.addComponentData.position.x = 0;
      this.addComponentData.position.y = 0;
      this.addComponentData.position.z = 0;
      this.addSignalDialog.segment = null;
      this.addSegmentBoundaryData.segments = [];
      this.addLabelData.text = "";
    },
    attemptCreateComponent(data, successMessage, successHandler) {
      createComponent(this.railSystem, data)
        .then(() => {
          this.quasar.notify({
            color: "positive",
            message: successMessage
          });
          successHandler();
        })
        .catch(error => {
          console.log(error);
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    }
  }
};
</script>

<style scoped>

</style>
