import axios from "axios";
import {API_URL} from "./constants";

export function refreshRailSystems(rsStore) {
    return new Promise(resolve => {
        axios.get(`${API_URL}/rs`)
            .then(response => {
                rsStore.railSystems = response.data;
                resolve();
            })
            .catch(error => console.error(error));
    })
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
