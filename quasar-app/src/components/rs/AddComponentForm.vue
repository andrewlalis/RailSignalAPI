<template>
  <q-card>
    <q-form>
      <q-input
        label="Name"
        type="text"
        v-model="component.name"
      />
      <q-input
        label="X"
        type="number"
        v-model="component.position.x"
      />
      <q-input
        label="Y"
        type="number"
        v-model="component.position.y"
      />
      <q-input
        label="Z"
        type="number"
        v-model="component.position.z"
      />
    </q-form>
  </q-card>
</template>

<script>
export default {
  name: "AddComponentDialog",
  data() {
    return {
      component: {
        name: "",
        position: {
          x: 0,
          y: 0,
          z: 0
        }
      }
    }
  }
};
</script>

<style scoped>

</style>
