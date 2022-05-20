import {defineStore} from "pinia";
import {refreshSegments} from "../api/segments"
import {refreshComponents} from "../api/components";
import {closeWebsocketConnection, establishWebsocketConnection} from "../api/websocket";
import { refreshRailSystems } from "src/api/railSystems";

export const useRailSystemsStore = defineStore('RailSystemsStore', {
    state: () => ({
        /**
         * @type {RailSystem[]}
         */
        railSystems: [],
        /**
         * @type {RailSystem | null}
         */
        selectedRailSystem: null,

        loaded: false
    }),
    actions: {
      async selectRailSystem(rsId) {
        if (!this.loaded) {
          await refreshRailSystems(this);
        }
        this.railSystems.forEach(r => {
          r.components.length = 0;
          r.segments.length = 0;
          closeWebsocketConnection(r);
        });
        if (!rsId) return;
        const rs = this.railSystems.find(r => r.id === rsId);
        await refreshSegments(rs);
        await refreshComponents(rs);
        establishWebsocketConnection(rs);
        this.selectedRailSystem = rs;
      }
    },
    getters: {
        rsId() {
            if (this.selectedRailSystem === null) return null;
            return this.selectedRailSystem.id;
        }
    }
});
