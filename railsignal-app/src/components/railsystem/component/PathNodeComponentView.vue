<template>
  <h5>Connected Nodes</h5>
  <ul v-if="pathNode.connectedNodes.length > 0">
    <li v-for="node in pathNode.connectedNodes" :key="node.id">
      {{node.id}} | {{node.name}}
    </li>
  </ul>
  <p v-if="pathNode.connectedNodes.length === 0">
    There are no connected nodes.
  </p>
</template>

<script>
export default {
  name: "PathNodeComponentView",
  props: {
    pathNode: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>