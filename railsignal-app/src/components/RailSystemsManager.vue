<template>
  <select v-model="rsStore.selectedRailSystem">
    <option v-for="rs in rsStore.railSystems" :key="rs.id" :value="rs">
      {{rs.name}}
    </option>
  </select>
  <p v-if="rsStore.railSystems.length === 0">
    There are no rail systems.
  </p>
  <button v-if="rsStore.selectedRailSystem !== null" @click="rsStore.removeRailSystem(rsStore.selectedRailSystem)">
    Remove this Rail System
  </button>

  <h3>Create a New Rail System</h3>
  <form>
    <label for="rsNameInput">Name</label>
    <input id="rsNameInput" type="text" v-model="formData.rsName"/>
    <button type="submit" @click.prevent="formSubmitted">Submit</button>
  </form>
</template>

<script>
import {useRailSystemsStore} from "../stores/railSystemsStore";

export default {
  name: "RailSystemsManager.vue",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  data() {
    return {
      formData: {
        rsName: ""
      }
    }
  },
  methods: {
    formSubmitted() {
      this.rsStore.createRailSystem(this.formData.rsName)
          .then(() => {
            this.formData.rsName = "";
          });
    }
  },
  mounted() {
    this.rsStore.refreshRailSystems();
  }
}
</script>

<style>

</style>