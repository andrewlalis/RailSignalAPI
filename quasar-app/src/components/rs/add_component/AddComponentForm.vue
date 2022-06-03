<template>
  <div class="row q-pa-md">
    <div class="col full-width">
      <q-form>
        <div class="text-h4">Add Component</div>
        <p>
          Add a new component to the rail system.
        </p>

        <!-- Basic Attributes -->
        <div class="row">
          <div class="col full-width">
            <q-input label="Name" type="text" v-model="component.name" autofocus/>
          </div>
        </div>
        <div class="row">
          <div class="col">
            <q-input
              label="X"
              type="number"
              class="col-sm-4"
              v-model="component.position.x"
            />
          </div>
          <div class="col">
            <q-input
              label="Y"
              type="number"
              class="col-sm-4"
              v-model="component.position.y"
            />
          </div>
          <div class="col">
            <q-input
              label="Z"
              type="number"
              class="col-sm-4"
              v-model="component.position.z"
            />
          </div>
        </div>
        <div class="row">
          <div class="col">
            <q-select
              v-model="component.type"
              :options="typeOptions"
              option-value="value"
              option-label="label"
              emit-value
              map-options
              label="Type"
            />
          </div>
        </div>

        <!-- Signal Attributes -->
        <div v-if="component.type === 'SIGNAL'" class="q-mt-md">
          <div class="row">
            <div class="col">
              <q-select
                v-model="signal.segment"
                :options="railSystem.segments"
                option-value="id"
                option-label="name"
                label="Segment"
              />
            </div>
          </div>
        </div>

        <!-- Label Attributes -->
        <div v-if="component.type === 'LABEL'" class="q-mt-md">
          <div class="row">
            <div class="col">
              <q-input
                v-model="label.text"
                type="text"
                label="Text"
              />
            </div>
          </div>
        </div>

        <!-- Segment Boundary Attributes -->
        <div v-if="component.type === 'SEGMENT_BOUNDARY'" class="q-mt-md">
          <div class="row">
            <div class="col">
              <q-select
                v-model="segmentBoundary.segments"
                :options="railSystem.segments"
                :option-value="segment => segment"
                :option-label="segment => segment.name"
                use-chips
                stack-label
                label="Segments"
                multiple
                :max-values="2"
              />
            </div>
          </div>
        </div>

        <!-- Switch Attributes -->
        <div v-if="component.type === 'SWITCH'" class="q-mt-md">
          <div class="row">
            <div class="col">
              <q-list>
                <q-item
                  v-for="config in switchData.possibleConfigurations"
                  :key="config.key"
                >
                  <q-item-section>
                    <q-item-label class="q-gutter-sm">
                      <q-chip
                        v-for="node in config.nodes"
                        :key="node.id"
                        :label="node.name"
                        dense
                        size="sm"
                      />
                    </q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <q-btn size="12px" flat dense round icon="delete" @click="removeSwitchConfig(config)"/>
                  </q-item-section>
                </q-item>
              </q-list>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6">
              <q-select
                v-model="switchData.configNode1"
                :options="getEligibleSwitchConfigNodes(switchData.configNode2)"
                :option-value="node => node"
                :option-label="node => node.name"
                label="First Node"
              />
            </div>
            <div class="col-sm-6">
              <q-select
                v-model="switchData.configNode2"
                :options="getEligibleSwitchConfigNodes(switchData.configNode1)"
                :option-value="node => node"
                :option-label="node => node.name"
                label="Second Node"
              />
            </div>
          </div>
          <div class="row">
            <q-btn label="Add Configuration" @click="addSwitchConfig" v-if="canAddSwitchConfig"/>
          </div>
        </div>

        <div class="row q-mt-md">
          <div class="col">
            <q-btn color="primary" label="Add" @click="submit" :disable="!canAdd()"/>
          </div>
        </div>
      </q-form>
    </div>
  </div>
</template>

<script>
import { RailSystem } from "src/api/railSystems";
import { useQuasar } from "quasar";
import { createComponent } from "src/api/components";

export default {
  name: "AddComponentForm",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    }
  },
  setup() {
    const typeOptions = [
      {label: "Signal", value: "SIGNAL"},
      {label: "Segment Boundary", value: "SEGMENT_BOUNDARY"},
      {label: "Switch", value: "SWITCH"},
      {label: "Label", value: "LABEL"}
    ];
    const quasar = useQuasar();
    return {
      typeOptions,
      quasar
    };
  },
  data() {
    return {
      component: {
        name: "",
        position: {x: 0, y: 0, z: 0},
        type: null
      },
      signal: {
        segment: null
      },
      label: {
        text: ""
      },
      segmentBoundary: {
        segments: []
      },
      switchData: {
        possibleConfigurations: [],
        configNode1: null,
        configNode2: null
      }
    }
  },
  methods: {
    submit() {
      const data = this.component;
      if (this.component.type === 'SIGNAL') {
        Object.assign(data, this.signal);
      }
      if (this.component.type === 'LABEL') {
        Object.assign(data, this.label);
      }
      if (this.component.type === 'SEGMENT_BOUNDARY') {
        Object.assign(data, this.segmentBoundary);
      }
      if (this.component.type === 'SWITCH') {
        Object.assign(data, this.switchData);
      }
      createComponent(this.railSystem, data)
        .then(() => {
          this.$emit('created');
          this.quasar.notify({
            color: "positive",
            message: "Added component: " + data.name
          });
        })
        .catch(error => {
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    canAdd() {
      if (this.component.type === null || this.component.name.length < 1) return false;
      if (this.component.type === 'SIGNAL') {
        return this.signal.segment !== null;
      }
      if (this.component.type === 'LABEL') {
        return this.label.text.length > 0;
      }
      if (this.component.type === 'SEGMENT_BOUNDARY') {
        return this.segmentBoundary.segments.length > 0;
      }
      return true;
    },
    getEligibleSwitchConfigNodes(excludedNode) {
      return this.railSystem.components.filter(c => {
        return (c.connectedNodes !== undefined && c.connectedNodes !== null) &&
          (excludedNode === null || c.id !== excludedNode.id);
      })
    },
    removeSwitchConfig(config) {
      const idx = this.switchData.possibleConfigurations.findIndex(cfg => cfg.key === config.key);
      if (idx > -1) {
        this.switchData.possibleConfigurations.splice(idx, 1);
      }
    },
    canAddSwitchConfig() {
      const n1 = this.switchData.configNode1;
      const n2 = this.switchData.configNode2;
      return n1 !== null && n2 !== null && n1.id !== n2.id &&
        !this.switchData.possibleConfigurations.some(config => {
          return config.nodes.every(node => {
            return node.id === n1.id || node.id === n2.id;
          });
        });
    },
    addSwitchConfig() {
      this.switchData.possibleConfigurations.push({
        nodes: [this.switchData.configNode1, this.switchData.configNode2],
        key: this.switchData.configNode1.id + '_' + this.switchData.configNode2.id
      });
      this.switchData.configNode1 = null;
      this.switchData.configNode2 = null;
    }
  }
};
</script>

<style scoped>

</style>
