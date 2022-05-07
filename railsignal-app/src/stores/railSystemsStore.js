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
                    .then(() => {
                        this.refreshRailSystems()
                            .then(() => resolve())
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
        refreshComponents(rs) {
            return new Promise(resolve => {
                axios.get(`${this.apiUrl}/rs/${rs.id}/c`)
                    .then(response => {
                        rs.components = response.data;
                        resolve();
                    });
            });
        },
        fetchSelectedRailSystemData() {
            if (!this.selectedRailSystem) return;
            this.refreshSegments(this.selectedRailSystem);
            this.refreshComponents(this.selectedRailSystem);
        },
        addSegment(name) {
            const rs = this.selectedRailSystem;
            axios.post(`${this.apiUrl}/rs/${rs.id}/s`, {name: name})
                .then(() => this.refreshSegments(rs))
                .catch(error => console.log(error));
        },
        removeSegment(id) {
            const rs = this.selectedRailSystem;
            axios.delete(`${this.apiUrl}/rs/${rs.id}/s/${id}`)
                .then(() => this.refreshSegments(rs))
                .catch(error => console.log(error));
        },
        addComponent(data) {
            const rs = this.selectedRailSystem;
            axios.post(`${this.apiUrl}/rs/${rs.id}/c`, data)
                .then(() => this.refreshComponents(rs))
                .catch(error => console.log(error));
        },
        removeComponent(id) {
            const rs = this.selectedRailSystem;
            axios.delete(`${this.apiUrl}/rs/${rs.id}/c/${id}`)
                .then(() => this.refreshComponents(rs))
                .catch(error => console.log(error));
        },
        fetchComponentData(component) {
            return new Promise(resolve => {
                const rs = this.selectedRailSystem;
                axios.get(`${this.apiUrl}/rs/${rs.id}/c/${component.id}`)
                    .then(response => resolve(response.data))
                    .catch(error => console.log(error));
            });
        }
    }
});