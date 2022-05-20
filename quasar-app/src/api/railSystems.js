import axios from "axios";
import {API_URL} from "./constants";
import { refreshSegments } from "src/api/segments";
import { refreshComponents } from "src/api/components";

export class RailSystem {
    constructor(data) {
        this.id = data.id;
        this.name = data.name;
        this.segments = [];
        this.components = [];
        this.websocket = null;
        this.selectedComponent = null;
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
                rsStore.selectedRailSystem = null;
                refreshRailSystems(rsStore)
                    .then(() => resolve)
                    .catch(error => reject(error));
            });
    });
}
