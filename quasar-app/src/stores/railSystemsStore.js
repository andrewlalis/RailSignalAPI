import { defineStore } from "pinia";
import { refreshSegments } from "../api/segments";
import { refreshComponents } from "../api/components";
import { closeWebsocketConnection, establishWebsocketConnection } from "../api/websocket";
import { refreshRailSystems } from "src/api/railSystems";
import { refreshLinkTokens } from "src/api/linkTokens";
import { refreshSettings } from "src/api/settings";

export const useRailSystemsStore = defineStore('RailSystemsStore', {
    state: () => ({
        /**
         * @type {RailSystem[]}
         */
        railSystems: [],
        /**
         * @type {RailSystem | null}
         */
        selectedRailSystem: null
    }),
    actions: {
      /**
       * Updates the selected rail system.
       * @param rsId {Number | null} The new rail system id.
       * @returns {Promise} A promise that resolves when the new rail system is
       * fully loaded and ready.
       */
      selectRailSystem(rsId) {
        // Close any existing websocket connections prior to refreshing.
        const wsClosePromises = [];
        if (this.selectedRailSystem !== null) {
          wsClosePromises.push(closeWebsocketConnection(this.selectedRailSystem));
        }
        if (rsId === null) return Promise.all(wsClosePromises);
        return new Promise(resolve => {
          Promise.all(wsClosePromises).then(() => {
            refreshRailSystems(this).then(() => {
              const rs = this.railSystems.find(r => r.id === rsId);
              console.log(rs);
              const updatePromises = [];
              updatePromises.push(refreshSegments(rs));
              updatePromises.push(refreshComponents(rs));
              updatePromises.push(refreshLinkTokens(rs));
              updatePromises.push(refreshSettings(rs));
              updatePromises.push(establishWebsocketConnection(rs));
              Promise.all(updatePromises).then(() => {
                this.selectedRailSystem = rs;
                resolve();
              });
            });
          });
        });
      }
    },
    getters: {
        rsId() {
            if (this.selectedRailSystem === null) return null;
            return this.selectedRailSystem.id;
        }
    }
});
