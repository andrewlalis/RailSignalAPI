<template>
  <q-list>
    <q-item>
      <q-item-section side>
        <q-btn label="Add Link" color="positive" />
      </q-item-section>
    </q-item>
    <q-item
      v-for="token in linkTokens"
      :key="token.id"
    >
      <q-item-section>
        <q-item-label>{{token.name}}</q-item-label>
        <q-item-label caption>{{token.id}}</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>

</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { getLinkTokens } from "src/api/linkTokens";

export default {
  name: "LinkTokensView",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  data() {
    return {
      linkTokens: [],
      addTokenDialog: false
    };
  },
  mounted() {
    getLinkTokens(this.railSystem)
      .then(tokens => this.linkTokens = tokens);
  }
};
</script>

<style scoped>

</style>
