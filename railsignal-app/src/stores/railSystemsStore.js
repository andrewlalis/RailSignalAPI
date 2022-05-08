import { defineStore } from "pinia";
import axios from "axios";

export const useRailSystemsStore = defineStore('RailSystemsStore', {
    state: () => ({
        railSystems: [],
        /**
         * @type {{segments: [Object], components: [Object], selectedComponent: Object} | null}
         */
        selectedRailSystem: null,
        apiUrl: import.meta.env.VITE_API_URL
    }),
    actions: {
        refreshRailSystems() {
            return new Promise((resolve, reject) => {
                axios.get(this.apiUrl + "/rs")
                    .then(response => {
                        this.railSystems = response.data;
                        resolve();
                    })
                    .catch(error => {
                        reject(error);
                    });
            });
        },
        createRailSystem(name) {
            return new Promise((resolve, reject) => {
                axios.post(this.apiUrl + "/rs", {name: name})
                    .then(response => {
                        const newId = response.data.id;
                        this.refreshRailSystems()
                            .then(() => resolve(this.railSystems.find(rs => rs.id === newId)))
                            .catch(error => reject(error));
                    })
                    .catch(error => reject(error));
            });
        },
        removeRailSystem(rs) {
            return new Promise((resolve, reject) => {
                axios.delete(this.apiUrl + "/rs/" + rs.id)
                    .then(() => {
                        this.selectedRailSystem = null;
                        this.refreshRailSystems()
                            .then(() => resolve)
                            .catch(error => reject(error));
                    })
            })
        },
        refreshSegments(rs) {
            return new Promise(resolve => {
                axios.get(`${this.apiUrl}/rs/${rs.id}/s`)
                    .then(response => {
                        rs.segments = response.data;
                        resolve();
                    });
            });
        },
        refreshAllComponents(rs) {
            return new Promise(resolve => {
                axios.get(`${this.apiUrl}/rs/${rs.id}/c`)
                    .then(response => {
                        rs.selectedComponent = null;
                        rs.components = response.data;
                        resolve();
                    });
            });
        },
        fetchSelectedRailSystemData() {
            if (!this.selectedRailSystem) return;
            this.refreshSegments(this.selectedRailSystem);
            this.refreshAllComponents(this.selectedRailSystem);
        },
        addSegment(name) {
            const rs = this.selectedRailSystem;
            return new Promise((resolve, reject) => {
                axios.post(`${this.apiUrl}/rs/${rs.id}/s`, {name: name})
                    .then(() => {
                        this.refreshSegments(rs)
                            .then(() => resolve())
                            .catch(error => reject(error));
                    })
                    .catch(error => reject(error));
            });
        },
        removeSegment(id) {
            const rs = this.selectedRailSystem;
            axios.delete(`${this.apiUrl}/rs/${rs.id}/s/${id}`)
                .then(() => this.refreshSegments(rs))
                .catch(error => console.log(error));
        },
        addComponent(data) {
            const rs = this.selectedRailSystem;
            return new Promise((resolve, reject) => {
                axios.post(`${this.apiUrl}/rs/${rs.id}/c`, data)
                    .then(() => {
                        this.refreshAllComponents(rs)
                            .then(() => resolve())
                            .catch(error => reject(error));
                    })
                    .catch(error => reject(error));
            });
        },
        removeComponent(id) {
            const rs = this.selectedRailSystem;
            axios.delete(`${this.apiUrl}/rs/${rs.id}/c/${id}`)
                .then(() => this.refreshAllComponents(rs))
                .catch(error => console.log(error));
        },
        fetchComponentData(component) {
            return new Promise(resolve => {
                const rs = this.selectedRailSystem;
                axios.get(`${this.apiUrl}/rs/${rs.id}/c/${component.id}`)
                    .then(response => resolve(response.data))
                    .catch(error => console.log(error));
            });
        },
        refreshComponents(components) {
            const rs = this.selectedRailSystem;
            for (let i = 0; i < components.length; i++) {
                axios.get(`${this.apiUrl}/rs/${rs.id}/c/${components[i].id}`)
                    .then(resp => {
                        const idx = this.selectedRailSystem.components.findIndex(c => c.id === resp.data.id);
                        if (idx > -1) this.selectedRailSystem.components[idx] = resp.data;
                    })
                    .catch(error => console.log(error));
            }
        },
        updateConnections(pathNode) {
            const rs = this.selectedRailSystem;
            return new Promise(resolve => {
                axios.patch(
                    `${this.apiUrl}/rs/${rs.id}/c/${pathNode.id}/connectedNodes`,
                    pathNode
                )
                    .then(response => {
                        pathNode.connectedNodes = response.data.connectedNodes;
                        resolve();
                    })
                    .catch(error => console.log(error));
            });
        },
        addConnection(pathNode, other) {
            pathNode.connectedNodes.push(other);
            this.updateConnections(pathNode)
                .then(() => {
                    this.refreshComponents(pathNode.connectedNodes);
                });
        },
        removeConnection(pathNode, other) {
            const idx = pathNode.connectedNodes.findIndex(n => n.id === other.id);
            if (idx > -1) {
                pathNode.connectedNodes.splice(idx, 1);
                this.updateConnections(pathNode)
                    .then(() => {
                        const nodes = [];
                        nodes.push(pathNode.connectedNodes);
                        nodes.push(other);
                        this.refreshComponents(nodes);
                    })
            }
        }
    }
});