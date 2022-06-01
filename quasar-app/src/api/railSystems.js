import axios from "axios";
import {API_URL} from "./constants";
import { refreshSegments } from "src/api/segments";
import { refreshComponents } from "src/api/components";
import { closeWebsocketConnection, establishWebsocketConnection } from "src/api/websocket";
import { refreshLinkTokens } from "src/api/linkTokens";
import { refreshSettings } from "src/api/settings";

export class RailSystem {
    constructor(data) {
        this.id = data.id;
        this.name = data.name;
        this.settings = null;

        this.segments = [];
        this.components = [];
        this.linkTokens = [];

        this.websocket = null;
        this.selectedComponents = [];
        this.loaded = false;
    }
}

export function refreshRailSystems(rsStore) {
    return new Promise(resolve => {
        axios.get(`${API_URL}/rs`)
            .then(response => {
                const rsItems = response.data;
                rsStore.railSystems.length = 0;
                for (let i = 0; i < rsItems.length; i++) {
                    rsStore.railSystems.push(new RailSystem(rsItems[i]));
                }
                resolve();
            })
            .catch(error => console.error(error));
    })
}

export function refreshRailSystem(rs) {
  const promises = [];
  promises.push(refreshSegments(rs));
  promises.push(refreshComponents(rs));
  return Promise.all(promises);
}

export function createRailSystem(rsStore, name) {
    return new Promise((resolve, reject) => {
        axios.post(`${API_URL}/rs`, {name: name})
            .then(response => {
                const newId = response.data.id;
                refreshRailSystems(rsStore)
                    .then(() => resolve(rsStore.railSystems.find(rs => rs.id === newId)))
                    .catch(error => reject(error));
            })
            .catch(error => reject(error));
    });
}

export function removeRailSystem(rsStore, id) {
    return new Promise((resolve, reject) => {
        axios.delete(`${API_URL}/rs/${id}`)
            .then(() => {
                if (rsStore.selectedRailSystem !== null && rsStore.selectedRailSystem.id === id) {
                  rsStore.selectedRailSystem = null;
                }
                refreshRailSystems(rsStore)
                    .then(resolve)
                    .catch(reject);
            })
          .catch(reject);
    });
}

/**
 * Loads all data for a rail system. This is generally done when a rail system
 * is selected.
 * @param {RailSystem} rs
 */
export async function loadData(rs) {
  console.log("Loading rail system " + rs.id);
  await closeWebsocketConnection(rs);
  console.log("Closed websocket connection to " + rs.id);
  const updatePromises = [];
  updatePromises.push(refreshSegments(rs));
  updatePromises.push(refreshComponents(rs));
  updatePromises.push(refreshLinkTokens(rs));
  updatePromises.push(refreshSettings(rs));
  await Promise.all(updatePromises);
  await establishWebsocketConnection(rs);
  console.log("Finished loading rail system " + rs.id);
  rs.loaded = true;
}

/**
 * Unloads all data for a rail system. This is generally done when the user
 * navigates away from a rail system's page.
 * @param {RailSystem} rs
 * @returns {Promise}
 */
export async function unloadData(rs) {
  console.log("Unloading data for rail system " + rs.id);
  await closeWebsocketConnection(rs);
  rs.segments = [];
  rs.components = [];
  rs.linkTokens = [];
  rs.selectedComponents = [];
  rs.settings = null;
  rs.loaded = false;
  console.log("Finished unloading data for rail system " + rs.id);
}
