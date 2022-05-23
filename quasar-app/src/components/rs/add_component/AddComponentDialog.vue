<template>
  <q-dialog v-model="componentData.toggle" style="min-width: 400px" @hide="onReset">
    <q-card>
      <q-form @submit="onSubmit" @reset="onReset" @change="onChange">
        <q-card-section>
          <div class="text-h6">{{title}}</div>
          <slot name="subtitle"></slot>
        </q-card-section>
        <q-card-section>
          <q-input
            label="Name"
            type="text"
            v-model="componentData.name"
            autofocus
          />
          <div class="row">
            <q-input
              label="X"
              type="number"
              class="col-sm-4"
              v-model="componentData.position.x"
            />
            <q-input
              label="Y"
              type="number"
              class="col-sm-4"
              v-model="componentData.position.y"
            />
            <q-input
              label="Z"
              type="number"
              class="col-sm-4"
              v-model="componentData.position.z"
            />
          </div>
        </q-card-section>
        <slot :component-data="componentData"></slot>
        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancel" type="reset" @click="componentData.toggle = false"/>
          <q-btn flat label="Add" type="submit"/>
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script>
import { createComponent } from "src/api/components";
import { useQuasar } from "quasar";
import { RailSystem } from "src/api/railSystems";

export default {
  name: "AddComponentDialog",
  props: {
    modelValue: {
      type: Object,
      required: true
    },
    type: {
      type: String,
      required: true
    },
    railSystem: {
      type: RailSystem,
      required: true
    },
    title: {
      type: String,
      required: true
    },
    successMessage: {
      type: String,
      required: true
    }
  },
  setup() {
    const quasar = useQuasar();
    return {quasar};
  },
  data() {
    return {
      componentData: this.modelValue
    }
  },
  methods: {
    onSubmit() {
      const postData = {type: this.type, ...this.componentData};
      createComponent(this.railSystem, postData)
        .then(() => {
          this.quasar.notify({
            color: "positive",
            message: this.successMessage
          });
          this.componentData.toggle = false;
        })
        .catch(error => {
          console.log(error);
          this.quasar.notify({
            color: "negative",
            message: "An error occurred: " + error.response.data.message
          });
        });
    },
    onReset() {
      this.componentData.name = "";
      this.componentData.position.x = 0;
      this.componentData.position.y = 0;
      this.componentData.position.z = 0;
      this.$emit("reset");
    },
    onChange() {
      this.$emit("update:modelValue", this.componentData);
    }
  }
};
</script>

<style scoped>

</style>
