<template>
  <div class="q-pa-md">
    <div class="text-h6">{{component.name}}</div>
    <q-list bordered>
      <q-item clickable>
        <q-item-section>
          <q-item-label>{{component.type}}</q-item-label>
          <q-item-label caption>Id: {{component.id}}</q-item-label>
        </q-item-section>
        <q-item-section v-if="component.online === true" top side>
          <q-chip color="positive" text-color="white">Online</q-chip>
        </q-item-section>
        <q-item-section v-if="component.online === false" top side>
          <q-chip color="negative" text-color="white">Offline</q-chip>
        </q-item-section>
      </q-item>

      <q-item clickable>
        <q-item-section>
          <q-item-label>Position</q-item-label>
          <q-item-label caption>
            X: {{component.position.x}},
            Y: {{component.position.y}},
            Z: {{component.position.z}}
          </q-item-label>
        </q-item-section>
      </q-item>

      <!-- Signal info -->
      <q-expansion-item
        id="signalInfo"
        label="Signal Information"
        v-if="component.type === 'SIGNAL'"
        :content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <segment-list-item v-for="segment in [component.segment]" :key="segment.id" :segment="segment" />
        </q-list>
      </q-expansion-item>

      <!-- Path node info -->
      <q-expansion-item
        label="Connected Nodes"
        v-if="component.connectedNodes !== undefined && component.connectedNodes !== null"
        :content-inset-level="0.5"
        switch-toggle-side
        expand-separator
        class="q-gutter-md"
      >
        <q-list>
          <q-item
            v-for="node in component.connectedNodes"
            :key="node.id"
            clickable
            v-ripple
            @click="select(node)"
          >
            <q-item-section>
              <q-item-label>{{node.name}}</q-item-label>
              <q-item-label caption>Id: {{node.id}}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <!-- Segment boundary info -->
      <q-expansion-item
        label="Connected Segments"
        v-if="component.type === 'SEGMENT_BOUNDARY'"
        :content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <segment-list-item v-for="segment in component.segments" :key="segment.id" :segment="segment"/>
        </q-list>
      </q-expansion-item>

      <!-- Switch info -->
      <q-expansion-item
        label="Switch Information"
        v-if="component.type === 'SWITCH'"
        :content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <q-item
            v-for="config in component.possibleConfigurations"
            :key="config.id"
          >
            <q-item-section>
              <q-item-label class="q-gutter-sm">
                <q-chip
                  v-for="node in config.nodes"
                  :key="node.id"
                  dense
                  size="sm"
                  :label="node.name"
                  :color="component.activeConfiguration && component.activeConfiguration.id === config.id ? 'primary' : 'secondary'"
                  :text-color="'white'"
                  clickable
                  @click="select(node)"
                />
              </q-item-label>
            </q-item-section>
            <q-item-section v-if="component.activeConfiguration === null || component.activeConfiguration.id !== config.id" side>
              <q-btn dense size="sm" color="positive" @click="setActiveSwitchConfig(component, config.id)">Set Active</q-btn>
            </q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <q-item>
        <q-item-section side>
          <q-btn color="warning" label="Remove" size="sm" @click="remove(component)"/>
        </q-item-section>
      </q-item>
    </q-list>
  </div>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { useRailSystemsStore } from "stores/railSystemsStore";
import SegmentListItem from "components/rs/SegmentListItem.vue";
import { useQuasar } from "quasar";
import { removeComponent, updateSwitchConfiguration } from "src/api/components";

export default {
  name: "SelectedComponentView",
  components: { SegmentListItem },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
  },
  props: {
    component: {
      type: Object,
      required: true
    },
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  methods: {
    select(component) {
      const c = this.rsStore.selectedRailSystem.components.find(cp => cp.id === component.id);
      if (c) {
        this.rsStore.selectedRailSystem.selectedComponents.length = 0;
        this.rsStore.selectedRailSystem.selectedComponents.push(c);
      }
    },
    remove(component) {
      this.quasar.dialog({
        title: "Confirm Removal",
        message: "Are you sure you want to remove this component? This cannot be undone.",
        cancel: true
      }).onOk(() => {
        removeComponent(this.railSystem, component.id)
          .then(() => {
            this.quasar.notify({
              color: "positive",
              message: "Component has been removed."
            });
          })
          .catch(error => {
            this.quasar.notify({
              color: "negative",
              message: "An error occurred: " + error.response.data.message
            });
          });
      });
    },
    setActiveSwitchConfig(sw, configId) {
      updateSwitchConfiguration(this.railSystem, sw, configId);
    }
  }
};
</script>

<style scoped>

</style>
