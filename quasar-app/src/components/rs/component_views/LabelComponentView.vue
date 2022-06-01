<template>
  <base-component-view :component="label">
    <q-item-label header>Label</q-item-label>
    <q-item>
      <q-item-section>
        <q-item-label>{{label.text}}</q-item-label>
      </q-item-section>
    </q-item>
  </base-component-view>
</template>

<script>
import BaseComponentView from "components/rs/component_views/BaseComponentView.vue";
export default {
  name: "LabelComponentView",
  components: {BaseComponentView},
  props: {
    label: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
