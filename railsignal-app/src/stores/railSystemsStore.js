import { defineStore } from "pinia";
import axios from "axios";

export const useRailSystemsStore = defineStore('RailSystemsStore', {
    state: () => ({
        railSystems: [],
        selectedRailSystem: null,
        selectedComponent: null
    }),
    actions: {
        refreshRailSystems() {
            return new Promise((resolve, reject) => {
                axios.get(import.meta.env.VITE_API_URL + "/rs")
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
                axios.post(
                    import.meta.env.VITE_API_URL + "/rs",
                    {name: name}
                )
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
                axios.delete(import.meta.env.VITE_API_URL + "/rs/" + rs.id)
                    .then(() => {
                        this.refreshRailSystems()
                            .then(() => resolve)
                            .catch(error => reject(error));
                    })
            })
        }
    }
});