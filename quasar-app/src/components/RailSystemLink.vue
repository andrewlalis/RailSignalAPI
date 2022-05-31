<template>
  <q-item
    clickable
    v-ripple
    :to="'/rail-systems/' + railSystem.id"
  >
    <q-item-section>
      <q-item-label>{{railSystem.name}}</q-item-label>
    </q-item-section>
    <q-item-section top side>
      <q-btn size="12px" flat dense round icon="delete" @click.prevent="deleteRs"/>
    </q-item-section>
  </q-item>
</template>

<script>
import { RailSystem, removeRailSystem } from "src/api/railSystems";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { useQuasar } from "quasar";

export default {
  name: "RailSystemLink",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
  },
  methods: {
    deleteRs() {
      this.quasar.dialog({
        title: "Confirm Removal",
        message: "Are you sure you want to remove this rail system? All associated data will be deleted, permanently.",
        cancel: true
      }).onOk(() => {
        removeRailSystem(this.rsStore, this.railSystem.id)
          .then(() => {
            this.quasar.notify({
              color: "positive",
              message: "Rail system removed."
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
