<template>
  <div>
    <h3>{{component.name}}</h3>
    <small class="text-muted">
      {{component.type}}
    </small>
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
      </tbody>
    </table>
    <SignalComponentView v-if="component.type === 'SIGNAL'" :signal="component" />
    <SegmentBoundaryNodeComponentView v-if="component.type === 'SEGMENT_BOUNDARY'" :node="component" />
    <SwitchComponentView v-if="component.type === 'SWITCH'" :sw="component"/>
    <PathNodeComponentView v-if="component.connectedNodes" :pathNode="component" :railSystem="railSystem" />
    <button @click="remove()" class="btn btn-sm btn-danger">Remove</button>
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
import SwitchComponentView from "./SwitchComponentView.vue";
import {removeComponent} from "../../../api/components";

export default {
  components: {
    SwitchComponentView,
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
    remove() {
      this.$refs.removeConfirm.showConfirm()
          .then(() => {
            removeComponent(this.rsStore.selectedRailSystem, this.component.id)
                .catch(console.error);
          });
    }
  }
}
</script>

<style>
</style>