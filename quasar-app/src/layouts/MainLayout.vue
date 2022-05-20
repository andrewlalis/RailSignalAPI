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
      <q-list>
        <q-item-label header>Rail Systems</q-item-label>

        <rail-system-link
          v-for="rs in rsStore.railSystems"
          :key="rs.id"
          :rail-system="rs"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref } from "vue";
import RailSystemLink from "components/RailSystemLink.vue";
import { useRailSystemsStore } from "stores/railSystemsStore";
import { refreshRailSystems } from "src/api/railSystems";

export default defineComponent({
  name: 'MainLayout',

  components: {
    RailSystemLink
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
