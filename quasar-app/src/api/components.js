import axios from "axios";
import {API_URL} from "./constants";

export function refreshComponents(rs) {
    return new Promise((resolve, reject) => {
        axios.get(`${API_URL}/rs/${rs.id}/c`)
            .then(response => {
                const previousSelectedComponentIds = rs.selectedComponents.map(c => c.id);
                rs.selectedComponents.length = 0;
                rs.components = response.data;
                for (let i = 0; i < previousSelectedComponentIds.length; i++) {
                  const component = rs.components.find(c => c.id === previousSelectedComponentIds[i]);
                  if (component) {
                    rs.selectedComponents.push(component);
                  }
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
        axios.get(`${API_URL}/rs/${rs.id}/c/${id}`)
            .then(response => resolve(response.data))
            .catch(reject);
    });
}

/**
 * Searches through the rail system's components.
 * @param {RailSystem} rs
 * @param {string|null} searchQuery
 * @return {Promise<Object>}
 */
export function searchComponents(rs, searchQuery) {
    return new Promise((resolve, reject) => {
        const params = {
            page: 0,
            size: 25
        };
        if (searchQuery) params.q = searchQuery;
        axios.get(`${API_URL}/rs/${rs.id}/c/search`, {params: params})
            .then(response => {
                resolve(response.data);
            })
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
                          rs.selectedComponents.length = 0;
                          rs.selectedComponents.push(newComponent);
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
