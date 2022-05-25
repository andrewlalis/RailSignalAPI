<template>
  <div class="row">
    <div class="col-sm-4">
      <q-list>
        <q-item-label header>General Settings</q-item-label>
        <q-item>
          <q-item-section>
            <q-item-label>Read Only</q-item-label>
            <q-item-label caption>Freeze this rail system and prevent all updates.</q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-toggle v-model="settings.readOnly"/>
          </q-item-section>
        </q-item>
        <q-item>
          <q-item-section>
            <q-item-label>Authentication Required</q-item-label>
            <q-item-label caption>Require users to login to view and manage the system.</q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-toggle v-model="settings.authenticationRequired"/>
          </q-item-section>
        </q-item>

        <q-separator/>

        <link-tokens-view :rail-system="railSystem"/>
      </q-list>
    </div>
  </div>

</template>

<script>
import { RailSystem } from "src/api/railSystems";
import LinkTokensView from "components/rs/settings/LinkTokensView.vue";
import { useQuasar } from "quasar";
import { updateSettings } from "src/api/settings";

export default {
  name: "SettingsView",
  components: { LinkTokensView },
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  setup() {
    const quasar = useQuasar();
    return {quasar};
  },
  data() {
    return {
      settings: {
        readOnly: this.railSystem.settings.readOnly,
        authenticationRequired: this.railSystem.settings.authenticationRequired
      }
    }
  },
  watch: {
    settings: {
      handler(newValue, oldValue) {
        this.update();
      },
      deep: true
    }
  },
  methods: {
    update() {
      updateSettings(this.railSystem, this.settings)
        .then(() => {
          this.quasar.notify({
            color: "positive",
            message: "Settings have been updated.",
            closeBtn: true
          });
        })
        .catch(error => {
          this.quasar.notify({
            color: "negative",
            message: "Settings could not be updated: " + error.response.data.message
          });
        });
    }
  }
};
</script>

<style scoped>

</style>
