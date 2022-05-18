<template>
  <nav class="navbar navbar-expand-md navbar-light bg-light">
    <div class="container-fluid">
      <span class="navbar-brand h1 mb-0">
        <img src="@/assets/icon.svg" height="24"/>
        Rail Signal
      </span>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2, mb-md-0">
          <li class="nav-item me-2 mb-2 mb-md-0">
            <select
                id="railSystemSelect"
                v-model="rsStore.selectedRailSystem"
                class="form-select form-select-sm"
            >
              <option v-for="rs in rsStore.railSystems" :key="rs.id" :value="rs">
                {{rs.name}}
              </option>
            </select>
          </li>
          <li class="nav-item me-2" v-if="rsStore.selectedRailSystem !== null">
            <button
                @click="remove()"
                class="btn btn-danger btn-sm"
                type="button"
            >
              Remove this Rail System
            </button>
          </li>
          <li class="nav-item">
            <button
              type="button"
              class="btn btn-success btn-sm"
              data-bs-toggle="modal"
              data-bs-target="#addRailSystemModal"
            >
              Add Rail System
            </button>
            <AddRailSystem />
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <ConfirmModal
      ref="confirmModal"
      :id="'removeRailSystemModal'"
      :title="'Confirm Rail System Removal'"
      :message="'Are you sure you want to remove this rail system? This CANNOT be undone. All data will be permanently lost.'"
  />
</template>

<script>
import {useRailSystemsStore} from "../stores/railSystemsStore";
import AddRailSystemModal from "./railsystem/AddRailSystemModal.vue";
import ConfirmModal from "./ConfirmModal.vue";
import {refreshRailSystems, removeRailSystem} from "../api/railSystems";

export default {
  name: "AppNavbar",
  components: {AddRailSystem: AddRailSystemModal, ConfirmModal},
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  mounted() {
    refreshRailSystems(this.rsStore);
  },
  methods: {
    remove() {
      this.$refs.confirmModal.showConfirm()
          .then(() => removeRailSystem(this.rsStore, this.rsStore.selectedRailSystem.id));

    }
  }
}
</script>

<style scoped>

</style>