<template>
  <q-list>
    <q-item-label header>Rail Systems</q-item-label>

    <rail-system-link
      v-for="rs in railSystems"
      :key="rs.id"
      :rail-system="rs"
    />

    <q-item>
      <q-input
        dense
        type="text"
        label="Name"
        v-model="addRailSystemName"
      />
      <q-btn label="Add" color="primary" @click="create"></q-btn>
    </q-item>
  </q-list>
</template>

<script>
import RailSystemLink from "components/RailSystemLink.vue";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { createRailSystem } from "src/api/railSystems";
import { useQuasar } from "quasar";

export default {
  name: "RailSystemsList",
  components: { RailSystemLink },
  props: {
    railSystems: {
      type: Array,
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
      addRailSystemName: ""
    }
  },
  methods: {
    create() {
      createRailSystem(this.rsStore, this.addRailSystemName)
        .then(() => {
          this.addRailSystemName = "";
          this.quasar.notify({
            color: "positive",
            message: "Rail system created."
          });
        })
        .catch(error => {
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
