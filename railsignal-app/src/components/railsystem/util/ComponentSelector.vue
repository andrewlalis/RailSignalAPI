<template>
  <div class="border p-2">
    <input type="text" class="form-control mb-2" placeholder="Search for components" v-model="searchQuery" @keyup="refreshComponents()" />
    <ul class="list-group list-group-flush" style="overflow: auto; max-height: 200px">
      <li
        class="list-group-item"
        v-for="component in possibleComponents"
        :key="component.id"
      >
        {{component.name}}

        <button
          type="button"
          class="badge btn btn-sm btn-success float-end"
          @click="selectComponent(component)"
          v-if="!isComponentSelected(component)"
        >
          Select
        </button>
        <span v-if="isComponentSelected(component)" class="badge bg-secondary float-end">Selected</span>
      </li>
    </ul>
    <div v-if="selectedComponents.length > 0" class="mt-2">
      <span
          class="badge bg-secondary me-1 mb-1"
          v-for="component in selectedComponents"
          :key="component.id"
      >
        {{component.name}}
        <button class="badge rounded-pill bg-danger" @click="deselectComponent(component)">
          X
        </button>
      </span>
    </div>
    <button
      class="btn btn-sm btn-secondary"
      type="button"
      v-if="selectedComponents.length > 1"
      @click="selectedComponents.length = 0"
    >
      Clear All
    </button>
  </div>
</template>

<script>
import {RailSystem} from "../../../api/railSystems";
import {searchComponents} from "../../../api/components";

export default {
  name: "ComponentSelector",
  props: {
    railSystem: {
      type: RailSystem,
      required: true
    },
    modelValue: {
      type: Array,
      required: true
    }
  },
  emits: ["update:modelValue"],
  data() {
    return {
      searchQuery: null,
      selectedComponents: [],
      possibleComponents: []
    };
  },
  mounted() {
    this.refreshComponents();
  },
  methods: {
    refreshComponents() {
      searchComponents(this.railSystem, this.searchQuery)
          .then(page => this.possibleComponents = page.content);
    },
    isComponentSelected(component) {
      return this.selectedComponents.some(c => c.id === component.id);
    },
    selectComponent(component) {
      if (this.isComponentSelected(component)) return;
      this.selectedComponents.push(component);
      this.selectedComponents.sort((a, b) => {
        const nameA = a.name.toUpperCase();
        const nameB = b.name.toUpperCase();
        if (nameA < nameB) return -1;
        if (nameA > nameB) return 1;
        return 0;
      });
      this.$emit("update:modelValue", this.selectedComponents);
    },
    deselectComponent(component) {
      const idx = this.selectedComponents.findIndex(c => c.id === component.id);
      if (idx > -1) {
        this.selectedComponents.splice(idx, 1);
        this.$emit("update:modelValue", this.selectedComponents);
      }
    }
  }
}
</script>

<style scoped>

</style>