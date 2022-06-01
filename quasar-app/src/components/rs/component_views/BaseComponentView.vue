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

      <q-separator/>
      <slot></slot>

      <q-item>
        <q-item-section side>
          <q-btn color="negative" label="Remove" size="sm" @click="remove(component)"/>
        </q-item-section>
      </q-item>
    </q-list>
  </div>
</template>

<script>
import {removeComponent} from "src/api/components";
import {useQuasar} from "quasar";
import {useRailSystemsStore} from "stores/railSystemsStore";

export default {
  name: "BaseComponentView",
  props: {
    component: {
      type: Object,
      required: true
    }
  },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
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
    }
  }
}
</script>

<style scoped>

</style>
