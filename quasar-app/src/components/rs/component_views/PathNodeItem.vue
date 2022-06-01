<template>
  <q-item-label header>Connected Path Nodes</q-item-label>
  <q-item>
    <q-item-section>
      <q-item-label class="q-gutter-sm">
        <q-chip
          v-for="node in pathNode.connectedNodes"
          :key="node.id"
          dense
          size="sm"
          :label="node.name"
        />
      </q-item-label>
    </q-item-section>
  </q-item>
  <q-item dense v-if="editable">
    <q-btn size="sm" color="accent" label="Edit Connected Nodes" @click="showDialog"/>
    <q-dialog v-model="dialog.visible" style="min-width: 400px" @hide="reset">
      <q-card>
        <q-form @submit="onSubmit" @reset="reset">
          <q-card-section>
            <div class="text-h6">Edit Connected Nodes</div>
            <p>
              Update the nodes that this path node is connected to. These
              connections define how the system understands your rail network,
              and are used for routing and analytics.
            </p>
            <p v-if="pathNode.type ==='SEGMENT_BOUNDARY'">
              You're editing a <strong>segment boundary</strong> node. This
              means that you can only have at most <strong>2</strong> nodes
              connected to it.
            </p>
          </q-card-section>
          <q-card-section>
            <q-select
              v-model="dialog.selectedNodes"
              :options="getEligibleNodes()"
              multiple
              :option-value="node => node"
              :option-label="node => node.name"
              use-chips
              stack-label
              label="Nodes"
            />
          </q-card-section>
          <q-card-actions align="right" class="text-primary">
            <q-btn flat label="Cancel" type="reset" @click="dialog.visible = false"/>
            <q-btn flat label="Edit" type="submit"/>
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>
  </q-item>
</template>

<script>
import {useRailSystemsStore} from "stores/railSystemsStore";
import {useQuasar} from "quasar";
import {updateComponent} from "src/api/components";

export default {
  name: "PathNodeItem",
  props: {
    pathNode: {
      type: Object,
      required: true
    },
    editable: {
      type: Boolean,
      required: false
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
        selectedNodes: []
      }
    }
  },
  methods: {
    showDialog() {
      this.dialog.selectedNodes = this.pathNode.connectedNodes.slice();
      this.dialog.visible = true;
    },
    reset() {
      this.dialog.selectedNodes.length = 0;
    },
    onSubmit() {
      const data = {...this.pathNode};
      data.connectedNodes = this.dialog.selectedNodes;
      updateComponent(this.rsStore.selectedRailSystem, data)
        .then(() => {
          this.dialog.visible = false;
          this.quasar.notify({
            color: "positive",
            message: "Path node updated."
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
    getEligibleNodes() {
      return this.rsStore.selectedRailSystem.components.filter(c => {
        return (c.connectedNodes !== null && c.connectedNodes !== undefined) &&
          this.dialog.selectedNodes.findIndex(node => node.id === c.id) === -1;
      });
    }
  }
}
</script>

<style scoped>

</style>
