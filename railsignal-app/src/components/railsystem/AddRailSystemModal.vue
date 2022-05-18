<template>
  <div class="modal fade" tabindex="-1" id="addRailSystemModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add New Rail System</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form>
            <label for="addRailSystemNameInput" class="form-label">Name</label>
            <input
                id="addRailSystemNameInput"
                class="form-control"
                type="text"
                v-model="formData.rsName"
                required
            />
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
import {useRailSystemsStore} from "../../stores/railSystemsStore";
import {Modal} from "bootstrap";
import {createRailSystem} from "../../api/railSystems";

export default {
  name: "AddRailSystem",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  data() {
    return {
      formData: {
        name: ""
      },
      warnings: []
    };
  },
  methods: {
    formSubmitted() {
      createRailSystem(this.rsStore, this.formData.rsName)
          .then(rs => {
            this.formData.rsName = "";
            const modal = Modal.getInstance(document.getElementById("addRailSystemModal"));
            modal.hide();
            this.rsStore.selectedRailSystem = rs;
          })
          .catch(error => {
            console.log(error);
            this.warnings.length = 0;
            this.warnings.push("Couldn't add the rail system: " + error.response.data.message);
          });
    }
  }
}
</script>

<style scoped>

</style>