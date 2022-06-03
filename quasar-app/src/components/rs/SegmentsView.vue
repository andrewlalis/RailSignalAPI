<template>
  <div class="flex">
    <div class="row full-width">
      <div class="col-md-6">
        <q-list>
          <q-item
            v-for="segment in railSystem.segments"
            :key="segment.id"
            clickable
            v-ripple
          >
            <q-item-section>
              <q-item-label>{{segment.name}}</q-item-label>
              <q-item-label caption>Id: {{segment.id}}</q-item-label>
            </q-item-section>
            <q-item-section v-if="segment.occupied" side>
              <q-chip label="Occupied"/>
            </q-item-section>

            <q-menu touch-position context-menu>
              <q-list dense style="min-width: 100px">
                <q-item clickable v-close-popup @click="toggleOccupiedInline(segment)">
                  <q-item-section>Toggle Occupied</q-item-section>
                </q-item>
                <q-item clickable v-close-popup @click="remove(segment)">
                  <q-item-section>Delete</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-item>
        </q-list>
      </div>
      <div class="col-md-6">

      </div>
    </div>
  </div>
  <q-page-sticky position="bottom-right" :offset="[18, 18]">
    <q-btn fab icon="add" color="accent" @click="openAddSegmentDialog"></q-btn>
  </q-page-sticky>
  <q-dialog v-model="showAddSegmentDialog" persistent>
    <q-card style="min-width: 400px">
      <q-form @submit="onSubmit" @reset="onReset">
        <q-card-section>
          <div class="text-h6">Add Segment</div>
          <p>
            Add a new segment to the rail system. A segment can be thought of as
            the basic building block of any rail system, and segments define a
            section of rails that trains can travel in and out of, usually one at
            a time.
          </p>
        </q-card-section>
        <q-card-section>
          <q-input
            v-model="segmentName"
            autofocus
            label="Segment Name"
            :rules="[value => value && value.length > 0]"
            lazy-rules
            type="text"
          />
        </q-card-section>
        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancel" type="reset" @click="showAddSegmentDialog = false"/>
          <q-btn flat label="Add Segment" type="submit"/>
        </q-card-actions>
      </q-form>

    </q-card>
  </q-dialog>
</template>

<script>
import { useRailSystemsStore } from "stores/railSystemsStore";
import { RailSystem } from "src/api/railSystems";
import { useQuasar } from "quasar";
import { createSegment, removeSegment, toggleOccupied } from "src/api/segments";
import { ref } from "vue";

export default {
  name: "SegmentsView",
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
  },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  data() {
    return {
      showAddSegmentDialog: false,
      segmentName: ""
    }
  },
  methods: {
    openAddSegmentDialog() {
      this.showAddSegmentDialog = true;
    },
    onSubmit() {
      createSegment(this.railSystem, this.segmentName)
        .then(() => {
          this.quasar.notify({
            color: "positive",
            message: "Segment created."
          });
          this.onReset();
          this.showAddSegmentDialog = false;
        })
        .catch(error => {
          console.log(error);
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    onReset() {
      this.segmentName = "";
    },
    toggleOccupiedInline(segment) {
      toggleOccupied(this.rsStore.selectedRailSystem, segment.id)
    },
    remove(segment) {
      this.quasar.dialog({
        title: "Confirm",
        message: "Are you sure you want to remove this segment? This will remove any connected components, and it cannot be undone.",
        cancel: true,
        persistent: true
      }).onOk(() => {
        removeSegment(this.railSystem, segment.id)
          .then(() => {
            this.quasar.notify({
              color: "positive",
              message: "Segment " + segment.name + " has been removed."
            });
          })
          .catch(error => {
            this.quasar.notify({
              color: "negative",
              message: "An error occurred: " + error.response.data.message
            });
          });
      });
    }
  }
};
</script>

<style scoped>

</style>
