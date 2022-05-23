<template>
  <add-component-dialog
    v-model="addSignalData"
    type="SIGNAL"
    :rail-system="railSystem"
    title="Add Signal"
    success-message="Signal added."
    @reset="onReset"
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
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import AddComponentDialog from "components/rs/add_component/AddComponentDialog.vue";

export default {
  name: "AddSignalDialog",
  components: {AddComponentDialog},
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    },
    modelValue: {
      type: Boolean,
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
        toggle: this.modelValue
      }
    }
  },
  methods: {
    onReset() {
      this.addSignalData.segment = null;
    }
  }
};
</script>

<style scoped>

</style>
