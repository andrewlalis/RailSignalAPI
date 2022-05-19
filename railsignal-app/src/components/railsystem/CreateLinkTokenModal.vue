<template>
  <div class="modal fade" tabindex="-1" id="createLinkTokenModal">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Create Link Token</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>
            Create a <em>link token</em> to link components in this rail system
            to actual devices in your system, so your world can talk to this
            system. Each link token should have a unique label that can be used
            to identify it, and a list of components that it's linked to.
          </p>
          <p>
            Note that for security purposes, the raw token that's generated is
            only shown once, and is never available again. If you lose the
            token, you must create a new one instead and delete the old one.
          </p>
          <form>
            <div class="mb-3">
              <label for="createLinkTokenLabel" class="form-label">Label</label>
              <input
                  id="createLinkTokenLabel"
                  class="form-control"
                  type="text"
                  v-model="formData.label"
                  required
              />
            </div>
            <div class="mb-3">
              <label class="form-label" for="createLinkTokenComponentSelect">Select Components to Link</label>
              <ComponentSelector id="createLinkTokenComponentSelect" :railSystem="railSystem"  v-model="components"/>
            </div>
          </form>
          <div v-if="warnings.length > 0">
            <div v-for="msg in warnings" :key="msg" class="alert alert-danger mt-2">
              {{msg}}
            </div>
          </div>
          <div v-if="token !== null" class="alert alert-success mt-2">
            Created token: {{token}}
            <br>
            <small>Copy this token now; it will not be shown again.</small>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="reset()">Close</button>
          <button
              type="button"
              class="btn btn-primary"
              @click="formSubmitted()"
              v-if="token == null"
          >
            Add
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {createLinkToken} from "../../api/linkTokens";
import {RailSystem} from "../../api/railSystems";
import ComponentSelector from "./util/ComponentSelector.vue";

export default {
  name: "CreateLinkTokenModal",
  components: {ComponentSelector},
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  data() {
    return {
      formData: {
        label: "",
        componentIds: []
      },
      components: [],
      warnings: [],
      token: null
    };
  },
  methods: {
    formSubmitted() {
      this.formData.componentIds = this.components.map(c => c.id);
      createLinkToken(this.railSystem, this.formData)
          .then(token => {
            this.token = token;
          })
          .catch(error => {
            this.warnings.length = 0;
            this.warnings.push("Couldn't create token: " + error.response.data.message);
          });
    },
    reset() {
      // TODO: Fix this!! Reset doesn't work.
      this.formData.label = "";
      this.formData.componentIds = [];
      this.components = [];
      this.warnings = [];
      this.token = null;
    }
  }
}
</script>

<style scoped>

</style>