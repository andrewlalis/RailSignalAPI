<template>
  <div class="modal fade" tabindex="-1" id="addSwitchModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Add Switch</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form>
            <div class="mb-3">
              <label for="addSwitchName" class="form-label">Name</label>
              <input class="form-control" type="text" id="addSwitchName" v-model="formData.name" required/>
            </div>
            <div class="input-group mb-3">
              <label for="addSwitchX" class="input-group-text">X</label>
              <input class="form-control" type="number" id="addSwitchX" v-model="formData.position.x" required/>
              <label for="addSwitchY" class="input-group-text">Y</label>
              <input class="form-control" type="number" id="addSwitchY" v-model="formData.position.y" required/>
              <label for="addSwitchZ" class="input-group-text">Z</label>
              <input class="form-control" type="number" id="addSwitchZ" v-model="formData.position.z" required/>
            </div>
            <div class="mb-3">
              <ul class="list-group overflow-auto" style="height: 200px;">
                <li class="list-group-item" v-for="(config, idx) in formData.possibleConfigurations" :key="idx">
                  {{getConfigString(config)}}
                  <button class="btn btn-sm btn-secondary" @click="formData.possibleConfigurations.splice(idx, 1)">Remove</button>
                </li>
              </ul>
            </div>
            <div class="mb-3">
              <label for="addSwitchConfigs" class="form-label">Select two nodes this switch can connect.</label>
              <select id="addSwitchConfigs" class="form-select" multiple v-model="formData.possibleConfigQueue">
                <option v-for="node in getEligibleNodes()" :key="node.id" :value="node">
                  {{node.name}}
                </option>
              </select>
              <button type="button" class="btn btn-sm btn-success" @click="addPossibleConfig()">Add</button>
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
  name: "AddSwitchModal",
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
        possibleConfigurations: [],
        possibleConfigQueue: [],
        type: "SWITCH"
      },
      warnings: []
    };
  },
  methods: {
    formSubmitted() {
      const modal = Modal.getInstance(document.getElementById("addSwitchModal"));
      createComponent(this.rsStore.selectedRailSystem, this.formData)
          .then(() => {
            modal.hide();
          })
          .catch(error => {
            this.warnings.length = 0;
            this.warnings.push("Couldn't add the signal: " + error.response.data.message)
          });
    },
    getConfigString(config) {
      return config.nodes.map(n => n.name).join(", ");
    },
    getEligibleNodes() {
      return this.rsStore.selectedRailSystem.components.filter(c => {
        if (c.connectedNodes === undefined || c.connectedNodes === null) return false;
        for (let i = 0; i < this.formData.possibleConfigurations.length; i++) {
          const config = this.formData.possibleConfigurations[i];
          for (const node in config.nodes) {
            if (node.id === c.id) return false;
          }
        }
        return true;
      });
    },
    addPossibleConfig() {
      if (this.formData.possibleConfigQueue.length < 2) return;
      this.formData.possibleConfigurations.push({nodes: this.formData.possibleConfigQueue});
      this.formData.possibleConfigQueue = [];
    }
  }
}
</script>

<style scoped>

</style>