<template>
  <h5>Segments Connected</h5>
  <table class="table">
    <thead>
      <tr>
        <th>Name</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="segment in node.segments" :key="segment.id">
        <td>{{segment.name}}</td>
      </tr>
    </tbody>
  </table>
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