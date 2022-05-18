import {defineStore} from "pinia";
import {refreshSegments} from "../api/segments"
import {refreshComponents} from "../api/components";
import {closeWebsocketConnection, establishWebsocketConnection} from "../api/websocket";

export const useRailSystemsStore = defineStore('RailSystemsStore', {
    state: () => ({
        railSystems: [],
        /**
         * @type {{
         * segments: [Object],
         * components: [Object],
         * selectedComponent: Object | null,
         * websocket: WebSocket | null
         * } | null}
         */
        selectedRailSystem: null
    }),
    actions: {
        onSelectedRailSystemChanged() {
            this.railSystems.forEach(rs => closeWebsocketConnection(rs));
            if (!this.selectedRailSystem) return;
            refreshSegments(this.selectedRailSystem);
            refreshComponents(this.selectedRailSystem);
            establishWebsocketConnection(this.selectedRailSystem);
        }
    },
    getters: {
        rsId() {
            if (this.selectedRailSystem === null) return null;
            return this.selectedRailSystem.id;
        }
    }
});