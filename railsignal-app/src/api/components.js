import axios from "axios";
import {API_URL} from "./constants";

export function refreshComponents(rs) {
    return new Promise((resolve, reject) => {
        axios.get(`${API_URL}/rs/${rs.id}/c`)
            .then(response => {
                const previousSelectedComponentId = rs.selectedComponent ? rs.selectedComponent.id : null;
                rs.components = response.data;
                if (previousSelectedComponentId !== null) {
                    const previousComponent = rs.components.find(c => c.id === previousSelectedComponentId);
                    if (previousComponent) {
                        rs.selectedComponent = previousComponent;
                    } else {
                        rs.selectedComponent = null;
                    }
                } else {
                    rs.selectedComponent = null;
                }
                resolve();
            })
            .catch(reject);
    });
}

export function refreshSomeComponents(rs, components) {
    const promises = [];
    for (let i = 0; i < components.length; i++) {
        const promise = new Promise((resolve, reject) => {
            axios.get(`${API_URL}/rs/${rs.id}/c/${components[i].id}`)
                .then(resp => {
                    const idx = rs.components.findIndex(c => c.id === resp.data.id);
                    if (idx > -1) rs.components[idx] = resp.data;
                    resolve();
                })
                .catch(reject);
        });
        promises.push(promise);
    }
    return Promise.all(promises);
}

export function getComponent(rs, id) {
    return new Promise((resolve, reject) => {
        axios.get(`${this.apiUrl}/rs/${rs.id}/c/${id}`)
            .then(response => resolve(response.data))
            .catch(reject);
    });
}

export function createComponent(rs, data) {
    return new Promise((resolve, reject) => {
        axios.post(`${API_URL}/rs/${rs.id}/c`, data)
            .then(response => {
                const newComponentId = response.data.id;
                refreshComponents(rs)
                    .then(() => {
                        const newComponent = rs.components.find(c => c.id === newComponentId);
                        if (newComponent) {
                            rs.selectedComponent = newComponent;
                        }
                        resolve();
                    })
                    .catch(reject);
            })
            .catch(reject);
    });
}

export function removeComponent(rs, id) {
    return new Promise((resolve, reject) => {
        axios.delete(`${API_URL}/rs/${rs.id}/c/${id}`)
            .then(() => {
                refreshComponents(rs)
                    .then(resolve)
                    .catch(reject);
            })
            .catch(reject);
    });
}
