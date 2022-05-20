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
      />
      <q-btn label="Add" color="primary"></q-btn>
    </q-item>
  </q-list>
</template>

<script>
import RailSystemLink from "components/RailSystemLink.vue";

export default {
  name: "RailSystemsList",
  components: { RailSystemLink },
  props: {
    railSystems: {
      type: Array,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
