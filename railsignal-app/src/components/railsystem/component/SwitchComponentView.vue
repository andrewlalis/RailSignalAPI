<template>
    <h5>Switch Configurations</h5>
    <ul class="list-group list-group-flush border" v-if="sw.possibleConfigurations.length > 0" style="overflow: auto; max-height: 150px;">
      <li
          v-for="config in sw.possibleConfigurations"
          :key="config.id"
          class="list-group-item"
      >
      <span
          v-for="node in config.nodes"
          :key="node.id"
          class="badge bg-secondary me-1"
      >
        {{node.name}}
      </span>
        <span
            v-if="sw.activeConfiguration !== null && sw.activeConfiguration.id === config.id"
            class="badge bg-success"
        >
        Active
      </span>
      </li>
    </ul>
</template>

<script>
export default {
  name: "SwitchComponentView",
  props: {
    sw: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>