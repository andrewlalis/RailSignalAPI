<template>
  <q-expansion-item
    expand-separator
    label="Component Links"
    caption="Link components to your system."
    @before-show="refreshLinkTokens"
    switch-toggle-side
    :content-inset-level="0.5"
  >
    <q-list>
      <q-item
        v-for="token in linkTokens"
        :key="token.id"
      >
        <q-item-section>
          <q-item-label>{{token.label}}</q-item-label>
          <q-item-label caption>{{token.id}}</q-item-label>
        </q-item-section>
      </q-item>
      <q-item v-if="linkTokens.length === 0">
        <q-item-section>
          <q-item-label caption>There are no link tokens. Add one via the <em>Add Link</em> button below.</q-item-label>
        </q-item-section>
      </q-item>

      <q-separator/>

      <q-item>
        <q-item-section side>
          <q-btn label="Add Link" color="primary" @click="addTokenDialog = true" />
        </q-item-section>
      </q-item>
    </q-list>
  </q-expansion-item>

  <q-dialog v-model="addTokenDialog" style="min-width: 400px;" @hide="onReset">
    <q-card>
      <q-form @submit="onSubmit" @reset="onReset">
        <q-card-section>
          <div class="text-h6">Add Link Token</div>
          <p>
            Create a new link token that gives external components access to
            this rail system's live data streams. Use a link token to let signals
            in your system get real-time updates, and to let segment boundaries
            send real-time status updates as trains move.
          </p>
          <p>
            You will be shown the link token <strong>only once</strong> when the
            token is first created. If you lose it, you will need to delete the
            token and create a new one.
          </p>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Label"
            type="text"
            v-model="addTokenData.label"
            autofocus
          />
          <q-select
            label="Components"
            multiple
            v-model="addTokenData.selectedComponents"
            :options="getEligibleComponents()"
            :option-value="component => component.id"
            :option-label="component => component.name"
            use-chips
            stack-label
          />
        </q-card-section>

        <q-card-section v-if="token">
          <q-banner class="bg-primary text-white">
            <p>
              Link token created: <code class="text-bold">{{token}}</code>
            </p>
            <p>
              Copy this token now; it won't be shown again.
            </p>
          </q-banner>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Close" type="reset" @click="addTokenDialog = false"/>
          <q-btn flat label="Add" type="submit" v-if="!token"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { createLinkToken, getLinkTokens } from "src/api/linkTokens";
import { useQuasar } from "quasar";

export default {
  name: "LinkTokensView",
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
      linkTokens: [],
      addTokenDialog: false,
      addTokenData: {
        label: "",
        selectedComponents: [],
        componentIds: []
      },
      token: null
    };
  },
  methods: {
    refreshLinkTokens() {
      getLinkTokens(this.railSystem)
        .then(tokens => {
          this.linkTokens = tokens;
        });
    },
    getEligibleComponents() {
      return this.railSystem.components.filter(component => {
        return component.type === "SIGNAL" || component.type === "SEGMENT_BOUNDARY" || component.type === "SWITCH";
      });
    },
    onSubmit() {
      this.addTokenData.componentIds = this.addTokenData.selectedComponents.map(c => c.id);
      createLinkToken(this.railSystem, this.addTokenData)
        .then(token => {
          this.token = token;
        })
        .catch(error => {
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    onReset() {
      this.addTokenData.name = "";
      this.addTokenData.selectedComponents.length = 0;
      this.addTokenData.componentIds.length = 0;
      this.token = null;
    }
  }
};
</script>

<style scoped>

</style>
