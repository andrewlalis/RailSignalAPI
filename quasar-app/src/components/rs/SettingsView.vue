<template>
  <q-list>
    <link-tokens-view :rail-system="railSystem"/>
  </q-list>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import LinkTokensView from "components/rs/settings/LinkTokensView.vue";

export default {
  name: "SettingsView",
  components: { LinkTokensView },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  }
};
</script>

<style scoped>

</style>
