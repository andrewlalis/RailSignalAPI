<template>
  <div class="rs-component">
    <h3>{{component.name}}</h3>
    <p>
      Id: {{component.id}}
    </p>
    <p>
      Position: (x = {{component.position.x}}, y = {{component.position.y}}, z = {{component.position.z}})
    </p>
    <p>
      Type: {{component.type}}
    </p>
    <p>
      Online: {{component.online}}
    </p>
    <SignalComponentView v-if="component.type === 'SIGNAL'" :signal="component" />
    <SegmentBoundaryNodeComponentView v-if="component.type === 'SEGMENT_BOUNDARY'" :node="component" />
    <PathNodeComponentView v-if="component.connectedNodes" :pathNode="component" :railSystem="railSystem" />
    <button @click="rsStore.removeComponent(component.id)">Remove</button>
  </div>
</template>

<script>
import SignalComponentView from "./SignalComponentView.vue";
import PathNodeComponentView from "./PathNodeComponentView.vue";
import SegmentBoundaryNodeComponentView from "./SegmentBoundaryNodeComponentView.vue";
import {useRailSystemsStore} from "../../../stores/railSystemsStore";

export default {
  components: {
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
  }
}
</script>

<style>
.rs-component {
  width: 20%;
  border: 1px solid black;
}
</style>