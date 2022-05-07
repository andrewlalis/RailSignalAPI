<template>
  <h5>Segments</h5>
  <ul>
    <li v-for="segment in node.segments" :key="segment.id">
      {{segment.id}} | {{segment.name}}
    </li>
  </ul>
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