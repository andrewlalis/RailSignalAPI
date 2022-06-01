<template>
  <label-component-view
    :label="component"
    v-if="component.type === 'LABEL'"
  />
  <signal-component-view
    :signal="component"
    v-if="component.type === 'SIGNAL'"
  />
  <segment-boundary-component-view
    :segment-boundary="component"
    v-if="component.type === 'SEGMENT_BOUNDARY'"
  />
  <switch-component-view
    :sw="component"
    v-if="component.type === 'SWITCH'"
  />
</template>

<script>
import SignalComponentView from "components/rs/component_views/SignalComponentView.vue";
import SegmentBoundaryComponentView from "components/rs/component_views/SegmentBoundaryComponentView.vue";
import LabelComponentView from "components/rs/component_views/LabelComponentView.vue";
import SwitchComponentView from "components/rs/component_views/SwitchComponentView.vue";

export default {
  name: "SelectedComponentView",
  components: {
    SwitchComponentView,
    LabelComponentView, SegmentBoundaryComponentView, SignalComponentView },
  props: {
    component: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
