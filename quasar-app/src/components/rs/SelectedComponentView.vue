<template>
  <div class="q-pa-md">
    <div class="text-h6">{{component.name}}</div>
    <q-list bordered>
      <q-item clickable>
        <q-item-section>
          <q-item-label>{{component.type}}</q-item-label>
          <q-item-label caption>Id: {{component.id}}</q-item-label>
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
        content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <q-item
            v-for="segment in [component.segment]"
            :key="segment.id"
            clickable
            v-ripple
          >
            <q-item-section>
              <q-item-label>Linked to segment: {{segment.name}}</q-item-label>
              <q-item-label caption>Id: {{segment.id}}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <!-- Path node info -->
      <q-expansion-item
        label="Path Node Information"
        v-if="component.connectedNodes !== undefined && component.connectedNodes !== null"
        content-inset-level="0.5"
        switch-toggle-side
        expand-separator
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
        label="Segment Boundary Information"
        v-if="component.type === 'SEGMENT_BOUNDARY'"
        content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <q-item
            v-for="segment in component.segments"
            :key="segment.id"
            clickable
            v-ripple
          >
            <q-item-section>
              <q-item-label>{{segment.name}}</q-item-label>
              <q-item-label caption>Id: {{segment.id}}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>

      <!-- Switch info -->
      <q-expansion-item
        label="Switch Information"
        v-if="component.type === 'SWITCH'"
        content-inset-level="0.5"
        switch-toggle-side
        expand-separator
      >
        <q-list>
          <q-item
            v-for="config in component.possibleConfigurations"
            :key="config.id"
          >
            <q-item-section>
              <q-item-label class="q-gutter-md">
                <q-btn
                  v-for="node in config.nodes"
                  :key="node.id"
                  dense
                  size="sm"
                  :label="node.name"
                  :color="component.activeConfiguration && component.activeConfiguration.id === config.id ? 'primary' : 'secondary'"
                  @click="select(node)"
                />
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </q-expansion-item>
    </q-list>
  </div>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { useRailSystemsStore } from "stores/railSystemsStore";

export default {
  name: "SelectedComponentView",
  setup() {
    const rsStore = useRailSystemsStore();
    return {rsStore};
  },
  props: {
    component: {
      type: Object,
      required: true
    }
  },
  methods: {
    select(component) {
      const c = this.rsStore.selectedRailSystem.components.find(cp => cp.id === component.id);
      if (c) {
        this.rsStore.selectedRailSystem.selectedComponent = c;
      }
    }
  }
};
</script>

<style scoped>

</style>
