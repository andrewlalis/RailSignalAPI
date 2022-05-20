<template>
  <q-item
    clickable
    :to="'/rail-systems/' + railSystem.id"
  >
    <q-item-section>
      <q-item-label>{{railSystem.name}}</q-item-label>
      <q-item-label caption>Id: {{railSystem.id}}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script>
import { RailSystem } from "src/api/railSystems";

export default {
  name: "RailSystemLink",
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
