<template>
  <h4>Add Segment</h4>
  <form @submit.prevent="rsStore.addSegment(this.formData.segmentName)">
    <label for="addSegmentName">Name</label>
    <input type="text" v-model="formData.segmentName" />
    <button type="submit">Add</button>
  </form>
</template>

<script>
import {useRailSystemsStore} from "../../stores/railSystemsStore";

export default {
  name: "AddSegment",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  data() {
    return {
      formData: {
        segmentName: ""
      }
    }
  }
}
</script>

<style scoped>

</style>