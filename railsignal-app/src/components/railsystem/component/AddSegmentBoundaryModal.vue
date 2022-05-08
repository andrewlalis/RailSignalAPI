<template>
  <div class="modal fade" tabindex="-1" id="addSegmentBoundaryModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add Segment Boundary</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>
            A <em>segment boundary</em> is a component that defines a link
            between one segment and another. This component can be used to
            monitor trains entering and exiting the connected segments. Usually
            used in conjunction with signals for classic railway signalling
            systems.
          </p>
          <form>
            <div class="mb-3">
              <label for="addSignalName" class="form-label">Name</label>
              <input class="form-control" type="text" id="addSignalName" v-model="formData.name" required/>
            </div>
            <div class="input-group mb-3">
              <label for="addSignalX" class="input-group-text">X</label>
              <input class="form-control" type="number" id="addSignalX" v-model="formData.position.x" required/>
              <label for="addSignalY" class="input-group-text">Y</label>
              <input class="form-control" type="number" id="addSignalY" v-model="formData.position.y" required/>
              <label for="addSignalZ" class="input-group-text">Z</label>
              <input class="form-control" type="number" id="addSignalZ" v-model="formData.position.z" required/>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="addSegmentBoundarySegmentA" class="form-label">Segment A</label>
                <select id="addSegmentBoundarySegmentA" class="form-select" v-model="formData.segmentA">
                  <option v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id" :value="segment">
                    {{segment.name}}
                  </option>
                </select>
              </div>
              <div class="col-md-6">
                <label for="addSegmentBoundarySegmentA" class="form-label">Segment B</label>
                <select id="addSegmentBoundarySegmentA" class="form-select" v-model="formData.segmentB">
                  <option v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id" :value="segment">
                    {{segment.name}}
                  </option>
                </select>
              </div>
            </div>
          </form>
          <div v-if="warnings.length > 0">
            <div v-for="msg in warnings" :key="msg" class="alert alert-danger mt-2">
              {{msg}}
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary" @click="formSubmitted()">Add</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {useRailSystemsStore} from "../../../stores/railSystemsStore";
import {Modal} from "bootstrap";

export default {
  name: "AddSegmentBoundaryModal",
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
      },
      warnings: []
    };
  },
  methods: {
    formSubmitted() {
      const modal = Modal.getInstance(document.getElementById("addSegmentBoundaryModal"));
      this.formData.segments = [this.formData.segmentA, this.formData.segmentB];
      this.rsStore.addComponent(this.formData)
          .then(() => {
            modal.hide();
          })
          .catch(error => {
            this.warnings.length = 0;
            this.warnings.push("Couldn't add the segment boundary: " + error.response.data.message)
          });
    }
  }
}
</script>

<style scoped>

</style>