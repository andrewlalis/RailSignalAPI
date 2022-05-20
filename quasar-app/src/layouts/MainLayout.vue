<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          Rail Signal
          <span v-if="rsStore.selectedRailSystem">
            - {{rsStore.selectedRailSystem.name}}
          </span>
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
    >
      <rail-systems-list :rail-systems="rsStore.railSystems" />
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from "vue";
import RailSystemsList from "components/RailSystemsList.vue";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { refreshRailSystems } from "src/api/railSystems";

export default defineComponent({
  name: 'MainLayout',
  components: {
    RailSystemsList
  },
  setup () {
    const rsStore = useRailSystemsStore()
    const leftDrawerOpen = ref(false)

    return {
      rsStore,
      leftDrawerOpen,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      }
    }
  },
  created() {
    refreshRailSystems(this.rsStore);
  }
})
</script>
