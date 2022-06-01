import {defineStore} from "pinia";

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
    })
});
