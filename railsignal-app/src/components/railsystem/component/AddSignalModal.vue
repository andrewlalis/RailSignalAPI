<template>
  <div class="modal fade" tabindex="-1" id="addSignalModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add Signal</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>
            A <em>signal</em> is a component that relays information about your
            rail system to in-world devices. Classically, rail signals show a
            lamp indicator to tell information about the segment of the network
            they're attached to.
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
            <div class="mb-3">
              <label for="addSignalSegment" class="form-label">Segment</label>
              <select id="addSignalSegment" class="form-select" v-model="formData.segment">
                <option v-for="segment in rsStore.selectedRailSystem.segments" :key="segment.id" :value="segment">
                  {{segment.name}}
                </option>
              </select>
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
import {createComponent} from "../../../api/components";

export default {
  name: "AddSignalModal",
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
      },
      warnings: []
    };
  },
  methods: {
    formSubmitted() {
      const modal = Modal.getInstance(document.getElementById("addSignalModal"));
      createComponent(this.rsStore.selectedRailSystem, this.formData)
          .then(() => {
            modal.hide();
          })
          .catch(error => {
            this.warnings.length = 0;
            this.warnings.push("Couldn't add the signal: " + error.response.data.message)
          });
    }
  }
}
</script>

<style scoped>

</style>