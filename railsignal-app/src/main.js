import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";
import {useRailSystemsStore} from "./stores/railSystemsStore";


const pinia = createPinia();
const app = createApp(App);
app.use(pinia);

// Configure rail system updates.
const rsStore = useRailSystemsStore();
rsStore.$subscribe(mutation => {
    const evt = mutation.events;
    if (evt.key === "selectedRailSystem" && evt.newValue !== null) {
        rsStore.onSelectedRailSystemChanged();
    }
});

app.mount('#app')
