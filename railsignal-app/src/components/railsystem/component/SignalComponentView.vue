<template>
  <p>
    Connected segment: {{signal.segment.id}} {{signal.segment.name}}
  </p>
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