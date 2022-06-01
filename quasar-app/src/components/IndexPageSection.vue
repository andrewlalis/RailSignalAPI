<template>
  <div class="row">
    <div class="col-md-6 q-pa-md">
      <div class="text-h4">{{title}}</div>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "IndexPageSection",
  props: {
    title: {
      type: String,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
