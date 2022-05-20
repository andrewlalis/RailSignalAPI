<template>
  <div class="flex">
    <div class="row full-width">
      <div class="col-md-6">
        <q-list>
          <q-item
            v-for="component in railSystem.components"
            :key="component.id"
            clickable
            v-ripple
          >
            <q-item-section>
              <q-item-label>{{component.name}}</q-item-label>
              <q-item-label caption>Id: {{component.id}}</q-item-label>
            </q-item-section>
            <q-item-section side top>
              <q-item-label caption>x: {{component.position.x}}, y: {{component.position.y}}, z: {{component.position.z}}</q-item-label>
              <q-item-label caption>{{component.type}}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>
      </div>
    </div>
  </div>
  <q-page-sticky position="bottom-right" :offset="[50, 18]">
    <q-fab
      icon="add"
      direction="up"
      color="accent"
    >
      <q-fab-action color="primary" label="Signal"/>
      <q-fab-action color="primary" label="Switch"/>
      <q-fab-action color="primary" label="Signal Boundary"/>
    </q-fab>
  </q-page-sticky>
</template>

<script>
import { RailSystem } from "src/api/railSystems";

export default {
  name: "ComponentsView",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
