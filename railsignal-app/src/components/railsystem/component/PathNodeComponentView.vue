<template>
    <h5>Connected Nodes</h5>
    <ul class="list-group list-group-flush mb-2 border" v-if="pathNode.connectedNodes.length > 0" style="overflow: auto; max-height: 150px;">
      <li
          v-for="node in pathNode.connectedNodes"
          :key="node.id"
          class="list-group-item"
      >
        {{node.name}}
        <button @click="remove(node)" class="btn btn-sm btn-danger float-end">
          Remove
        </button>
      </li>
    </ul>
    <p v-if="pathNode.connectedNodes.length === 0">
      There are no connected nodes.
    </p>
    <form
        @submit.prevent="add(formData.nodeToAdd)"
        v-if="getEligibleConnections().length > 0"
        class="input-group mb-3"
    >
      <select v-model="formData.nodeToAdd" class="form-select form-select-sm">
        <option v-for="node in this.getEligibleConnections()" :key="node.id" :value="node">
          {{node.name}}
        </option>
      </select>
      <button type="submit" class="btn btn-sm btn-success">Add Connection</button>
    </form>
</template>

<script>
import {addConnection, removeConnection} from "../../../api/paths";

export default {
  name: "PathNodeComponentView",
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
    },
    remove(node) {
      removeConnection(this.railSystem, this.pathNode, node);
    },
    add(node) {
      addConnection(this.railSystem, this.pathNode, node);
    }
  }
}
</script>

<style scoped>

</style>