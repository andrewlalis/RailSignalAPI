<template>
  <base-component-view :component="sw">
    <q-item-label header>Switch Configurations</q-item-label>
    <q-list>
      <q-item
        v-for="config in sw.possibleConfigurations"
        :key="config.id"
        dense
      >
        <q-item-section>
          <q-item-label class="q-gutter-sm">
            <q-chip
              v-for="node in config.nodes"
              :key="node.id"
              dense
              size="sm"
              :label="node.name"
              clickable
            />
          </q-item-label>
          <q-item-label caption>Configuration #{{config.id}}</q-item-label>
        </q-item-section>
        <q-item-section v-if="!isConfigActive(config)" side top>
          <q-btn dense size="sm" color="positive" @click="setActiveSwitchConfig(config.id)">Set Active</q-btn>
        </q-item-section>
      </q-item>
      <q-item>
        <q-btn size="sm" color="accent" label="Edit Configurations" @click="showDialog"/>
        <q-dialog v-model="dialog.visible" style="min-width: 400px" @hide="reset">
          <q-card>
            <q-form @submit="onSubmit" @reset="reset">
              <q-card-section>
                <div class="text-h6">Edit Switch Configurations</div>
                <p>
                  Edit the possible configurations for this switch.
                </p>
              </q-card-section>
              <q-card-section>
                <div class="row">
                  <q-list>
                    <q-item
                      v-for="config in dialog.configurations"
                      :key="config.key"
                    >
                      <q-item-section>
                        <q-item-label>
                          <q-chip
                            v-for="node in config.nodes"
                            :key="node.id"
                            :label="node.name"
                          />
                        </q-item-label>
                      </q-item-section>
                      <q-item-section side>
                        <q-btn size="12px" flat dense round icon="delete" @click="removeConfig(config)"/>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </div>
                <div class="row">
                  <div class="col-sm-6">
                    <q-select
                      v-model="dialog.pathNode1"
                      :options="getEligibleSwitchNodes(dialog.pathNode2)"
                      :option-value="node => node"
                      :option-label="node => node.name"
                      label="First Path Node"
                    />
                  </div>
                  <div class="col-sm-6">
                    <q-select
                      v-model="dialog.pathNode2"
                      :options="getEligibleSwitchNodes(dialog.pathNode1)"
                      :option-value="node => node"
                      :option-label="node => node.name"
                      label="Second Path Node"
                    />
                  </div>
                </div>
                <div class="row">
                  <q-btn label="Add Configuration" @click="addConfiguration" v-if="canAddConfig(dialog.pathNode1, dialog.pathNode2)"/>
                </div>
              </q-card-section>
              <q-card-actions align="right" class="text-primary">
                <q-btn flat label="Cancel" type="reset" @click="dialog.visible = false"/>
                <q-btn flat label="Edit" type="submit"/>
              </q-card-actions>
            </q-form>
          </q-card>
        </q-dialog>
      </q-item>
    </q-list>
    <q-separator/>
    <path-node-item :path-node="sw"/>
  </base-component-view>
</template>

<script>
import BaseComponentView from "components/rs/component_views/BaseComponentView.vue";
import {updateComponent, updateSwitchConfiguration} from "src/api/components";
import PathNodeItem from "components/rs/component_views/PathNodeItem.vue";
import {useRailSystemsStore} from "stores/railSystemsStore";
import {useQuasar} from "quasar";
export default {
  name: "SwitchComponentView",
  components: {PathNodeItem, BaseComponentView},
  props: {
    sw: {
      type: Object,
      required: true
    }
  },
  setup() {
    const rsStore = useRailSystemsStore();
    const quasar = useQuasar();
    return {rsStore, quasar};
  },
  data() {
    return {
      dialog: {
        visible: false,
        configurations: [],
        pathNode1: null,
        pathNode2: null
      }
    }
  },
  methods: {
    setActiveSwitchConfig(configId) {
      updateSwitchConfiguration(this.rsStore.selectedRailSystem, this.sw, configId)
        .then(() => {
          this.quasar.notify({
            color: "positive",
            message: "Sent switch configuration update request."
          });
        })
        .catch(error => {
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    isConfigActive(config) {
      return this.sw.activeConfiguration !== null && this.sw.activeConfiguration.id === config.id;
    },
    showDialog() {
      this.dialog.configurations = this.sw.possibleConfigurations.slice();
      this.dialog.configurations.forEach(cfg => {
        cfg.key = cfg.nodes[0].id + '_' + cfg.nodes[1].id
      });
      this.dialog.visible = true;
    },
    reset() {
      this.dialog.pathNode1 = null;
      this.dialog.pathNode2 = null;
      this.dialog.configurations.length = 0;
    },
    onSubmit() {
      const data = {...this.sw};
      data.possibleConfigurations = this.dialog.configurations;
      data.activeConfiguration = null;
      updateComponent(this.rsStore.selectedRailSystem, data)
        .then(() => {
          this.dialog.visible = false;
          this.quasar.notify({
            color: "positive",
            message: "Switch configurations updated."
          });
        })
        .catch(error => {
          console.log(error);
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    getEligibleSwitchNodes(exclude) {
      return this.rsStore.selectedRailSystem.components.filter(c => {
        return (c.connectedNodes !== undefined && c.connectedNodes !== null) &&
          (exclude === null || c.id !== exclude.id);
      });
    },
    canAddConfig(n1, n2) {
      return this.dialog.configurations.length < 2 &&
        n1 !== null && n2 !== null &&
        n1.id !== n2.id &&
        !this.dialog.configurations.some(config => {
          return config.nodes.every(node => {
            return node.id === n1.id || node.id === n2.id;
          });
        });
    },
    addConfiguration() {
      this.dialog.configurations.push({
        nodes: [this.dialog.pathNode1, this.dialog.pathNode2],
        key: this.dialog.pathNode1.id + '_' + this.dialog.pathNode2.id
      });
    },
    removeConfig(config) {
      const idx = this.dialog.configurations.findIndex(cfg => cfg.key === config.key);
      if (idx === -1) return;
      this.dialog.configurations.splice(idx, 1);
    }
  }
}
</script>

<style scoped>

</style>
