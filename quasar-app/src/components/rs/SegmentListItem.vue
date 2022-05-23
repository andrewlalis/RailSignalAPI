<template>
  <q-item clickable v-ripple>
    <q-item-section>
      <q-item-label>{{segment.name}}</q-item-label>
      <q-item-label caption>Id: {{segment.id}}</q-item-label>
    </q-item-section>
    <q-item-section side v-if="segment.occupied">
      <q-badge align="middle"></q-badge>
    </q-item-section>
  </q-item>
</template>

<script>
export default {
  name: "SegmentListItem",
  props: {
    segment: {
      type: Object,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
