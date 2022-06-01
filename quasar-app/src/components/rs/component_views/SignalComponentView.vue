<template>
  <base-component-view :component="signal">
    <q-item-label header>Connected to Segment</q-item-label>
    <segment-list-item :segment="signal.segment"/>
  </base-component-view>
</template>

<script>
import BaseComponentView from "components/rs/component_views/BaseComponentView.vue";
import SegmentListItem from "components/rs/SegmentListItem.vue";
export default {
  name: "SignalComponentView",
  components: {SegmentListItem, BaseComponentView},
  props: {
    signal: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
