<template>
  <h5>Segments Connected</h5>
  <div class="mb-2">
    <span
      v-for="segment in node.segments"
      :key="segment.id"
      class="badge bg-secondary me-1"
    >
      {{segment.name}}
    </span>
  </div>
</template>

<script>
export default {
  name: "SegmentBoundaryNodeComponentView",
  props: {
    node: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>