<template>
  <h5>Connected Nodes</h5>
  <table class="table" v-if="pathNode.connectedNodes.length > 0">
    <thead>
      <tr>
        <th>Name</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="node in pathNode.connectedNodes" :key="node.id">
        <td>{{node.name}}</td>
        <td>
          <button
              @click="rsStore.removeConnection(pathNode, node)"
              class="btn btn-sm btn-danger"
          >
            Remove
          </button>
        </td>
      </tr>
    </tbody>
  </table>
  <p v-if="pathNode.connectedNodes.length === 0">
    There are no connected nodes.
  </p>
  <form
      @submit.prevent="rsStore.addConnection(pathNode, formData.nodeToAdd)"
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