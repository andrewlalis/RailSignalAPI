<template>
  <h5>Signal Properties</h5>
  <table class="table">
    <tbody>
      <tr>
        <th>Connected to</th>
        <td>{{signal.segment.name}}, Occupied: {{signal.segment.occupied}}</td>
      </tr>
    </tbody>
  </table>
</template>

<script>
export default {
  name: "SignalComponentView",
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