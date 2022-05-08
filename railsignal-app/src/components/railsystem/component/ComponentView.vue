<template>
  <div>
    <h3>{{component.name}}</h3>
    <small class="text-muted">{{component.type}}</small>
    <table class="table">
      <tbody>
        <tr>
          <th>Id</th><td>{{component.id}}</td>
        </tr>
        <tr>
          <th>Position</th>
          <td>
            <table class="table table-borderless m-0 p-0">
              <tbody>
                <tr>
                  <td class="p-0">X = {{component.position.x}}</td>
                  <td class="p-0">Y = {{component.position.y}}</td>
                  <td class="p-0">Z = {{component.position.z}}</td>
                </tr>
              </tbody>
            </table>
          </td>
        </tr>
        <tr>
          <th>Online</th><td>{{component.online}}</td>
        </tr>
      </tbody>
    </table>
    <SignalComponentView v-if="component.type === 'SIGNAL'" :signal="component" />
    <SegmentBoundaryNodeComponentView v-if="component.type === 'SEGMENT_BOUNDARY'" :node="component" />
    <PathNodeComponentView v-if="component.connectedNodes" :pathNode="component" :railSystem="railSystem" />
    <button @click="removeComponent()" class="btn btn-sm btn-danger">Remove</button>
  </div>
  <ConfirmModal
      ref="removeConfirm"
      :id="'removeComponentModal'"
      :title="'Remove Component'"
      :message="'Are you sure you want to remove this component? It, and all associated data, will be permanently deleted.'"
  />
</template>

<script>
import SignalComponentView from "./SignalComponentView.vue";
import PathNodeComponentView from "./PathNodeComponentView.vue";
import SegmentBoundaryNodeComponentView from "./SegmentBoundaryNodeComponentView.vue";
import {useRailSystemsStore} from "../../../stores/railSystemsStore";
import ConfirmModal from "../../ConfirmModal.vue";

export default {
  components: {
    ConfirmModal,
    SegmentBoundaryNodeComponentView,
    SignalComponentView,
    PathNodeComponentView
  },
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  props: {
    component: {
      type: Object,
      required: true
    },
    railSystem: {
      type: Object,
      required: true
    }
  },
  methods: {
    removeComponent() {
      this.$refs.removeConfirm.showConfirm()
          .then(() => this.rsStore.removeComponent(this.component.id));
    }
  }
}
</script>

<style>
</style>