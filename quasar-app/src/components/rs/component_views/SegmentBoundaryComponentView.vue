<template>
  <base-component-view :component="segmentBoundary">
    <q-item-label header>Connected Segments</q-item-label>
    <q-item>
      <q-item-section
        v-for="segment in segmentBoundary.segments"
        :key="segment.id"
      >
        <q-item-label>{{segment.name}}</q-item-label>
        <q-item-label caption>ID: {{segment.id}}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item dense>
      <q-btn size="sm" color="accent" label="Edit Segments" @click="showDialog"/>
      <q-dialog v-model="dialog.visible" style="max-width: 400px" @hide="reset">
        <q-card>
          <q-form @submit="onSubmit" @reset="reset">
            <q-card-section>
              <div class="text-h6">Edit Segments</div>
              <p>
                Update the segments that this boundary joins.
              </p>
            </q-card-section>
            <q-card-section>
              <q-select
                v-model="dialog.segments"
                :options="rsStore.selectedRailSystem.segments"
                multiple
                :option-value="s => s"
                :option-label="s => s.name"
                use-chips
                stack-label
                label="Segments"
                max-values="2"
              />
            </q-card-section>
            <q-card-actions align="right" class="text-primary">
              <q-btn flat label="Cancel" type="reset" @click="dialog.visible = false"/>
              <q-btn flat label="Edit" type="submit"/>
            </q-card-actions>
          </q-form>
        </q-card>
      </q-dialog>
    </q-item>
    <q-separator/>
    <path-node-item :path-node="segmentBoundary" :editable="true"/>
  </base-component-view>
</template>

<script>
import BaseComponentView from "components/rs/component_views/BaseComponentView.vue";
import PathNodeItem from "components/rs/component_views/PathNodeItem.vue";
import { useQuasar } from "quasar";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { updateComponent } from "src/api/components";
export default {
  name: "SegmentBoundaryComponentView",
  components: {PathNodeItem, BaseComponentView},
  props: {
    segmentBoundary: {
      type: Object,
      required: true
    }
  },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
  },
  data() {
    return {
      dialog: {
        visible: false,
        segments: []
      }
    }
  },
  methods: {
    showDialog() {
      this.dialog.segments = this.segmentBoundary.segments.slice();
      this.dialog.visible = true;
    },
    reset() {
      this.dialog.segments.length = 0;
    },
    onSubmit() {
      if (this.dialog.segments.length > 2) {
        this.quasar.notify({
          color: "warning",
          message: "Segment boundaries can only join 2 adjacent segments."
        });
        return;
      }
      const data = {...this.segmentBoundary};
      data.segments = this.dialog.segments;
      updateComponent(this.rsStore.selectedRailSystem, data)
        .then(() => {
          this.dialog.visible = false;
          this.quasar.notify({
            color: "positive",
            message: "Segments updated."
          });
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
}
</script>

<style scoped>

</style>
