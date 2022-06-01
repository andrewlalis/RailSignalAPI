<template>
  <base-component-view :component="segmentBoundary">
    <q-item-label header>Connected Segments</q-item-label>
    <q-item>
      <q-item-section
        v-for="segment in segmentBoundary.segments"
        :key="segment.id"
      >
        <q-item-label>{{segment.name}}</q-item-label>
        <q-item-label caption>ID: {{segment.id}}</q-item-label>
      </q-item-section>
    </q-item>
    <q-separator/>
    <path-node-item :path-node="segmentBoundary" :editable="true"/>
  </base-component-view>
</template>

<script>
import BaseComponentView from "components/rs/component_views/BaseComponentView.vue";
import PathNodeItem from "components/rs/component_views/PathNodeItem.vue";
export default {
  name: "SegmentBoundaryComponentView",
  components: {PathNodeItem, BaseComponentView},
  props: {
    segmentBoundary: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
