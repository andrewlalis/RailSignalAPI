<template>
  <h2>{{railSystem.name}}</h2>
  <RsMap :railSystem="railSystem" />
  <RsComponent v-if="selectedComponent !== null" :component="selectedComponent" />
</template>

<script>
import RsMap from './railsystem/MapView.vue'
import RsComponent from './railsystem/Component.vue'

export default {
    components: {
        RsMap,
        RsComponent
    },
    props: {
        railSystem: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            selectedComponent: null
        }
    }
}
</script>

<style>
</style>
