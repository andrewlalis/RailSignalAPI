<template>
  <h4>Add Segment Boundary</h4>
  <form @submit.prevent="submit()">
    <div>
      <label for="addSBX">X</label>
      <input type="number" id="addSBX" v-model="formData.position.x" required/>
    </div>
    <div>
      <label for="addSBY">Y</label>
      <input type="number" id="addSBY" v-model="formData.position.y" required/>
    </div>
    <div>
      <label for="addSBZ">Z</label>
      <input type="number" id="addSBZ" v-model="formData.position.z" required/>
    </div>
    <div>
      <label for="addSBName">Name</label>
      <input type="text" id="addSBName" v-model="formData.name"/>
    </div>
    <div>
      <label for="addSBSegmentA">Segment A</label>
      <select id="addSBSegmentA" v-model="formData.segmentA">
        <option v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id" :value="segment">
          {{segment.id}} | {{segment.name}}
        </option>
      </select>
      <label for="addSBSegmentB">Segment B</label>
      <select id="addSBSegmentB" v-model="formData.segmentB">
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
  name: "AddSegmentBoundary.vue",
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
        segmentA: null,
        segmentB: null,
        segments: [],
        type: "SEGMENT_BOUNDARY"
      }
    }
  },
  methods: {
    submit() {
      this.formData.segments = [this.formData.segmentA, this.formData.segmentB];
      this.rsStore.addComponent(this.formData);
    }
  }
}
</script>

<style scoped>

</style>