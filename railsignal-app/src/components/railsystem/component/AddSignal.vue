<template>
  <h4>Add Signal</h4>
  <form @submit.prevent="rsStore.addComponent(formData)">
    <div>
      <label for="addSignalX">X</label>
      <input type="number" id="addSignalX" v-model="formData.position.x" required/>
    </div>
    <div>
      <label for="addSignalY">Y</label>
      <input type="number" id="addSignalY" v-model="formData.position.y" required/>
    </div>
    <div>
      <label for="addSignalZ">Z</label>
      <input type="number" id="addSignalZ" v-model="formData.position.z" required/>
    </div>
    <div>
      <label for="addSignalName">Name</label>
      <input type="text" id="addSignalName" v-model="formData.name"/>
    </div>
    <div>
      <label for="addSignalSegment">Segment</label>
      <select id="addSignalSegment" v-model="formData.segment">
        <option v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id" :value="segment">
          {{segment.id}} | {{segment.name}}
        </option>
      </select>
    </div>
    <button type="submit">Submit</button>
  </form>
</template>

<script>
import {useRailSystemsStore} from "../../../stores/railSystemsStore";

export default {
  name: "AddSignal",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  data() {
    return {
      formData: {
        name: "",
        position: {
          x: 0,
          y: 0,
          z: 0
        },
        segment: null,
        type: "SIGNAL"
      }
    }
  }
}
</script>

<style scoped>

</style>