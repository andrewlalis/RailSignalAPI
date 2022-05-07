<template>
  <h5>Connected Nodes</h5>
  <ul v-if="pathNode.connectedNodes.length > 0">
    <li v-for="node in pathNode.connectedNodes" :key="node.id">
      {{node.id}} | {{node.name}}
      <button @click="rsStore.removeConnection(pathNode, node)">Remove</button>
    </li>
  </ul>
  <p v-if="pathNode.connectedNodes.length === 0">
    There are no connected nodes.
  </p>
  <form @submit.prevent="rsStore.addConnection(pathNode, formData.nodeToAdd)">
    <label for="pathNodeAddConnection">Add Connection</label>
    <select id="pathNodeAddConnection" v-model="formData.nodeToAdd">
      <option v-for="node in this.getEligibleConnections()" :key="node.id" :value="node">
        {{node.id}} | {{node.name}} | {{node.type}}
      </option>
    </select>
    <button type="submit">Add</button>
  </form>
</template>

<script>
import {useRailSystemsStore} from "../../../stores/railSystemsStore";

export default {
  name: "PathNodeComponentView",
  setup() {
    const rsStore = useRailSystemsStore();
    return {
      rsStore
    };
  },
  props: {
    pathNode: {
      type: Object,
      required: true
    },
    railSystem: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      formData: {
        nodeToAdd: null
      }
    }
  },
  methods: {
    getEligibleConnections() {
      const nodes = [];
      for (let i = 0; i < this.railSystem.components.length; i++) {
        const c = this.railSystem.components[i];
        if (c.id !== this.pathNode.id && c.connectedNodes !== undefined && c.connectedNodes !== null) {
          let exists = false;
          for (let j = 0; j < this.pathNode.connectedNodes.length; j++) {
            if (this.pathNode.connectedNodes[j].id === c.id) {
              exists = true;
              break;
            }
          }
          if (!exists) nodes.push(c);
        }
      }
      return nodes;
    }
  }
}
</script>

<style scoped>

</style>